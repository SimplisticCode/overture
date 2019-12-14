package org.overture.codegen.vdm2slang;

import com.google.googlejavaformat.java.FormatterException;
import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.trans.ModuleToClassTransformation;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.scalafmt.interfaces.Scalafmt;


public class SlangGen extends CodeGenBase {
    private Logger log = Logger.getLogger(this.getClass().getName());

    private SlangFormat slangFormat;
    private String SLANG_TEMPLATES_ROOT_FOLDER = "SlangTemplates";
    public static final String UTILS_FILE = "Utils";
    public static final String SEQ_UTIL_FILE = "SeqUtil";
    public static final String SET_UTIL_FILE = "SetUtil";
    public static final String MAP_UTIL_FILE = "MapUtil";
    public Scalafmt scalafmt;
    private SlangTransSeries transSeries;
    private SlangVarPrefixManager varPrefixManager;


    public SlangGen() {
        this.scalafmt = Scalafmt.create(this.getClass().getClassLoader());
        this.varPrefixManager = new SlangVarPrefixManager();
        this.slangFormat = new SlangFormat(SLANG_TEMPLATES_ROOT_FOLDER, generator.getIRInfo(), varPrefixManager);
        this.transSeries = new SlangTransSeries(this);
    }

    public SlangTransSeries getTransSeries() {
        return this.transSeries;
    }

    public void setTransSeries(SlangTransSeries transSeries) {
        this.transSeries = transSeries;
    }

    @Override
    protected GeneratedData genVdmToTargetLang(
            List<IRStatus<PIR>> statuses) throws AnalysisException {

        List<GeneratedModule> genModules = new LinkedList<GeneratedModule>();

        // Event notification
        statuses = initialIrEvent(statuses);

        List<String> userTestCases = getUserTestCases(statuses);

        List<IRStatus<AModuleDeclIR>> moduleStatuses = IRStatus.extract(statuses, AModuleDeclIR.class);
        List<IRStatus<PIR>> modulesAsNodes = IRStatus.extract(moduleStatuses);

        ModuleToClassTransformation moduleTransformation = new ModuleToClassTransformation(getInfo(), transAssistant, getModuleDecls(moduleStatuses));

        BinaryOptimiserTrans addTrans = new BinaryOptimiserTrans(transAssistant);

        for (IRStatus<PIR> status : modulesAsNodes) {
            try {
                generator.applyTotalTransformation(status, moduleTransformation);

                //generator.applyPartialTransformation(status, addTrans);

            } catch (org.overture.codegen.ir.analysis.AnalysisException e) {
                log.error(
                        "Error when applying partial transformation for module "
                                + status.getIrNodeName() + ": "
                                + e.getMessage());
                log.error("Skipping module..");
                e.printStackTrace();
            }
        }

        List<IRStatus<SClassDeclIR>> classStatuses = IRStatus.extract(modulesAsNodes, SClassDeclIR.class);
        classStatuses.addAll(IRStatus.extract(statuses, SClassDeclIR.class));
        List<IRStatus<SClassDeclIR>> canBeGenerated = new LinkedList<IRStatus<SClassDeclIR>>();


        for (IRStatus<SClassDeclIR> status : classStatuses) {
            if (status.canBeGenerated()) {
                canBeGenerated.add(status);
            } else {
                genModules.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status)));
            }
        }

        for (DepthFirstAnalysisAdaptor trans : transSeries.getSeries()) {
            for (IRStatus<SClassDeclIR> status : canBeGenerated) {
                try {
                    if (!getInfo().getDeclAssistant().isLibraryName(status.getIrNodeName())) {
                        generator.applyPartialTransformation(status, trans);
                    }

                } catch (org.overture.codegen.ir.analysis.AnalysisException e) {
                    log.error("Error when generating code for class "
                            + status.getIrNodeName() + ": " + e.getMessage());
                    log.error("Skipping class..");
                    e.printStackTrace();
                }
            }
        }

        MergeVisitor mergeVisitor = slangFormat.getMergeVisitor();

        for (IRStatus<SClassDeclIR> status : canBeGenerated) {
            try {
                genModules.add(genIrModule(mergeVisitor, status));
            } catch (org.overture.codegen.ir.analysis.AnalysisException e) {

                log.error("Error generating code for class "
                        + status.getIrNodeName() + ": " + e.getMessage());
                log.error("Skipping class..");
                e.printStackTrace();
            }
        }

        GeneratedData data = new GeneratedData();
        data.setClasses(genModules);

        return data;
    }

    private List<AModuleDeclIR> getModuleDecls(
            List<IRStatus<AModuleDeclIR>> statuses) {
        List<AModuleDeclIR> modules = new LinkedList<AModuleDeclIR>();

        for (IRStatus<AModuleDeclIR> status : statuses) {
            modules.add(status.getIrNode());
        }

        return modules;
    }


    public Generated generateSlangFromVdmExp(PExp exp) throws AnalysisException,
            org.overture.codegen.ir.analysis.AnalysisException {
        // There is no name validation here.
        IRStatus<SExpIR> expStatus = generator.generateFrom(exp);

        if (expStatus.canBeGenerated()) {
            // "expression" generator only supports a single transformation
            generator.applyPartialTransformation(expStatus, new DivideTrans(getInfo()));
        }

        try {
            return genIrExp(expStatus, slangFormat.getMergeVisitor());

        } catch (org.overture.codegen.ir.analysis.AnalysisException e) {
            log.error("Could not generate expression: " + exp);
            e.printStackTrace();
            return null;
        }
    }

    private GeneratedModule generate(IRStatus<PIR> status)
            throws org.overture.codegen.ir.analysis.AnalysisException {
        MergeVisitor codeEmitter = slangFormat.getCodeEmitter();

        if (status.canBeGenerated()) {
            codeEmitter.init();
            StringWriter writer = new StringWriter();
            status.getIrNode().apply(codeEmitter, writer);

            boolean isTestCase = isTestCase(status);

            if (codeEmitter.hasMergeErrors()) {
                return new GeneratedModule(status.getIrNodeName(), status.getIrNode(), codeEmitter.getMergeErrors(), isTestCase);
            } else if (codeEmitter.hasUnsupportedTargLangNodes()) {
                return new GeneratedModule(status.getIrNodeName(), new HashSet<VdmNodeInfo>(), codeEmitter.getUnsupportedInTargLang(), isTestCase);
            } else {
                // TODO: override formatCode and use dedicated code formatter
                GeneratedModule generatedModule = new GeneratedModule(status.getIrNodeName(), status.getIrNode(), formatCode(writer), isTestCase);
                generatedModule.setTransformationWarnings(status.getTransformationWarnings());
                return generatedModule;
            }
        } else {
            return new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status));
        }
    }



    @Override
    public String formatCode(StringWriter writer) {
        String code = writer.toString();
        Path config = Paths.get("/Users/simonthranehansen/Documents/GitHub/codegen/overture/core/codegen/vdm2slang/src/main/java/org/overture/codegen/vdm2slang/.scalafmt.conf");
        Path file = Paths.get("Main.scala");
        String formattedCode = scalafmt.format(config, file, code);
        return formattedCode;
    }

    public SlangVarPrefixManager getVarPrefixManager() {
        return varPrefixManager;
    }
}
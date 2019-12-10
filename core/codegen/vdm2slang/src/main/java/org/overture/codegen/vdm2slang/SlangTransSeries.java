package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.traces.TracesTrans;
import org.overture.codegen.trans.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.conc.EvalPermPredTrans;
import org.overture.codegen.trans.conc.MainClassConcTrans;
import org.overture.codegen.trans.conc.MutexDeclTrans;
import org.overture.codegen.trans.conc.SentinelTrans;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.trans.funcvalues.FuncValPrefixes;
import org.overture.codegen.trans.funcvalues.FuncValTrans;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.iterator.JavaLanguageIterator;
import org.overture.codegen.trans.letexps.FuncTrans;
import org.overture.codegen.trans.letexps.IfExpTrans;
import org.overture.codegen.trans.patterns.PatternTrans;
import org.overture.codegen.trans.patterns.PatternVarPrefixes;
import org.overture.codegen.trans.quantifier.CounterData;
import org.overture.codegen.trans.uniontypes.NonDetStmTrans;
import org.overture.codegen.trans.uniontypes.UnionTypeTrans;
import org.overture.codegen.trans.uniontypes.UnionTypeVarPrefixes;

import java.util.LinkedList;
import java.util.List;

//This class should contain all the code for the translation
public class SlangTransSeries {

    private SlangGen codeGen;
    private List<DepthFirstAnalysisAdaptor> series;
    private FuncValAssistant funcValAssist;

    public SlangTransSeries(SlangGen codeGen)
    {
        this.codeGen = codeGen;
        this.series = new LinkedList<>();
        this.funcValAssist = new FuncValAssistant();
        setupAnalysis();
    }

    private List<DepthFirstAnalysisAdaptor> setupAnalysis() {
        // Data and functionality to support the transformations
        IRInfo info = codeGen.getIRGenerator().getIRInfo();
        SlangVarPrefixManager varMan = codeGen.getVarPrefixManager();
        IterationVarPrefixes iteVarPrefixes = varMan.getIteVarPrefixes();
        Exp2StmVarPrefixes exp2stmPrefixes = varMan.getExp2stmPrefixes();
        FuncValPrefixes funcValPrefixes = varMan.getFuncValPrefixes();
        PatternVarPrefixes patternPrefixes = varMan.getPatternPrefixes();
        //UnionTypeVarPrefixes unionTypePrefixes = varMan.getUnionTypePrefixes();
        //List<INode> cloneFreeNodes = codeGen.getJavaFormat().getValueSemantics().getCloneFreeNodes();

        TransAssistantIR transAssist = codeGen.getTransAssistant();
        //IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(varMan.postCheckMethodName());

        // Construct the transformations
        RenamedTrans renamedTr = new RenamedTrans(transAssist);
        //ModuleRenamerTrans moduleRenamerTr = new ModuleRenamerTrans(transAssist);
        FieldOrderTrans fieldOrderTr = new FieldOrderTrans();
        AtomicStmTrans atomicTr = new AtomicStmTrans(transAssist, varMan.atomicTmpVar());
        NonDetStmTrans nonDetTr = new NonDetStmTrans(transAssist);
        DivideTrans divideTr = new DivideTrans(info);
        CallObjStmTrans callObjTr = new CallObjStmTrans(info);
        AssignStmTrans assignTr = new AssignStmTrans(transAssist);
        IfExpTrans ifExpTr = new IfExpTrans(transAssist);
        PolyFuncTrans polyTr = new PolyFuncTrans(transAssist);
        FuncValTrans funcValTr = new FuncValTrans(transAssist, funcValAssist, funcValPrefixes);
        //LetBeStTrans letBeStTr = new LetBeStTrans(transAssist, langIte, iteVarPrefixes);
        WhileStmTrans whileTr = new WhileStmTrans(transAssist, varMan.whileCond());
        IsExpTrans isExpTr = new IsExpTrans(transAssist, varMan.isExpSubject());
        SeqConvTrans seqConvTr = new SeqConvTrans(transAssist);
        //UnionTypeTrans unionTypeTr = new UnionTypeTrans(transAssist, unionTypePrefixes, cloneFreeNodes);
        SlAccessTrans slAccessTr = new SlAccessTrans();

        // Set up order of transformations
        series.add(renamedTr);
        series.add(fieldOrderTr);
        series.add(atomicTr);
        series.add(nonDetTr);
        series.add(divideTr);
        series.add(assignTr);
        series.add(callObjTr);
        series.add(polyTr);
        //series.add(funcTr);
        //series.add(prePostTr);
        series.add(ifExpTr);
        series.add(funcValTr);
        //series.add(letBeStTr);
        series.add(whileTr);
        //series.add(exp2stmTr);
        //series.add(patternTr);

        series.add(isExpTr);
        series.add(seqConvTr);
        series.add(slAccessTr);

        return series;
    }

    public FuncValAssistant getFuncValAssist()
    {
        return funcValAssist;
    }

    public List<DepthFirstAnalysisAdaptor> getSeries()
    {
        return series;
    }


    private CounterData consCounterData()
    {
        AExternalTypeIR type = new AExternalTypeIR();
        type.setName("Long");

        IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
        AIntLiteralExpIR initExp = irInfo.getExpAssistant().consIntLiteral(0);

        return new CounterData(type, initExp);
    }

    public void clear()
    {
        funcValAssist.getFuncValInterfaces().clear();
    }
}

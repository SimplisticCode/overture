package org.overture.codegen.vdm2slang;

import org.apache.log4j.Logger;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.*;
import org.overture.codegen.ir.statements.*;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;

import javax.enterprise.inject.New;
import java.io.StringWriter;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class SlangFormat {
    private static final String SLANG_FORMAT_KEY = "SlangFormat";

    protected SlangValueSemantics valueSemantics;
    protected Logger log = Logger.getLogger(this.getClass().getName());
    protected IRInfo info;


    private MergeVisitor codeEmitter;
    protected FuncValAssistant funcValAssist;
    private SExpIR loopVariable;
    private SlangVarPrefixManager varPrefixManager;

    public SlangFormat(String root, IRInfo info, SlangVarPrefixManager varPrefixManager) {
        TemplateCallable[] callables = new TemplateCallable[]{
                new TemplateCallable(SLANG_FORMAT_KEY, this)};
        this.codeEmitter = new MergeVisitor(new TemplateManager(root), callables);
        this.info = info;
        this.varPrefixManager = varPrefixManager;
        this.funcValAssist = null;
        this.valueSemantics = new SlangValueSemantics(this);
    }

    public IRInfo getIrInfo() {
        return info;
    }

    public MergeVisitor getMergeVisitor() {
        return codeEmitter;
    }

    public String format(SExpIR exp, boolean leftChild) throws AnalysisException {
        String formattedExp = format(exp);

        SlangPrecedence precedence = new SlangPrecedence();

        INode parent = exp.parent();

        if (!(parent instanceof SExpIR)) {
            return formattedExp;
        }

        boolean isolate = precedence.mustIsolate((SExpIR) parent, exp, leftChild);

        return isolate ? "(" + formattedExp + ")" : formattedExp;
    }


    public String genForIndexToVarName() {
        return info.getTempVarNameGen().nextVarName(varPrefixManager.getIteVarPrefixes().forIndexToVar());
    }

    public String genForIndexByVarName() {
        return info.getTempVarNameGen().nextVarName(varPrefixManager.getIteVarPrefixes().forIndexByVar());
    }


    public String formatIgnoreContext(INode node) throws AnalysisException {
        return format(node, true);
    }

    private String format(INode node, boolean ignoreContext)
            throws AnalysisException {
        StringWriter writer = new StringWriter();
        node.apply(codeEmitter, writer);

        return writer.toString();
    }


    public String formatUnary(SExpIR exp) throws AnalysisException {
        return format(exp, false);
    }

    //Does only handle one binding
    public String formatCollection(List<SMultipleBindIR> bindings) throws Exception {
        StringBuilder setComp = new StringBuilder();
        if (bindings.size() > 1)
            throw new Exception("Multiple binding are not handled by translator");
        for (SMultipleBindIR bind : bindings) {
            if (bind instanceof ASetMultipleBindIR) {
                setComp.append(format(((ASetMultipleBindIR) bind).getSet()));
            } else if (bind instanceof ASeqMultipleBindIR) {
                setComp.append(format(((ASeqMultipleBindIR) bind).getSeq()));
            }
        }

        return setComp.toString();
    }

    public String formatMapping(AMapletExpIR node) throws AnalysisException {
        StringBuilder map = new StringBuilder();
        map.append(format(node.getLeft()));
        map.append(", ");
        map.append(format(node.getRight()));

        return map.toString();
    }

    //Ask Peter
    public String formatInvariant(Object inv) {
        return "";
    }

    public String formatRecordPattern(ARecordPatternIR record) {
        return record.getTypename().toLowerCase();
    }


    public String formatEnum(ANamedTypeDeclIR node) {
        if (!(node.getType() instanceof AUnionTypeIR)) {
            return "";
        }
        AUnionTypeIR union = (AUnionTypeIR) node.getType();
        String NEWLINE = "\n";
        List<STypeIR> quotes = union.getTypes();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@enum object ");
        stringBuilder.append(node.getName());
        stringBuilder.append("{" + NEWLINE);
        for (STypeIR quote : quotes) {
            if (quote instanceof AQuoteTypeIR) {
                AQuoteTypeIR quoteType = (AQuoteTypeIR) quote;
                stringBuilder.append("'" + quoteType.getValue());
                stringBuilder.append(NEWLINE);
            }
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public String formatUnionType(AUnionTypeIR node) {
        return node.getNamedInvType().getName().getName() + ".Type";
    }

    public String formatPredicateVar(List<SMultipleBindIR> bindings) throws AnalysisException {
        for (SMultipleBindIR bind : bindings) {
            return (format(bind.getPatterns().getFirst()));
        }
        return "";
    }


    public String formatQuote(AQuoteLiteralExpIR quote) {
        return quote.getValue();
    }


    public String format(AMethodTypeIR methodType) throws AnalysisException {
        final String OBJ = "Object";

        if (funcValAssist == null) {
            return OBJ;
        }

        AInterfaceDeclIR methodTypeInterface = funcValAssist.findInterface(methodType);

        if (methodTypeInterface == null) {
            return OBJ; // Should not happen
        }

        AInterfaceTypeIR methodClass = new AInterfaceTypeIR();
        methodClass.setName(methodTypeInterface.getName());

        LinkedList<STypeIR> params = methodType.getParams();

        for (STypeIR param : params) {
            methodClass.getTypes().add(param.clone());
        }

        methodClass.getTypes().add(methodType.getResult().clone());

        if (methodType.parent() != null) {
            methodType.parent().replaceChild(methodType, methodClass);
        }

        return methodClass != null ? format(methodClass) : OBJ;
    }

    public String formatNotUnary(SExpIR exp) throws AnalysisException {
        String formattedExp = format(exp);
        boolean doNotWrap = exp instanceof ABoolLiteralExpIR
                || formattedExp.startsWith("(") && formattedExp.endsWith(")");

        formattedExp = doNotWrap ? "!" + formattedExp : "!(" + formattedExp + ")";
        return formattedExp.replace("\n", "");
    }

    public String formatName(INode node) throws AnalysisException {
        if (node instanceof ANewExpIR) {
            ANewExpIR newExp = (ANewExpIR) node;

            return formatTypeName(node, newExp.getName());
        } else if (node instanceof ARecordTypeIR) {
            ARecordTypeIR record = (ARecordTypeIR) node;
            ATypeNameIR typeName = record.getName();

            return formatTypeName(node, typeName);
        }

        throw new AnalysisException("Unexpected node in formatName: "
                + node.getClass().getName());
    }

    public String formatArgs(List<? extends SExpIR> exps)
            throws AnalysisException {
        StringWriter writer = new StringWriter();

        if (exps.size() <= 0) {
            return "";
        }

        SExpIR firstExp = exps.get(0);
        writer.append(format(firstExp));

        for (int i = 1; i < exps.size(); i++) {
            SExpIR exp = exps.get(i);
            writer.append(", " + format(exp));
        }

        return writer.toString();
    }

    public String formatTypeName(INode node, ATypeNameIR typeName) {
        // Type names are also used for quotes, which do not have a defining class.
        if (typeName.getDefiningClass() != null && !(node instanceof ARecordTypeIR)) {
            String typeNameStr = "";

            typeNameStr += typeName.getName();

            return typeNameStr;
        }

        SClassDeclIR classDef = node.getAncestor(SClassDeclIR.class);

        String definingClass = typeName.getDefiningClass() != null
                && classDef != null
                && !classDef.getName().equals(typeName.getDefiningClass())
                ? typeName.getDefiningClass() + "." : "";

        return definingClass + typeName.getName();
    }


    public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpIR node)
            throws AnalysisException {
        ANotUnaryExpIR transformed = transNotEquals(node);
        return formatNotUnary(transformed.getExp());
    }

    public boolean isNull(INode node) {
        return node == null;
    }

    public boolean isVoidType(STypeIR node) {
        return node instanceof AVoidTypeIR;
    }

    private ANotUnaryExpIR transNotEquals(ANotEqualsBinaryExpIR notEqual) {
        ANotUnaryExpIR notUnary = new ANotUnaryExpIR();
        notUnary.setType(new ABoolBasicTypeIR());

        AEqualsBinaryExpIR equal = new AEqualsBinaryExpIR();
        equal.setType(new ABoolBasicTypeIR());
        equal.setLeft(notEqual.getLeft().clone());
        equal.setRight(notEqual.getRight().clone());

        notUnary.setExp(equal);

        // Replace the "notEqual" expression with the transformed expression
        INode parent = notEqual.parent();

        // It may be the case that the parent is null if we execute e.g. [1] <> [1] in isolation
        if (parent != null) {
            parent.replaceChild(notEqual, notUnary);
            notEqual.parent(null);
        }

        return notUnary;
    }

    public static boolean isScoped(ABlockStmIR block) {
        return block != null && block.getScoped() != null && block.getScoped();
    }


    public String formatVdmSource(PIR irNode) {
        if (irNode != null) {
            org.overture.ast.node.INode vdmNode = LocationAssistantIR.getVdmNode(irNode);

            if (vdmNode != null) {
            }
        }

        return "";
    }


    public static String formatMetaData(List<ClonableString> metaData) {
        if (metaData == null || metaData.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ClonableString str : metaData) {
            sb.append(str.value).append('\n');
        }

        return sb.append('\n').toString();
    }

    private String handleEquals(AEqualsBinaryExpIR valueType)
            throws AnalysisException {
        return String.format("%s.equals(%s)", format(valueType.getLeft()), format(valueType.getRight()));
    }

    public String findModifier(Boolean isFinal) throws AnalysisException {
        return isFinal ? "val" : "var";
    }

    public String formatTemplateTypes(List<STypeIR> types)
            throws AnalysisException {
        return !types.isEmpty() ? "<" + formattedTypes(types, "") + ">" : "";
    }

    private String formattedTypes(List<STypeIR> types, String typePostFix)
            throws AnalysisException {
        STypeIR firstType = types.get(0);

        StringWriter writer = new StringWriter();
        writer.append(format(firstType) + typePostFix);

        for (int i = 1; i < types.size(); i++) {
            STypeIR currentType = types.get(i);
            writer.append(", " + format(currentType) + typePostFix);
        }

        String result = writer.toString();

        return result;
    }

    public String getConstructorArguments(List<AMethodDeclIR> methods) throws AnalysisException {
        String args = "";
        for (AMethodDeclIR method : methods) {
            if (method.getIsConstructor()) {
                args = format(method.getFormalParams());
            }
        }

        return args;
    }

    public String formatQuoteLiteral(AQuoteLiteralExpIR node) throws AnalysisException {
        String quoteName = "";
        ListIterator iterator = info.getClasses().listIterator();
        while (iterator.hasNext() && quoteName.isEmpty()) {
            Object classType = iterator.next();
            if (classType instanceof ADefaultClassDeclIR) {
                ADefaultClassDeclIR defaultClassDeclIR = (ADefaultClassDeclIR) classType;
                for (ATypeDeclIR typedeclaration : defaultClassDeclIR.getTypeDecls()) {
                    if (typedeclaration.getDecl() instanceof ANamedTypeDeclIR) {
                        ANamedTypeDeclIR namedTypeDeclIR = (ANamedTypeDeclIR) typedeclaration.getDecl();
                        if (namedTypeDeclIR.getType() instanceof AUnionTypeIR) {
                            AUnionTypeIR union = (AUnionTypeIR) namedTypeDeclIR.getType();
                            ListIterator unionIterator = union.getTypes().listIterator();
                            while (unionIterator.hasNext()) {
                                AQuoteTypeIR quote = (AQuoteTypeIR) unionIterator.next();
                                if (quote.getValue().contains(node.getValue())) {
                                    quoteName = namedTypeDeclIR.getName().toString();
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        }
        return quoteName + "." + node.getValue();

    }

    public String format(INode node) throws AnalysisException {
        StringWriter writer = new StringWriter();

        if (node instanceof AIdentifierVarExpIR) {
            AIdentifierVarExpIR varExpIR = (AIdentifierVarExpIR) node;
            if (varExpIR.getName().startsWith("_")) {
                varExpIR.setName(varExpIR.getName().replace("_", "IN("));
                varExpIR.setName(varExpIR.getName() + ")");
            }
        }

        //A constructor is handled differently in Slang
        if (node instanceof AMethodDeclIR) {
            AMethodDeclIR method = (AMethodDeclIR) node;
            if (method.getIsConstructor())
                return "";
        }

        if (isCollectionType(node))
            return handleCollection(node);

        node.apply(codeEmitter, writer);

        return writer.toString();
    }

    private String handleCollection(INode node) throws AnalysisException {
        if (node instanceof ASeqSeqTypeIR) {
            if (((ASeqSeqTypeIR) node).getSeqOf() instanceof ACharBasicTypeIR) {
                return "String";
            }
            String seqType = format(((ASeqSeqTypeIR) node).getSeqOf());
            return "ISZ[" + seqType + "]";

        }
        if (node instanceof AMapMapTypeIR) {
            String toType = format(((AMapMapTypeIR) node).getTo());
            String fromType = format(((AMapMapTypeIR) node).getFrom());
            return "Map[" + fromType + ", " + toType + "]";
        }
        if (node instanceof ASetSetTypeIR) {
            String setType = format(((ASetSetTypeIR) node).getSetOf());
            return "Set[" + setType + "]";
        }
        return "";
    }

    private boolean isCollectionType(INode node) {
        return node instanceof ASeqSeqTypeIR || node instanceof AMapMapTypeIR || node instanceof ASetSetTypeIR;
    }

    public String getSlangNumber() {
        return "Z";
    }

    public String formatDataFields(List<AFieldDeclIR> fields) throws AnalysisException {
        if (fields.size() <= 0) {
            return "";
        }

        StringWriter writer = new StringWriter();

        AFieldDeclIR firstParam = fields.get(0);
        firstParam.setFinal(true);
        writer.append(format(firstParam));

        for (int i = 1; i < fields.size(); i++) {
            fields.get(i).setFinal(true);
            AFieldDeclIR field = fields.get(i);
            writer.append(", ");
            writer.append(format(field));
        }
        return writer.toString();
    }

    public String format(List<AFormalParamLocalParamIR> params)
            throws AnalysisException {
        StringWriter writer = new StringWriter();

        if (params.size() <= 0) {
            return "";
        }

        AFormalParamLocalParamIR firstParam = params.get(0);
        writer.append(format(firstParam));

        for (int i = 1; i < params.size(); i++) {
            AFormalParamLocalParamIR param = params.get(i);
            writer.append(", ");
            writer.append(format(param));
        }
        return writer.toString();
    }

    public String formatOperationBody(SExpIR body) throws AnalysisException {
        String NEWLINE = "\n";
        if (body == null) {
            return "";
        }

        StringWriter generatedBody = new StringWriter();
        generatedBody.append("{" + NEWLINE);

        AFuncDeclIR func = body.getAncestor(AFuncDeclIR.class);
        SDeclIR preConditions = func.getPreCond();
        SDeclIR postConditions = func.getPostCond();
        generateContract(NEWLINE, generatedBody, preConditions, postConditions);

        generatedBody.append(formatUnary(body));
        generatedBody.append(NEWLINE + "}");

        return generatedBody.toString();
    }

    public String genClassInvariant(ADefaultClassDeclIR node) {
        if (node.getInvariant() == null) {
            return "";
        }
        return "in";
    }

    public void generateContract(String NEWLINE, StringWriter generatedBody, SDeclIR preConditions, SDeclIR postConditions) throws AnalysisException {
        if (preConditions != null || postConditions != null) {
            generatedBody.append("Contract(" + NEWLINE);
            if (preConditions != null) {
                generatedBody.append("Requires(" + NEWLINE);
                generatedBody.append(formatCond(preConditions));
                generatedBody.append("),");

            }
            generatedBody.append(createModifyClause(preConditions.parent()));
            generatedBody.append(")");
            if (postConditions != null) {
                generatedBody.append(",");
            }
            if (postConditions != null) {
                generatedBody.append("Ensures(" + NEWLINE);
                generatedBody.append(formatCond(postConditions));
                generatedBody.append(")" + NEWLINE);
            }
            generatedBody.append(")" + NEWLINE);
        }

    }

    private String createModifyClause(INode node) throws AnalysisException {
        StringWriter clause = new StringWriter();
        clause.append("Modifies(");
        if (node instanceof AMethodDeclIR) {
            SStmIR body = ((AMethodDeclIR) node).getBody();
            Collection<Object> values = body.getChildren(true).values();

            for (Object val : values) {
                GetModifyVariable(clause, val);
            }

        }
        if (node instanceof AFuncDeclIR) {
            //No change of state in a function
        }
        String clauseString = clause.toString();

        return clauseString.replaceAll(",$", ")");
    }

    private void GetModifyVariable(StringWriter clause, Object val) throws AnalysisException {
        if (val instanceof AAssignToExpStmIR) {
            AAssignToExpStmIR exp = (AAssignToExpStmIR) val;
            SExpIR lhs = exp.getTarget();
            if (!((AIdentifierVarExpIR) exp.getTarget()).getIsLocal()) {
                clause.append(format(lhs, false));
                clause.append(",");
            }
        }
        if (val instanceof NodeList) {
            NodeList exp = (NodeList) val;
            ListIterator iterator = exp.listIterator();
            while (iterator.hasNext()) {
                Object node = iterator.next();
                if (node instanceof AElseIfStmIR) {
                    AElseIfStmIR elseIfStm = (AElseIfStmIR) node;
                    GetModifyVariable(clause, elseIfStm.getThenStm());
                } else {
                    GetModifyVariable(clause, iterator.next());
                }
            }
        }
    }


    public boolean isUndefined(ACastUnaryExpIR cast) {
        return info.getExpAssistant().isUndefined(cast);
    }


    public String formatOperationBody(SStmIR body) throws AnalysisException {
        String NEWLINE = "\n";
        if (body == null) {
            return "";
        }

        StringWriter generatedBody = new StringWriter();
        generatedBody.append("{" + NEWLINE);
        AMethodDeclIR func = body.getAncestor(AMethodDeclIR.class);
        SDeclIR preConditions = func.getPreCond();
        SDeclIR postConditions = func.getPostCond();
        generateContract(NEWLINE, generatedBody, preConditions, postConditions);
        generatedBody.append(handleOpBody(body));
        generatedBody.append(NEWLINE + "}");

        return generatedBody.toString();
    }

    public String formatTuple(ATuplePatternIR node) throws AnalysisException {
        StringWriter arguments = new StringWriter();
        for (SPatternIR pattern : node.getPatterns()) {
            arguments.append(format(pattern));
        }

        return arguments.toString();
    }

    public String formatQuantifier(String quantifierType, List<SMultipleBindIR> bindings, SExpIR predicate) throws Exception {
        StringWriter quantifier = new StringWriter();
        int numbersOfBindings = 0;
        for (SMultipleBindIR binding : bindings) {
            for (SPatternIR identifier : binding.getPatterns()) {
                quantifier.append(quantifierType);
                quantifier.append("(");
                quantifier.append(formatCollection(binding));
                quantifier.append(".elements");
                quantifier.append(")(");
                quantifier.append(format(identifier));
                quantifier.append("=> ");
                numbersOfBindings++;
            }
        }
        quantifier.append(format(predicate, false));
        for (int i = 0; i < numbersOfBindings; i++) {
            quantifier.append(")");
        }
        return quantifier.toString();
    }

    private String formatCollection(SMultipleBindIR binding) throws AnalysisException {
        if (binding instanceof ASetMultipleBindIR) {
            return format(((ASetMultipleBindIR) binding).getSet());
        } else if (binding instanceof ASeqMultipleBindIR) {
            return format(((ASeqMultipleBindIR) binding).getSeq());
        }
        return "";
    }

    public String formatCond(SDeclIR cond) throws AnalysisException {
        String conditionString = "";
        if (cond instanceof AMethodDeclIR) {
            SStmIR exp = ((AMethodDeclIR) cond).getBody();
            //A pre/post condition should not contain a return
            conditionString = format(exp);
        }
        if (cond instanceof AFuncDeclIR) {
            SExpIR exp = ((AFuncDeclIR) cond).getBody();
            //A pre/post condition should not contain a return
            conditionString = format(exp);
        }
        return conditionString.trim().replace("&", ",\t").replace("return", "").trim();
    }

    private String handleOpBody(SStmIR body) throws AnalysisException {
        AMethodDeclIR method = body.getAncestor(AMethodDeclIR.class);

        if (method == null) {
            log.error("Could not find enclosing method when formatting operation body. Got: "
                    + body);
        }

        return format(body);
    }

    public MergeVisitor getCodeEmitter() {
        return codeEmitter;
    }

    public boolean isSeqType(SExpIR exp) {
        return info.getAssistantManager().getTypeAssistant().isSeqType(exp);
    }

    public boolean isMapType(SExpIR exp) {
        return info.getAssistantManager().getTypeAssistant().isMapType(exp);
    }

    public boolean isStringType(STypeIR type) {
        return (type instanceof ASeqSeqTypeIR && type.getSourceNode().getVdmNode().parent() instanceof AStringLiteralExp);
        //return info.getAssistantManager().getTypeAssistant().isStringType(type);
    }

    public boolean isStringType(SExpIR exp) {
        return info.getAssistantManager().getTypeAssistant().isStringType(exp);
    }


    public boolean genTypeDecl(ATypeDeclIR node) {
        if (node.getDecl() instanceof ARecordDeclIR) {
            return getSlangSettings().genRecsAsInnerClasses();
        } else {
            return info.getSettings().generateInvariants();
        }
    }

    public boolean isEnum(ATypeDeclIR node) {
        if (node.getDecl() instanceof ANamedTypeDeclIR) {
            return ((ANamedTypeDeclIR) node.getDecl()).getType() instanceof AUnionTypeIR;
        }
        return false;
    }

    public String buildString(List<SExpIR> exps) throws AnalysisException {
        StringBuilder sb = new StringBuilder();

        //sb.append("new String(new char[]{");
        sb.append("\"");
        if (exps.size() > 0) {
            for (int i = 0; i < exps.size(); i++) {
                sb.append(exps.get(i));
            }
        }

        sb.append("\"");
        //sb.append("})");

        return sb.toString();
    }

    public SlangSettings getSlangSettings() {
        return valueSemantics.getSlangSettings();
    }

    public boolean genDecl(ATypeDeclIR node) {
        return !(node.getDecl() instanceof ANamedTypeDeclIR);
    }

    public String formatIdentifierVar(AIdentifierPatternIR var) {
        String varName = "";
        varName = var.getName();
        return varName;
    }

    public boolean isLambda(AApplyExpIR applyExp) {
        SExpIR root = applyExp.getRoot();

        if (root instanceof AApplyExpIR
                && root.getType() instanceof AMethodTypeIR) {
            return true;
        }

        if (!(root instanceof SVarExpIR)) {
            return false;
        }

        SVarExpIR varExp = (SVarExpIR) root;

        return varExp.getIsLambda() != null && varExp.getIsLambda();
    }

    public String formatIdentifierVar(AIdentifierVarExpIR var) {
        String varName = "";
        if (!getSlangSettings().genRecsAsInnerClasses() && (var != null && !var.getIsLocal())) {
            // Only the VDM-SL-to-JML generator uses this strategy
            ADefaultClassDeclIR enclosingIrClass = var.getAncestor(ADefaultClassDeclIR.class);

            if (enclosingIrClass != null) {
                org.overture.ast.node.INode vdmNode = AssistantBase.getVdmNode(enclosingIrClass);

                if (!(vdmNode instanceof AModuleModules)) {
                    // The VDM node is a record (or state definition, which is also translated to a record class)
                    // The variable  ('var') must belong to the VDM module enclosing 'vdmNode'

                    AModuleModules module = vdmNode.getAncestor(AModuleModules.class);

                    if (module != null) {
                        List<String> fieldNames = new LinkedList<>();

                        if (vdmNode instanceof ARecordInvariantType) {
                            ARecordInvariantType rec = (ARecordInvariantType) vdmNode;
                            fieldNames = rec.getFields().stream().map(f -> f.getTagname().getName()).collect(Collectors.toList());
                        } else if (vdmNode instanceof AStateDefinition) {
                            AStateDefinition stateDef = (AStateDefinition) vdmNode;
                            fieldNames = stateDef.getFields().stream().map(f -> f.getTagname().getName()).collect(Collectors.toList());
                        } else {
                            log.error("Expected a record or statedefinition at this point, but got: " + vdmNode);
                        }

                        if (!fieldNames.contains(var.getName())) {
                            // It's not one of the record field names

							/*if (JavaCodeGenUtil.isValidJavaPackage(getJavaSettings().getJavaRootPackage()))
							{
								varName += getJavaSettings().getJavaRootPackage() + ".";
							}

							varName += module.getName().getName() + "." + var.getName();

							 */
                        } else {
                            // We're accessing a record field, so there's no need to use a fully qualified name
                            varName = var.getName();
                        }
                    } else {
                        log.error("Expected 'vdmNode' to be enclosed by a module");
                    }
                } else {
                    // Orginary case, we're inside a module
                    varName = var.getName();
                }
            }
        } else {
            varName = var.getName();
        }

        return varName;
    }

    public String formatEqualsBinaryExp(AEqualsBinaryExpIR node)
            throws AnalysisException {
        STypeIR leftNodeType = node.getLeft().getType();

        if (leftNodeType instanceof SSeqTypeIR
                || leftNodeType instanceof SSetTypeIR
                || leftNodeType instanceof SMapTypeIR) {
            return handleCollectionComparison(node);
        } else {
            return handleEquals(node);
        }
    }

    private String handleCollectionComparison(SBinaryExpIR node)
            throws AnalysisException {
        // In VDM the types of the equals are compatible when the AST passes the type check
        SExpIR leftNode = node.getLeft();
        SExpIR rightNode = node.getRight();

        String empty = "Utils.empty(%s)";

        if (isEmptyCollection(leftNode.getType())) {
            return String.format(empty, format(node.getRight()));
        } else if (isEmptyCollection(rightNode.getType())) {
            return String.format(empty, format(node.getLeft()));
        }

        return ".equals(" + format(node.getLeft()) + ", "
                + format(node.getRight()) + ")";
    }

    private boolean isEmptyCollection(STypeIR type) {
        if (type instanceof SSeqTypeIR) {
            SSeqTypeIR seq = (SSeqTypeIR) type;
            return seq.getEmpty();
        } else if (type instanceof SSetTypeIR) {
            SSetTypeIR set = (SSetTypeIR) type;
            return set.getEmpty();
        } else if (type instanceof SMapTypeIR) {
            SMapTypeIR map = (SMapTypeIR) type;
            return map.getEmpty();
        }

        return false;
    }

}
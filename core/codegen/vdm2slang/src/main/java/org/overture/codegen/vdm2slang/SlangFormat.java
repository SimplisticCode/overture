package org.overture.codegen.vdm2slang;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;

public class SlangFormat
{
	private static final String SLANG_FORMAT_KEY = "SlangFormat";

	protected SlangValueSemantics valueSemantics;
	protected Logger log = Logger.getLogger(this.getClass().getName());
	protected IRInfo info;



	private MergeVisitor codeEmitter;
	protected FuncValAssistant funcValAssist;

	public SlangFormat(String root, IRInfo info)
	{
		TemplateCallable[] callables = new TemplateCallable[] {
				new TemplateCallable(SLANG_FORMAT_KEY, this) };
		this.codeEmitter = new MergeVisitor(new TemplateManager(root), callables);
		this.info = info;
		this.funcValAssist = null;
		this.valueSemantics = new SlangValueSemantics(this);
	}

	public IRInfo getIrInfo()
	{
		return info;
	}

	public MergeVisitor getMergeVisitor()
	{
		return codeEmitter;
	}

	public String format(Object exp)
	{
		String a = "as";
		return a;
	}

	public String format(SExpIR exp, boolean leftChild) throws AnalysisException
	{
		String formattedExp = format(exp);

		SlangPrecedence precedence = new SlangPrecedence();

		INode parent = exp.parent();

		if (!(parent instanceof SExpIR))
		{
			return formattedExp;
		}

		boolean isolate = precedence.mustIsolate((SExpIR) parent, exp, leftChild);

		return isolate ? "(" + formattedExp + ")" : formattedExp;
	}

	public String formatUnary(SExpIR exp) throws AnalysisException
	{
		return format(exp, false);
	}


	public String format(AMethodTypeIR methodType) throws AnalysisException
	{
		final String OBJ = "Object";

		if (funcValAssist == null)
		{
			return OBJ;
		}

		AInterfaceDeclIR methodTypeInterface = funcValAssist.findInterface(methodType);

		if (methodTypeInterface == null)
		{
			return OBJ; // Should not happen
		}

		AInterfaceTypeIR methodClass = new AInterfaceTypeIR();
		methodClass.setName(methodTypeInterface.getName());

		LinkedList<STypeIR> params = methodType.getParams();

		for (STypeIR param : params)
		{
			methodClass.getTypes().add(param.clone());
		}

		methodClass.getTypes().add(methodType.getResult().clone());

		if(methodType.parent() != null)
		{
			methodType.parent().replaceChild(methodType, methodClass);
		}

		return methodClass != null ? format(methodClass) : OBJ;
	}

	public String formatNotUnary(SExpIR exp) throws AnalysisException
	{
		String formattedExp = format(exp);

		boolean doNotWrap = exp instanceof ABoolLiteralExpIR
				|| formattedExp.startsWith("(") && formattedExp.endsWith(")");

		return doNotWrap ? "!" + formattedExp : "!(" + formattedExp + ")";
	}

    public String formatName(INode node) throws AnalysisException
    {
        if (node instanceof ANewExpIR)
        {
            ANewExpIR newExp = (ANewExpIR) node;

            return formatTypeName(node, newExp.getName());
        } else if (node instanceof ARecordTypeIR)
        {
            ARecordTypeIR record = (ARecordTypeIR) node;
            ATypeNameIR typeName = record.getName();

            return formatTypeName(node, typeName);
        }

        throw new AnalysisException("Unexpected node in formatName: "
                + node.getClass().getName());
    }

	public String formatArgs(List<? extends SExpIR> exps)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (exps.size() <= 0)
		{
			return "";
		}

		SExpIR firstExp = exps.get(0);
		writer.append(format(firstExp));

		for (int i = 1; i < exps.size(); i++)
		{
			SExpIR exp = exps.get(i);
			writer.append(", " + format(exp));
		}

		return writer.toString();
	}

    public String formatTypeName(INode node, ATypeNameIR typeName)
    {
        // Type names are also used for quotes, which do not have a defining class.
        if (typeName.getDefiningClass() != null)
        {
            String typeNameStr = "";

            typeNameStr += typeName.getDefiningClass();//  + TYPE_DECL_PACKAGE_SUFFIX + ".";

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


    public String formatEqualsBinaryExp(AEqualsBinaryExpIR node)throws AnalysisException
	{
		return handleEquals(node);
	}

	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpIR node)
			throws AnalysisException
	{
		ANotUnaryExpIR transformed = transNotEquals(node);
		return formatNotUnary(transformed.getExp());
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	public boolean isVoidType(STypeIR node)
	{
		return node instanceof AVoidTypeIR;
	}

	private ANotUnaryExpIR transNotEquals(ANotEqualsBinaryExpIR notEqual)
	{
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
		if (parent != null)
		{
			parent.replaceChild(notEqual, notUnary);
			notEqual.parent(null);
		}

		return notUnary;
	}

	public static boolean isScoped(ABlockStmIR block)
	{
		return block != null && block.getScoped() != null && block.getScoped();
	}


	public String formatVdmSource(PIR irNode)
	{
		if (irNode != null)
		{
			org.overture.ast.node.INode vdmNode = LocationAssistantIR.getVdmNode(irNode);

			if (vdmNode != null)
			{
			}
		}

		return "";
	}


	public static String formatMetaData(List<ClonableString> metaData)
	{
		if (metaData == null || metaData.isEmpty())
		{
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (ClonableString str : metaData)
		{
			sb.append(str.value).append('\n');
		}

		return sb.append('\n').toString();
	}

	private String handleEquals(AEqualsBinaryExpIR valueType)
			throws AnalysisException
	{
		return String.format("%s.equals(%s, %s)", format(valueType.getLeft()), format(valueType.getRight()));
	}

	public String findModifier(Boolean isFinal) throws AnalysisException
	{
		return isFinal ? "val" : "var";
	}

	public String formatTemplateTypes(List<STypeIR> types)
			throws AnalysisException
	{
		return !types.isEmpty() ? "<" + formattedTypes(types, "") + ">" : "";
	}

	private String formattedTypes(List<STypeIR> types, String typePostFix)
			throws AnalysisException
	{
		STypeIR firstType = types.get(0);

		StringWriter writer = new StringWriter();
		writer.append(format(firstType) + typePostFix);

		for (int i = 1; i < types.size(); i++)
		{
			STypeIR currentType = types.get(i);
			writer.append(", " + format(currentType) + typePostFix);
		}

		String result = writer.toString();

		return result;
	}

	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(codeEmitter, writer);

		return writer.toString();
	}

	public String getSlangNumber()
	{
		return "Z";
	}

	public String format(List<AFormalParamLocalParamIR> params)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (params.size() <= 0) {
			return "";
		}

		AFormalParamLocalParamIR firstParam = params.get(0);
		writer.append(format(firstParam));

		for (int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalParamIR param = params.get(i);
			writer.append(", ");
			writer.append(format(param));
		}
		return writer.toString();
	}

    public String formatOperationBody(SStmIR body) throws AnalysisException
    {
        String NEWLINE = "\n";
        if (body == null)
        {
            return "";
        }

        StringWriter generatedBody = new StringWriter();

        generatedBody.append("{" + NEWLINE);
        generatedBody.append(handleOpBody(body));
        generatedBody.append(NEWLINE + "}");

        return generatedBody.toString();
    }

    private String handleOpBody(SStmIR body) throws AnalysisException
    {
        AMethodDeclIR method = body.getAncestor(AMethodDeclIR.class);

        if (method == null)
        {
            log.error("Could not find enclosing method when formatting operation body. Got: "
                    + body);
        }

        return format(body);
    }

	public MergeVisitor getCodeEmitter()
	{
		return codeEmitter;
	}

	public boolean isSeqType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isSeqType(exp);
	}

	public boolean isMapType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isMapType(exp);
	}

	public boolean isStringType(STypeIR type)
	{
		return (type instanceof ASeqSeqTypeIR && type.getSourceNode().getVdmNode().parent() instanceof AStringLiteralExp);
		//return info.getAssistantManager().getTypeAssistant().isStringType(type);
	}

	public boolean isStringType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isStringType(exp);
	}



	public boolean genTypeDecl(ATypeDeclIR node)
	{
		if (node.getDecl() instanceof ARecordDeclIR)
		{
			return getSlangSettings().genRecsAsInnerClasses();
		} else
		{
			return info.getSettings().generateInvariants();
		}
	}

	public String buildString(List<SExpIR> exps) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();

		//sb.append("new String(new char[]{");
		sb.append("\"");
		if (exps.size() > 0)
		{
			for (int i = 0; i < exps.size(); i++)
			{
				sb.append(exps.get(i));
			}
		}

		sb.append("\"");
		//sb.append("})");

		return sb.toString();
	}

	public SlangSettings getSlangSettings()
	{
		return valueSemantics.getSlangSettings();
	}

	public boolean genDecl(ATypeDeclIR node)
	{
		return !(node.getDecl() instanceof ANamedTypeDeclIR);
	}

	/*public String formatIdentifierVar(Object var)
	{
		Integer a = 1;
		return a.toString();
	}*/

	public String formatIdentifierVar(AIdentifierVarExpIR var)
	{
		String varName = "";

		if(!getSlangSettings().genRecsAsInnerClasses() && (var != null && !var.getIsLocal()))
		{
			// Only the VDM-SL-to-JML generator uses this strategy
			ADefaultClassDeclIR enclosingIrClass = var.getAncestor(ADefaultClassDeclIR.class);

			if(enclosingIrClass != null)
			{
				org.overture.ast.node.INode vdmNode = AssistantBase.getVdmNode(enclosingIrClass);

				if(!(vdmNode instanceof AModuleModules))
				{
					// The VDM node is a record (or state definition, which is also translated to a record class)
					// The variable  ('var') must belong to the VDM module enclosing 'vdmNode'

					AModuleModules module = vdmNode.getAncestor(AModuleModules.class);

					if(module != null)
					{
						List<String> fieldNames = new LinkedList<>();

						if(vdmNode instanceof ARecordInvariantType)
						{
							ARecordInvariantType rec = (ARecordInvariantType) vdmNode;
							fieldNames = rec.getFields().stream().map(f -> f.getTagname().getName()).collect(Collectors.toList());
						}
						else if(vdmNode instanceof AStateDefinition)
						{
							AStateDefinition stateDef = (AStateDefinition) vdmNode;
							fieldNames = stateDef.getFields().stream().map(f -> f.getTagname().getName()).collect(Collectors.toList());
						}
						else
						{
							log.error("Expected a record or statedefinition at this point, but got: " + vdmNode);
						}

						if(!fieldNames.contains(var.getName()))
						{
							// It's not one of the record field names

							/*if (JavaCodeGenUtil.isValidJavaPackage(getJavaSettings().getJavaRootPackage()))
							{
								varName += getJavaSettings().getJavaRootPackage() + ".";
							}

							varName += module.getName().getName() + "." + var.getName();

							 */
						}
						else
						{
							// We're accessing a record field, so there's no need to use a fully qualified name
							varName = var.getName();
						}
					}
					else
					{
						log.error("Expected 'vdmNode' to be enclosed by a module");
					}
				}
				else
				{
					// Orginary case, we're inside a module
					varName = var.getName();
				}
			}
		}
		else
		{
			varName = var.getName();
		}

		return varName;
	}
}

package org.overture.codegen.vdm2slang;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;

public class SlangFormat
{
	private static final String SLANG_FORMAT_KEY = "SlangFormat";

	protected SlangValueSemantics valueSemantics;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	private MergeVisitor codeEmitter;
	protected FuncValAssistant funcValAssist;

	public SlangFormat(String root)
	{
		TemplateCallable[] callables = new TemplateCallable[] {
				new TemplateCallable(SLANG_FORMAT_KEY, this) };
		this.codeEmitter = new MergeVisitor(new TemplateManager(root), callables);
		this.funcValAssist = null;
	}


	public MergeVisitor getMergeVisitor()
	{
		return codeEmitter;
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
}

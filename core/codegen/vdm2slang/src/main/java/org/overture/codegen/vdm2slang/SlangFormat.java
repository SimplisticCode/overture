package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.SMapTypeIR;
import org.overture.codegen.ir.types.SSeqTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;

import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;

public class SlangFormat
{
	private static final String SLANG_FORMAT_KEY = "SlangFormat";

	protected Logger log = Logger.getLogger(this.getClass().getName());

	private MergeVisitor codeEmitter;

	public SlangFormat(String root)
	{
		TemplateCallable[] callables = new TemplateCallable[] {
				new TemplateCallable(SLANG_FORMAT_KEY, this) };
		this.codeEmitter = new MergeVisitor(new TemplateManager(root), callables);
	}

	public MergeVisitor getMergeVisitor()
	{
		return codeEmitter;
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

	private String handleEquals(AEqualsBinaryExpIR valueType)
			throws AnalysisException
	{
		return String.format("%s.equals(%s, %s)", format(valueType.getLeft()), format(valueType.getRight()));
	}

	public String findModifier(Boolean isFinal) throws AnalysisException
	{
		if(isFinal) return "val";

		return "var";
	}

	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(codeEmitter, writer);

		return writer.toString();
	}

	public String format(List<AFormalParamLocalParamIR> params)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (params.size() <= 0)
		{
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

        generatedBody.append("{" + NEWLINE + NEWLINE);
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

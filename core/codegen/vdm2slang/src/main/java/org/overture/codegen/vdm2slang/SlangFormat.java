package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;

import java.io.StringWriter;
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

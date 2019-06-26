package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;

import java.io.StringWriter;

public class SlangFormat
{
	private static final String SLANG_FORMAT_KEY = "SlangFormat";

	private MergeVisitor codeEmitter;

	public SlangFormat(String root)
	{
		TemplateCallable[] callables = new TemplateCallable[] {
				new TemplateCallable(SLANG_FORMAT_KEY, this) };
		this.codeEmitter = new MergeVisitor(new TemplateManager(root), callables);
	}

	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(codeEmitter, writer);

		return writer.toString();
	}

	public MergeVisitor getCodeEmitter()
	{
		return codeEmitter;
	}
}

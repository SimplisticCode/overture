package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.node.INode;
import org.overture.codegen.logging.Logger;

public class AbstractVisitorCG<Q, A extends org.overture.codegen.cgast.INode> extends QuestionAnswerAdaptor<Q, A>
{

	private static final long serialVersionUID = -6079145428576751512L;

	@Override
	public A defaultINode(INode node,
			Q question) throws AnalysisException
	{
		
		if(node instanceof AUndefinedExp)
		{
			Logger.getLog().printErrorln("Ignoring undefined expression in " + this.getClass().getName());
			return null;
		}
			
		throw new AnalysisException("Code generation does not support the following VDM construct: " + node.toString());
	}
	
}

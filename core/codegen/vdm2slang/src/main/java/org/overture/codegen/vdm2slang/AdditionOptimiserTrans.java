package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ASubtractNumericBinaryExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class AdditionOptimiserTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR assist;

	public AdditionOptimiserTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override public void caseAPlusNumericBinaryExpIR(
			APlusNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseAPlusNumericBinaryExpIR(node);

		SExpIR left = node.getLeft();
		SExpIR right = node.getRight();
		if (left instanceof AIntLiteralExpIR
				&& right instanceof AIntLiteralExpIR)
		{
			AIntLiteralExpIR leftLit = (AIntLiteralExpIR) left;
			AIntLiteralExpIR rightLit = (AIntLiteralExpIR) right;

			long sum = leftLit.getValue() + rightLit.getValue();

			AIntLiteralExpIR sumNode = new AIntLiteralExpIR();
			sumNode.setType(node.getType().clone());
			sumNode.setValue(sum);

			assist.replaceNodeWith(node, sumNode);
		}
	}

	@Override public void caseASubtractNumericBinaryExpIR(
			ASubtractNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseASubtractNumericBinaryExpIR(node);

		SExpIR left = node.getLeft();
		SExpIR right = node.getRight();
		if (left instanceof AIntLiteralExpIR
				&& right instanceof AIntLiteralExpIR)
		{
			AIntLiteralExpIR leftLit = (AIntLiteralExpIR) left;
			AIntLiteralExpIR rightLit = (AIntLiteralExpIR) right;

			long sum = leftLit.getValue() - rightLit.getValue();

			AIntLiteralExpIR sumNode = new AIntLiteralExpIR();
			sumNode.setType(node.getType().clone());
			sumNode.setValue(sum);

			assist.replaceNodeWith(node, sumNode);
		}
	}

	@Override public void caseADivideNumericBinaryExpIR(
			ADivideNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseADivideNumericBinaryExpIR(node);

		SExpIR left = node.getLeft();
		SExpIR right = node.getRight();
		if (left instanceof AIntLiteralExpIR
				&& right instanceof AIntLiteralExpIR)
		{
			AIntLiteralExpIR leftLit = (AIntLiteralExpIR) left;
			AIntLiteralExpIR rightLit = (AIntLiteralExpIR) right;

			long sum = leftLit.getValue() / rightLit.getValue();

			AIntLiteralExpIR sumNode = new AIntLiteralExpIR();
			sumNode.setType(node.getType().clone());
			sumNode.setValue(sum);

			assist.replaceNodeWith(node, sumNode);
		}
	}

}

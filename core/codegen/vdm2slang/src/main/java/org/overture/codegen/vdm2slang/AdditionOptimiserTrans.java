package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.function.IntBinaryOperator;

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
		IntBinaryOperator plusOperation = (a, b) -> a + b;

		BinaryExpressionHandle(node, plusOperation);
	}



	@Override public void caseASubtractNumericBinaryExpIR(
			ASubtractNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseASubtractNumericBinaryExpIR(node);
		IntBinaryOperator subtractOperation = (a, b) -> a - b;

		BinaryExpressionHandle(node, subtractOperation);
	}

	@Override public void caseADivideNumericBinaryExpIR(
			ADivideNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseADivideNumericBinaryExpIR(node);

		IntBinaryOperator divideOperation = (a, b) -> a / b;

		BinaryExpressionHandle(node, divideOperation);
	}

	@Override public void caseATimesNumericBinaryExpIR(
			ATimesNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseATimesNumericBinaryExpIR(node);

		IntBinaryOperator multiplyOperation = (a, b) -> a * b;

		BinaryExpressionHandle(node, multiplyOperation);
	}

	@Override public void caseAIntDivNumericBinaryExpIR(
			AIntDivNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseAIntDivNumericBinaryExpIR(node);
		IntBinaryOperator intDivOperation = (a, b) -> a / b;
		BinaryExpressionHandle(node, intDivOperation);
	}

	@Override public void caseAModNumericBinaryExpIR(AModNumericBinaryExpIR node) throws AnalysisException
	{
		super.caseAModNumericBinaryExpIR(node);

		IntBinaryOperator modulusOperation = (a, b) -> a % b;
		BinaryExpressionHandle(node, modulusOperation);

	}

	private void BinaryExpressionHandle(SNumericBinaryBase node, IntBinaryOperator op) {
		SExpIR left = node.getLeft();
		SExpIR right = node.getRight();
		if (left instanceof AIntLiteralExpIR
				&& right instanceof AIntLiteralExpIR)
		{
			AIntLiteralExpIR leftLit = (AIntLiteralExpIR) left;
			AIntLiteralExpIR rightLit = (AIntLiteralExpIR) right;

			long sum = op.applyAsInt(leftLit.getValue().intValue(), rightLit.getValue().intValue());

			AIntLiteralExpIR sumNode = new AIntLiteralExpIR();
			sumNode.setType(node.getType().clone());
			sumNode.setValue(sum);

			assist.replaceNodeWith(node, sumNode);
		}
	}

}

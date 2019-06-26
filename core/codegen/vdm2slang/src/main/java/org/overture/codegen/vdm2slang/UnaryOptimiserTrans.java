package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.function.IntBinaryOperator;

public class UnaryOptimiserTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR assist;

	public UnaryOptimiserTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override public void caseAPlusUnaryExpIR(
			APlusUnaryExpIR node) throws AnalysisException
	{
		super.caseAPlusUnaryExpIR(node);
	}

	@Override public void caseAMinusUnaryExpIR(
			AMinusUnaryExpIR node) throws AnalysisException
	{
		super.caseAMinusUnaryExpIR(node);
	}
}

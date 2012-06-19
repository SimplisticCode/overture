package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.SSeqExpAssistantTC;

public class SSeqExpAssistantInterpreter extends SSeqExpAssistantTC
{

	public static ValueList getValues(SSeqExp exp, ObjectContext ctxt)
	{
		switch (exp.kindSSeqExp())
		{
			case SEQCOMP:
				return ASeqCompSeqExpAssistantInterpreter.getValues((ASeqCompSeqExp)exp,ctxt);
			case SEQENUM:
				return ASeqEnumSeqExpAssistantInterpreter.getValues((ASeqEnumSeqExp)exp,ctxt);
			default:
				return new ValueList();
		}
	}

}

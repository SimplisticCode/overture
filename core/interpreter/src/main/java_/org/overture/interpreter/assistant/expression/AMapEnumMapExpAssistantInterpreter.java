package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AMapEnumMapExpAssistantTC;

public class AMapEnumMapExpAssistantInterpreter extends
		AMapEnumMapExpAssistantTC
{

	public static ValueList getValues(AMapEnumMapExp exp, ObjectContext ctxt)
	{
		ValueList list = new ValueList();

		for (AMapletExp maplet: exp.getMembers())
		{
			list.addAll(AMapletExpAssistantInterpreter.getValues(maplet,ctxt));
		}

		return list;
	}

}

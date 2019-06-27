package org.overture.codegen.vdm2slang;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.Generated;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import java.util.Formatter;

public class SlangGenUtil
{
	private static Logger log = Logger.getLogger(SlangGenUtil.class.getName());

	public static Generated generateSlangFromExp(String exp, IRSettings irSettings, Dialect dialect)
			throws AnalysisException
	{
		SlangGen vdmCodeGen = new SlangGen();
		vdmCodeGen.setSettings(irSettings);

		return generateSlangFromExp(exp, vdmCodeGen, dialect);
	}

	public static Generated generateSlangFromExp(String exp,
			SlangGen vdmCodeGen, Dialect dialect) throws AnalysisException

	{
		Settings.dialect = dialect;
		TypeCheckResult<PExp> typeCheckResult = GeneralCodeGenUtils.validateExp(exp);

		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp);
		}

		try
		{
			return vdmCodeGen.generateSlangFromVdmExp(typeCheckResult.result);

		} catch (AnalysisException
				| org.overture.codegen.ir.analysis.AnalysisException e)
		{
			throw new AnalysisException("Unable to generate code from expression: "
					+ exp + ". Exception message: " + e.getMessage());
		}
	}

//	public static String formatSlangCode(String code)
//	{
//		try
//		{
//			return new Formatter().formatSource(code);
//		} catch (FormatterException e)
//		{
//			log.error("Could not format code: " + e.getMessage());
//			e.printStackTrace();
//			return code;
//		}
//	}


	public static boolean isSlangKeyword(String s)
	{
		if (s == null)
		{
			return false;
		} else
		{
			s = s.trim();

			if (s.isEmpty())
			{
				return false;
			}
		}

//		for (String kw : ISlangConstants.RESERVED_WORDS)
//		{
//			if (s.equals(kw))
//			{
//				return true;
//			}
//		}

		return false;
	}

}

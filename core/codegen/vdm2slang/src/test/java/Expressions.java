import junit.framework.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2slang.SlangGen;
import org.overture.codegen.vdm2slang.SlangGenUtil;
import org.overture.config.Release;
import org.overture.config.Settings;

import java.io.File;
import java.util.List;

public class Expressions
{

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	@Test public void or() throws Exception
	{
		File file = new File("src/test/resources/Expressions/OrOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "true | false";
		validateCode(expectedCode, slangCode);
	}

	@Test public void and() throws Exception
	{
		File file = new File("src/test/resources/Expressions/AndOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "true & false";
		validateCode(expectedCode, slangCode);
	}

	@Test public void equals() throws Exception
	{
		File file = new File("src/test/resources/Expressions/EqualsNumeric.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "1 == 2";
		validateCode(expectedCode, slangCode);
	}

	@Test public void notEquals() throws Exception
	{
		File file = new File("src/test/resources/Expressions/NotEqualsNumeric.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "1 != 2";
		validateCode(expectedCode, slangCode);
	}


	@Test public void not() throws Exception
	{
		File file = new File("src/test/resources/Expressions/NotOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "!true";
		validateCode(expectedCode, slangCode);
	}

	@Test public void InverseOfProduct() throws Exception
	{
		File file = new File("src/test/resources/Expressions/InverseOfProduct.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Utils.divide((1.0 * 1), 1 * 2 * 3)";
		validateCode(expectedCode, slangCode);
	}


	@Test public void InverseOfSum() throws Exception
	{
		File file = new File("src/test/resources/Expressions/InverseOfSum.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Utils.divide((1.0 * 1), 1 + 1 + 1)";
		validateCode(expectedCode, slangCode);
	}


	@Test public void SetCard() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetCardNatSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)).size";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetDifference() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetDifferenceNatSets.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.SetDifference(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(2, 3)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetInSet() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetInSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)).contains(4)";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetNotInSet() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetNotInSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "!(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)).contains(4))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetEqual() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetEqual.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "!true";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SubSet() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetSubsetNatSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.Subset(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(1, 2, 3, 4)))";
		validateCode(expectedCode, slangCode);
	}


	@Test public void SeqConc() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqConcatenationExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "ISZ(1, 2, 3) ++ ISZ(4, 5, 6)";
		validateCode(expectedCode, slangCode);
	}



	private void validateCode(String expectedCode, String actualCode)
	{
		Assert.assertEquals("Got unexpected code", expectedCode, actualCode);
	}

	private String generateSlangCode(File file)	throws Exception
	{
		String fileContent = GeneralUtils.readFromFile(file);
		SlangGen codeGen = new SlangGen();

		String generatedJava = SlangGenUtil.generateSlangFromExp(fileContent, codeGen, Settings.dialect).getContent().trim();
		String trimmed = GeneralUtils.cleanupWhiteSpaces(generatedJava);
		return trimmed;
	}

	private void assertSingleClass(List<GeneratedModule> classes)
	{
		Assert.assertEquals("Expected one class to be generated", 1, classes.size());
	}
}

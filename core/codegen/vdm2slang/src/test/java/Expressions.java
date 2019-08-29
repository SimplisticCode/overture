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

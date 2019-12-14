import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import testUitl.TestUtils;


public class Slang_Specifications
{
	TestUtils testUtils = new TestUtils();

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Test public void RecordCreation() throws Exception
	{
		testUtils.RunTest("Specification/RecordCreation.vdmpp", "Specification/ResultFiles/RecordCreation.scala");
	}

	@Test public void RecordDecls() throws Exception
	{
		testUtils.RunTest("Specification/RecordDecls.vdmpp", "Specification/ResultFiles/RecordDecl.scala");
	}

	@Test public void RecordNesting() throws Exception
	{
		testUtils.RunTest("Specification/RecordNesting.vdmpp", "Specification/ResultFiles/RecordNesting.scala");
	}


	@Test public void RecordUsage() throws Exception
	{
		testUtils.RunTest("Specification/RecordUsage.vdmpp", "Specification/ResultFiles/RecordUsage.scala");
	}

	/*@Test public void ValueRefInAcrossClass() throws Exception
	{
		File file = new File("src/test/resources/Specification/ValueRefInAcrossClass.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		//assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}*/

	@Test public void VariableExpressions() throws Exception
	{
		testUtils.RunTest("Specification/VariableExpressions.vdmpp", "Specification/ResultFiles/VariableExpressions.scala");
	}

}

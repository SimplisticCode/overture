import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import testUitl.TestUtils;

public class SlangGen_classes {

    TestUtils testUtils = new TestUtils();

    @BeforeAll
    public static void initTesting() {
        Settings.dialect = Dialect.VDM_PP;
        Settings.release = Release.VDM_10;
    }


    @Test
    public void emptyClass() throws Exception {
        testUtils.RunTest("Classes/EmptyClass.vdmpp", "Classes/ResultFiles/EmptyClass.scala");
    }


    @Test
    public void classSingleField() throws Exception {
        testUtils.RunTest("Classes/ClassSingleValue.vdmpp", "Classes/ResultFiles/ClassSingleValue.scala");
    }

    @Test
    public void classMultipleFields() throws Exception {
        testUtils.RunTest("Classes/ClassMultipleFields.vdmpp", "Classes/ResultFiles/ClassMultipleFields.scala");
    }

    @Test
    public void AssignmentDefinitionReturn() throws Exception {
        testUtils.RunTest("Classes/AssignmentDefinitionReturn.vdmpp", "Classes/ResultFiles/AssignmentDefinitionReturn.scala");
    }

    @Test
    public void classSingleBooleanField() throws Exception {
        testUtils.RunTest("Classes/ClassBooleanSingleValue.vdmpp", "Classes/ResultFiles/ClassBooleanSingleValue.scala");
    }

    @Test
    public void classIdentityFunction() throws Exception {
        testUtils.RunTest("Classes/ClassIdentityFunction.vdmpp", "Classes/ResultFiles/ClassIdentityFunction.scala");
    }

    @Test
    public void alarm() throws Exception {
        testUtils.RunTest("Classes/alarm.vdmpp", "Classes/ResultFiles/alarm.scala");
    }

    @Test
    public void buffers() throws Exception {
        testUtils.RunTest("Classes/buffers.vdmpp", "Classes/ResultFiles/buffers.scala");
    }
	/*


	@Test public void worldcup() throws AnalysisException
	{
		File file = new File("src/test/resources/Classes/worldcup.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}
	 */


}

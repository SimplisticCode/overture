import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import testUitl.TestUtils;

public class SlangGen_Quantifiers {

    TestUtils testUtils = new TestUtils();

    @BeforeAll
    public static void initTesting() {
        Settings.dialect = Dialect.VDM_PP;
        Settings.release = Release.VDM_10;
    }


    @Test
    public void ForAll() throws Exception {
        testUtils.RunTest("Quantifiers/ForAllExpInIfExp.vdmpp", "Quantifiers/ResultFiles/ForAllExpInIfExp.scala");
    }


    @Test
    public void ForallBlock() throws Exception {
        testUtils.RunTest("Quantifiers/ForAllExpBlockStm.vdmpp", "Quantifiers/ResultFiles/ForAllExpBlockStm.scala");
    }

    @Test
    public void ExistsExpReturned() throws Exception {
        testUtils.RunTest("Quantifiers/ExistsExpReturned.vdmpp", "Quantifiers/ResultFiles/ExistsExpReturned.scala");
    }


    @Test
    public void ExistsRemoveAllMultipleSetBinds() throws Exception {
        testUtils.RunTest("Quantifiers/ExistsRemoveAllMultipleSetBinds.vdmpp", "Quantifiers/ResultFiles/ExistsRemoveAllMultipleSetBinds.scala");
    }

    @Test
    public void ExistsExpSeveralMultipleSetBindsCond1() throws Exception {
        testUtils.RunTest("Quantifiers/ExistsExpSeveralMultipleSetBindsCond1.vdmpp", "Quantifiers/ResultFiles/ExistsExpSeveralMultipleSetBindsCond1.scala");
    }



    @Test
    public void Exists() throws Exception {
        testUtils.RunTest("Quantifiers/ExistsSeqBind.vdmpp", "Quantifiers/ResultFiles/ExistsSeqBind.scala");
    }

    @Test
    public void NestedQuantifiers() throws Exception {
        testUtils.RunTest("Quantifiers/NestExistsForall.vdmpp", "Quantifiers/ResultFiles/NestExistsForall.scala");
    }

    @Test
    public void ExistExpTransformedCond() throws Exception {
        testUtils.RunTest("Quantifiers/ExistExpTransformedCond.vdmpp", "Quantifiers/ResultFiles/ExistExpTransformedCond.scala");
    }
/*
	@Test public void ifExp() throws AnalysisException
	{
		File file = new File("src/test/resources/Conditionals/IfExp.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		String expectedCode = "def absfunct(x : Z):Z =\nif(x < 0) -x else x\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}*/

}

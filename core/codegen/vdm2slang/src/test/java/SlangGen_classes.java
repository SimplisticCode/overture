import junit.framework.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.codegen.vdm2slang.SlangGen;

import java.io.File;
import java.util.List;

public class SlangGen_classes
{

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}


	@Test public void emptyClass() throws AnalysisException
	{
		File file = new File("src/test/resources/Classes/EmptyClass.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	private void validateCode(String expectedCode, String actualCode)
	{
		Assert.assertEquals("Got unexpected code", expectedCode, actualCode);
	}

	@Test public void classSingleField() throws AnalysisException
	{
		File file = new File("src/test/resources/Classes/ClassSingleValue.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\nval x : Z = 5\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void classMultipleFields() throws AnalysisException
	{
		File file = new File("src/test/resources/Classes/ClassMultipleFields.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\nval x : Z = 5\nval y : B = true\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void AssignmentDefinitionReturn() throws AnalysisException
{
	File file = new File("src/test/resources/Classes/AssignmentDefinitionReturn.vdmpp");

	List<GeneratedModule> classes = generateModules(file);

	assertSingleClass(classes);
	String expectedCode = "import org.sireum._\n\nclass A:\nval x : B = true\n\ndef A():A\n{\n\t\t\n}\n\n";
	String actualCode = classes.get(0).getContent();
	validateCode(expectedCode, actualCode);
}

	@Test public void classSingleBooleanField() throws AnalysisException
	{
		File file = new File("src/test/resources/Classes/ClassBooleanSingleValue.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\nval x : B = true\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void classIdentityFunction() throws AnalysisException
	{
		File file = new File("src/test/resources/Classes/ClassIdentityFunction.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void alarm() throws AnalysisException
	{
		File file = new File("src/test/resources/Classes/alarm.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	private List<GeneratedModule> generateModules(File file)
			throws AnalysisException
	{
		TypeCheckerUtil.TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckPp(file);

		Assert.assertTrue("Expected no parse errors", tcResult.parserResult.errors.isEmpty());
		Assert.assertTrue("Expected no type errors", tcResult.errors.isEmpty());

		SlangGen codeGen = new SlangGen();

		List<INode> nodes = CodeGenBase.getNodes(tcResult.result);
		GeneratedData data = codeGen.generate(nodes);

		return data.getClasses();
	}

	private void assertSingleClass(List<GeneratedModule> classes)
	{
		Assert.assertEquals("Expected one class to be generated", 1, classes.size());
	}
}

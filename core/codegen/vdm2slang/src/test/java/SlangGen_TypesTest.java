import junit.framework.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2slang.SlangGen;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;

import java.io.File;
import java.util.List;

public class SlangGen_TypesTest
{

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}


	@Test public void booleanType() throws AnalysisException
	{
		File file = new File("src/test/resources/Boolean.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		String expectedCode = "class A:\n  val i_am_boolean : B = true\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}



	@Test public void integerType() throws AnalysisException
	{
		File file = new File("src/test/resources/ClassSingleValue.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		String expectedCode = "class A:\n  val x : Z = 5\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void additionOptimisation() throws AnalysisException
	{
		File file = new File("src/test/resources/AdditionOptimisation.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "class A:\n  val x : Z = 42\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	private void validateCode(String expectedCode, String actualCode)
	{
		Assert.assertEquals("Got unexpected code", expectedCode, actualCode);
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

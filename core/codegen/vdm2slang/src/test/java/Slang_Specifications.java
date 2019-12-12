import junit.framework.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2slang.SlangGen;
import org.overture.codegen.vdm2slang.SlangGenUtil;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;

import java.io.File;
import java.util.List;

public class Slang_Specifications
{

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Test public void RecordCreation() throws Exception
	{
		File file = new File("src/test/resources/Specification/RecordCreation.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void RecordDecls() throws Exception
	{
		File file = new File("src/test/resources/Specification/RecordDecls.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void RecordNesting() throws Exception
	{
		File file = new File("src/test/resources/Specification/RecordNesting.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}


	@Test public void RecordUsage() throws Exception
	{
		File file = new File("src/test/resources/Specification/RecordUsage.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = "import org.sireum._\n\nclass A:\ndef f(x:Z):Z = return x\n\ndef A():A\n{\n\t\t\n}\n\n";
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

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
import org.overture.codegen.vdm2slang.SlangGen;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Slang_StmTest
{

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Test public void MapCompBlockStm() throws Exception
	{
		Test("src/test/resources/Statements/MapCompBlockStm.vdmpp", "src/test/resources/Statements/ResultFiles/MapCompBlockStm.scala");
	}

	@Test public void MapCompInLet() throws Exception
	{
		Test("src/test/resources/Statements/MapCompInLet.vdmpp", "src/test/resources/Statements/ResultFiles/MapCompInLet.scala");
	}


	@Test public void MapCompInOpCallInLoop() throws Exception
	{
		Test("src/test/resources/Statements/MapCompInOpCallInLoop.vdmpp", "src/test/resources/Statements/ResultFiles/MapCompInOpCallInLoop.scala");
	}


	private void Test(String testFile, String resultFile) throws Exception {
		File file = new File( testFile);

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		//TODO could I remove all spacing and test for equality
		String expectedCode = readFileAsString(resultFile);
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	private void validateCode(String expectedCode, String actualCode)
	{
		Assert.assertEquals("Got unexpected code", expectedCode, actualCode);
	}

	public static String readFileAsString(String fileName)throws Exception
	{
		String filePath = new File("").getAbsolutePath();
		filePath = filePath.concat("/" + fileName);
		String data = new String(Files.readAllBytes(Paths.get(filePath)));
		return data;
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

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

public class SlangGen_Quantifiers
{

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}


	@Test public void ForAll() throws Exception
	{
		File file = new File("src/test/resources/Quantifiers/ForAllExpInIfExp.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = readFileAsString("src/test/resources/ResultFiles/ForAllExpInIfExp.scala");
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}



	@Test public void ForallBlock() throws Exception
	{
		File file = new File("src/test/resources/Quantifiers/ForAllExpBlockStm.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = readFileAsString("src/test/resources/ResultFiles/ForAllExpBlockStm.scala");
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void Exists() throws Exception
	{
		File file = new File("src/test/resources/Quantifiers/ExistsSeqBind.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = readFileAsString("src/test/resources/ResultFiles/ForAllExpBlockStm.scala");
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void NestedQuantifiers() throws Exception
	{
		File file = new File("src/test/resources/Quantifiers/NestExistsForall.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = readFileAsString("src/test/resources/ResultFiles/NextExistsForall.scala");
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	@Test public void NestedQuantifiers1() throws Exception
	{
		File file = new File("src/test/resources/Quantifiers/ExistExpTransformedCond.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		assertSingleClass(classes);

		String expectedCode = readFileAsString("src/test/resources/ResultFiles/ForAllExpBlockStm.scala");
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}




	private void validateCode(String expectedCode, String actualCode)
	{
		Assert.assertEquals("Got unexpected code", expectedCode.trim().replaceAll("\n ", ""), actualCode.trim().replaceAll("\n ", ""));
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

	@Test public void ifExp() throws AnalysisException
	{
		File file = new File("src/test/resources/Conditionals/IfExp.vdmpp");

		List<GeneratedModule> classes = generateModules(file);

		String expectedCode = "def absfunct(x : Z):Z =\nif(x < 0) -x else x\n";
		String actualCode = classes.get(0).getContent();
		validateCode(expectedCode, actualCode);
	}

	public static String readFileAsString(String fileName)throws Exception
	{
		String filePath = new File("").getAbsolutePath();
		filePath = filePath.concat("/" + fileName);
		String data = new String(Files.readAllBytes(Paths.get(filePath)));
		return data;
	}

	private void assertSingleClass(List<GeneratedModule> classes)
	{
		Assert.assertEquals("Expected one class to be generated", 1, classes.size());
	}
}

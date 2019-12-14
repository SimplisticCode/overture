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
import testUitl.TestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Slang_StmTest
{
    TestUtils testUtils = new TestUtils();

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Test public void MapCompBlockStm() throws Exception
	{
		testUtils.RunTest("Statements/MapCompBlockStm.vdmpp", "Statements/ResultFiles/MapCompBlockStm.scala");
	}

	@Test public void MapCompInLet() throws Exception
	{
		testUtils.RunTest("Statements/MapCompInLet.vdmpp", "Statements/ResultFiles/MapCompInLet.scala");
	}

	@Test public void MapCompInOpCallInLoop() throws Exception
	{
		testUtils.RunTest("Statements/MapCompInOpCallInLoop.vdmpp", "Statements/ResultFiles/MapCompInOpCallInLoop.scala");
	}

	@Test public void BlockStmInitialized() throws Exception
	{
		testUtils.RunTest("Statements/BlockStmInitialized.vdmpp", "Statements/ResultFiles/BlockStmInitialized.scala");
	}


}

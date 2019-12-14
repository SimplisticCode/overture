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
import testUitl.TestUtils;

import java.io.File;
import java.util.List;

public class SlangGen_Loops
{

	TestUtils testUtils = new TestUtils();

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Test public void whileLoop() throws Exception
	{
		testUtils.RunTest("Loops/whileLoop.vdmpp", "Loops/ResultFiles/whileLoop.scala");
	}

	@Test public void forAll1() throws Exception
	{
		testUtils.RunTest("Loops/ForAllExpBlockStm.vdmpp", "Loops/ResultFiles/ForAllExpBlockStm.scala");
	}


	@Test public void forAll2() throws Exception
	{
		testUtils.RunTest("Loops/ForAllExpInIfExp.vdmpp", "Loops/ResultFiles/ForAllExpInIfExp.scala");
	}

	@Test public void ForIndexCorner() throws Exception
	{
		testUtils.RunTest("Loops/ForIndexCorner.vdmpp", "Loops/ResultFiles/ForIndexCorner.scala");
	}

	@Test public void ForIndexStm() throws Exception
	{
		testUtils.RunTest("Loops/ForIndexStm.vdmpp", "Loops/ResultFiles/ForIndexStm.scala");
	}

	@Test public void ForIndexLoopVar() throws Exception
	{
		testUtils.RunTest("Loops/ForIndexLoopVar.vdmpp", "Loops/ResultFiles/ForIndexLoopVar.scala");
	}

	@Test public void WhileStmForAllExpCond() throws Exception
	{
		testUtils.RunTest("Loops/WhileStmForAllExpCond.vdmpp", "Loops/ResultFiles/WhileStmForAllExpCond.scala");
	}

	@Test public void ForAllSeqLoop() throws Exception
	{
		testUtils.RunTest("Loops/ForAllSeqLoop.vdmpp", "Loops/ResultFiles/ForAllSeqLoop.scala");
	}

	@Test public void WhileStmSetSum() throws Exception
	{
		testUtils.RunTest("Loops/WhileStmSetSum.vdmpp", "Loops/ResultFiles/WhileStmSetSum.scala");
	}
}

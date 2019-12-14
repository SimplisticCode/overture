import junit.framework.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.analysis.AnalysisException;
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

public class SlangGen_PreCon
{

	TestUtils testUtils = new TestUtils();

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}


	@Test public void SimplePre() throws Exception
	{
		testUtils.RunTestSl("Pre/simplePrePost.vdmpp", "Pre/ResultFiles/simplePrePost.scala");
	}

	@Test public void CompoundPost() throws Exception
	{
		testUtils.RunTestSl("Pre/simpleCompoundPrePost.vdmpp", "Pre/ResultFiles/simpleCompoundPrePost.scala");
	}


	@Test public void explicitFunction() throws Exception
	{
		testUtils.RunTestSl("Pre/explicit.vdmpp", "Pre/ResultFiles/explicit.scala");
	}

	@Test public void ImplicitFunc() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		testUtils.RunTest("Pre/ImplicitFunc.vdmpp", "Pre/ResultFiles/ImplicitFunc.scala");
	}

	@Test public void ImplicitOp() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		testUtils.RunTest("Pre/ImplicitOp.vdmpp", "Pre/ResultFiles/ImplicitOp.scala");
	}


	@Test public void PrePassFuncSimple1() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		testUtils.RunTest("Pre/PrePassFuncSimple1.vdmpp", "Pre/ResultFiles/PrePassFuncSimple1.scala");
	}

	@Test public void PrePassNonStaticOp() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		testUtils.RunTest("Pre/PrePassNonStaticOp.vdmpp", "Pre/ResultFiles/PrePassNonStaticOp.scala");
	}


	@Test public void PrePassOpTuplePattern() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		testUtils.RunTest("Pre/PrePassOpTuplePattern.vdmpp", "Pre/ResultFiles/PrePassOpTuplePattern.scala");
	}


}

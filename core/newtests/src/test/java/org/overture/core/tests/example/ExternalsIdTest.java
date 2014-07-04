package org.overture.core.tests.example;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.node.INode;
import org.overture.core.tests.ExternalsTest;

/**
 * A very simple alternate version of {@link IdTest} to work with external tests. We cannot directly reuse the
 * {@link IdTest} since we must inherit {@link ExternalsTest}. But since we factor most of the important code out to
 * {@link IdTestResult}, this class is actually very small.<br>
 * <br>
 * Also note that since this test works with external inputs, the data provider is already set up in
 * {@link ExternalsTest}. To launch these tests simply use the property <code>-DexternalTestsPath=/path/to/files/</code>
 * .<br>
 * <br>
 * If the property is not set, the tests will not be executed under Maven. In Eclipse, they will also not execute but
 * the test entry will show up (with no success/failure indication) and count as one test. We're working on a way to fix
 * this.
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)
public class ExternalsIdTest extends ExternalsTest<IdTestResult>
{

	// the update property for this test
	private static final String UPDATE_PROPERTY = "tests.update.example.ExternalID";

	/**
	 * As usual in the new tests, the constructor only needs to pass the parameters up to super.
	 * 
	 * @param nameParameter
	 * @param testParameter
	 * @param resultParameter
	 */
	public ExternalsIdTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	/**
	 * Main comparison method. Simply call on {@link IdTestResult}.
	 */
	@Override
	public void testCompare(IdTestResult actual, IdTestResult expected)
	{
		IdTestResult.compare(actual, expected, testName);
	}

	/**
	 * Main model processing. Does nothing to the model and then converts it via {@link IdTestResult}.
	 */
	@Override
	public IdTestResult processModel(List<INode> ast)
	{
		IdTestResult actual = IdTestResult.convert(ast);
		return actual;
	}

	/**
	 * Return the update property for this test. In general, it's good practice to do put it in a constant and return
	 * that.
	 */
	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

}

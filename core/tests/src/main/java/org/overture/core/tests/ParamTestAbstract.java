package org.overture.core.tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Main parameterized test class for the new tests. Runs tests on VDM source
 * models and compares them with stored result files. This class is meant to be
 * subclassed as a way to quickly create your own tests suites.<br>
 * <br>
 * A comparison method for results must be provided. Serialization of results is
 * as automated as possible but can be overridden if your results classes are to
 * complex for auto. <br>
 * <br>
 * These tests are meant to be used as parameterized JUnit test and so any
 * subclass of it must be annotated with
 * <code>@RunWith(Parameterized.class)</code>. <br>
 * <br>
 * This class is parameterized on a type <code>R</code> that represents the
 * output of of the analysis of the functionality under test. These types must
 * implement {@link Serializable} and you will most likely need to write some
 * kind of transformation between your native output type and <code>R</code>.
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)
public abstract class ParamTestAbstract<R extends Serializable> {

	protected String modelPath;
	protected String resultPath;
	protected String testName;

	/**
	 * Constructor for the test. Works with outputs gotten from
	 * {@link PathsProvider} which must to supplied via a static method.
	 * Subclasses must implement reate this method.<br>
	 * <br>
	 * In order to use JUnit parameterized tests, you must annotate the
	 * data-supplying method with <b>
	 * <code>@Parameters(name = "{index} : {0}")</code></b>. Supporting
	 * parameterized tests is also the reason the method must be static.
	 * 
	 * @param testParameter
	 *            filename for the VDM source to test
	 * @param resultParameter
	 *            test result file
	 */
	public ParamTestAbstract(String nameParameter, String testParameter,
			String resultParameter) {
		this.testName = testParameter;
		this.modelPath = testParameter;
		this.resultPath = resultParameter;
	}

	/**
	 * The main test executor. Constructs ASTs and processes them via
	 * {@link #processModel(List)} and Results via
	 * {@link #deSerializeResult(String)}. It then compares the two according to
	 * {@link #testCompare(Object, IResult)}.
	 * 
	 * @param <R>
	 *            a result type produced by the analyzed plug-in. You may need
	 *            to create one.
	 * 
	 * @throws IOException
	 * @throws LexException
	 * @throws ParserException
	 */
	@Test
	public void testCase() throws ParserException, LexException, IOException {
		List<INode> ast = InputProcessor.typedAst(modelPath);
		R actual = processModel(ast);
		R expected = deSerializeResult(resultPath);
		this.testCompare(actual, expected);
	}

	/**
	 * Compares output of the processed model with a previous. This method must
	 * be overridden to implement result comparison behavior. Don't forget to
	 * assert something.
	 * 
	 * @param actual
	 *            the processed model
	 * @param expected
	 *            the stored result
	 */
	public abstract void testCompare(R actual, R expected);

	/**
	 * Analyses a model (represented by its AST). This method must be overridden
	 * to perform whatever analysis the functionality under test performs.<br>
	 * <br>
	 * The output of this method must be of type <code>R</code> that implements
	 * {@link IResult}.
	 * 
	 * @param ast
	 *            representing the model to process
	 * @return the output of the analysis
	 */
	public abstract R processModel(List<INode> ast);

	/**
	 * This method tries its best to deserialize any results file. If your
	 * results are too complex for it to handle, you should override
	 * {@link #getResultType()} to deal with it. If that fails, override this
	 * entire.
	 * 
	 * @param resultPath
	 * @return the stored result
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public R deSerializeResult(String resultPath) throws FileNotFoundException,
			IOException {
		Gson gson = new Gson();
		Type resultType = getResultType();
		String json = IOUtils.toString(new FileReader(resultPath));
		R results = gson.fromJson(json, resultType);
		return results;
	}

	/**
	 * Calculates the type of the result. This method does its best but doesn't
	 * always succeed. Override it if necessary.
	 * 
	 * @return the {@link Type} of the result file
	 */
	public Type getResultType() {
		Type resultType = new TypeToken<R>() {
		}.getType();
		return resultType;
	}

}

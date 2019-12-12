import junit.framework.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2slang.SlangGen;
import org.overture.codegen.vdm2slang.SlangGenUtil;
import org.overture.config.Release;
import org.overture.config.Settings;

import java.io.File;
import java.util.List;

public class Expressions
{

	@BeforeAll public static void initTesting()
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	@Test public void or() throws Exception
	{
		File file = new File("src/test/resources/Expressions/OrOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "true | false";
		validateCode(expectedCode, slangCode);
	}

	@Test public void and() throws Exception
	{
		File file = new File("src/test/resources/Expressions/AndOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "true & false";
		validateCode(expectedCode, slangCode);
	}

	@Test public void equals() throws Exception
	{
		File file = new File("src/test/resources/Expressions/EqualsNumeric.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "1 == 2";
		validateCode(expectedCode, slangCode);
	}


	@Test public void power() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Power.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Math.pow(2.1, 4.1)";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetProperSubsetNatSets() throws Exception
	{
		File file = new File("src/test/resources/Expressions/SetProperSubsetNatSets.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.PSubset(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(1, 2, 3, 4)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void UnaryMinusBinaryExp() throws Exception
	{
		File file = new File("src/test/resources/Expressions/UnaryMinusBinaryExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "-(1 + 2)";
		validateCode(expectedCode, slangCode);
	}


	@Test public void UnaryPlusBinaryExp() throws Exception
	{
		File file = new File("src/test/resources/Expressions/UnaryPlusOfBinaryExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "2 * +(3 + 1)";
		validateCode(expectedCode, slangCode);
	}



	@Test public void notEquals() throws Exception
	{
		File file = new File("src/test/resources/Expressions/NotEqualsNumeric.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "1 != 2";
		validateCode(expectedCode, slangCode);
	}


	@Test public void not() throws Exception
	{
		File file = new File("src/test/resources/Expressions/NotOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "!true";
		validateCode(expectedCode, slangCode);
	}

	@Test public void InverseOfProduct() throws Exception
	{
		File file = new File("src/test/resources/Expressions/InverseOfProduct.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Utils.divide((1.0 * 1), 1 * 2 * 3)";
		validateCode(expectedCode, slangCode);
	}


	@Test public void InverseOfSum() throws Exception
	{
		File file = new File("src/test/resources/Expressions/InverseOfSum.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Utils.divide((1.0 * 1), 1 + 1 + 1)";
		validateCode(expectedCode, slangCode);
	}

	@Test public void TupleEquals() throws Exception
	{
		File file = new File("src/test/resources/Expressions/TupleEquals.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "(1, true).equals((2, false))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void Implication() throws Exception
	{
		File file = new File("src/test/resources/Expressions/ImplicationOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		//TODO translation could be different
		String expectedCode = "!true | false";
		validateCode(expectedCode, slangCode);
	}

	@Test public void CharLitSingleQuote() throws Exception
	{
		File file = new File("src/test/resources/Expressions/CharLitSingleQuote.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "'''";
		validateCode(expectedCode, slangCode);
	}

	@Test public void ElemsSeqofNat1() throws Exception
	{
		File file = new File("src/test/resources/Expressions/ElemsSeqofNat1.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SeqUtil.Elems(ISZ(1, 2, 3))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void ElemsSeqOfSeqOfNat1() throws Exception
	{
		File file = new File("src/test/resources/Expressions/ElemsSeqOfSeqOfNat1.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SeqUtil.Elems(ISZ(ISZ(1, 2, 3)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void FloorExpression() throws Exception
	{
		File file = new File("src/test/resources/Expressions/FloorExpression.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Utils.floor(-1.5)";
		validateCode(expectedCode, slangCode);
	}

	@Test public void FieldNumberExp() throws Exception
	{
		File file = new File("src/test/resources/Expressions/FieldNumberExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "!true | false";
		validateCode(expectedCode, slangCode);
	}

	@Test public void AbsInTuple() throws Exception
	{
		File file = new File("src/test/resources/Expressions/AbsInTuple.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "(Utils.abs(-2), Utils.abs(-2.5))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void AbsOperator() throws Exception
	{
		File file = new File("src/test/resources/Expressions/AbsOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Utils.abs(-2.1)";
		validateCode(expectedCode, slangCode);
	}


	@Test public void ModOperator() throws Exception
	{
		File file = new File("src/test/resources/Expressions/ModOperator.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "7 % 2";
		validateCode(expectedCode, slangCode);
	}


	@Test public void SetCard() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetCardNatSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)).size";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetDifference() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetDifferenceNatSets.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.SetDifference(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(2, 3)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetInSet() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetInSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)).contains(4)";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetNotInSet() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetNotInSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "!(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)).contains(4))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetEqual() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetEqual.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "!true";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetNotEqual() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetNotEqual.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)) != SetUtil.CreateSetFromSeq(ISZ(1, 2, 3))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SubSet() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetSubsetNatSet.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.Subset(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(1, 2, 3, 4)))";
		validateCode(expectedCode, slangCode);
	}


	@Test public void SetIntersect() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetIntersectNatSets.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.SetIntersect(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(4, 5, 6)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetUnion() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Set/SetUnionNatSets.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.SetUnion(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetRangeRealBounds() throws Exception
	{
		File file = new File("src/test/resources/Expressions/SetRangeRealBounds.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.SetUnion(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SetRangeOneToTen() throws Exception
	{
		File file = new File("src/test/resources/Expressions/SetRangeOneToTen.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SetUtil.SetUnion(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)), SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SeqConc() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqConcatenationExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "ISZ(1, 2, 3) ++ ISZ(4, 5, 6)";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SeqIndexExp() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqIndexExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "ISZ(1, 2, 3)(1)";
		validateCode(expectedCode, slangCode);
	}


	@Test public void SeqHeadExp() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqHdExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "ISZOps(ISZ(1, 2, 3)).first";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SeqLen() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqLenExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "ISZOps(ISZ(1, 2, 3)).first";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SeqReverseExp() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqReverseExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "ISZOps(ISZ(1, 2, 3)).reverse";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SeqTlExp() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqTlExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SeqUtil.tail(ISZ(1, 2, 3))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SeqInds() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqIndsExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SeqUtil.Inds(ISZ(1, 2, 3))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void SeqElems() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/SeqElemsExp.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "SeqUtil.Elems(ISZ(1, 2, 3))";
		validateCode(expectedCode, slangCode);
	}


	@Test public void StringEqual() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/StringEqual.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "ISZOps(ISZ(1, 2, 3)).first";
		validateCode(expectedCode, slangCode);
	}

	@Test public void StringNotEqual() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Sequences/StringNotEqual.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "\"meow\" != \"cat\"";
		validateCode(expectedCode, slangCode);
	}

	@Test public void MapEnumExpNats() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Maps/MapEnumExpNats.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "Map.empty ++ ISZ((1, 2), (3, 4))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void MapMergeEmptyMaps() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Maps/MapMergeEmptyMaps.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "MapUtil.Merge(Map.empty ++ ISZ((1, 2), (3, 4)))";
		validateCode(expectedCode, slangCode);
	}


	@Test public void MapRangeNatToNatMap() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Maps/MapRangeNatToNatMap.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "MapUtil.Range(Map.empty ++ ISZ((1, 2), (3, 4)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void MapRangeEmptyMap() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Maps/MapRangeEmptyMap.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "MapUtil.Range(Map.empty ++ ISZ())";
		validateCode(expectedCode, slangCode);
	}

	@Test public void MapInverseNatToNatMap() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Maps/MapInverseNatToNatMap.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "MapUtil.Inverse(Map.empty ++ ISZ((1, 2), (3, 4)))";
		validateCode(expectedCode, slangCode);
	}

	@Test public void MapUnionEmptyMaps() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Maps/MapUnionEmptyMaps.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "MapUtil.MUnion(Map.empty ++ ISZ(), Map.empty ++ ISZ())";
		validateCode(expectedCode, slangCode);
	}


	@Test public void MapUnionNatToNatMaps() throws Exception
	{
		File file = new File("src/test/resources/Expressions/Maps/MapUnionNatToNatMaps.vdmpp");

		String slangCode = generateSlangCode(file);

		String expectedCode = "MapUtil.MUnion(Map.empty ++ ISZ((1, 2), (3, 4)), Map.empty ++ ISZ((5, 6), (7, 8)))";
		validateCode(expectedCode, slangCode);
	}






	private void validateCode(String expectedCode, String actualCode)
	{
		Assert.assertEquals("Got unexpected code", expectedCode, actualCode);
	}

	private String generateSlangCode(File file)	throws Exception
	{
		String fileContent = GeneralUtils.readFromFile(file);
		SlangGen codeGen = new SlangGen();

		String generatedJava = SlangGenUtil.generateSlangFromExp(fileContent, codeGen, Settings.dialect).getContent().trim();
		String trimmed = GeneralUtils.cleanupWhiteSpaces(generatedJava);
		return trimmed;
	}

	private void assertSingleClass(List<GeneratedModule> classes)
	{
		Assert.assertEquals("Expected one class to be generated", 1, classes.size());
	}
}

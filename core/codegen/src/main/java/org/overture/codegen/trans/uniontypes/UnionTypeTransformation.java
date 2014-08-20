/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.trans.uniontypes;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.AMapDomainUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMissingMemberRuntimeErrorExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpBase;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class UnionTypeTransformation extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;
	private IRInfo info;
	private List<AClassDeclCG> classes;

	private String objExpPrefix;
	private String applyExpResulPrefix;
	private String callStmObjPrefix;

	private ITempVarGen nameGen;
	
	private String missingOpMember;

	public UnionTypeTransformation(BaseTransformationAssistant baseAssistant,
			IRInfo info, List<AClassDeclCG> classes,
			String applyExpResultPrefix, String objExpPrefix,
			String callStmObjPrefix, String missingOpMember, ITempVarGen nameGen)
	{
		this.baseAssistant = baseAssistant;
		this.info = info;
		this.classes = classes;
		this.nameGen = nameGen;

		this.applyExpResulPrefix = applyExpResultPrefix;
		this.objExpPrefix = objExpPrefix;
		this.missingOpMember = missingOpMember;
		this.callStmObjPrefix = callStmObjPrefix;
	}

	private interface TypeFinder<T extends STypeCG>
	{
		public T findType(PType type)
				throws org.overture.ast.analysis.AnalysisException;
	}

	public <T extends STypeCG> T getMapType(SExpCG exp, TypeFinder<T> typeFinder)
	{
		if (exp == null || exp.getType() == null)
		{
			return null;
		}

		SourceNode sourceNode = exp.getType().getSourceNode();

		if (sourceNode == null)
		{
			return null;
		}

		org.overture.ast.node.INode vdmTypeNode = sourceNode.getVdmNode();

		if (vdmTypeNode instanceof PType)
		{
			try
			{
				PType vdmType = (PType) vdmTypeNode;

				return typeFinder.findType(vdmType);

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}

		return null;
	}

	private SExpCG correctTypes(SExpCG exp, STypeCG castedType)
			throws AnalysisException
	{
		if (exp.getType() instanceof AUnionTypeCG
				&& !(exp instanceof ACastUnaryExpCG))
		{
			ACastUnaryExpCG casted = new ACastUnaryExpCG();
			casted.setType(castedType.clone());
			casted.setExp(exp.clone());

			baseAssistant.replaceNodeWith(exp, casted);

			return casted;
		}

		return exp;
	}

	private boolean correctArgTypes(List<SExpCG> args, List<STypeCG> paramTypes)
			throws AnalysisException
	{
		if (info.getAssistantManager().getTypeAssistant().checkArgTypes(info, args, paramTypes))
		{
			for (int k = 0; k < paramTypes.size(); k++)
			{
				correctTypes(args.get(k), paramTypes.get(k));
			}
			return true;
		}

		return false;
	}

	private boolean handleUnaryExp(SUnaryExpCG exp) throws AnalysisException
	{
		STypeCG type = exp.getExp().getType();

		if (type instanceof AUnionTypeCG)
		{
			org.overture.ast.node.INode vdmNode = type.getSourceNode().getVdmNode();

			if (vdmNode instanceof PType)
			{
				return true;
			}
		}

		return false;
	}

	private AInstanceofExpCG consInstanceCheck(SExpCG copy, STypeCG type)
	{
		AInstanceofExpCG check = new AInstanceofExpCG();
		check.setType(new ABoolBasicTypeCG());
		check.setCheckedType(type.clone());
		check.setExp(copy.clone());
		return check;
	}

	@Override
	public void defaultInSNumericBinaryExpCG(SNumericBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG expectedType = node.getType();

		correctTypes(node.getLeft(), expectedType);
		correctTypes(node.getRight(), expectedType);
	}

	@Override
	public void caseAFieldExpCG(AFieldExpCG node) throws AnalysisException
	{
		node.getObject().apply(this);

		INode parent = node.parent();

		if (parent instanceof AApplyExpCG)
		{
			// TODO: Deflatten structure

			STypeCG objectType = node.getObject().getType();

			if (!(objectType instanceof AUnionTypeCG))
			{
				return;
			}

			SStmCG enclosingStatement = baseAssistant.getEnclosingStm(node, "field expression");

			String applyResultName = nameGen.nextVarName(applyExpResulPrefix);
			AVarLocalDeclCG resultDecl = new AVarLocalDeclCG();
			resultDecl.setSourceNode(node.getSourceNode());
			resultDecl.setExp(new ANullExpCG());
			resultDecl.setType(((AApplyExpCG) parent).getType().clone());
			AIdentifierPatternCG id = new AIdentifierPatternCG();
			id.setName(applyResultName);
			resultDecl.setPattern(id);

			AIdentifierVarExpCG resultVar = new AIdentifierVarExpCG();
			resultVar.setSourceNode(node.getSourceNode());
			resultVar.setIsLambda(false);
			resultVar.setOriginal(applyResultName);
			resultVar.setType(resultDecl.getType().clone());

			ABlockStmCG replacementBlock = new ABlockStmCG();
			SExpCG obj = null;
			if (!(node.getObject() instanceof SVarExpBase))
			{
				String objName = nameGen.nextVarName(objExpPrefix);
				AVarLocalDeclCG objectDecl = new AVarLocalDeclCG();
				objectDecl.setExp(node.getObject().clone());
				objectDecl.setType(node.getObject().getType().clone());
				AIdentifierPatternCG objectVarId = new AIdentifierPatternCG();
				objectVarId.setName(objName);
				objectDecl.setPattern(objectVarId);

				replacementBlock.getLocalDefs().add(objectDecl);

				AIdentifierVarExpCG objectVar = new AIdentifierVarExpCG();
				objectVar.setIsLambda(false);
				objectVar.setOriginal(objName);
				objectVar.setType(objectDecl.getType().clone());
				obj = objectVar;
			} else
			{
				obj = node.getObject().clone();
			}

			LinkedList<STypeCG> possibleTypes = ((AUnionTypeCG) objectType).getTypes();

			AIfStmCG ifChecks = new AIfStmCG();

			for (int i = 0; i < possibleTypes.size(); i++)
			{
				AApplyExpCG apply = (AApplyExpCG) parent.clone();
				AFieldExpCG fieldExp = (AFieldExpCG) apply.getRoot();

				STypeCG currentType = possibleTypes.get(i);

				ACastUnaryExpCG castedFieldExp = new ACastUnaryExpCG();
				castedFieldExp.setType(currentType.clone());
				castedFieldExp.setExp(obj.clone());

				fieldExp.setObject(castedFieldExp);

				ALocalAssignmentStmCG assignment = new ALocalAssignmentStmCG();
				assignment.setTarget(resultVar.clone());
				assignment.setExp(apply);

				if (i == 0)
				{
					ifChecks.setIfExp(consInstanceCheck(obj, currentType));
					ifChecks.setThenStm(assignment);
				} else if (i < possibleTypes.size() - 1)
				{
					AElseIfStmCG elseIf = new AElseIfStmCG();
					elseIf.setElseIf(consInstanceCheck(obj, currentType));
					elseIf.setThenStm(assignment);

					ifChecks.getElseIf().add(elseIf);
				} else
				{
					ifChecks.setElseStm(assignment);
				}
			}

			baseAssistant.replaceNodeWith(parent, resultVar);
			replacementBlock.getLocalDefs().add(resultDecl);
			replacementBlock.getStatements().add(ifChecks);

			baseAssistant.replaceNodeWith(enclosingStatement, replacementBlock);
			replacementBlock.getStatements().add(enclosingStatement);
			ifChecks.apply(this);
		}
	}

	@Override
	public void caseAApplyExpCG(AApplyExpCG node) throws AnalysisException
	{
		for (SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}
		SExpCG root = node.getRoot();
		root.apply(this);

		if (root.getType() instanceof AUnionTypeCG)
		{
			SMapTypeCG mapType = getMapType(root, new TypeFinder<SMapTypeCG>()
			{

				@Override
				public SMapTypeCG findType(PType type)
						throws org.overture.ast.analysis.AnalysisException
				{
					SMapType mapType = info.getTcFactory().createPTypeAssistant().getMap(type);

					return mapType != null ? (SMapTypeCG) mapType.apply(info.getTypeVisitor(), info)
							: null;
				}
			});

			if (mapType != null && node.getArgs().size() == 1)
			{
				correctTypes(root, mapType);
				return;
			}
		} else if (root.getType() instanceof AMethodTypeCG)
		{
			AMethodTypeCG methodType = (AMethodTypeCG) root.getType();

			LinkedList<STypeCG> paramTypes = methodType.getParams();

			LinkedList<SExpCG> args = node.getArgs();

			correctArgTypes(args, paramTypes);
		}
	}

	@Override
	public void inANotUnaryExpCG(ANotUnaryExpCG node) throws AnalysisException
	{
		correctTypes(node.getExp(), new ABoolBasicTypeCG());
	}

	@Override
	public void inAEqualsBinaryExpCG(AEqualsBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG leftType = node.getLeft().getType();
		STypeCG rightType = node.getRight().getType();

		SExpCG unionTypedExp = null;
		SExpCG notUnionTypedExp = null;

		if (leftType instanceof AUnionTypeCG
				&& !(rightType instanceof AUnionTypeCG))
		{
			unionTypedExp = node.getLeft();
			notUnionTypedExp = node.getRight();
		} else if (rightType instanceof AUnionTypeCG
				&& !(leftType instanceof AUnionTypeCG))
		{
			unionTypedExp = node.getRight();
			notUnionTypedExp = node.getLeft();
		} else
		{
			return;
		}

		STypeCG expectedType = notUnionTypedExp.getType();
		correctTypes(unionTypedExp, expectedType);
	}

	@Override
	public void inANewExpCG(ANewExpCG node) throws AnalysisException
	{
		LinkedList<SExpCG> args = node.getArgs();

		boolean hasUnionTypes = false;

		for (SExpCG arg : args)
		{
			if (arg.getType() instanceof AUnionTypeCG)
			{
				hasUnionTypes = true;
				break;
			}
		}

		if (!hasUnionTypes)
		{
			return;
		}

		STypeCG type = node.getType();

		if (type instanceof AClassTypeCG)
		{
			for (AClassDeclCG classCg : classes)
			{
				for (AMethodDeclCG method : classCg.getMethods())
				{
					if (!method.getIsConstructor())
					{
						continue;
					}

					if (correctArgTypes(args, method.getMethodType().getParams()))
					{
						return;
					}
				}
			}
		} else if (type instanceof ARecordTypeCG)
		{
			ARecordTypeCG recordType = (ARecordTypeCG) type;
			String definingClassName = recordType.getName().getDefiningClass();
			String recordName = recordType.getName().getName();

			AClassDeclCG classDecl = info.getAssistantManager().getDeclAssistant().findClass(classes, definingClassName);
			ARecordDeclCG record = info.getAssistantManager().getDeclAssistant().findRecord(classDecl, recordName);

			List<STypeCG> fieldTypes = info.getAssistantManager().getTypeAssistant().getFieldTypes(record);

			if (correctArgTypes(args, fieldTypes))
			{
				return;
			}
		}
	}

	@Override
	public void inAIfStmCG(AIfStmCG node) throws AnalysisException
	{
		ABoolBasicTypeCG expectedType = new ABoolBasicTypeCG();

		correctTypes(node.getIfExp(), expectedType);

		LinkedList<AElseIfStmCG> elseIfs = node.getElseIf();

		for (AElseIfStmCG currentElseIf : elseIfs)
		{
			correctTypes(currentElseIf.getElseIf(), expectedType);
		}
	}

	@Override
	public void caseACallStmCG(ACallStmCG node) throws AnalysisException
	{
		for (SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}

		AClassTypeCG classType = node.getClassType();
		String className = classType != null ? classType.getName()
				: node.getAncestor(AClassDeclCG.class).getName();
		String fieldName = node.getName();
		LinkedList<SExpCG> args = node.getArgs();

		TypeAssistantCG typeAssistant = info.getAssistantManager().getTypeAssistant();
		AMethodTypeCG methodType = typeAssistant.getMethodType(info, classes, className, fieldName, args);

		if (methodType != null)
		{
			correctArgTypes(args, methodType.getParams());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inACallObjectStmCG(ACallObjectStmCG node)
			throws AnalysisException
	{
		for (SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}

		SObjectDesignatorCG designator = node.getDesignator();

		ObjectDesignatorToExpCG converter = new ObjectDesignatorToExpCG(info, classes);

		SExpCG objExp = designator.apply(converter);

		STypeCG objType = objExp.getType();
		if (!(objType instanceof AUnionTypeCG))
		{
			return;
		}

		STypeCG type = node.getType();
		LinkedList<SExpCG> args = node.getArgs();
		String className = node.getClassName();
		String fieldName = node.getFieldName();
		SourceNode sourceNode = node.getSourceNode();

		ACallObjectExpStmCG call = new ACallObjectExpStmCG();
		call.setObj(objExp);
		call.setType(type.clone());
		call.setArgs((List<? extends SExpCG>) args.clone());
		call.setClassName(className);
		call.setFieldName(fieldName);
		call.setSourceNode(sourceNode);

		ABlockStmCG replacementBlock = new ABlockStmCG();

		if (!(designator instanceof AIdentifierObjectDesignatorCG))
		{
			String callStmObjName = nameGen.nextVarName(callStmObjPrefix);
			AVarLocalDeclCG objDecl = new AVarLocalDeclCG();
			objDecl.setSourceNode(node.getSourceNode());
			objDecl.setExp(objExp.clone());
			objDecl.setType(objType.clone());
			AIdentifierPatternCG id = new AIdentifierPatternCG();
			id.setName(callStmObjName);
			objDecl.setPattern(id);

			AIdentifierVarExpCG objVar = new AIdentifierVarExpCG();
			objVar.setSourceNode(node.getSourceNode());
			objVar.setIsLambda(false);
			objVar.setOriginal(callStmObjName);
			objVar.setType(objDecl.getType().clone());

			objExp = objVar;

			replacementBlock.getLocalDefs().add(objDecl);
		}

		TypeAssistantCG typeAssistant = info.getAssistantManager().getTypeAssistant();

		LinkedList<STypeCG> possibleTypes = ((AUnionTypeCG) objType).getTypes();
		AIfStmCG ifChecks = new AIfStmCG();

		for (int i = 0; i < possibleTypes.size(); i++)
		{
			ACallObjectExpStmCG callCopy = call.clone();

			AClassTypeCG currentType = (AClassTypeCG) possibleTypes.get(i);

			AMethodTypeCG methodType = typeAssistant.getMethodType(info, classes, currentType.getName(), fieldName, args);

			if (methodType != null)
			{
				correctArgTypes(callCopy.getArgs(), methodType.getParams());
			}
			else
			{
				//It's possible (due to the way union types work) that the method type for the
				//field in the object type does not exist. Let's say we are trying to invoke the
				//operation 'op' for an object type that is either A or B but it might be the
				//case that only 'A' has the operation 'op' defined.
				continue;
			}

			ACastUnaryExpCG castedVarExp = new ACastUnaryExpCG();
			castedVarExp.setType(currentType.clone());
			castedVarExp.setExp(objExp.clone());

			callCopy.setObj(castedVarExp);

			if (i == 0)
			{
				ifChecks.setIfExp(consInstanceCheck(objExp, currentType));
				ifChecks.setThenStm(callCopy);
			} else
			{
				AElseIfStmCG elseIf = new AElseIfStmCG();
				elseIf.setElseIf(consInstanceCheck(objExp, currentType));
				elseIf.setThenStm(callCopy);

				ifChecks.getElseIf().add(elseIf);
			}
		}
		
		AMissingMemberRuntimeErrorExpCG missingMember = new AMissingMemberRuntimeErrorExpCG();
		missingMember.setType(new AErrorTypeCG());
		missingMember.setMessage(missingOpMember + fieldName);

		ARaiseErrorStmCG raise = new ARaiseErrorStmCG();
		raise.setError(missingMember);
		ifChecks.setElseStm(raise);

		replacementBlock.getStatements().add(ifChecks);
		baseAssistant.replaceNodeWith(node, replacementBlock);
		ifChecks.apply(this);
	}

	@Override
	public void inAVarLocalDeclCG(AVarLocalDeclCG node)
			throws AnalysisException
	{
		STypeCG expectedType = node.getType();

		if (!(expectedType instanceof AUnionTypeCG))
		{
			correctTypes(node.getExp(), expectedType);
		}
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		if (node.getExp() == null)
		{
			return; // When the return type of the method is 'void'
		}

		node.getExp().apply(this);

		AMethodDeclCG methodDecl = node.getAncestor(AMethodDeclCG.class);

		STypeCG expectedType = methodDecl.getMethodType().getResult();

		if (!(expectedType instanceof AUnionTypeCG))
		{
			correctTypes(node.getExp(), expectedType);
		}
	}

	@Override
	public void inAElemsUnaryExpCG(AElemsUnaryExpCG node)
			throws AnalysisException
	{
		if (handleUnaryExp(node))
		{
			SExpCG exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SSeqType seqType = info.getTcFactory().createPTypeAssistant().getSeq(vdmType);

			try
			{
				STypeCG typeCg = seqType.apply(info.getTypeVisitor(), info);

				if (typeCg instanceof SSeqTypeCG)
				{
					correctTypes(exp, typeCg);
				}

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}

	@Override
	public void inAMapDomainUnaryExpCG(AMapDomainUnaryExpCG node)
			throws AnalysisException
	{
		if (handleUnaryExp(node))
		{
			SExpCG exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SMapType mapType = info.getTcFactory().createPTypeAssistant().getMap(vdmType);

			try
			{
				STypeCG typeCg = mapType.apply(info.getTypeVisitor(), info);

				if (typeCg instanceof SMapTypeCG)
				{
					correctTypes(exp, typeCg);
				}

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}
}

package org.overture.codegen.vdm2jml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.assistant.PatternAssistantCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AMapSeqGetExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class TargetNormaliserTrans extends DepthFirstAnalysisAdaptor
{
	public static final String STATE_DES = "stateDes_";

	// Consider atomic
	// Consider the 'index' exp and side effects a.b('a').c (also for applies, although get calls do not really receive
	// any arguments)

	private JmlGenerator jmlGen;
	
	private Map<SStmCG,List<AIdentifierVarExpCG>> stateDesVars;

	public TargetNormaliserTrans(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
		this.stateDesVars = new HashMap<SStmCG,List<AIdentifierVarExpCG>>();
	}

	@Override
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		if(!(node.getObj() instanceof SVarExpCG))
		{
			normaliseTarget(node, node.getObj());
		}
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		if(!(node.getCol() instanceof SVarExpCG))
		{
			normaliseTarget(node, node.getCol());
		}
	}

	private void normaliseTarget(SStmCG node, SExpCG target)
	{
		List<AVarDeclCG> varDecls = new LinkedList<AVarDeclCG>();
		List<AIdentifierVarExpCG> vars = new LinkedList<AIdentifierVarExpCG>();

		SExpCG newTarget = splitTarget(target, varDecls, vars);

		stateDesVars.put(node, vars);
		
		if (varDecls.isEmpty())
		{
			return;
		}

		ABlockStmCG replBlock = new ABlockStmCG();
		jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, replBlock);

		for (AVarDeclCG t : varDecls)
		{
			replBlock.getLocalDefs().add(t);
		}

		replBlock.getStatements().add(node);

		if (newTarget != target)
		{
			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(target, newTarget);
		}
	}

	private SExpCG splitTarget(SExpCG target, List<AVarDeclCG> varDecls, List<AIdentifierVarExpCG> vars)
	{
		DeclAssistantCG dAssist = jmlGen.getJavaGen().getInfo().getDeclAssistant();
		PatternAssistantCG pAssist = jmlGen.getJavaGen().getInfo().getPatternAssistant();
		ExpAssistantCG eAssist = jmlGen.getJavaGen().getInfo().getExpAssistant();
		ITempVarGen nameGen = jmlGen.getJavaGen().getInfo().getTempVarNameGen();
		TransAssistantCG tr = jmlGen.getJavaGen().getTransAssistant();

		if (target instanceof SVarExpCG)
		{
			AIdentifierVarExpCG var = ((AIdentifierVarExpCG) target).clone();
			vars.add(var);
			return var;
		} else if (target instanceof AMapSeqGetExpCG)
		{
			// Utils.mapSeqGet(a.get_m(), 1).get_b()

			AMapSeqGetExpCG get = (AMapSeqGetExpCG) target;
			SExpCG newCol = splitTarget(get.getCol().clone(), varDecls, vars);
			tr.replaceNodeWith(get.getCol(), newCol);
			// Utils.mapSeqGet(tmp_2, 1).get_b()

			AIdentifierPatternCG id = pAssist.consIdPattern(nameGen.nextVarName(STATE_DES));
			AVarDeclCG varDecl = dAssist.consLocalVarDecl(get.getType().clone(), id, get.clone());
			varDecls.add(varDecl);
			// B tmp_1 = Utils.mapSeqGet(tmp_2, 1).get_b()

			// tmp_1
			AIdentifierVarExpCG var = eAssist.consIdVar(id.getName(), get.getType().clone());
			vars.add(var);
			return var;

		} else if (target instanceof AApplyExpCG)
		{
			// a.get_b().get_c()

			AApplyExpCG app = (AApplyExpCG) target;
			SExpCG newRoot = splitTarget(app.getRoot().clone(), varDecls, vars);
			tr.replaceNodeWith(app.getRoot(), newRoot);
			// tmp_2.get_c()

			AIdentifierPatternCG id = pAssist.consIdPattern(nameGen.nextVarName(STATE_DES));
			AVarDeclCG varDecl = dAssist.consLocalVarDecl(app.getType().clone(), id, app.clone());
			varDecls.add(varDecl);
			// C tmp_1 = tmp_2.get_c()

			// tmp_1
			AIdentifierVarExpCG var = eAssist.consIdVar(id.getName(), app.getType().clone());
			vars.add(var);
			return var;

		} else if (target instanceof AFieldExpCG)
		{
			AFieldExpCG field = (AFieldExpCG) target;
			SExpCG newObj = splitTarget(field.getObject().clone(), varDecls, vars);
			tr.replaceNodeWith(field.getObject(), newObj);

			return field;
		} else
		{
			Logger.getLog().printErrorln("Expected a variable expression at this point in '"
					+ this.getClass().getSimpleName() + "'. Got " + target);
			return null;
		}
	}

	public Map<SStmCG, List<AIdentifierVarExpCG>> getStateDesVars()
	{
		return stateDesVars;
	}
}
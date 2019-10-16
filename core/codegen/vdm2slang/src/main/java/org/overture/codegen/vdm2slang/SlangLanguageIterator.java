package org.overture.codegen.vdm2slang;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.AbstractLanguageIterator;
import org.overture.codegen.trans.iterator.ILanguageIterator;

import java.util.List;
/*
public class SlangLanguageIterator extends AbstractLanguageIterator {
    public SlangLanguageIterator(TransAssistantIR transAssist, IterationVarPrefixes iteVarPrefixes) {
    }

    @Override
    public List<SStmIR> getPreForLoopStms(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern) {
        return null;
    }

    @Override
    public AVarDeclIR getForLoopInit(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern) {
        return null;
    }

    @Override
    public SExpIR getForLoopCond(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern) throws AnalysisException {
        return null;
    }

    @Override
    public SExpIR getForLoopInc(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern) {
        return null;
    }

    @Override
    public AVarDeclIR getNextElementDeclared(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern) throws AnalysisException {
        return null;
    }

    @Override
    public ALocalPatternAssignmentStmIR getNextElementAssigned(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern, AVarDeclIR successVarDecl, AVarDeclIR nextElementDecl) throws AnalysisException {
        return null;
    }

    @Override
    public SExpIR consNextElementCall(AIdentifierVarExpIR setVar) throws AnalysisException {
        return null;
    }
}

 */

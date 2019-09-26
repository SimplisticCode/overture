package org.overture.codegen.vdm2slang;

import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.statements.*;
import org.overture.codegen.ir.types.*;

import java.util.LinkedList;
import java.util.List;

public class SlangValueSemantics {
    private SlangFormat slangFormat;
    private SlangSettings slangSettings;
    private List<INode> cloneFreeNodes;

    public SlangValueSemantics(SlangFormat slangFormat)
    {
        this.slangFormat = slangFormat;
        this.slangSettings = new SlangSettings();
        this.cloneFreeNodes = new LinkedList<>();
    }

    public void clear()
    {
        cloneFreeNodes.clear();
    }

    public void addCloneFreeNode(INode node)
    {
        cloneFreeNodes.add(node);
    }

    public List<INode> getCloneFreeNodes()
    {
        return cloneFreeNodes;
    }

    public void setSlangSettings(SlangSettings slangSettings)
    {
        this.slangSettings = slangSettings;
    }

    public SlangSettings getSlangSettings()
    {
        return slangSettings;
    }

    public boolean cloneMember(AFieldNumberExpIR exp)
    {
        if (slangSettings.getDisableCloning())
        {
            return false;
        }

        if (isCloneFree(exp))
        {
            return false;
        }

        // Generally tuples need to be cloned, for example, if they
        // contain a record field (that must be cloned)

        if (exp.parent() instanceof AFieldNumberExpIR)
        {
            return false;
        }

        if (cloneNotNeededMapPutGet(exp))
        {
            return false;
        }

        if (cloneNotNeededAssign(exp))
        {
            return false;
        }

        List<ATupleTypeIR> tupleTypes = getTypes(exp.getTuple().getType(), ATupleTypeIR.class);
        final int idx = (int) (exp.getField() - 1);

        for (ATupleTypeIR tupleType : tupleTypes)
        {
            STypeIR fieldType = tupleType.getTypes().get(idx);

            if (mayBeValueType(fieldType))
            {
                return true;
            }
        }

        return false;
    }

    public boolean cloneMember(AFieldExpIR exp)
    {
        if (slangSettings.getDisableCloning())
        {
            return false;
        }

        if (isCloneFree(exp))
        {
            return false;
        }

        INode parent = exp.parent();
        if (cloneNotNeeded(parent))
        {
            return false;
        }

        if (cloneNotNeededMapPutGet(exp))
        {
            return false;
        }

        if (cloneNotNeededAssign(exp))
        {
            return false;
        }

        List<ARecordTypeIR> recTypes = getTypes(exp.getObject().getType(), ARecordTypeIR.class);
        String memberName = exp.getMemberName();
        List<SClassDeclIR> classes = slangFormat.getIrInfo().getClasses();
        AssistantManager man = slangFormat.getIrInfo().getAssistantManager();

        for (ARecordTypeIR r : recTypes)
        {
            AFieldDeclIR field = man.getDeclAssistant().getFieldDecl(classes, r, memberName);

            if (field != null && mayBeValueType(field.getType()))
            {
                return true;
            }
        }

        return false;
    }

    private <T extends STypeIR> List<T> getTypes(STypeIR type, Class<T> filter)
    {
        List<T> filteredTypes = new LinkedList<>();

        if (filter.isInstance(type))
        {
            filteredTypes.add(filter.cast(type));
        } else if (type instanceof AUnionTypeIR)
        {
            List<STypeIR> types = ((AUnionTypeIR) type).getTypes();

            for (STypeIR t : types)
            {
                filteredTypes.addAll(getTypes(t, filter));
            }
        }

        return filteredTypes;
    }

    public boolean shouldClone(SExpIR exp)
    {
        if (slangSettings.getDisableCloning())
        {
            return false;
        }

        if (isCloneFree(exp))
        {
            return false;
        }

        if (inRecClassNonConstructor(exp))
        {
            return false;
        }

        if (compAdd(exp))
        {
            return false;
        }

        INode parent = exp.parent();

        if (cloneNotNeeded(parent))
        {
            return false;
        }

        if (parent instanceof AAssignToExpStmIR)
        {
            AAssignToExpStmIR assignment = (AAssignToExpStmIR) parent;
            if (assignment.getTarget() == exp)
            {
                return false;
            }
        }

        if (parent instanceof ACallObjectExpStmIR)
        {
            ACallObjectExpStmIR callObjStm = (ACallObjectExpStmIR) parent;

            if (callObjStm.getObj() == exp)
            {
                return false;
            }
        }

        if (cloneNotNeededMapPutGet(exp))
        {
            return false;
        }

        if (isPrePostArgument(exp))
        {
            return false;
        }

        STypeIR type = exp.getType();

        if (mayBeValueType(type))
        {
            if (parent instanceof ANewExpIR)
            {
                ANewExpIR newExp = (ANewExpIR) parent;
                STypeIR newExpType = newExp.getType();

                if (mayBeValueType(newExpType))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private boolean compAdd(SExpIR exp)
    {
        INode parent = exp.parent();

        if (parent instanceof ASeqCompAddStmIR)
        {
            ASeqCompAddStmIR add = (ASeqCompAddStmIR) parent;
            return add.getSeq() == exp;
        }

        if (parent instanceof ASetCompAddStmIR)
        {
            ASetCompAddStmIR add = (ASetCompAddStmIR) parent;
            return add.getSet() == exp;
        }

        if (parent instanceof AMapCompAddStmIR)
        {
            AMapCompAddStmIR add = (AMapCompAddStmIR) parent;
            return add.getMap() == exp;
        }

        return false;
    }

    private boolean inRecClassNonConstructor(SExpIR exp)
    {
        ADefaultClassDeclIR encClass = exp.getAncestor(ADefaultClassDeclIR.class);

        if (encClass != null)
        {
            LinkedList<AMethodDeclIR> methods = encClass.getMethods();

            boolean isRec = false;
            for (AMethodDeclIR m : methods)
            {
                if (m.getIsConstructor()
                        && m.getMethodType().getResult() instanceof ARecordTypeIR)
                {
                    isRec = true;
                    break;
                }
            }

            if (!isRec)
            {
                return false;
            } else
            {
                AMethodDeclIR encMethod = exp.getAncestor(AMethodDeclIR.class);

                if (encMethod != null)
                {
                    return !encMethod.getIsConstructor();
                } else
                {
                    return false;
                }
            }
        } else
        {
            return false;
        }
    }

    private boolean cloneNotNeededMapPutGet(SExpIR exp)
    {
        INode parent = exp.parent();

        if (parent instanceof AMapSeqUpdateStmIR)
        {
            AMapSeqUpdateStmIR mapSeqUpd = (AMapSeqUpdateStmIR) parent;

            if (mapSeqUpd.getCol() == exp)
            {
                return true;
            }
        }

        if (parent instanceof AMapSeqGetExpIR)
        {
            AMapSeqGetExpIR mapSeqGet = (AMapSeqGetExpIR) parent;

            if (mapSeqGet.getCol() == exp)
            {
                return true;
            }
        }

        return false;
    }

    private boolean cloneNotNeeded(INode parent)
    {
        while (parent instanceof ACastUnaryExpIR)
        {
            parent = parent.parent();
        }

        if (parent instanceof AApplyExpIR)
        {
            // Cloning is not needed if the expression is
            // used to look up a value in a sequence or a map
            SExpIR root = ((AApplyExpIR) parent).getRoot();

            if (!(root.getType() instanceof AMethodTypeIR))
            {
                return true;
            }
        }

        return parent instanceof AFieldExpIR
                || parent instanceof AFieldNumberExpIR
                || parent instanceof ATupleSizeExpIR
                || parent instanceof ATupleCompatibilityExpIR
                || parent instanceof AEqualsBinaryExpIR
                || parent instanceof ANotEqualsBinaryExpIR
                || parent instanceof AAddrEqualsBinaryExpIR
                || parent instanceof AAddrNotEqualsBinaryExpIR
                || parent instanceof AForAllStmIR
                || parent instanceof AIsOfClassExpIR
                || parent instanceof SIsExpIR
                || cloneNotNeededCollectionOperator(parent)
                || cloneNotNeededUtilCall(parent);
    }

    private boolean isPrePostArgument(SExpIR exp)
    {
        INode parent = exp.parent();

        if (!(parent instanceof AApplyExpIR))
        {
            return false;
        }

        AApplyExpIR applyExp = (AApplyExpIR) parent;

        Object tag = applyExp.getTag();

        /*if (!(tag instanceof SlangValueSemanticsTag))
        {
            return false;
        }

        JavaValueSemanticsTag javaTag = (JavaValueSemanticsTag) tag;

        if (javaTag.mustClone())
        {
            return false;
        }*/

        return applyExp.getArgs().contains(exp);
    }

    private boolean cloneNotNeededCollectionOperator(INode parent)
    {
        return cloneNotNeededSeqOperators(parent)
                || cloneNotNeededSetOperators(parent);
    }

    private boolean cloneNotNeededSeqOperators(INode parent)
    {
        return parent instanceof ALenUnaryExpIR
                || parent instanceof AIndicesUnaryExpIR
                || parent instanceof AHeadUnaryExpIR;
    }

    private boolean cloneNotNeededSetOperators(INode parent)
    {
        return parent instanceof ACardUnaryExpIR
                || parent instanceof AInSetBinaryExpIR
                || parent instanceof ASetSubsetBinaryExpIR
                || parent instanceof ASetProperSubsetBinaryExpIR;
    }

    private boolean cloneNotNeededUtilCall(INode node)
    {
        if (!(node instanceof AApplyExpIR))
        {
            return false;
        }

        AApplyExpIR applyExp = (AApplyExpIR) node;
        SExpIR root = applyExp.getRoot();

        if (!(root instanceof AExplicitVarExpIR))
        {
            return false;
        }

        AExplicitVarExpIR explicitVar = (AExplicitVarExpIR) root;

        STypeIR classType = explicitVar.getClassType();

        return classType instanceof AExternalTypeIR;
    }

    public boolean mayBeValueType(STypeIR type)
    {
        if (type instanceof AUnionTypeIR)
        {
            LinkedList<STypeIR> types = ((AUnionTypeIR) type).getTypes();

            for (STypeIR t : types)
            {
                if (mayBeValueType(t))
                {
                    return true;
                }
            }

            return false;
        } else
        {
            return type instanceof ARecordTypeIR || type instanceof ATupleTypeIR
                    || type instanceof SSeqTypeIR || type instanceof SSetTypeIR
                    || type instanceof SMapTypeIR;
        }
    }

    private boolean cloneNotNeededAssign(SExpIR exp)
    {
        INode parent = exp.parent();

        if (parent instanceof AAssignToExpStmIR)
        {
            AAssignToExpStmIR assignment = (AAssignToExpStmIR) parent;
            if (assignment.getTarget() == exp)
            {
                return true;
            }
        }

        return false;
    }

    public boolean isCloneFree(SExpIR exp)
    {
        if (exp == null)
        {
            return false;
        }

        INode next = exp;

        while (next != null)
        {
            if (contains(next))
            {
                return true;
            } else
            {
                next = next.parent();
            }
        }

        return false;
    }

    private boolean contains(INode node)
    {
        for (INode n : cloneFreeNodes)
        {
            if (n == node)
            {
                return true;
            }
        }

        return false;
    }
}

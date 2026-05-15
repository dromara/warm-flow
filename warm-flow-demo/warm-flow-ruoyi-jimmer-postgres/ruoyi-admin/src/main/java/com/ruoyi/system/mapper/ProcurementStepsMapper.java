package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ProcurementSteps;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.ProcurementStepsModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 企业采购流程 Jimmer 数据层 */
@Repository
public class ProcurementStepsMapper extends JimmerRepositorySupport<ProcurementSteps, ProcurementStepsModel>
{
    public ProcurementStepsMapper()
    {
        super(ProcurementSteps.class, ProcurementStepsModel.class, "id");
    }

    public ProcurementSteps selectProcurementStepsById(Long id)
    {
        return selectById(id);
    }

    public List<ProcurementSteps> selectProcurementStepsByIds(Long[] ids)
    {
        return list(predicates(in("id", Arrays.asList(ids))), orders("createTime desc"));
    }

    public List<ProcurementSteps> selectProcurementStepsList(ProcurementSteps procurementSteps)
    {
        return list(procurementPredicates(procurementSteps), orders("createTime desc"));
    }

    public int insertProcurementSteps(ProcurementSteps procurementSteps)
    {
        if (procurementSteps.getDelFlag() == null)
        {
            procurementSteps.setDelFlag("0");
        }
        return insert(procurementSteps);
    }

    public int updateProcurementSteps(ProcurementSteps procurementSteps)
    {
        return updateById(procurementSteps);
    }

    public int deleteProcurementStepsById(Long id)
    {
        return deleteById(id);
    }

    public int deleteProcurementStepsByIds(Long[] ids)
    {
        return deleteByIds(ids);
    }

    private List<Predicate> procurementPredicates(ProcurementSteps procurementSteps)
    {
        List<Predicate> predicates = predicates();
        if (procurementSteps != null)
        {
            predicates.add(like("purchaseName", procurementSteps.getPurchaseName()));
            predicates.add(eq("updateTime", procurementSteps.getUpdateTime()));
            predicates.add(eq("flowStatus", procurementSteps.getFlowStatus()));
            predicates.add(eq("createBy", procurementSteps.getCreateBy()));
        }
        return predicates;
    }
}

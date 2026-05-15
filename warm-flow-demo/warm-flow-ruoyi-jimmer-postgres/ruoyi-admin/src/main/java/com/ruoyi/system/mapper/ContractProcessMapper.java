package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ContractProcess;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.ContractProcessModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 合同流程 Jimmer 数据层 */
@Repository
public class ContractProcessMapper extends JimmerRepositorySupport<ContractProcess, ContractProcessModel>
{
    public ContractProcessMapper()
    {
        super(ContractProcess.class, ContractProcessModel.class, "id");
    }

    public ContractProcess selectContractProcessById(Long id)
    {
        return selectById(id);
    }

    public List<ContractProcess> selectContractProcessByIds(Long[] ids)
    {
        return list(predicates(in("id", Arrays.asList(ids))), orders("createTime desc"));
    }

    public List<ContractProcess> selectContractProcessList(ContractProcess contractProcess)
    {
        return list(contractPredicates(contractProcess), orders("createTime desc"));
    }

    public int insertContractProcess(ContractProcess contractProcess)
    {
        if (contractProcess.getDelFlag() == null)
        {
            contractProcess.setDelFlag("0");
        }
        return insert(contractProcess);
    }

    public int updateContractProcess(ContractProcess contractProcess)
    {
        return updateById(contractProcess);
    }

    public int deleteContractProcessById(Long id)
    {
        return deleteById(id);
    }

    public int deleteContractProcessByIds(Long[] ids)
    {
        return deleteByIds(ids);
    }

    private List<Predicate> contractPredicates(ContractProcess contractProcess)
    {
        List<Predicate> predicates = predicates();
        if (contractProcess != null)
        {
            predicates.add(like("contractName", contractProcess.getContractName()));
            predicates.add(eq("contractType", contractProcess.getContractType()));
            predicates.add(eq("flowStatus", contractProcess.getFlowStatus()));
            predicates.add(eq("createBy", contractProcess.getCreateBy()));
        }
        return predicates;
    }
}

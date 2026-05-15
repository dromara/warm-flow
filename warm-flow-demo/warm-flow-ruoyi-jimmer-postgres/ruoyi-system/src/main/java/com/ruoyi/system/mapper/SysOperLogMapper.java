package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysOperLogModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 操作日志 Jimmer 数据层 */
@Repository
public class SysOperLogMapper extends JimmerRepositorySupport<SysOperLog, SysOperLogModel>
{
    public SysOperLogMapper()
    {
        super(SysOperLog.class, SysOperLogModel.class, "operId");
    }

    public void insertOperlog(SysOperLog operLog)
    {
        insert(operLog);
    }

    public List<SysOperLog> selectOperLogList(SysOperLog operLog)
    {
        return list(operPredicates(operLog), orders("operId desc"));
    }

    public int deleteOperLogByIds(Long[] operIds)
    {
        return deleteByIds(operIds);
    }

    public SysOperLog selectOperLogById(Long operId)
    {
        return selectById(operId);
    }

    public void cleanOperLog()
    {
        deleteWhere(Predicate.sql("1 = 1"));
    }

    private List<Predicate> operPredicates(SysOperLog operLog)
    {
        List<Predicate> predicates = predicates();
        if (operLog != null)
        {
            predicates.add(like("title", operLog.getTitle()));
            predicates.add(eq("businessType", operLog.getBusinessType()));
            if (operLog.getBusinessTypes() != null && operLog.getBusinessTypes().length > 0)
            {
                predicates.add(in("businessType", Arrays.asList(operLog.getBusinessTypes())));
            }
            predicates.add(eq("status", operLog.getStatus()));
            predicates.add(like("operName", operLog.getOperName()));
            addDateRange(predicates, operLog, "operTime");
        }
        return predicates;
    }
}

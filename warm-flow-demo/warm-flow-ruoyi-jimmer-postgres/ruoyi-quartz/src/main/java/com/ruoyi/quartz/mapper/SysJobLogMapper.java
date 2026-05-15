package com.ruoyi.quartz.mapper;

import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysJobLogModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/** 调度任务日志 Jimmer 数据层 */
@Repository
public class SysJobLogMapper extends JimmerRepositorySupport<SysJobLog, SysJobLogModel>
{
    public SysJobLogMapper()
    {
        super(SysJobLog.class, SysJobLogModel.class, "jobLogId");
    }

    public List<SysJobLog> selectJobLogList(SysJobLog jobLog)
    {
        return list(jobLogPredicates(jobLog), orders("createTime desc", "jobLogId desc"));
    }

    public List<SysJobLog> selectJobLogAll()
    {
        return listAll();
    }

    public SysJobLog selectJobLogById(Long jobLogId)
    {
        return selectById(jobLogId);
    }

    public int insertJobLog(SysJobLog jobLog)
    {
        if (jobLog.getCreateTime() == null)
        {
            jobLog.setCreateTime(new Date());
        }
        return insert(jobLog);
    }

    public int deleteJobLogByIds(Long[] logIds)
    {
        return deleteByIds(logIds);
    }

    public int deleteJobLogById(Long jobId)
    {
        return deleteById(jobId);
    }

    public void cleanJobLog()
    {
        deleteWhere(Predicate.sql("1 = 1"));
    }

    private List<Predicate> jobLogPredicates(SysJobLog jobLog)
    {
        List<Predicate> predicates = predicates();
        if (jobLog != null)
        {
            predicates.add(like("jobName", jobLog.getJobName()));
            predicates.add(eq("jobGroup", jobLog.getJobGroup()));
            predicates.add(eq("status", jobLog.getStatus()));
            predicates.add(like("invokeTarget", jobLog.getInvokeTarget()));
            addDateRange(predicates, jobLog, "createTime");
        }
        return predicates;
    }
}

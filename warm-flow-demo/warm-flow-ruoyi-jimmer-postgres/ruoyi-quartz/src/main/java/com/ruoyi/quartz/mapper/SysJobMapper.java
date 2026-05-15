package com.ruoyi.quartz.mapper;

import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysJobModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/** 调度任务 Jimmer 数据层 */
@Repository
public class SysJobMapper extends JimmerRepositorySupport<SysJob, SysJobModel>
{
    public SysJobMapper()
    {
        super(SysJob.class, SysJobModel.class, "jobId");
    }

    public List<SysJob> selectJobList(SysJob job)
    {
        return list(jobPredicates(job), orders("jobId"));
    }

    public List<SysJob> selectJobAll()
    {
        return listAll();
    }

    public SysJob selectJobById(Long jobId)
    {
        return selectById(jobId);
    }

    public int deleteJobById(Long jobId)
    {
        return deleteById(jobId);
    }

    public int deleteJobByIds(Long[] ids)
    {
        return deleteByIds(ids);
    }

    public int updateJob(SysJob job)
    {
        job.setUpdateTime(new Date());
        return updateById(job);
    }

    public int insertJob(SysJob job)
    {
        if (job.getCreateTime() == null)
        {
            job.setCreateTime(new Date());
        }
        return insert(job);
    }

    private List<Predicate> jobPredicates(SysJob job)
    {
        List<Predicate> predicates = predicates();
        if (job != null)
        {
            predicates.add(like("jobName", job.getJobName()));
            predicates.add(eq("jobGroup", job.getJobGroup()));
            predicates.add(eq("status", job.getStatus()));
            predicates.add(like("invokeTarget", job.getInvokeTarget()));
        }
        return predicates;
    }
}

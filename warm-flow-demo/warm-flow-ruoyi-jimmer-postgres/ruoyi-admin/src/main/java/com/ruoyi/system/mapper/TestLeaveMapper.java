package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.TestLeave;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.TestLeaveModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** OA 请假申请 Jimmer 数据层 */
@Repository
public class TestLeaveMapper extends JimmerRepositorySupport<TestLeave, TestLeaveModel>
{
    public TestLeaveMapper()
    {
        super(TestLeave.class, TestLeaveModel.class, "id");
    }

    public TestLeave selectTestLeaveById(String id)
    {
        return selectById(id);
    }

    public List<TestLeave> selectTestLeaveByIds(String[] ids)
    {
        return list(predicates(in("id", Arrays.asList(ids))), orders("createTime desc"));
    }

    public List<TestLeave> selectTestLeaveList(TestLeave testLeave)
    {
        return list(testLeavePredicates(testLeave), orders("createTime desc"));
    }

    public int insertTestLeave(TestLeave testLeave)
    {
        if (testLeave.getDelFlag() == null)
        {
            testLeave.setDelFlag("0");
        }
        return insert(testLeave);
    }

    public int updateTestLeave(TestLeave testLeave)
    {
        return updateById(testLeave);
    }

    public int deleteTestLeaveById(String id)
    {
        return deleteById(id);
    }

    public int deleteTestLeaveByIds(String[] ids)
    {
        return deleteByIds(ids);
    }

    private List<Predicate> testLeavePredicates(TestLeave testLeave)
    {
        List<Predicate> predicates = predicates();
        if (testLeave != null)
        {
            predicates.add(eq("type", testLeave.getType()));
            predicates.add(eq("day", testLeave.getDay()));
            predicates.add(eq("flowStatus", testLeave.getFlowStatus()));
            predicates.add(eq("createBy", testLeave.getCreateBy()));
            Object beginCreateTime = testLeave.getParams().get("beginCreateTime");
            Object endCreateTime = testLeave.getParams().get("endCreateTime");
            if (beginCreateTime != null && endCreateTime != null)
            {
                predicates.add(sql("create_time between ? and ?", beginCreateTime, endCreateTime));
            }
        }
        return predicates;
    }
}

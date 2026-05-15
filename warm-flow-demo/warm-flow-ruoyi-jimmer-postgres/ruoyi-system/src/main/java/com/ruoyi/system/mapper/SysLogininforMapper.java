package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysLogininforModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 登录日志 Jimmer 数据层 */
@Repository
public class SysLogininforMapper extends JimmerRepositorySupport<SysLogininfor, SysLogininforModel>
{
    public SysLogininforMapper()
    {
        super(SysLogininfor.class, SysLogininforModel.class, "infoId");
    }

    public void insertLogininfor(SysLogininfor logininfor)
    {
        insert(logininfor);
    }

    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor)
    {
        return list(loginPredicates(logininfor), orders("infoId desc"));
    }

    public int deleteLogininforByIds(Long[] infoIds)
    {
        return deleteByIds(infoIds);
    }

    public int cleanLogininfor()
    {
        return deleteWhere(Predicate.sql("1 = 1"));
    }

    private List<Predicate> loginPredicates(SysLogininfor logininfor)
    {
        List<Predicate> predicates = predicates();
        if (logininfor != null)
        {
            predicates.add(like("ipaddr", logininfor.getIpaddr()));
            predicates.add(eq("status", logininfor.getStatus()));
            predicates.add(like("userName", logininfor.getUserName()));
            addDateRange(predicates, logininfor, "loginTime");
        }
        return predicates;
    }
}

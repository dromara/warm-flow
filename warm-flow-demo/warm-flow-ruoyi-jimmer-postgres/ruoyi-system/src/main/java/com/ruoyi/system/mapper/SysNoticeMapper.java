package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysNoticeModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 通知公告 Jimmer 数据层 */
@Repository
public class SysNoticeMapper extends JimmerRepositorySupport<SysNotice, SysNoticeModel>
{
    public SysNoticeMapper()
    {
        super(SysNotice.class, SysNoticeModel.class, "noticeId");
    }

    public SysNotice selectNoticeById(Long noticeId)
    {
        return selectById(noticeId);
    }

    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return list(noticePredicates(notice), orders("noticeId desc"));
    }

    public int insertNotice(SysNotice notice)
    {
        return insert(notice);
    }

    public int updateNotice(SysNotice notice)
    {
        return updateById(notice);
    }

    public int deleteNoticeById(Long noticeId)
    {
        return deleteById(noticeId);
    }

    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return deleteByIds(noticeIds);
    }

    private List<Predicate> noticePredicates(SysNotice notice)
    {
        List<Predicate> predicates = predicates();
        if (notice != null)
        {
            predicates.add(like("noticeTitle", notice.getNoticeTitle()));
            predicates.add(eq("noticeType", notice.getNoticeType()));
            predicates.add(eq("createBy", notice.getCreateBy()));
        }
        return predicates;
    }
}

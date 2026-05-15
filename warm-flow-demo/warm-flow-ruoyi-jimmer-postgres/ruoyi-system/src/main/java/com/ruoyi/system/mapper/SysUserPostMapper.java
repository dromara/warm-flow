package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysUserPost;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysUserPostModel;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 用户与岗位关联 Jimmer 数据层 */
@Repository
public class SysUserPostMapper extends JimmerRepositorySupport<SysUserPost, SysUserPostModel>
{
    public SysUserPostMapper()
    {
        super(SysUserPost.class, SysUserPostModel.class, "userPostId");
    }

    public int deleteUserPostByUserId(Long userId)
    {
        return deleteWhere(eq("userId", userId));
    }

    public int countUserPostById(Long postId)
    {
        return (int) count(predicates(eq("postId", postId)));
    }

    public int deleteUserPost(Long[] ids)
    {
        return deleteWhere(in("userId", Arrays.asList(ids)));
    }

    public int batchUserPost(List<SysUserPost> userPostList)
    {
        return batchInsert(userPostList);
    }
}

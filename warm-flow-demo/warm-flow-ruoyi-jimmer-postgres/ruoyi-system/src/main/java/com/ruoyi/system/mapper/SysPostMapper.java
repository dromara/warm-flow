package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysPostModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 岗位 Jimmer 数据层 */
@Repository
public class SysPostMapper extends JimmerRepositorySupport<SysPost, SysPostModel>
{
    public SysPostMapper()
    {
        super(SysPost.class, SysPostModel.class, "postId");
    }

    public List<SysPost> selectPostList(SysPost post)
    {
        return list(postPredicates(post), orders("postSort", "postId"));
    }

    public List<SysPost> selectPostAll()
    {
        return list(predicates(eq("status", "0")), orders("postSort", "postId"));
    }

    public SysPost selectPostById(Long postId)
    {
        return selectById(postId);
    }

    public List<Long> selectPostListByUserId(Long userId)
    {
        return sqlClient.createQuery(com.ruoyi.system.jimmer.model.SysUserPostModelTable.$)
            .where(com.ruoyi.system.jimmer.model.SysUserPostModelTable.$.userId().eq(userId))
            .orderBy(com.ruoyi.system.jimmer.model.SysUserPostModelTable.$.postId().asc())
            .select(com.ruoyi.system.jimmer.model.SysUserPostModelTable.$.postId())
            .execute();
    }

    public List<SysPost> selectPostsByUserName(String userName)
    {
        return list(predicates(sql("post_id in (select up.post_id from sys_user u join sys_user_post up on u.user_id = up.user_id where u.user_name = ?)", userName)), orders("postSort", "postId"));
    }

    public int deletePostById(Long postId)
    {
        return deleteById(postId);
    }

    public int deletePostByIds(Long[] postIds)
    {
        return deleteByIds(postIds);
    }

    public int updatePost(SysPost post)
    {
        return updateById(post);
    }

    public int insertPost(SysPost post)
    {
        return insert(post);
    }

    public SysPost checkPostNameUnique(String postName)
    {
        return findOne(predicates(eq("postName", postName)), null);
    }

    public SysPost checkPostCodeUnique(String postCode)
    {
        return findOne(predicates(eq("postCode", postCode)), null);
    }

    private List<org.babyfish.jimmer.sql.ast.Predicate> postPredicates(SysPost post)
    {
        List<org.babyfish.jimmer.sql.ast.Predicate> predicates = predicates();
        if (post != null)
        {
            predicates.add(like("postCode", post.getPostCode()));
            predicates.add(like("postName", post.getPostName()));
            predicates.add(eq("status", post.getStatus()));
        }
        return predicates;
    }
}

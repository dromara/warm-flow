package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysUserModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/** 用户 Jimmer 数据层 */
@Repository
public class SysUserMapper extends JimmerRepositorySupport<SysUser, SysUserModel>
{
    public SysUserMapper()
    {
        super(SysUser.class, SysUserModel.class, "userId");
    }

    public List<SysUser> selectUserList(SysUser sysUser)
    {
        return hydrateUsers(list(userPredicates(sysUser), orders("userId")));
    }

    public List<SysUser> selectAllocatedList(SysUser user)
    {
        List<Predicate> predicates = userPredicates(user);
        predicates.add(sql("user_id in (select user_id from sys_user_role where role_id = ?)", user.getRoleId()));
        return hydrateUsers(list(predicates, orders("userId")));
    }

    public List<SysUser> selectUnallocatedList(SysUser user)
    {
        List<Predicate> predicates = userPredicates(user);
        predicates.add(sql("user_id not in (select user_id from sys_user_role where role_id = ?)", user.getRoleId()));
        return hydrateUsers(list(predicates, orders("userId")));
    }

    public SysUser selectUserByUserName(String userName)
    {
        return hydrateUser(findOne(predicates(eq("userName", userName), eq("delFlag", "0")), null));
    }

    public SysUser selectUserById(Long userId)
    {
        return hydrateUser(selectById(userId));
    }

    public int insertUser(SysUser user)
    {
        if (user.getDelFlag() == null)
        {
            user.setDelFlag("0");
        }
        return insert(user);
    }

    public int updateUser(SysUser user)
    {
        return updateById(user);
    }

    public int updateUserAvatar(Long userId, String avatar)
    {
        return updateFieldsById(userId, mapOf("avatar", avatar));
    }

    public int updateUserStatus(Long userId, String status)
    {
        return updateFieldsById(userId, mapOf("status", status));
    }

    public int updateLoginInfo(Long userId, String loginIp, Date loginDate)
    {
        return updateFieldsById(userId, mapOf("loginIp", loginIp, "loginDate", loginDate));
    }

    public int resetUserPwd(Long userId, String password)
    {
        return updateFieldsById(userId, mapOf("password", password, "pwdUpdateDate", new Date()));
    }

    public int deleteUserById(Long userId)
    {
        return softDeleteById(userId);
    }

    public int deleteUserByIds(Long[] userIds)
    {
        return softDeleteByIds(userIds);
    }

    public SysUser checkUserNameUnique(String userName)
    {
        return selectUserByUserName(userName);
    }

    public SysUser checkPhoneUnique(String phonenumber)
    {
        return findOne(predicates(eq("phonenumber", phonenumber), eq("delFlag", "0")), null);
    }

    public SysUser checkEmailUnique(String email)
    {
        return findOne(predicates(eq("email", email), eq("delFlag", "0")), null);
    }

    private List<Predicate> userPredicates(SysUser user)
    {
        List<Predicate> predicates = predicates(eq("delFlag", "0"));
        if (user != null)
        {
            predicates.add(eq("userId", user.getUserId()));
            predicates.add(eq("deptId", user.getDeptId()));
            predicates.add(like("userName", user.getUserName()));
            predicates.add(like("phonenumber", user.getPhonenumber()));
            predicates.add(eq("status", user.getStatus()));
            predicates.add(dataScope(user));
        }
        return predicates;
    }

    private List<SysUser> hydrateUsers(List<SysUser> users)
    {
        for (SysUser user : users)
        {
            hydrateUser(user);
        }
        return users;
    }

    private SysUser hydrateUser(SysUser user)
    {
        if (user == null)
        {
            return null;
        }
        if (user.getDeptId() != null)
        {
            com.ruoyi.system.jimmer.model.SysDeptModelTable d = com.ruoyi.system.jimmer.model.SysDeptModelTable.$;
            List<com.ruoyi.system.jimmer.model.SysDeptModel> rows = sqlClient.createQuery(d).where(d.deptId().eq(user.getDeptId())).select(d).limit(1).execute();
            if (!rows.isEmpty())
            {
                com.ruoyi.system.mapper.SysDeptMapper deptMapper = new com.ruoyi.system.mapper.SysDeptMapper();
                try
                {
                    java.lang.reflect.Field f = com.ruoyi.system.jimmer.JimmerRepositorySupport.class.getDeclaredField("sqlClient");
                    f.setAccessible(true);
                    f.set(deptMapper, sqlClient);
                    user.setDept(deptMapper.fromModel(rows.get(0)));
                }
                catch (Exception ignored)
                {
                    SysDept dept = new SysDept();
                    dept.setDeptId(user.getDeptId());
                    user.setDept(dept);
                }
            }
        }
        user.setRoles(listRoles(user.getUserId()));
        return user;
    }

    private List<SysRole> listRoles(Long userId)
    {
        SysRoleMapper roleMapper = new SysRoleMapper();
        try
        {
            java.lang.reflect.Field f = com.ruoyi.system.jimmer.JimmerRepositorySupport.class.getDeclaredField("sqlClient");
            f.setAccessible(true);
            f.set(roleMapper, sqlClient);
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
        return roleMapper.selectRolePermissionByUserId(userId);
    }
}

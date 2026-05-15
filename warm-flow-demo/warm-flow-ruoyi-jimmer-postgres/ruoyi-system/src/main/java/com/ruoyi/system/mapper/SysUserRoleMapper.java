package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysUserRoleModel;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 用户与角色关联 Jimmer 数据层 */
@Repository
public class SysUserRoleMapper extends JimmerRepositorySupport<SysUserRole, SysUserRoleModel>
{
    public SysUserRoleMapper()
    {
        super(SysUserRole.class, SysUserRoleModel.class, "userRoleId");
    }

    public int deleteUserRoleByUserId(Long userId)
    {
        return deleteWhere(eq("userId", userId));
    }

    public int deleteUserRole(Long[] ids)
    {
        return deleteWhere(in("userId", Arrays.asList(ids)));
    }

    public int countUserRoleByRoleId(Long roleId)
    {
        return (int) count(predicates(eq("roleId", roleId)));
    }

    public int batchUserRole(List<SysUserRole> userRoleList)
    {
        return batchInsert(userRoleList);
    }

    public int deleteUserRoleInfo(SysUserRole userRole)
    {
        return deleteWhere(eq("userId", userRole.getUserId()), eq("roleId", userRole.getRoleId()));
    }

    public int deleteUserRoleInfos(Long roleId, Long[] userIds)
    {
        return deleteWhere(eq("roleId", roleId), in("userId", Arrays.asList(userIds)));
    }
}

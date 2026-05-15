package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysRoleModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 角色 Jimmer 数据层 */
@Repository
public class SysRoleMapper extends JimmerRepositorySupport<SysRole, SysRoleModel>
{
    public SysRoleMapper()
    {
        super(SysRole.class, SysRoleModel.class, "roleId");
    }

    public List<SysRole> selectRoleList(SysRole role)
    {
        return list(rolePredicates(role), orders("roleSort", "roleId"));
    }

    public List<SysRole> selectRolePermissionByUserId(Long userId)
    {
        if (userId != null && userId == 1L)
        {
            return selectRoleAll();
        }
        return list(predicates(eq("delFlag", "0"), sql("role_id in (select ur.role_id from sys_user_role ur where ur.user_id = ?)", userId)), orders("roleSort", "roleId"));
    }

    public List<SysRole> selectRoleAll()
    {
        return list(predicates(eq("delFlag", "0")), orders("roleSort", "roleId"));
    }

    public List<Long> selectRoleListByUserId(Long userId)
    {
        return sqlClient.createQuery(com.ruoyi.system.jimmer.model.SysUserRoleModelTable.$)
            .where(com.ruoyi.system.jimmer.model.SysUserRoleModelTable.$.userId().eq(userId))
            .orderBy(com.ruoyi.system.jimmer.model.SysUserRoleModelTable.$.roleId().asc())
            .select(com.ruoyi.system.jimmer.model.SysUserRoleModelTable.$.roleId())
            .execute();
    }

    public SysRole selectRoleById(Long roleId)
    {
        return selectById(roleId);
    }

    public List<SysRole> selectRolesByUserName(String userName)
    {
        return list(predicates(eq("delFlag", "0"), sql("role_id in (select ur.role_id from sys_user u join sys_user_role ur on u.user_id = ur.user_id where u.user_name = ?)", userName)), orders("roleSort", "roleId"));
    }

    public SysRole checkRoleNameUnique(String roleName)
    {
        return findOne(predicates(eq("roleName", roleName), eq("delFlag", "0")), null);
    }

    public SysRole checkRoleKeyUnique(String roleKey)
    {
        return findOne(predicates(eq("roleKey", roleKey), eq("delFlag", "0")), null);
    }

    public int updateRole(SysRole role)
    {
        return updateById(role);
    }

    public int insertRole(SysRole role)
    {
        return insert(role);
    }

    public int deleteRoleById(Long roleId)
    {
        return softDeleteById(roleId);
    }

    public int deleteRoleByIds(Long[] roleIds)
    {
        return softDeleteByIds(roleIds);
    }

    private List<Predicate> rolePredicates(SysRole role)
    {
        List<Predicate> predicates = predicates(eq("delFlag", "0"));
        if (role != null)
        {
            predicates.add(eq("roleId", role.getRoleId()));
            predicates.add(like("roleName", role.getRoleName()));
            predicates.add(eq("status", role.getStatus()));
            predicates.add(like("roleKey", role.getRoleKey()));
            predicates.add(dataScope(role));
        }
        return predicates;
    }
}

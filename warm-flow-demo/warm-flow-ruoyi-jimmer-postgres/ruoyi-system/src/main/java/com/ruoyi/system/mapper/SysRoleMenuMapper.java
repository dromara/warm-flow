package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysRoleMenu;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysRoleMenuModel;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 角色与菜单关联 Jimmer 数据层 */
@Repository
public class SysRoleMenuMapper extends JimmerRepositorySupport<SysRoleMenu, SysRoleMenuModel>
{
    public SysRoleMenuMapper()
    {
        super(SysRoleMenu.class, SysRoleMenuModel.class, "roleMenuId");
    }

    public int checkMenuExistRole(Long menuId)
    {
        return (int) count(predicates(eq("menuId", menuId)));
    }

    public int deleteRoleMenuByRoleId(Long roleId)
    {
        return deleteWhere(eq("roleId", roleId));
    }

    public int deleteRoleMenu(Long[] ids)
    {
        return deleteWhere(in("roleId", Arrays.asList(ids)));
    }

    public int batchRoleMenu(List<SysRoleMenu> roleMenuList)
    {
        return batchInsert(roleMenuList);
    }
}

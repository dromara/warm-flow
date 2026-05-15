package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysMenuModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/** 菜单 Jimmer 数据层 */
@Repository
public class SysMenuMapper extends JimmerRepositorySupport<SysMenu, SysMenuModel>
{
    public SysMenuMapper()
    {
        super(SysMenu.class, SysMenuModel.class, "menuId");
    }

    public List<SysMenu> selectMenuList(SysMenu menu)
    {
        return list(menuPredicates(menu), orders("parentId", "orderNum", "menuId"));
    }

    public List<String> selectMenuPerms()
    {
        return sqlClient.createQuery(table()).where(prop("status").eq("0")).select(this.<String>prop("perms")).execute();
    }

    public List<SysMenu> selectMenuListByUserId(SysMenu menu)
    {
        Long userId = (Long) param(menu, "userId");
        List<Predicate> predicates = menuPredicates(menu);
        predicates.add(sql("menu_id in (select distinct m.menu_id from sys_menu m join sys_role_menu rm on m.menu_id = rm.menu_id join sys_user_role ur on rm.role_id = ur.role_id join sys_role ro on ur.role_id = ro.role_id where ur.user_id = ? and ro.status = '0' and m.status = '0')", userId));
        return list(predicates, orders("parentId", "orderNum", "menuId"));
    }

    public List<String> selectMenuPermsByRoleId(Long roleId)
    {
        return sqlClient.createQuery(table())
            .where(prop("status").eq("0"), sql("menu_id in (select menu_id from sys_role_menu where role_id = ?)", roleId))
            .select(this.<String>prop("perms"))
            .execute();
    }

    public List<String> selectMenuPermsByUserId(Long userId)
    {
        return sqlClient.createQuery(table())
            .where(prop("status").eq("0"), sql("menu_id in (select distinct rm.menu_id from sys_role_menu rm join sys_user_role ur on rm.role_id = ur.role_id join sys_role ro on ur.role_id = ro.role_id where ur.user_id = ? and ro.status = '0')", userId))
            .select(this.<String>prop("perms"))
            .execute();
    }

    public List<SysMenu> selectMenuTreeAll()
    {
        return list(predicates(eq("status", "0"), in("menuType", java.util.Arrays.asList("M", "C"))), orders("parentId", "orderNum", "menuId"));
    }

    public List<SysMenu> selectMenuTreeByUserId(Long userId)
    {
        return list(predicates(eq("status", "0"), in("menuType", java.util.Arrays.asList("M", "C")), sql("menu_id in (select distinct m.menu_id from sys_menu m join sys_role_menu rm on m.menu_id = rm.menu_id join sys_user_role ur on rm.role_id = ur.role_id join sys_role ro on ur.role_id = ro.role_id where ur.user_id = ? and ro.status = '0')", userId)), orders("parentId", "orderNum", "menuId"));
    }

    public List<Long> selectMenuListByRoleId(Long roleId, boolean menuCheckStrictly)
    {
        String strict = menuCheckStrictly ? " and m.menu_id not in (select m2.parent_id from sys_menu m2 inner join sys_role_menu rm2 on m2.menu_id = rm2.menu_id and rm2.role_id = ?)" : "";
        return sqlClient.createQuery(table())
            .where(sql("menu_id in (select m.menu_id from sys_menu m inner join sys_role_menu rm on m.menu_id = rm.menu_id where rm.role_id = ?" + strict + ")", menuCheckStrictly ? new Object[]{roleId, roleId} : new Object[]{roleId}))
            .select(this.<Long>prop("menuId"))
            .execute();
    }

    public SysMenu selectMenuById(Long menuId)
    {
        return selectById(menuId);
    }

    public int hasChildByMenuId(Long menuId)
    {
        return (int) count(predicates(eq("parentId", menuId)));
    }

    public int insertMenu(SysMenu menu)
    {
        return insert(menu);
    }

    public int updateMenu(SysMenu menu)
    {
        return updateById(menu);
    }

    public int deleteMenuById(Long menuId)
    {
        return deleteById(menuId);
    }

    public SysMenu checkMenuNameUnique(String menuName, Long parentId)
    {
        return findOne(predicates(eq("menuName", menuName), eq("parentId", parentId)), null);
    }

    public List<SysMenu> selectMenusByPathOrRouteName(String path, String routeName)
    {
        List<Predicate> predicates = new ArrayList<>();
        if (path != null && routeName != null)
        {
            predicates.add(Predicate.or(eq("path", path), eq("routeName", routeName)));
        }
        else if (path != null)
        {
            predicates.add(eq("path", path));
        }
        else if (routeName != null)
        {
            predicates.add(eq("routeName", routeName));
        }
        return list(predicates, null);
    }

    private List<Predicate> menuPredicates(SysMenu menu)
    {
        List<Predicate> predicates = predicates();
        if (menu != null)
        {
            predicates.add(eq("menuId", menu.getMenuId()));
            predicates.add(like("menuName", menu.getMenuName()));
            predicates.add(eq("visible", menu.getVisible()));
            predicates.add(eq("status", menu.getStatus()));
        }
        return predicates;
    }
}

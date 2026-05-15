package com.ruoyi.system.jimmer.model;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Entity
@Table(name = "sys_menu")
public interface SysMenuModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    long menuId();

    @Column(name = "menu_name")
    String menuName();

    @Column(name = "parent_id")
    @Nullable
    Long parentId();

    @Column(name = "order_num")
    @Nullable
    Integer orderNum();

    @Column(name = "path")
    @Nullable
    String path();

    @Column(name = "component")
    @Nullable
    String component();

    @Column(name = "query")
    @Nullable
    String query();

    @Column(name = "route_name")
    @Nullable
    String routeName();

    @Column(name = "is_frame")
    @Nullable
    String frameFlag();

    @Column(name = "is_cache")
    @Nullable
    String cacheFlag();

    @Column(name = "menu_type")
    @Nullable
    String menuType();

    @Column(name = "visible")
    @Nullable
    String visible();

    @Column(name = "status")
    @Nullable
    String status();

    @Column(name = "perms")
    @Nullable
    String perms();

    @Column(name = "icon")
    @Nullable
    String icon();

    @Column(name = "create_by")
    @Nullable
    String createBy();

    @Column(name = "create_time")
    @Nullable
    Date createTime();

    @Column(name = "update_by")
    @Nullable
    String updateBy();

    @Column(name = "update_time")
    @Nullable
    Date updateTime();

    @Column(name = "remark")
    @Nullable
    String remark();

}

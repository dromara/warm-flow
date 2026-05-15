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
@Table(name = "sys_role")
public interface SysRoleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    long roleId();

    @Column(name = "role_name")
    String roleName();

    @Column(name = "role_key")
    String roleKey();

    @Column(name = "role_sort")
    Integer roleSort();

    @Column(name = "data_scope")
    @Nullable
    String dataScope();

    @Column(name = "menu_check_strictly")
    @Nullable
    Boolean menuCheckStrictly();

    @Column(name = "dept_check_strictly")
    @Nullable
    Boolean deptCheckStrictly();

    @Column(name = "status")
    String status();

    @Column(name = "del_flag")
    @Nullable
    String delFlag();

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

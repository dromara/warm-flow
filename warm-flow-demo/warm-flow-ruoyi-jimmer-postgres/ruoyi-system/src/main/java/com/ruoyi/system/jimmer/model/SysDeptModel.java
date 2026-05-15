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
@Table(name = "sys_dept")
public interface SysDeptModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    long deptId();

    @Column(name = "parent_id")
    @Nullable
    Long parentId();

    @Column(name = "ancestors")
    @Nullable
    String ancestors();

    @Column(name = "dept_name")
    @Nullable
    String deptName();

    @Column(name = "order_num")
    @Nullable
    Integer orderNum();

    @Column(name = "leader")
    @Nullable
    String leader();

    @Column(name = "phone")
    @Nullable
    String phone();

    @Column(name = "email")
    @Nullable
    String email();

    @Column(name = "status")
    @Nullable
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

}

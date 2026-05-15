package com.ruoyi.system.jimmer.model;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(name = "sys_role_dept")
public interface SysRoleDeptModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_dept_id")
    long roleDeptId();

    @Key
    @Column(name = "role_id")
    long roleId();

    @Key
    @Column(name = "dept_id")
    long deptId();

}

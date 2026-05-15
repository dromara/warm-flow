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
@Table(name = "sys_user")
public interface SysUserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    long userId();

    @Column(name = "dept_id")
    @Nullable
    Long deptId();

    @Column(name = "user_name")
    String userName();

    @Column(name = "nick_name")
    String nickName();

    @Column(name = "user_type")
    @Nullable
    String userType();

    @Column(name = "email")
    @Nullable
    String email();

    @Column(name = "phonenumber")
    @Nullable
    String phonenumber();

    @Column(name = "sex")
    @Nullable
    String sex();

    @Column(name = "avatar")
    @Nullable
    String avatar();

    @Column(name = "password")
    @Nullable
    String password();

    @Column(name = "status")
    @Nullable
    String status();

    @Column(name = "del_flag")
    @Nullable
    String delFlag();

    @Column(name = "login_ip")
    @Nullable
    String loginIp();

    @Column(name = "login_date")
    @Nullable
    Date loginDate();

    @Column(name = "pwd_update_date")
    @Nullable
    Date pwdUpdateDate();

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

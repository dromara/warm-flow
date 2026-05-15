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
@Table(name = "sys_logininfor")
public interface SysLogininforModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id")
    long infoId();

    @Column(name = "user_name")
    @Nullable
    String userName();

    @Column(name = "status")
    @Nullable
    String status();

    @Column(name = "ipaddr")
    @Nullable
    String ipaddr();

    @Column(name = "login_location")
    @Nullable
    String loginLocation();

    @Column(name = "browser")
    @Nullable
    String browser();

    @Column(name = "os")
    @Nullable
    String os();

    @Column(name = "msg")
    @Nullable
    String msg();

    @Column(name = "login_time")
    @Nullable
    Date loginTime();

}

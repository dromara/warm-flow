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
@Table(name = "sys_config")
public interface SysConfigModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    long configId();

    @Column(name = "config_name")
    String configName();

    @Column(name = "config_key")
    String configKey();

    @Column(name = "config_value")
    String configValue();

    @Column(name = "config_type")
    @Nullable
    String configType();

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

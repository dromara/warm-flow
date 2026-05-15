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
@Table(name = "sys_job")
public interface SysJobModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    long jobId();

    @Column(name = "job_name")
    String jobName();

    @Column(name = "job_group")
    String jobGroup();

    @Column(name = "invoke_target")
    String invokeTarget();

    @Column(name = "cron_expression")
    @Nullable
    String cronExpression();

    @Column(name = "misfire_policy")
    @Nullable
    String misfirePolicy();

    @Column(name = "concurrent")
    @Nullable
    String concurrent();

    @Column(name = "status")
    @Nullable
    String status();

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

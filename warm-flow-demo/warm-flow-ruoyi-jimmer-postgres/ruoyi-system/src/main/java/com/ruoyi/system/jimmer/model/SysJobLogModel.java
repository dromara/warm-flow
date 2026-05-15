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
@Table(name = "sys_job_log")
public interface SysJobLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_log_id")
    long jobLogId();

    @Column(name = "job_name")
    String jobName();

    @Column(name = "job_group")
    String jobGroup();

    @Column(name = "invoke_target")
    String invokeTarget();

    @Column(name = "job_message")
    @Nullable
    String jobMessage();

    @Column(name = "status")
    @Nullable
    String status();

    @Column(name = "exception_info")
    @Nullable
    String exceptionInfo();

    @Column(name = "create_time")
    @Nullable
    Date createTime();

    @Column(name = "start_time")
    @Nullable
    Date startTime();

    @Column(name = "stop_time")
    @Nullable
    Date stopTime();

}

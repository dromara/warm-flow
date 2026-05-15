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
@Table(name = "sys_oper_log")
public interface SysOperLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oper_id")
    long operId();

    @Column(name = "title")
    @Nullable
    String title();

    @Column(name = "business_type")
    @Nullable
    Integer businessType();

    @Column(name = "method")
    @Nullable
    String method();

    @Column(name = "request_method")
    @Nullable
    String requestMethod();

    @Column(name = "operator_type")
    @Nullable
    Integer operatorType();

    @Column(name = "oper_name")
    @Nullable
    String operName();

    @Column(name = "dept_name")
    @Nullable
    String deptName();

    @Column(name = "oper_url")
    @Nullable
    String operUrl();

    @Column(name = "oper_ip")
    @Nullable
    String operIp();

    @Column(name = "oper_location")
    @Nullable
    String operLocation();

    @Column(name = "oper_param")
    @Nullable
    String operParam();

    @Column(name = "json_result")
    @Nullable
    String jsonResult();

    @Column(name = "status")
    @Nullable
    Integer status();

    @Column(name = "error_msg")
    @Nullable
    String errorMsg();

    @Column(name = "oper_time")
    @Nullable
    Date operTime();

    @Column(name = "cost_time")
    @Nullable
    Long costTime();

}

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
@Table(name = "test_leave")
public interface TestLeaveModel {
    @Id
    @Column(name = "id")
    String id();

    @Column(name = "type")
    @Nullable
    Long type();

    @Column(name = "reason")
    @Nullable
    String reason();

    @Column(name = "start_time")
    @Nullable
    Date startTime();

    @Column(name = "end_time")
    @Nullable
    Date endTime();

    @Column(name = "day")
    @Nullable
    Long day();

    @Column(name = "instance_id")
    @Nullable
    Long instanceId();

    @Column(name = "node_code")
    @Nullable
    String nodeCode();

    @Column(name = "node_name")
    @Nullable
    String nodeName();

    @Column(name = "node_type")
    @Nullable
    Integer nodeType();

    @Column(name = "flow_status")
    @Nullable
    String flowStatus();

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

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
@Table(name = "sys_notice")
public interface SysNoticeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    long noticeId();

    @Column(name = "notice_title")
    String noticeTitle();

    @Column(name = "notice_type")
    String noticeType();

    @Column(name = "notice_content")
    @Nullable
    String noticeContent();

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

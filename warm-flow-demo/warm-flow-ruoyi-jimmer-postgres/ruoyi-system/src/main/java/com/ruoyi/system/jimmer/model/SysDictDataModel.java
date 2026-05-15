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
@Table(name = "sys_dict_data")
public interface SysDictDataModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dict_code")
    long dictCode();

    @Column(name = "dict_sort")
    @Nullable
    Long dictSort();

    @Column(name = "dict_label")
    String dictLabel();

    @Column(name = "dict_value")
    String dictValue();

    @Column(name = "dict_type")
    String dictType();

    @Column(name = "css_class")
    @Nullable
    String cssClass();

    @Column(name = "list_class")
    @Nullable
    String listClass();

    @Column(name = "is_default")
    @Nullable
    String defaultFlag();

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

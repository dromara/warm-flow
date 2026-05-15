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
@Table(name = "gen_table_column")
public interface GenTableColumnModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_id")
    long columnId();

    @Column(name = "table_id")
    long tableId();

    @Column(name = "column_name")
    String columnName();

    @Column(name = "column_comment")
    @Nullable
    String columnComment();

    @Column(name = "column_type")
    String columnType();

    @Column(name = "java_type")
    @Nullable
    String javaType();

    @Column(name = "java_field")
    @Nullable
    String javaField();

    @Column(name = "is_pk")
    @Nullable
    String pkFlag();

    @Column(name = "is_increment")
    @Nullable
    String incrementFlag();

    @Column(name = "is_required")
    @Nullable
    String requiredFlag();

    @Column(name = "is_insert")
    @Nullable
    String insertFlag();

    @Column(name = "is_edit")
    @Nullable
    String editFlag();

    @Column(name = "is_list")
    @Nullable
    String listFlag();

    @Column(name = "is_query")
    @Nullable
    String queryFlag();

    @Column(name = "query_type")
    @Nullable
    String queryType();

    @Column(name = "html_type")
    @Nullable
    String htmlType();

    @Column(name = "dict_type")
    @Nullable
    String dictType();

    @Column(name = "sort")
    @Nullable
    Integer sort();

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

}

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
@Table(name = "gen_table")
public interface GenTableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    long tableId();

    @Column(name = "table_name")
    String tableName();

    @Column(name = "table_comment")
    String tableComment();

    @Column(name = "sub_table_name")
    @Nullable
    String subTableName();

    @Column(name = "sub_table_fk_name")
    @Nullable
    String subTableFkName();

    @Column(name = "class_name")
    String className();

    @Column(name = "tpl_category")
    @Nullable
    String tplCategory();

    @Column(name = "tpl_web_type")
    @Nullable
    String tplWebType();

    @Column(name = "package_name")
    @Nullable
    String packageName();

    @Column(name = "module_name")
    @Nullable
    String moduleName();

    @Column(name = "business_name")
    @Nullable
    String businessName();

    @Column(name = "function_name")
    @Nullable
    String functionName();

    @Column(name = "function_author")
    @Nullable
    String functionAuthor();

    @Column(name = "gen_type")
    @Nullable
    String genType();

    @Column(name = "gen_path")
    @Nullable
    String genPath();

    @Column(name = "options")
    @Nullable
    String options();

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

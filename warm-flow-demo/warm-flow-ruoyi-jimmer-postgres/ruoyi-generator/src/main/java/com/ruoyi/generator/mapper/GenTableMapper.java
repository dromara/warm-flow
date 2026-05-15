package com.ruoyi.generator.mapper;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.generator.domain.GenTable;
import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.GenTableModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** 代码生成业务表 Jimmer 数据层 */
@Repository
public class GenTableMapper extends JimmerRepositorySupport<GenTable, GenTableModel>
{
    @Autowired
    private GenTableColumnMapper genTableColumnMapper;

    public GenTableMapper()
    {
        super(GenTable.class, GenTableModel.class, "tableId");
    }

    public List<GenTable> selectGenTableList(GenTable genTable)
    {
        return list(genTablePredicates(genTable), orders("tableId"));
    }

    public List<GenTable> selectDbTableList(GenTable genTable)
    {
        List<Object> params = new java.util.ArrayList<>();
        StringBuilder sql = new StringBuilder("select table_name, obj_description(format('%I.%I', table_schema, table_name)::regclass, 'pg_class') as table_comment, null::timestamp as create_time, null::timestamp as update_time from information_schema.tables where table_schema = current_schema() and table_type = 'BASE TABLE' and table_name not like 'qrtz\\_%' and table_name not like 'gen\\_%' and table_name not in (select table_name from gen_table)");
        appendDbTableFilters(sql, params, genTable);
        sql.append(" order by table_name");
        return queryPagedSql(sql.toString(), rs -> {
            GenTable table = new GenTable();
            table.setTableName(rs.getString("table_name"));
            table.setTableComment(rs.getString("table_comment"));
            table.setCreateTime(rs.getTimestamp("create_time"));
            table.setUpdateTime(rs.getTimestamp("update_time"));
            return table;
        }, params.toArray());
    }

    public List<GenTable> selectDbTableListByNames(String[] tableNames)
    {
        if (tableNames == null || tableNames.length == 0)
        {
            return java.util.Collections.emptyList();
        }
        String sql = "select table_name, obj_description(format('%I.%I', table_schema, table_name)::regclass, 'pg_class') as table_comment, null::timestamp as create_time, null::timestamp as update_time from information_schema.tables where table_schema = current_schema() and table_type = 'BASE TABLE' and table_name not like 'qrtz\\_%' and table_name not like 'gen\\_%' and table_name in " + placeholders(Arrays.asList(tableNames)) + " order by table_name";
        return querySql(sql, rs -> {
            GenTable table = new GenTable();
            table.setTableName(rs.getString("table_name"));
            table.setTableComment(rs.getString("table_comment"));
            table.setCreateTime(rs.getTimestamp("create_time"));
            table.setUpdateTime(rs.getTimestamp("update_time"));
            return table;
        }, (Object[]) tableNames);
    }

    public List<GenTable> selectGenTableAll()
    {
        return withColumns(listAll());
    }

    public GenTable selectGenTableById(Long id)
    {
        return withColumns(selectById(id));
    }

    public GenTable selectGenTableByName(String tableName)
    {
        return withColumns(findOne(predicates(eq("tableName", tableName)), null));
    }

    public int insertGenTable(GenTable genTable)
    {
        return insert(genTable);
    }

    public int updateGenTable(GenTable genTable)
    {
        return updateById(genTable);
    }

    public int deleteGenTableByIds(Long[] ids)
    {
        return deleteByIds(ids);
    }

    public int createTable(String sql)
    {
        executeSql(sql);
        return 0;
    }

    private List<GenTable> withColumns(List<GenTable> tables)
    {
        for (GenTable table : tables)
        {
            withColumns(table);
        }
        return tables;
    }

    private GenTable withColumns(GenTable table)
    {
        if (table != null && table.getTableId() != null)
        {
            List<GenTableColumn> columns = genTableColumnMapper.selectGenTableColumnListByTableId(table.getTableId());
            table.setColumns(columns);
        }
        return table;
    }

    private List<Predicate> genTablePredicates(GenTable genTable)
    {
        List<Predicate> predicates = predicates();
        if (genTable != null)
        {
            predicates.add(ilike("tableName", genTable.getTableName()));
            predicates.add(ilike("tableComment", genTable.getTableComment()));
            addDateRange(predicates, genTable, "createTime");
        }
        return predicates;
    }

    private void appendDbTableFilters(StringBuilder sql, List<Object> params, GenTable genTable)
    {
        if (genTable == null)
        {
            return;
        }
        if (StringUtils.isNotBlank(genTable.getTableName()))
        {
            sql.append(" and lower(table_name) like lower(?)");
            params.add("%" + genTable.getTableName() + "%");
        }
        if (StringUtils.isNotBlank(genTable.getTableComment()))
        {
            sql.append(" and lower(coalesce(obj_description(format('%I.%I', table_schema, table_name)::regclass, 'pg_class'), '')) like lower(?)");
            params.add("%" + genTable.getTableComment() + "%");
        }
    }
}

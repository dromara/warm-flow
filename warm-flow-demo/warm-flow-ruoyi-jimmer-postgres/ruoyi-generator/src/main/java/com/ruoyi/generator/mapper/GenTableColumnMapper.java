package com.ruoyi.generator.mapper;

import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.GenTableColumnModel;
import org.springframework.stereotype.Repository;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** 业务字段 Jimmer 数据层 */
@Repository
public class GenTableColumnMapper extends JimmerRepositorySupport<GenTableColumn, GenTableColumnModel>
{
    public GenTableColumnMapper()
    {
        super(GenTableColumn.class, GenTableColumnModel.class, "columnId");
    }

    public List<GenTableColumn> selectDbTableColumnsByName(String tableName)
    {
        return jdbc(connection -> {
            DatabaseMetaData metaData = connection.getMetaData();
            List<GenTableColumn> columns = new ArrayList<>();
            try (ResultSet rs = metaData.getColumns(connection.getCatalog(), currentSchema(connection), tableName, null))
            {
                while (rs.next())
                {
                    GenTableColumn column = new GenTableColumn();
                    column.setColumnName(rs.getString("COLUMN_NAME"));
                    column.setColumnComment(rs.getString("REMARKS"));
                    column.setColumnType(columnType(rs));
                    column.setIsRequired(rs.getInt("NULLABLE") == DatabaseMetaData.columnNoNulls ? "1" : "0");
                    column.setSort(rs.getInt("ORDINAL_POSITION"));
                    columns.add(column);
                }
            }
            List<String> primaryKeys = primaryKeys(metaData, connection.getCatalog(), currentSchema(connection), tableName);
            for (GenTableColumn column : columns)
            {
                column.setIsPk(primaryKeys.contains(column.getColumnName()) ? "1" : "0");
                column.setIsIncrement(isIdentity(connection, tableName, column.getColumnName()) ? "1" : "0");
            }
            return columns;
        });
    }

    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId)
    {
        return list(predicates(eq("tableId", tableId)), orders("sort"));
    }

    public int insertGenTableColumn(GenTableColumn genTableColumn)
    {
        return insert(genTableColumn);
    }

    public int updateGenTableColumn(GenTableColumn genTableColumn)
    {
        return updateById(genTableColumn);
    }

    public int deleteGenTableColumns(List<GenTableColumn> genTableColumns)
    {
        if (genTableColumns == null || genTableColumns.isEmpty())
        {
            return 0;
        }
        return deleteByIds(genTableColumns.stream().map(GenTableColumn::getColumnId).toArray(Long[]::new));
    }

    public int deleteGenTableColumnByIds(Long[] ids)
    {
        return deleteWhere(in("tableId", java.util.Arrays.asList(ids)));
    }

    private static String currentSchema(java.sql.Connection connection) throws SQLException
    {
        String schema = connection.getSchema();
        return schema == null ? "public" : schema;
    }

    private static List<String> primaryKeys(DatabaseMetaData metaData, String catalog, String schema, String tableName) throws SQLException
    {
        List<String> names = new ArrayList<>();
        try (ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName))
        {
            while (rs.next())
            {
                names.add(rs.getString("COLUMN_NAME"));
            }
        }
        return names;
    }

    private boolean isIdentity(java.sql.Connection connection, String tableName, String columnName)
    {
        Long total = queryScalar(
            "select count(*) from information_schema.columns where table_schema = current_schema() and table_name = ? and column_name = ? and (is_identity = 'YES' or column_default like 'nextval(%')",
            Long.class, tableName, columnName);
        return total != null && total > 0;
    }

    private static String columnType(ResultSet rs) throws SQLException
    {
        String type = rs.getString("TYPE_NAME").toLowerCase(Locale.ROOT);
        int size = rs.getInt("COLUMN_SIZE");
        int scale = rs.getInt("DECIMAL_DIGITS");
        if ((type.contains("char") || type.equals("varchar")) && size > 0)
        {
            return "varchar(" + size + ")";
        }
        if ((type.equals("numeric") || type.equals("decimal")) && size > 0)
        {
            return scale > 0 ? type + "(" + size + "," + scale + ")" : type + "(" + size + ")";
        }
        if (type.equals("int4"))
        {
            return "integer";
        }
        if (type.equals("int8"))
        {
            return "bigint";
        }
        if (type.equals("bool"))
        {
            return "boolean";
        }
        return type;
    }
}

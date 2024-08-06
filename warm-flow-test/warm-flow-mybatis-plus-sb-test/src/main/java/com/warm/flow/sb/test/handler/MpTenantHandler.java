package com.warm.flow.sb.test.handler;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MpTenantHandler implements TenantLineHandler {

    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @Override
    public Expression getTenantId() {
        // 返回租户ID的表达式，LongValue 是 JSQLParser 中表示 bigint 类型的 class
        return new LongValue(2);
    }

    @Override
    public String getTenantIdColumn() {
        return threadLocal.get();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        fieldList.forEach(field -> {
            // 如果业务和工作流引擎中的租户字段不一致，可以通过这种方式动态切换
            if (field.getColumn().equals("tenant_id") || field.getColumn().equals("tenant_code")) {
                threadLocal.set(field.getColumn());
            }
        });
        // 获取表字段
        return false;
    }

}
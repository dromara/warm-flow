package com.warm.flow.orm.utils;

import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.utils.StringUtils;
import org.apache.ibatis.session.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class CommonUtil {

    private CommonUtil() {
    }

    public static void setDataSourceType(WarmFlow flowConfig, Configuration configuration) {
        String dataSourceType = flowConfig.getDataSourceType();
        // 未配置时尝试获取可用数据库类型
        if (StringUtils.isEmpty(dataSourceType)) {
            DataSource dataSource = configuration.getEnvironment().getDataSource();
            DatabaseMetaData metaData;
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                metaData = connection.getMetaData();
                dataSourceType = metaData.getDatabaseProductName().toLowerCase();
            } catch (Exception e) {
                // 不能因为一个字段的取值, 影响到框架自身运行环境
                // throw new RuntimeException(e);
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    // 不能因为一个字段的取值, 影响到框架自身运行环境
                    // throw new RuntimeException(e);
                }
            }
        }


        // 兜底数据库类型
        if (StringUtils.isEmpty(dataSourceType)) {
            dataSourceType = "mysql";
        }
        flowConfig.setDataSourceType(dataSourceType);
    }
}

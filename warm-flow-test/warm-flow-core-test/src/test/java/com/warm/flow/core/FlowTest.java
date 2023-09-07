package com.warm.flow.core;

import com.warm.flow.core.domain.entity.FlowDefinition;
import com.warm.flow.core.service.impl.DefServiceImpl;
import com.warm.mybatis.core.invoker.MapperInvoker;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FlowTest {

    @Test
    public void configuration() throws IOException {
        Configuration configuration = getConfiguration();

        List<String> mapperList = Arrays.asList("mapper/flow/FlowDefinitionMapper.xml", "mapper/flow/FlowHisTaskMapper.xml"
                , "mapper/flow/FlowInstanceMapper.xml", "mapper/flow/FlowNodeMapper.xml"
                , "mapper/flow/FlowSkipMapper.xml", "mapper/flow/FlowTaskMapper.xml");

        try {
            for (String mapper : mapperList) {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream(mapper),
                        configuration, FlowTest.class.getResource("/") + mapper, configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        MapperInvoker.mapperInvoker = new MapperInvoker(sqlSessionFactory);
        FlowDefinition flowDefinition = new DefServiceImpl().getById(1148442523895730176L);
        System.out.println(flowDefinition);

        // try (SqlSession session = sqlSessionFactory.openSession()) {
        //     FlowDefinitionMapper mapper = session.getMapper(FlowDefinitionMapper.class);
        //     FlowDefinition flowDefinition = mapper.selectById(1148442523895730176L);
        //     System.out.println(flowDefinition);
        // }
    }

    private static Configuration getConfiguration() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername("root");     // 用户名
        dataSource.setPassword("123456"); // 密码
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/hh-vue");// 数据库地址
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        return configuration;
    }
}

package com.monkey.flow.core.config;

import com.monkey.flow.core.handler.DataFillHandlerImpl;
import com.monkey.mybatis.core.handler.DataFillHandlerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Mybatis支持*匹配扫描包
 *
 * @author hh
 */
@Configuration
public class MonkeyMyBatisConfig
{

    @PostConstruct
    public void setFlexConfig()
    {
        DataFillHandlerFactory.set(new DataFillHandlerImpl());
    }
}

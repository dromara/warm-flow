package com.warm.flow.spring.boot.config;

import com.warm.flow.core.handler.DataFillHandlerImpl;
import com.warm.mybatis.core.handler.DataFillHandlerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Mybatis支持*匹配扫描包
 *
 * @author hh
 */
@Configuration
public class WarmMyBatisConfig {

    @PostConstruct
    public void init() {
        DataFillHandlerFactory.set(new DataFillHandlerImpl());
    }
}

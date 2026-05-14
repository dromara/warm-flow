/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.demo.jimmer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Deployable Spring Boot entrypoint for Warm Flow with Jimmer and PostgreSQL.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.dromara.warm.flow", "org.dromara.warm.plugin", "org.dromara.warm.flow.demo.jimmer"})
public class WarmFlowJimmerPostgresDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarmFlowJimmerPostgresDemoApplication.class, args);
    }
}

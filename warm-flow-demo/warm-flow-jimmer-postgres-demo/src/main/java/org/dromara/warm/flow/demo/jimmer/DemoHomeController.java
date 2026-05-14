/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.demo.jimmer;

import org.dromara.warm.flow.core.dto.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DemoHomeController {

    @GetMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("/warm-flow-ui/index.html");
    }

    @GetMapping("/demo/health")
    public ApiResult<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("ui", "/warm-flow-ui/index.html");
        data.put("config", "/warm-flow-ui/config");
        data.put("demo", "/demo/workflow/e2e");
        return ApiResult.ok(data);
    }
}

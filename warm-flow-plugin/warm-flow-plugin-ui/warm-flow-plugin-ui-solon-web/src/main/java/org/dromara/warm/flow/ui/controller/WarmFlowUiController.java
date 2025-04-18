/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.ui.controller;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.ApiResult;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.annotation.Tran;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设计器Controller 匿名访问
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/primary/designerIntroduced.htm">文档地址</a>
 */
@Controller
@Mapping("/warm-flow-ui")
public class WarmFlowUiController {

    /**
     * 保存流程xml字符串
     * @return ApiResult<String>
     * @throws Exception 异常
     */
    @Get
    @Mapping("/token-name")
    @Tran
    public ApiResult<List<String>> tokenName() {
        String tokenName = FlowEngine.getFlowConfig().getTokenName();
        if (StringUtils.isEmpty(tokenName)) {
            return ApiResult.fail("未配置tokenName");
        }
        String[] tokenNames = tokenName.split(",");
        List<String> tokenNameList = Arrays.stream(tokenNames).filter(StringUtils::isNotEmpty)
                .map(String::trim).collect(Collectors.toList());
        return ApiResult.ok(tokenNameList);
    }

}

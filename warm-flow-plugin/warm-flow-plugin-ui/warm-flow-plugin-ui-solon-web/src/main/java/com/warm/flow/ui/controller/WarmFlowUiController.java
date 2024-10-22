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
package com.warm.flow.ui.controller;

import com.warm.flow.core.dto.ApiResult;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.utils.ExceptionUtil;
import com.warm.flow.ui.dto.DefDto;
import com.warm.flow.ui.dto.HandlerQuery;
import com.warm.flow.ui.service.HandlerSelectService;
import com.warm.flow.ui.vo.HandlerSelectVo;
import org.noear.solon.annotation.*;
import org.noear.solon.data.annotation.Tran;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 设计器Controller
 *
 * @author warm
 */
@Controller
@Mapping("/warm-flow-ui")
public class WarmFlowUiController {

    private static final Logger log = LoggerFactory.getLogger(WarmFlowUiController.class);

    @Inject
    private DefService defService;

    /**
     * 保存流程xml字符串
     * @param defDto 流程定义dto
     * @return ApiResult<Void>
     * @throws Exception 异常
     */
    @Post
    @Mapping("/save-xml")
    @Tran
    public ApiResult<Void> saveXml(DefDto defDto) throws Exception {
        defService.saveXml(defDto.getId(), defDto.getXmlString());
        return ApiResult.ok();
    }

    /**
     * 获取流程xml字符串
     * @param id 流程定义id
     * @return ApiResult<String>
     */
    @Get
    @Mapping("/xml-string/{id}")
    public ApiResult<String> xmlString(Long id) {
        try {
            return ApiResult.ok(defService.xmlString(id));
        } catch (Exception e) {
            log.error("获取流程xml字符串", e);
            throw new FlowException(ExceptionUtil.handleMsg("获取流程xml字符串失败", e));
        }
    }

    /**
     * 办理人权限设置列表tabs页签
     * @return List<SelectGroup>
     */
    @Get
    @Mapping("/handler-type")
    public ApiResult<List<String>> handlerType() {
        try {
            // 需要业务系统实现该接口
            HandlerSelectService handlerSelectService = FrameInvoker.getBean(HandlerSelectService.class);
            if (handlerSelectService == null) {
                return ApiResult.ok(Collections.emptyList());
            }
            List<String> handlerType = handlerSelectService.getHandlerType();
            return ApiResult.ok(handlerType);
        } catch (Exception e) {
            log.error("办理人权限设置列表tabs页签异常", e);
            throw new FlowException(ExceptionUtil.handleMsg("办理人权限设置列表tabs页签失败", e));
        }
    }

    /**
     * 办理人权限设置列表结果
     * @return List<SelectGroup>
     */
    @Get
    @Mapping("/handler-result")
    public ApiResult<HandlerSelectVo> handlerResult(HandlerQuery query) {
        try {
            // 需要业务系统实现该接口
            HandlerSelectService handlerSelectService = FrameInvoker.getBean(HandlerSelectService.class);
            if (handlerSelectService == null) {
                return ApiResult.ok(new HandlerSelectVo());
            }
           HandlerSelectVo handlerSelectVo = handlerSelectService.getHandlerSelect(query);
            return ApiResult.ok(handlerSelectVo);
        } catch (Exception e) {
            log.error("办理人权限设置列表结果异常", e);
            throw new FlowException(ExceptionUtil.handleMsg("办理人权限设置列表结果失败", e));
        }
    }
}

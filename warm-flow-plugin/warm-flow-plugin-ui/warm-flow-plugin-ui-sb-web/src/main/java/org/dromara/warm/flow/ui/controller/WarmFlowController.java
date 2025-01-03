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

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.dto.ApiResult;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.utils.ExceptionUtil;
import org.dromara.warm.flow.ui.dto.DefDto;
import org.dromara.warm.flow.ui.dto.HandlerQuery;
import org.dromara.warm.flow.ui.service.HandlerSelectService;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 设计器Controller 可选择是否放行，放行可与业务系统共享权限，主要是用来访问业务系统数据
 *
 * @author warm
 */
@RestController
@RequestMapping("/warm-flow")
public class WarmFlowController {

    private static final Logger log = LoggerFactory.getLogger(WarmFlowController.class);

    /**
     * 保存流程xml字符串
     * @param defDto 流程定义dto
     * @return ApiResult<Void>
     * @throws Exception 异常
     */
    @PostMapping("/save-xml")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> saveXml(@RequestBody DefDto defDto) throws Exception {
        FlowFactory.defService().saveXml(defDto.getId(), defDto.getXmlString());
        return ApiResult.ok();
    }

    /**
     * 获取流程xml字符串
     * @param id 流程定义id
     * @return ApiResult<String>
     */
    @GetMapping("/xml-string/{id}")
    public ApiResult<String> xmlString(@PathVariable("id") Long id) {
        try {
            return ApiResult.ok(FlowFactory.defService().xmlString(id));
        } catch (Exception e) {
            log.error("获取流程xml字符串", e);
            throw new FlowException(ExceptionUtil.handleMsg("获取流程xml字符串失败", e));
        }
    }

    /**
     * 保存流程json字符串
     *
     * @param defDto 流程定义dto
     * @return ApiResult<Void>
     * @throws Exception 异常
     * @author xiarg
     * @since 2024/10/29 16:31
     */
    @PostMapping("/save-json")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> saveJson(@RequestBody DefDto defDto) throws Exception {
        FlowFactory.defService().saveJson(defDto.getId(), defDto.getJsonString());
        return ApiResult.ok();
    }

    /**
     * 获取流程xml字符串
     *
     * @param id 流程定义id
     * @return ApiResult<String>
     * @author xiarg
     * @since 2024/10/29 16:31
     */
    @GetMapping("/json-string/{id}")
    public ApiResult<String> jsonString(@PathVariable("id") Long id) {
        try {
            return ApiResult.ok(FlowFactory.defService().jsonString(id));
        } catch (Exception e) {
            log.error("获取流程json字符串", e);
            throw new FlowException(ExceptionUtil.handleMsg("获取流程json字符串失败", e));
        }
    }

    /**
     * 办理人权限设置列表tabs页签
     * @return List<String>
     */
    @GetMapping("/handler-type")
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
     * @return HandlerSelectVo
     */
    @GetMapping("/handler-result")
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
    /**
     * 获取所有的前置节点集合
     * @return List<Node>
     */
    @GetMapping("/previous-node-list/{definitionId}/{nowNodeCode}")
    public ApiResult<List<Node>> previousNodeList(@PathVariable("definitionId") Long definitionId
            , @PathVariable("nowNodeCode") String nowNodeCode) {
        try {
            return ApiResult.ok(FlowFactory.nodeService().previousNodeList(definitionId, nowNodeCode));
        } catch (Exception e) {
            log.error("获取所有的前置节点集合异常", e);
            throw new FlowException(ExceptionUtil.handleMsg("获取所有的前置节点集合失败", e));
        }
    }
}

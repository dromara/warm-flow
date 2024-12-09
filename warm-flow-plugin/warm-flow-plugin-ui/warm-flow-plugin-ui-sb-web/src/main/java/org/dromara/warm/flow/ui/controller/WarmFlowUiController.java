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
import org.dromara.warm.flow.core.dto.FlowPage;
import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.utils.ExceptionUtil;
import org.dromara.warm.flow.core.utils.HttpStatus;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.ui.dto.DefDto;
import org.dromara.warm.flow.ui.dto.FormDto;
import org.dromara.warm.flow.ui.dto.FormQuery;
import org.dromara.warm.flow.ui.dto.HandlerQuery;
import org.dromara.warm.flow.ui.service.HandlerSelectService;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设计器Controller
 *
 * @author warm
 */
@RestController
@RequestMapping("/warm-flow-ui")
public class WarmFlowUiController {

    private static final Logger log = LoggerFactory.getLogger(WarmFlowUiController.class);

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
     * 办理人权限设置列表tabs页签
     * @return List<SelectGroup>
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
     * @return List<SelectGroup>
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
     * 已发布表单列表
     * @return FlowPage<FormDto>
     */
    @GetMapping("/publish-form")
    public ApiResult<FlowPage<FormDto>> publishForm(FormQuery formQuery) {
        try {
            // 该接口不需要业务系统实现
            Page<Form> formPage = FlowFactory.formService().publishPage(formQuery.getFormName(), formQuery.getPageNum(), formQuery.getPageSize());
            FlowPage<FormDto> data = new FlowPage<FormDto>().setRows(formPage.getList().stream().map((form -> {
                        FormDto formDto = new FormDto();
                        formDto.setId(form.getId());
                        formDto.setFormName(form.getFormName());
                        formDto.setFormCode(form.getFormCode());
                        formDto.setVersion(form.getVersion());
                        return formDto;
                    })).collect(Collectors.toList()))
                    .setCode(HttpStatus.SUCCESS)
                    .setMsg("查询成功")
                    .setTotal(formPage.getTotal());
            return ApiResult.ok(data);
        } catch (Exception e) {
            log.error("已发布表单列表异常", e);
            throw new FlowException(ExceptionUtil.handleMsg("已发布表单列表异常", e));
        }
    }


    @GetMapping("/form-action")
    public ApiResult<Map> formAction(FormQuery formQuery) {
        // loadDict =>
        // loadXx =>
        return ApiResult.ok();
    }
}

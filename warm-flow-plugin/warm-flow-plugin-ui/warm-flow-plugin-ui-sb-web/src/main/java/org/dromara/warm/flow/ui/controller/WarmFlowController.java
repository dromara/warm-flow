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
import org.dromara.warm.flow.core.dto.*;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.utils.ExceptionUtil;
import org.dromara.warm.flow.core.utils.HttpStatus;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.core.vo.DefVo;
import org.dromara.warm.flow.ui.dto.DefDto;
import org.dromara.warm.flow.ui.dto.FormDto;
import org.dromara.warm.flow.ui.dto.FormQuery;
import org.dromara.warm.flow.ui.dto.HandlerQuery;
import org.dromara.warm.flow.ui.service.HandlerDictService;
import org.dromara.warm.flow.ui.service.HandlerSelectService;
import org.dromara.warm.flow.ui.vo.Dict;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
     * @param defVo 流程数据集合
     * @return ApiResult<Void>
     * @throws Exception 异常
     * @author xiarg
     * @since 2024/10/29 16:31
     */
    @PostMapping("/save-json")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> saveJson(@RequestBody DefVo defVo) throws Exception {
        Definition definition = new DefVo().copyDef(defVo);
        FlowCombine combine = new FlowCombine();
        combine.setDefinition(definition);
        combine.setAllNodes(definition.getNodeList());
        List<Skip> skipList = Optional.of(definition)
                .map(Definition::getNodeList)
                .orElse(Collections.emptyList())
                .stream()
                .map(Node::getSkipList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        combine.setAllSkips(skipList);
        FlowFactory.defService().saveJson(combine);
        return ApiResult.ok();
    }

    /**
     * 获取流程定义全部数据(包含节点和跳转)
     *
     * @param id 流程定义id
     * @return ApiResult<DefVo>
     * @author xiarg
     * @since 2024/10/29 16:31
     */
    @GetMapping("/query-def/{id}")
    public ApiResult<DefVo> queryDef(@PathVariable("id") Long id) {
        try {
            return ApiResult.ok(FlowFactory.defService().queryDesign(id));
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
     * 办理人选择项
     * @return List<Dict>
     */
    @GetMapping("/handler-dict")
    public ApiResult<List<Dict>> handlerDict() {
        try {
            // 需要业务系统实现该接口
            HandlerDictService handlerDictService = FrameInvoker.getBean(HandlerDictService.class);
            if (handlerDictService == null) {
                List<Dict> dictList = new ArrayList<>();
                Dict dict = new Dict();
                dict.setLabel("默认表达式");
                dict.setValue("${handler}");
                Dict dict1 = new Dict();
                dict1.setLabel("spel表达式");
                dict1.setValue("#{@user.evalVar(#handler)}");
                Dict dict2 = new Dict();
                dict2.setLabel("其他");
                dict2.setValue("");
                dictList.add(dict);
                dictList.add(dict1);
                dictList.add(dict2);

                return ApiResult.ok(dictList);
            }
            return ApiResult.ok(handlerDictService.getHandlerDict());
        } catch (Exception e) {
            log.error("办理人权限设置列表结果异常", e);
            throw new FlowException(ExceptionUtil.handleMsg("办理人权限设置列表结果失败", e));
        }
    }

    /**
     * 已发布表单列表 该接口不需要业务系统实现
     * @return FlowPage<FormDto>
     */
    @GetMapping("/published-form")
    public ApiResult<FlowPage<FormDto>> publishedForm(FormQuery formQuery) {
        try {
            Page<Form> formPage = FlowFactory.formService().publishedPage(formQuery.getFormName(), formQuery.getPageNum(), formQuery.getPageSize());
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

    /**
     * 读取表单内容
     * @param id
     * @return
     */
    @GetMapping("/form-content/{id}")
    public ApiResult<String> getFormContent(@PathVariable("id") Long id) {
        try {return ApiResult.ok(FlowFactory.formService().getById(id).getFormContent());
        } catch (Exception e) {
            log.error("获取表单内容字符串", e);
            throw new FlowException(ExceptionUtil.handleMsg("获取表单内容字符串失败", e));
        }
    }

    /**
     * 保存表单内容,该接口不需要系统实现
     * @param formDto
     * @return
     */
    @PostMapping("/form-content")
    public ApiResult<Void> saveFormContent(@RequestBody FormDto formDto) {
        FlowFactory.formService().saveContent(formDto.getId(), formDto.getFormContent());
        return ApiResult.ok();
    }


    /**
     * 根据任务id获取待办任务表单及数据
     *
     * @param taskId 当前任务id
     * @return {@link ApiResult<FlowForm>}
     * @author liangli
     * @date 2024/8/21 17:08
     **/
    @GetMapping(value = "/execute/load/{taskId}")
    public ApiResult<FlowForm> load(@PathVariable("taskId") Long taskId) {
        FlowParams flowParams = FlowParams.build();

        return ApiResult.ok(FlowFactory.taskService().load(taskId, flowParams));
    }

    /**
     * 根据任务id获取已办任务表单及数据
     *
     * @param hisTaskId
     * @return
     */
    @GetMapping(value = "/execute/hisLoad/{taskId}")
    public ApiResult<FlowForm> hisLoad(@PathVariable("taskId") Long hisTaskId) {
        FlowParams flowParams = FlowParams.build();

        return ApiResult.ok(FlowFactory.taskService().hisLoad(hisTaskId, flowParams));
    }

    /**
     * 通用表单流程审批接口
     *
     * @param formData
     * @param taskId
     * @param skipType
     * @param message
     * @param nodeCode
     * @param flowStatus
     * @return
     */
    @Transactional
    @PostMapping(value = "/execute/handle/{taskId}")
    public ApiResult<Instance> handle(@RequestBody Map<String, Object> formData, @PathVariable("taskId") Long taskId, String skipType, String message
            , String nodeCode, String flowStatus) {
        FlowParams flowParams = FlowParams.build()
                .skipType(skipType)
                .nodeCode(nodeCode)
                .message(message);

        flowParams.formData(formData);

        return ApiResult.ok(FlowFactory.taskService().skip(taskId, flowParams));
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

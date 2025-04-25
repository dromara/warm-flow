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
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.FlowDto;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.utils.ExceptionUtil;
import org.dromara.warm.flow.ui.dto.HandlerFeedBackDto;
import org.dromara.warm.flow.ui.dto.HandlerQuery;
import org.dromara.warm.flow.ui.service.HandlerDictService;
import org.dromara.warm.flow.ui.service.HandlerSelectService;
import org.dromara.warm.flow.ui.service.NodeExtService;
import org.dromara.warm.flow.ui.vo.Dict;
import org.dromara.warm.flow.ui.vo.HandlerFeedBackVo;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;
import org.dromara.warm.flow.ui.vo.NodeExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 设计器Controller 可选择是否放行，放行可与业务系统共享权限，主要是用来访问业务系统数据
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/primary/designerIntroduced.htm">文档地址</a>
 */
@RestController
@RequestMapping("/warm-flow")
public class WarmFlowController {

    private static final Logger log = LoggerFactory.getLogger(WarmFlowController.class);

    /**
     * 保存流程json字符串
     *
     * @param defJson 流程数据集合
     * @return ApiResult<Void>
     * @throws Exception 异常
     * @author xiarg
     * @since 2024/10/29 16:31
     */
    @PostMapping("/save-json")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> saveJson(@RequestBody DefJson defJson) throws Exception {
        FlowEngine.defService().saveDef(defJson);
        return ApiResult.ok();
    }

    /**
     * 获取流程定义数据(包含节点和跳转)
     *
     * @param id 流程定义id
     * @return ApiResult<DefVo>
     * @author xiarg
     * @since 2024/10/29 16:31
     */
    @GetMapping("/query-def/{id}")
    public ApiResult<DefJson> queryDef(@PathVariable("id") Long id) {
        try {
            return ApiResult.ok(FlowEngine.defService().queryDesign(id));
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
     * 办理人权限名称回显
     * @return HandlerSelectVo
     */
    @GetMapping("/handler-feedback")
    public ApiResult<List<HandlerFeedBackVo>> handlerFeedback(HandlerFeedBackDto handlerFeedBackDto) {
        try {
            // 需要业务系统实现该接口
            HandlerSelectService handlerSelectService = FrameInvoker.getBean(HandlerSelectService.class);
            if (handlerSelectService == null) {
                return ApiResult.ok(new ArrayList<>());
            }
            List<HandlerFeedBackVo> handlerFeedBackVos = handlerSelectService.handlerFeedback(handlerFeedBackDto.getStorageIds());
            return ApiResult.ok(handlerFeedBackVos);
        } catch (Exception e) {
            log.error("办理人权限名称回显", e);
            throw new FlowException(ExceptionUtil.handleMsg("办理人权限名称回显", e));
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
     */
    @GetMapping("/published-form")
    public ApiResult<List<Form>> publishedForm() {
        try {
            return ApiResult.ok(FlowEngine.formService().list(FlowEngine.newForm().setIsPublish(1)));
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
        try {return ApiResult.ok(FlowEngine.formService().getById(id).getFormContent());
        } catch (Exception e) {
            log.error("获取表单内容字符串", e);
            throw new FlowException(ExceptionUtil.handleMsg("获取表单内容字符串失败", e));
        }
    }

    /**
     * 保存表单内容,该接口不需要系统实现
     * @param flowDto
     * @return
     */
    @PostMapping("/form-content")
    public ApiResult<Void> saveFormContent(@RequestBody FlowDto flowDto) {
        FlowEngine.formService().saveContent(flowDto.getId(), flowDto.getFormContent());
        return ApiResult.ok();
    }


    /**
     * 根据任务id获取待办任务表单及数据
     *
     * @param taskId 当前任务id
     * @return {@link ApiResult< FlowDto >}
     * @author liangli
     * @date 2024/8/21 17:08
     **/
    @GetMapping(value = "/execute/load/{taskId}")
    public ApiResult<FlowDto> load(@PathVariable("taskId") Long taskId) {
        FlowParams flowParams = FlowParams.build();

        return ApiResult.ok(FlowEngine.taskService().load(taskId, flowParams));
    }

    /**
     * 根据任务id获取已办任务表单及数据
     *
     * @param hisTaskId
     * @return
     */
    @GetMapping(value = "/execute/hisLoad/{taskId}")
    public ApiResult<FlowDto> hisLoad(@PathVariable("taskId") Long hisTaskId) {
        FlowParams flowParams = FlowParams.build();

        return ApiResult.ok(FlowEngine.taskService().hisLoad(hisTaskId, flowParams));
    }

    /**
     * 通用表单流程审批接口
     *
     * @param formData
     * @param taskId
     * @param skipType
     * @param message
     * @param nodeCode
     * @return
     */
    @Transactional
    @PostMapping(value = "/execute/handle")
    public ApiResult<Instance> handle(@RequestBody Map<String, Object> formData, @RequestParam("taskId") Long taskId
            , @RequestParam("skipType") String skipType,  @RequestParam("message") String message
            , @RequestParam(value = "nodeCode", required = false) String nodeCode) {
        FlowParams flowParams = FlowParams.build()
                .skipType(skipType)
                .nodeCode(nodeCode)
                .message(message);

        flowParams.formData(formData);

        return ApiResult.ok(FlowEngine.taskService().skip(taskId, flowParams));
    }

    /**
     * 获取节点扩展属性
     * @return List<NodeExt>
     */
    @GetMapping("/node-ext")
    public ApiResult<List<NodeExt>> nodeExt() {
        try {
            // 需要业务系统实现该接口
            NodeExtService nodeExtService = FrameInvoker.getBean(NodeExtService.class);
            if (nodeExtService == null) {
                return ApiResult.ok(Collections.emptyList());
            }
            List<NodeExt> nodeExts = nodeExtService.getNodeExt();
            return ApiResult.ok(nodeExts);
        } catch (Exception e) {
            log.error("获取节点扩展属性", e);
            throw new FlowException(ExceptionUtil.handleMsg("获取节点扩展属性失败", e));
        }
    }

}

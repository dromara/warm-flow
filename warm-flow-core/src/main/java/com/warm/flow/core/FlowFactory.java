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
package com.warm.flow.core;

import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.entity.*;
import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.handler.TenantHandler;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.json.JsonConvert;
import com.warm.flow.core.service.*;
import com.warm.flow.core.utils.ClassUtil;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 流程定义工程
 *
 * @author warm
 */
public class FlowFactory {

    private static final Logger log = LoggerFactory.getLogger(FlowFactory.class);

    private static DefService defService = null;
    private static HisTaskService hisTaskService = null;
    private static InsService insService = null;

    private static NodeService nodeService = null;
    private static SkipService skipService = null;
    private static TaskService taskService = null;
    private static UserService userService = null;
    private static FormService formService = null;


    private static Supplier<Definition> defSupplier;
    private static Supplier<HisTask> hisTaskSupplier;
    private static Supplier<Instance> insSupplier;
    private static Supplier<Node> nodeSupplier;
    private static Supplier<Skip> skipSupplier;
    private static Supplier<Task> taskSupplier;
    private static Supplier<User> userSupplier;
    private static Supplier<Form> formSupplier;

    private static WarmFlow flowConfig;

    private static DataFillHandler dataFillHandler;

    private static boolean tenantHandlerFlag;

    private static TenantHandler tenantHandler;

    public static JsonConvert jsonConvert;

    public static DefService defService() {
        if (ObjectUtil.isNotNull(defService)) {
            return defService;
        }
        return defService = FrameInvoker.getBean(DefService.class);
    }

    public static HisTaskService hisTaskService() {
        if (ObjectUtil.isNotNull(hisTaskService)) {
            return hisTaskService;
        }
        return hisTaskService = FrameInvoker.getBean(HisTaskService.class);
    }

    public static InsService insService() {
        if (ObjectUtil.isNotNull(insService)) {
            return insService;
        }
        return insService = FrameInvoker.getBean(InsService.class);
    }

    public static NodeService nodeService() {
        if (ObjectUtil.isNotNull(nodeService)) {
            return nodeService;
        }
        return nodeService = FrameInvoker.getBean(NodeService.class);
    }

    public static SkipService skipService() {
        if (ObjectUtil.isNotNull(skipService)) {
            return skipService;
        }
        return skipService = FrameInvoker.getBean(SkipService.class);
    }

    public static TaskService taskService() {
        if (ObjectUtil.isNotNull(taskService)) {
            return taskService;
        }
        return taskService = FrameInvoker.getBean(TaskService.class);
    }

    public static UserService userService() {
        if (ObjectUtil.isNotNull(userService)) {
            return userService;
        }
        return userService = FrameInvoker.getBean(UserService.class);
    }

    public static FormService formService() {
        if (ObjectUtil.isNotNull(userService)) {
            return formService;
        }
        return formService = FrameInvoker.getBean(FormService.class);
    }

    public static void setNewDef(Supplier<Definition> supplier) {
        FlowFactory.defSupplier = supplier;
    }

    public static Definition newDef() {
        return defSupplier.get();
    }

    public static void setNewHisTask(Supplier<HisTask> supplier) {
        FlowFactory.hisTaskSupplier = supplier;
    }

    public static HisTask newHisTask() {
        return hisTaskSupplier.get();
    }

    public static void setNewIns(Supplier<Instance> supplier) {
        FlowFactory.insSupplier = supplier;
    }

    public static Instance newIns() {
        return insSupplier.get();
    }

    public static void setNewNode(Supplier<Node> supplier) {
        FlowFactory.nodeSupplier = supplier;
    }

    public static Node newNode() {
        return nodeSupplier.get();
    }

    public static void setNewSkip(Supplier<Skip> supplier) {
        FlowFactory.skipSupplier = supplier;
    }

    public static Skip newSkip() {
        return skipSupplier.get();
    }

    public static void setNewTask(Supplier<Task> supplier) {
        FlowFactory.taskSupplier = supplier;
    }

    public static Task newTask() {
        return taskSupplier.get();
    }

    public static void setNewUser(Supplier<User> supplier) {
        FlowFactory.userSupplier = supplier;
    }

    public static User newUser() {
        return userSupplier.get();
    }

    public static void setNewForm(Supplier<Form> supplier) {
        FlowFactory.formSupplier = supplier;
    }

    public static Form newForm() {
        return formSupplier.get();
    }

    public static WarmFlow getFlowConfig() {
        return FlowFactory.flowConfig;
    }

    public static void setFlowConfig(WarmFlow flowConfig) {
        FlowFactory.flowConfig = flowConfig;
    }

    public static boolean isLogicDelete() {
        return FlowFactory.flowConfig.isLogicDelete();
    }

    /**
     * 获取填充类
     */
    public static DataFillHandler dataFillHandler() {
        if (ObjectUtil.isNotNull(FlowFactory.dataFillHandler)) {
            return FlowFactory.dataFillHandler;
        }
        DataFillHandler o = null;
        try {
            String dataFillHandlerPath = flowConfig.getDataFillHandlerPath();
            if (!StringUtils.isEmpty(dataFillHandlerPath)) {
                Class<?> clazz = ClassUtil.getClazz(dataFillHandlerPath);
                if (clazz != null) {
                    return FlowFactory.dataFillHandler = o = (DataFillHandler) clazz.newInstance();
                }
            }
            o = FrameInvoker.getBean(DataFillHandler.class);
        } catch (Exception ignored) {
        }
        if (ObjectUtil.isNull(o)) {
            return FlowFactory.dataFillHandler = new DataFillHandler() {};
        }
        return FlowFactory.dataFillHandler = o;
    }

    /**
     * 获取租户数据
     */
    public static TenantHandler tenantHandler() {
        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler) || tenantHandlerFlag) {
            return FlowFactory.tenantHandler;
        }
        TenantHandler o = null;
        try {
            String tenantHandlerPath = flowConfig.getTenantHandlerPath();
            if (!StringUtils.isEmpty(tenantHandlerPath)) {
                Class<?> clazz = ClassUtil.getClazz(tenantHandlerPath);
                if (clazz != null) {
                    return FlowFactory.tenantHandler = o = (TenantHandler) clazz.newInstance();
                }
            }
            o = FrameInvoker.getBean(TenantHandler.class);
        } catch (Exception ignored) {
        }
        tenantHandlerFlag = true;
        return FlowFactory.tenantHandler = o;
    }

    /**
     * 获取租户数据
     */
    public static void jsonConvert(JsonConvert jsonConvert) {
        FlowFactory.jsonConvert = jsonConvert;
    }

    /**
     * 获取数据库类型
     */
    public static String dataSourceType() {
        return flowConfig.getDataSourceType();
    }
}

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
package org.dromara.warm.flow.core;

import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.handler.DataFillHandler;
import org.dromara.warm.flow.core.handler.PermissionHandler;
import org.dromara.warm.flow.core.handler.TenantHandler;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.json.JsonConvert;
import org.dromara.warm.flow.core.listener.GlobalListener;
import org.dromara.warm.flow.core.service.*;
import org.dromara.warm.flow.core.utils.ClassUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

/**
 * 流程引擎
 *
 * @author warm
 */
public class FlowEngine {

    private static final DefService defService = null;
    private static final NodeService nodeService = null;
    private static final SkipService skipService = null;
    private static final InsService insService = null;
    private static final TaskService taskService = null;
    private static final HisTaskService hisTaskService = null;
    private static final UserService userService = null;
    private static final FormService formService = null;
    private static final ChartService chartService = null;

    private static Supplier<Definition> defSupplier;
    private static Supplier<Node> nodeSupplier;
    private static Supplier<Skip> skipSupplier;
    private static Supplier<Instance> insSupplier;
    private static Supplier<Task> taskSupplier;
    private static Supplier<HisTask> hisTaskSupplier;
    private static Supplier<User> userSupplier;
    private static Supplier<Form> formSupplier;

    private static WarmFlow flowConfig;

    private static DataFillHandler dataFillHandler;

    private static TenantHandler tenantHandler;

    private static PermissionHandler permissionHandler;

    private static GlobalListener globalListener;

    public static JsonConvert jsonConvert;

    public static DefService defService() {
        return getObj(defService, DefService.class);
    }

    public static NodeService nodeService() {
        return getObj(nodeService, NodeService.class);
    }

    public static SkipService skipService() {
        return getObj(skipService, SkipService.class);
    }

    public static InsService insService() {
        return getObj(insService, InsService.class);
    }

    public static TaskService taskService() {
        return getObj(taskService, TaskService.class);
    }

    public static HisTaskService hisTaskService() {
        return getObj(hisTaskService, HisTaskService.class);
    }

    public static UserService userService() {
        return getObj(userService, UserService.class);
    }

    public static FormService formService() {
        return getObj(formService, FormService.class);
    }

    public static ChartService chartService() {
        return getObj(chartService, ChartService.class);
    }

    public static void setNewDef(Supplier<Definition> supplier) {
        FlowEngine.defSupplier = supplier;
    }

    public static Definition newDef() {
        return defSupplier.get();
    }

    public static void setNewNode(Supplier<Node> supplier) {
        FlowEngine.nodeSupplier = supplier;
    }

    public static Node newNode() {
        return nodeSupplier.get();
    }

    public static void setNewSkip(Supplier<Skip> supplier) {
        FlowEngine.skipSupplier = supplier;
    }

    public static Skip newSkip() {
        return skipSupplier.get();
    }

    public static void setNewIns(Supplier<Instance> supplier) {
        FlowEngine.insSupplier = supplier;
    }

    public static Instance newIns() {
        return insSupplier.get();
    }

    public static void setNewTask(Supplier<Task> supplier) {
        FlowEngine.taskSupplier = supplier;
    }

    public static Task newTask() {
        return taskSupplier.get();
    }

    public static void setNewHisTask(Supplier<HisTask> supplier) {
        FlowEngine.hisTaskSupplier = supplier;
    }

    public static HisTask newHisTask() {
        return hisTaskSupplier.get();
    }

    public static void setNewUser(Supplier<User> supplier) {
        FlowEngine.userSupplier = supplier;
    }

    public static User newUser() {
        return userSupplier.get();
    }

    public static void setNewForm(Supplier<Form> supplier) {
        FlowEngine.formSupplier = supplier;
    }

    public static Form newForm() {
        return formSupplier.get();
    }

    public static WarmFlow getFlowConfig() {
        return FlowEngine.flowConfig;
    }

    public static void setFlowConfig(WarmFlow flowConfig) {
        FlowEngine.flowConfig = flowConfig;
    }

    public static void initDataFillHandler(String handlerPath) {
        dataFillHandler = initBean(DataFillHandler.class, handlerPath, () -> new DataFillHandler(){});;
    }

    public static void initTenantHandler(String handlerPath) {
        tenantHandler = initBean(TenantHandler.class, handlerPath, null);
    }

    public static void initPermissionHandler() {
        permissionHandler = initBean(PermissionHandler.class, null, null);
    }

     public static void initGlobalListener() {
        globalListener = initBean(GlobalListener.class, null, null);
    }

    /**
     * 获取填充类
     */
    public static DataFillHandler dataFillHandler() {
        return dataFillHandler;
    }

    /**
     * 获取填充类
     */
    public static PermissionHandler permissionHandler() {
        return permissionHandler;
    }

    /**
     * 获取租户数据
     */
    public static TenantHandler tenantHandler() {
        return tenantHandler;
    }

    /**
     * 获取全局监听器
     */
    public static GlobalListener globalListener() {
        return globalListener;
    }

    /**
     * 获取数据库类型
     */
    public static String dataSourceType() {
        return flowConfig.getDataSourceType();
    }

    public static <T> T getObj(T t, Class<T> tClass) {
        if (ObjectUtil.isNotNull(t)) {
            return t;
        }
        t = FrameInvoker.getBean(tClass);
        return t;
    }

    /**
     * 初始化bean，先从yml配置获取bean的全包名路径，否则从spring容器获取bean，如果都没有，则通过supplier获取bean
     * @param tClazz bean的class类型
     * @param beanPath bean全包名路径
     * @param supplier 获取bean的lambda
     * @return bean
     * @param <T> bean类型
     */
    private static <T> T initBean(Class<T> tClazz, String beanPath, Supplier<T> supplier) {
        T hander = null;
        try {
            if (!StringUtils.isEmpty(beanPath)) {
                Class<?> clazz = ClassUtil.getClazz(beanPath);
                if (clazz != null && tClazz.isAssignableFrom(clazz)) {
                    Constructor<?> constructor = clazz.getConstructor();
                    hander = tClazz.cast(constructor.newInstance());
                }
            }
        } catch (Exception ignored) {
        }
        if (hander == null) {
            hander = FrameInvoker.getBean(tClazz);
        }
        if (hander == null && supplier != null) {
            hander = supplier.get();
        }
        return hander;
    }

}

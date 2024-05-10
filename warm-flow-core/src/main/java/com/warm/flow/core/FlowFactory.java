package com.warm.flow.core;

import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.entity.*;
import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.handler.DefaultDataFillHandler;
import com.warm.flow.core.handler.TenantHandler;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.service.*;
import com.warm.flow.core.utils.ClassUtil;
import com.warm.tools.utils.ObjectUtil;
import org.noear.snack.core.utils.StringUtil;
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


    private static Supplier<Definition> defSupplier;
    private static Supplier<HisTask> hisTaskSupplier;
    private static Supplier<Instance> insSupplier;
    private static Supplier<Node> nodeSupplier;
    private static Supplier<Skip> skipSupplier;
    private static Supplier<Task> taskSupplier;
    private static Supplier<User> userSupplier;

    private static WarmFlow flowConfig;

    private static DataFillHandler dataFillHandler;

    private static boolean tenantHandlerFlag;

    private static TenantHandler tenantHandler;

    public static void initFlowService(DefService definitionService, HisTaskService hisTaskService, InsService instanceService
            , NodeService nodeService, SkipService skipService, TaskService taskService, UserService userService) {
        FlowFactory.setDefService(definitionService);
        FlowFactory.setHisTaskService(hisTaskService);
        FlowFactory.setInsService(instanceService);
        FlowFactory.setNodeService(nodeService);
        FlowFactory.setSkipService(skipService);
        FlowFactory.setTaskService(taskService);
        FlowFactory.setUserService(userService);
    }

    public static void setDefService(DefService defService) {
        FlowFactory.defService = defService;
    }

    public static void setHisTaskService(HisTaskService hisTaskService) {
        FlowFactory.hisTaskService = hisTaskService;
    }

    public static void setInsService(InsService insService) {
        FlowFactory.insService = insService;
    }

    public static void setNodeService(NodeService nodeService) {
        FlowFactory.nodeService = nodeService;
    }

    public static void setSkipService(SkipService skipService) {
        FlowFactory.skipService = skipService;
    }

    public static void setTaskService(TaskService taskService) {
        FlowFactory.taskService = taskService;
    }

    public static void setUserService(UserService userService) {
        FlowFactory.userService = userService;
    }

    public static DefService defService() {
        return defService;
    }

    public static HisTaskService hisTaskService() {
        return hisTaskService;
    }

    public static InsService insService() {
        return insService;
    }

    public static NodeService nodeService() {
        return nodeService;
    }

    public static SkipService skipService() {
        return skipService;
    }

    public static TaskService taskService() {
        return taskService;
    }

    public static UserService userService() {
        return userService;
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
            if (!StringUtil.isEmpty(dataFillHandlerPath)) {
                Class<?> clazz = ClassUtil.getClazz(dataFillHandlerPath);
                if (clazz != null) {
                    return FlowFactory.dataFillHandler = o = (DataFillHandler) clazz.newInstance();
                }
            }
            o = FrameInvoker.getBean(DataFillHandler.class);
        } catch (Exception ignored) {
        }
        if (ObjectUtil.isNotNull(o)) {
            return FlowFactory.dataFillHandler = o = new DefaultDataFillHandler();
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
            if (!StringUtil.isEmpty(tenantHandlerPath)) {
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

}

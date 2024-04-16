package com.warm.flow.core.utils;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.listener.Listener;
import com.warm.flow.core.listener.ListenerVariable;
import com.warm.flow.core.listener.NodePermission;
import com.warm.flow.core.listener.ValueHolder;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;

import java.util.List;
import java.util.regex.Matcher;

/**
 * 监听器工具类
 *
 * @author warm
 */
public class ListenerUtil {

    private ListenerUtil() {

    }

    /**
     * 执行节点的权限监听器,并赋值权限值集合
     *
     * @param instance
     * @param flowParams
     * @param nodes
     * @return
     */
    public static void executeGetNodePermission(Instance instance, FlowParams flowParams, Node... nodes) {
        for (Node node : nodes) {
            if (StringUtils.isNotEmpty(node.getListenerType()) && node.getListenerType().contains(Listener.LISTENER_PERMISSION)) {
                //执行权限监听器
                ListenerVariable variable = executeListener(instance, node, Listener.LISTENER_PERMISSION, flowParams);
                //拿到监听器内的权限标识 给NowNode.的PermissionFlag 赋值

                if (variable != null && CollUtil.isNotEmpty(variable.getNodePermissionList())) {
                    NodePermission permissionByNode = variable.getPermissionByNode(node.getNodeCode());
                    if (ObjectUtil.isNotNull(permissionByNode) && StringUtils.isNotEmpty(permissionByNode.getPermissionFlag())) {
                        node.setDynamicPermissionFlag(permissionByNode.getPermissionFlag());
                    }
                }
            }
        }
    }

    /**
     * 执行结束监听器和下一节点的开始监听器
     *
     * @param instance
     * @param NowNode
     * @param nextNodes
     * @param flowParams
     */
    public static void executeListener(Instance instance, Node NowNode, List<Node> nextNodes, FlowParams flowParams) {
        // 执行任务开始监听器
        nextNodes.forEach(node -> {
            executeListener(instance, node, Listener.LISTENER_CREATE, flowParams);
        });

        // 执行任务完成监听器
        executeListener(instance, NowNode, Listener.LISTENER_END, flowParams);
    }

    public static ListenerVariable executeListener(Instance instance, Node node, String lisType, FlowParams flowParams) {
        // 执行监听器
        //listenerPath({"name": "John Doe", "age": 30})@@listenerPath@@listenerPath
        String listenerType = node.getListenerType();
        if (StringUtils.isNotEmpty(listenerType)) {
            String[] listenerTypeArr = listenerType.split(",");
            for (int i = 0; i < listenerTypeArr.length; i++) {
                String listenerTypeStr = listenerTypeArr[i].trim();
                if (listenerTypeStr.equals(lisType)) {
                    //"listenerPath1({\"name\": \"John Doe\", \"age\": 30})@@listenerPath2";
                    String listenerPathStr = node.getListenerPath();
                    if (StringUtils.isNotEmpty(listenerPathStr)) {
                        //"listenerPath1({\"name\": \"John Doe\", \"age\": 30})";
                        //listenerPath2
                        String[] listenerPathArr = listenerPathStr.split(FlowCons.splitAt);
                        String listenerPath = listenerPathArr[i].trim();
                        ValueHolder valueHolder = new ValueHolder();
                        //截取出path 和params
                        getListenerPath(listenerPath, valueHolder);
                        Class<?> clazz = ClassUtil.getClazz(valueHolder.getPath());
                        AssertUtil.isTrue(ObjectUtil.isNull(clazz), ExceptionCons.NOT_LISTENER);
                        Listener listener = (Listener) FrameInvoker.getBean(clazz);
                        ListenerVariable variable = new ListenerVariable(instance, node, flowParams.getVariable(), valueHolder.getParams());
                        listener.notify(variable);
                        return variable;
                    }

                }
            }
        }
        return null;
    }

    /**
     * 分别截取监听器path 和 监听器params
     * String input = "listenerPath({\"name\": \"John Doe\", \"age\": 30})";
     *
     * @param listenerStr
     * @param valueHolder
     */
    public static void getListenerPath(String listenerStr, ValueHolder valueHolder) {
        String path;
        String params;

        Matcher matcher = FlowCons.listenerPattern.matcher(listenerStr);
        if (matcher.find()) {
            path = matcher.group(1).replaceAll("[\\(\\)]", "");
            params = matcher.group(2).replaceAll("[\\(\\)]", "");
            valueHolder.setPath(path);
            valueHolder.setParams(params);
        }
    }
}

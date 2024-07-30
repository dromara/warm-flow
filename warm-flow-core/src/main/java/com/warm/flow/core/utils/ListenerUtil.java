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
package com.warm.flow.core.utils;

import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.listener.Listener;
import com.warm.flow.core.listener.ListenerVariable;
import com.warm.flow.core.listener.NodePermission;
import com.warm.flow.core.listener.ValueHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * @param listenerVariable
     */
    public static void executeGetNodePermission(ListenerVariable listenerVariable) {
        for (Node node : listenerVariable.getNextNodes()) {
            if (StringUtils.isNotEmpty(node.getListenerType()) && node.getListenerType().contains(Listener.LISTENER_PERMISSION)) {
                //执行权限监听器
                executeListener(listenerVariable, Listener.LISTENER_PERMISSION, node);

                //拿到监听器内的权限标识 给NowNode.的PermissionFlag 赋值
                if (CollUtil.isNotEmpty(listenerVariable.getNodePermissionList())) {
                    NodePermission permissionByNode = listenerVariable.getPermissionByNode(node.getNodeCode());
                    if (ObjectUtil.isNotNull(permissionByNode) && StringUtils.isNotEmpty(permissionByNode.getPermissionFlag())) {
                        if (CollUtil.isNotEmpty(permissionByNode.getPermissionFlagList())) {
                            node.setDynamicPermissionFlagList(permissionByNode.getPermissionFlagList());
                        } else if (StringUtils.isNotEmpty(permissionByNode.getPermissionFlag())) {
                            node.setDynamicPermissionFlagList(CollUtil.strToColl(permissionByNode.getPermissionFlag(), ","));
                        }
                    }
                }
            }
        }
    }

    /**
     * 执行结束监听器和下一节点的开始监听器
     *
     * @param listenerVariable
     */
    public static void endCreateListener(ListenerVariable listenerVariable) {
        // 执行任务完成监听器
        executeListener(listenerVariable, Listener.LISTENER_END);
        // 执行任务开始监听器
        listenerVariable.getNextNodes().forEach(node -> executeListener(listenerVariable, Listener.LISTENER_CREATE, node));
    }

    public static void executeListener(ListenerVariable listenerVariable, String lisType) {
        executeListener(listenerVariable, lisType, listenerVariable.getNode());
    }

    public static void executeListener(ListenerVariable listenerVariable, String lisType, Node listenerNode) {
        // 执行监听器
        //listenerPath({"name": "John Doe", "age": 30})@@listenerPath@@listenerPath
        String listenerType = listenerNode.getListenerType();
        if (StringUtils.isNotEmpty(listenerType)) {
            String[] listenerTypeArr = listenerType.split(",");
            for (int i = 0; i < listenerTypeArr.length; i++) {
                String listenerTypeStr = listenerTypeArr[i].trim();
                if (listenerTypeStr.equals(lisType)) {
                    //"listenerPath1({\"name\": \"John Doe\", \"age\": 30})@@listenerPath2";
                    String listenerPathStr = listenerNode.getListenerPath();
                    if (StringUtils.isNotEmpty(listenerPathStr)) {
                        //"listenerPath1({\"name\": \"John Doe\", \"age\": 30})";
                        //listenerPath2
                        String[] listenerPathArr = listenerPathStr.split(FlowCons.splitAt);
                        String listenerPath = listenerPathArr[i].trim();
                        ValueHolder valueHolder = new ValueHolder();
                        //截取出path 和params
                        getListenerPath(listenerPath, valueHolder);
                        Class<?> clazz = ClassUtil.getClazz(valueHolder.getPath());
                        // 增加传入类路径校验Listener接口, 防止强制类型转换失败
                        if (ObjectUtil.isNotNull(clazz) && Listener.class.isAssignableFrom(clazz)) {
                            Listener listener = (Listener) FrameInvoker.getBean(clazz);
                            if (ObjectUtil.isNotNull(listener)) {
                                Map<String, Object> variable = listenerVariable.getVariable();
                                variable = MapUtil.isEmpty(variable) ? new HashMap<>() : variable;
                                variable.put(FlowCons.WARM_LISTENER_PARAM, valueHolder.getParams());
                                listener.notify(listenerVariable.setVariable(variable));
                            }

                        }
                    }
                }
            }
        }
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

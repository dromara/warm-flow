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

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.listener.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 全局监听器工具类
 */
public class GlobalListenerUtil {
    private GlobalListenerUtil() {

    }

    /**
     * 执行节点的全局权限监听器,并赋值权限值集合
     *
     * @param listenerVariable
     */
    public static void executeGetNodePermission(ListenerVariable listenerVariable) {
        for (Node node : listenerVariable.getNextNodes()) {
            Definition definition = FlowFactory.defService().getById(node.getDefinitionId());
            if (StringUtils.isNotEmpty(definition.getListenerType()) && definition.getListenerType().contains(GlobalListener.LISTENER_PERMISSION)) {
                executeGlobalListener(listenerVariable, GlobalListener.LISTENER_PERMISSION, node);

                if (CollUtil.isNotEmpty(listenerVariable.getNodePermissionList())) {
                    NodePermission permissionByNode = listenerVariable.getPermissionByNode(node.getNodeCode());
                    if (ObjectUtil.isNotNull(permissionByNode) && StringUtils.isNotEmpty(permissionByNode.getPermissionFlag())) {
                        node.setDynamicPermissionFlagList(permissionByNode.getPermissionFlagList());
                    } else if (StringUtils.isNotEmpty(permissionByNode.getPermissionFlag())) {
                        node.setDynamicPermissionFlagList(CollUtil.strToColl(permissionByNode.getPermissionFlag(), ","));
                    }
                }
            }
        }
    }


    /**
     * 执行全局结束监听器和下一节点的全局开始监听器
     *
     * @param listenerVariable
     */
    public static void endCreateGlobalListener(ListenerVariable listenerVariable) {
        // 执行全局任务完成监听器
        executeGlobalListener(listenerVariable, GlobalListener.LISTENER_END);
        // 执行全局任务开始监听器
        listenerVariable.getNextNodes().forEach(node -> executeGlobalListener(listenerVariable, GlobalListener.LISTENER_CREATE, node));
    }

    public static void executeGlobalListener(ListenerVariable listenerVariable, String lisType) {
        executeGlobalListener(listenerVariable, lisType, listenerVariable.getNode());
    }


    public static void executeGlobalListener(ListenerVariable listenerVariable, String lisType, Node listenerNode) {
        // 执行监听器
        //listenerPath({"name": "John Doe", "age": 30})@@listenerPath@@listenerPath
        Definition definition = FlowFactory.defService().getById(listenerNode.getDefinitionId());
        String listenerType = definition.getListenerType();
        if (StringUtils.isNotEmpty(listenerType)) {
            String[] listenerTypeArr = listenerType.split(",");
            for (int i = 0; i < listenerTypeArr.length; i++) {
                String listenerTypeStr = listenerTypeArr[i].trim();
                if (listenerTypeStr.equals(lisType)) {
                    //"listenerPath1({\"name\": \"John Doe\", \"age\": 30})@@listenerPath2";
                    String listenerPathStr = definition.getListenerPath();
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
                        if (ObjectUtil.isNotNull(clazz) && GlobalListener.class.isAssignableFrom(clazz)) {
                            GlobalListener listener = (GlobalListener) FrameInvoker.getBean(clazz);
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

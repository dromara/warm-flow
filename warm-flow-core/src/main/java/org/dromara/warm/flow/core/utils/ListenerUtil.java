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
package org.dromara.warm.flow.core.utils;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.listener.GlobalListener;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.listener.ValueHolder;

import java.util.HashMap;
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
     * 执行完成监听器和下一节点的开始监听器
     *
     * @param listenerVariable 监听器变量
     */
    public static void endCreateListener(ListenerVariable listenerVariable) {
        // 执行任务完成监听器
        executeListener(listenerVariable, Listener.LISTENER_FINISH);
        // 执行任务创建监听器
        listenerVariable.getNextNodes().forEach(node -> {
            if (!NodeType.isEnd(node.getNodeType())) {
                executeListener(listenerVariable, Listener.LISTENER_CREATE, node);
            }
        });
    }

    public static void executeListener(ListenerVariable listenerVariable, String type) {
        executeListener(listenerVariable, type, listenerVariable.getNode());
    }

    public static void executeListener(ListenerVariable listenerVariable, String type, Node listenerNode) {
        // 执行监听器
        //listenerPath({"name": "John Doe", "age": 30})@@listenerPath@@listenerPath
        String listenerType = listenerNode.getListenerType();
        execute(listenerVariable, type, listenerNode.getListenerPath(), listenerType);
        Definition definition = listenerVariable.getDefinition();
        execute(listenerVariable, type, definition.getListenerPath(), definition.getListenerType());
        GlobalListener globalListener = FlowEngine.globalListener();
        if (ObjectUtil.isNotNull(globalListener)) {
            globalListener.notify(type, listenerVariable);
        }
    }

    public static void execute(ListenerVariable listenerVariable, String type, String listenerPaths, String listenerTypes) {
        if (StringUtils.isNotEmpty(listenerTypes)) {
            String[] listenerTypeArr = listenerTypes.split(",");
            for (int i = 0; i < listenerTypeArr.length; i++) {
                String listenerType = listenerTypeArr[i].trim();
                if (listenerType.equals(type)) {
                    if (StringUtils.isNotEmpty(listenerPaths)) {
                        String[] listenerPathArr = listenerPaths.split(FlowCons.splitAt);
                        String listenerPath = listenerPathArr[i].trim();
                        ValueHolder valueHolder = new ValueHolder();
                        //截取出path 和params
                        getListenerPath(listenerPath, valueHolder);

                        Map<String, Object> expressionMap = MapUtil.newAndPut("listenerVariable", listenerVariable);
                        // 如果返回为true，说明配置的path是表达式,并且已经执行完，不需要执行后续加载类路径（优先执行表达式监听器）
                        if (ExpressionUtil.evalListener(listenerPath, expressionMap)) {
                            return;
                        }

                        Class<?> clazz = ClassUtil.getClazz(valueHolder.getPath());
                        // 增加传入类路径校验Listener接口, 防止强制类型转换失败
                        if (ObjectUtil.isNotNull(clazz) && Listener.class.isAssignableFrom(clazz)) {
                            Listener listener = (Listener) FrameInvoker.getBean(clazz);
                            if (ObjectUtil.isNotNull(listener)) {
                                Map<String, Object> variable = listenerVariable.getVariable();
                                if (MapUtil.isEmpty(variable)) {
                                    variable = new HashMap<>();
                                } else {
                                    variable.remove(FlowCons.WARM_LISTENER_PARAM);
                                }
                                if (StringUtils.isNotEmpty(valueHolder.getParams())) {
                                    variable.put(FlowCons.WARM_LISTENER_PARAM, valueHolder.getParams());
                                }
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

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

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.variable.DefaultVariableStrategy;
import com.warm.flow.core.variable.VariableStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 变量替换工具类
 *
 * @author warm
 */
public class VariableUtil {

    private static final Map<String, VariableStrategy> map = new HashMap<>();

    private VariableUtil() {

    }

    static {
        setVariable(new DefaultVariableStrategy());
    }

    public static void setVariable(VariableStrategy variableStrategy) {
        map.put(variableStrategy.getType(), variableStrategy);
    }

    /**
     * @param addTasks 变量表达式，比如“@@default@@|${flag}” ，或者自定义策略
     * @param variable
     * @return
     */
    public static void eval(List<Task> addTasks, Map<String, Object> variable) {
        if (CollUtil.isEmpty(addTasks) || MapUtil.isEmpty(variable)) {
            return;
        }
        for (Task task : addTasks) {
            List<String> permissionList = task.getPermissionList();
            // 如果设置了发起人审批，则需要动态替换权限标识
            for (int i = 0; i < permissionList.size(); i++) {
                String permission = permissionList.get(i);
                if (StringUtils.isNotEmpty(permission)) {
                    for (Map.Entry<String, VariableStrategy> entry : map.entrySet()) {
                        String k = entry.getKey();
                        VariableStrategy v = entry.getValue();
                        if (permission.contains(k + "|")) {
                            if (v == null) {
                                throw new FlowException(ExceptionCons.NULL_EXPRESSION_STRATEGY);
                            }
                            permissionList.set(i, v.eval(permission.replace(k + "|", ""), variable));
                        }
                    }
                }
            }
        }

    }
}

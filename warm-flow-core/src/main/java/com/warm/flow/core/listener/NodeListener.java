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
package com.warm.flow.core.listener;

import java.io.Serializable;

/**
 * 节点监听器，与节点绑定
 */
public interface NodeListener extends Serializable {

    /**
     * 节点创建监听器，任务创建时执行
     */
    String LISTENER_CREATE = "create";

    /**
     * 节点开始开始监听器，任务开始办理时执行
     */
    String LISTENER_START = "start";

    /**
     * 节点结束结束监听器，当前任务完成后执行
     */
    String LISTENER_END = "finish";

    /**
     * 节点分派监听器，动态修改代办任务信息
     */
    String LISTENER_ASSIGNMENT = "assignment";

    /**
     * 节点权限监听器，办理任务动态设置权限(1.2.4版本后建议使用分派监听器修改办理人)
     */
    String LISTENER_PERMISSION = "permission";

    void notify(ListenerVariable variable);
}

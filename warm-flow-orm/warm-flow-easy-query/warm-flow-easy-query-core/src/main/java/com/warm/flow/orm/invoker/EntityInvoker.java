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
package com.warm.flow.orm.invoker;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.orm.entity.*;

/**
 * 设置创建对象方法
 *
 * @author warm
 */
public class EntityInvoker {

    public static void setNewEntity() {
        FlowFactory.setNewDef(FlowDefinition::new);
        FlowFactory.setNewIns(FlowInstance::new);
        FlowFactory.setNewHisTask(FlowHisTask::new);
        FlowFactory.setNewNode(FlowNode::new);
        FlowFactory.setNewSkip(FlowSkip::new);
        FlowFactory.setNewTask(FlowTask::new);
        FlowFactory.setNewUser(FlowUser::new);
    }
}

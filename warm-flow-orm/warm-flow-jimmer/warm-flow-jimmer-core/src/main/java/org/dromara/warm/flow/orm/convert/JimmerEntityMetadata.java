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
package org.dromara.warm.flow.orm.convert;

import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.orm.entity.*;
import org.dromara.warm.flow.orm.model.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JimmerEntityMetadata {

    private static final Map<Class<? extends RootEntity>, Class<?>> ENTITY_TO_MODEL;

    static {
        Map<Class<? extends RootEntity>, Class<?>> map = new LinkedHashMap<>();
        map.put(FlowDefinition.class, FlowDefinitionModel.class);
        map.put(FlowForm.class, FlowFormModel.class);
        map.put(FlowHisTask.class, FlowHisTaskModel.class);
        map.put(FlowInstance.class, FlowInstanceModel.class);
        map.put(FlowNode.class, FlowNodeModel.class);
        map.put(FlowSkip.class, FlowSkipModel.class);
        map.put(FlowTask.class, FlowTaskModel.class);
        map.put(FlowUser.class, FlowUserModel.class);
        ENTITY_TO_MODEL = Collections.unmodifiableMap(map);
    }

    private JimmerEntityMetadata() {
    }

    public static Map<Class<? extends RootEntity>, Class<?>> entityToModel() {
        return ENTITY_TO_MODEL;
    }
}

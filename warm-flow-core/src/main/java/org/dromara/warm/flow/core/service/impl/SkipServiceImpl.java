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
package org.dromara.warm.flow.core.service.impl;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.orm.dao.FlowSkipDao;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.SkipService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 节点跳转关联Service业务层处理
 *
 * @author warm
 * @since 2023-03-29
 */
public class SkipServiceImpl extends WarmServiceImpl<FlowSkipDao<Skip>, Skip> implements SkipService {

    @Override
    public SkipService setDao(FlowSkipDao<Skip> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        return getDao().deleteSkipByDefIds(defIds);
    }

    @Override
    public List<Skip> getByDefId(Long definitionId) {
        return list(FlowEngine.newSkip().setDefinitionId(definitionId));
    }

    @Override
    public List<Skip> getByDefIdAndNowNodeCode(Long definitionId, String nodeCode) {
        return list(FlowEngine.newSkip().setDefinitionId(definitionId).setNowNodeCode(nodeCode));
    }
}

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
package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.dao.FlowNodeDao;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowNode;
import org.dromara.warm.flow.orm.mapper.FlowNodeMapper;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * 流程节点Mapper接口
 *
 * @author warm
 * @since 2023-03-29
 */
public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNode> implements FlowNodeDao<FlowNode> {

    @Override
    public FlowNodeMapper getMapper() {
        return FrameInvoker.getBean(FlowNodeMapper.class);
    }

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return getMapper().getByNodeCodes(nodeCodes, definitionId, TenantDeleteUtil.getEntity(newEntity()));
    }

    /**
     * 批量删除流程节点
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        FlowNode entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateNodeByDefIdsLogic(defIds, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteNodeByDefIds(defIds, entity);
    }
}

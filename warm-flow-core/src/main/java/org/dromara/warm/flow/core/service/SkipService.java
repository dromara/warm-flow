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
package org.dromara.warm.flow.core.service;

import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.orm.service.IWarmService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 节点跳转关联Service接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface SkipService extends IWarmService<Skip> {

    /**
     * 批量删除节点跳转关联
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds);

    /**
     * 根据流程定义id查询节点跳转线
     * @param definitionId  流程定义id
     * @return List<Skip>
     */
    List<Skip> getByDefId(Long definitionId);

    /**
     * 根据流程定义id和节点编码查询节点跳转线
     * @param definitionId  流程定义id
     * @param nowNodeCode 其实节点编码
     * @return List<Skip>
     */
    List<Skip> getByDefIdAndNowNodeCode(Long definitionId, String nowNodeCode);
}

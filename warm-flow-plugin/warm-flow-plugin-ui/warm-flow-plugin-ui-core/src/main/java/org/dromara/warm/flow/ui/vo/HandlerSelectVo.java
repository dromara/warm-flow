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
package org.dromara.warm.flow.ui.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.dto.FlowPage;

import java.util.List;

/**
 * 流程设计器-办理人权限设置列表结果
 * 办理人权限设置列表，可能存在多个，比如：部门、角色、用户的情况
 *
 * @author warm
 */
@Getter
@Setter
@Accessors(chain = true)
public class HandlerSelectVo {

    /**
     * 办理人权限设置列表
     */
    private FlowPage<HandlerAuth> handlerAuths;

    /**
     * 左侧树状选择
     */
    private List<TreeSelection> treeSelections;

}

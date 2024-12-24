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
package org.dromara.warm.flow.ui.service;

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.dto.FlowPage;
import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.utils.HttpStatus;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.ui.dto.*;
import org.dromara.warm.flow.ui.utils.TreeUtil;
import org.dromara.warm.flow.ui.vo.HandlerAuth;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程设计器-获取办理人权限设置列表接口
 *
 * @author warm
 */
public interface HandlerSelectService {

    /**
     * 获取办理人权限设置列表tabs页签，如：用户、角色和部门等，可以返回其中一种或者多种，按业务需求决定
     *
     * @return tabs页签
     */
    List<String> getHandlerType();

    /**
     * 获取用户列表、角色列表、部门列表等，可以返回其中一种或者多种，按业务需求决定
     *
     * @param query 查询参数
     * @return 结果
     */
    HandlerSelectVo getHandlerSelect(HandlerQuery query);

    default <T> HandlerSelectVo getHandlerSelectVo(HandlerFunDto<T> handlerFunDto) {
        List<HandlerAuth> handlerAuths = new ArrayList<>();
        // 遍历角色数据，封装为组件可识别的数据
        for (T obj : handlerFunDto.getList()) {
            handlerAuths.add(new HandlerAuth()
                    .setStorageId(handlerFunDto.getStorageId() == null ? null : handlerFunDto.getStorageId().apply(obj))
                    .setHandlerCode(handlerFunDto.getHandlerCode() == null ? null : handlerFunDto.getHandlerCode().apply(obj))
                    .setHandlerName(handlerFunDto.getHandlerName() == null ? null : handlerFunDto.getHandlerName().apply(obj))
                    .setCreateTime(handlerFunDto.getCreateTime() == null ? null : handlerFunDto.getCreateTime().apply(obj))
                    .setGroupName(handlerFunDto.getGroupName() == null ? null : handlerFunDto.getGroupName().apply(obj)));
        }
        return getResult(handlerAuths, handlerFunDto.getTotal());
    }

    default <T, R> HandlerSelectVo getHandlerSelectVo(HandlerFunDto<T> handlerFunDto, TreeFunDto<R> treeFunDto) {
        HandlerSelectVo handlerSelectVo = getHandlerSelectVo(handlerFunDto);

        List<Tree> treeList = StreamUtils.toList(treeFunDto.getList(), org ->
                new Tree().setId(treeFunDto.getId() == null ? null : treeFunDto.getId().apply(org))
                        .setName(treeFunDto.getName() == null ? null : treeFunDto.getName().apply(org))
                        .setParentId(treeFunDto.getParentId() == null ? null : treeFunDto.getParentId().apply(org)));

        // 通过递归，构建树状结构
        return handlerSelectVo.setTreeSelections(TreeUtil.buildTree(treeList));
    }


    default HandlerSelectVo getResult(List<HandlerAuth> handlerAuths, long total) {
        return new HandlerSelectVo().setHandlerAuths(new FlowPage<HandlerAuth>()
                .setCode(HttpStatus.SUCCESS)
                .setMsg("查询成功")
                .setRows(handlerAuths)
                .setTotal(total));
    }

}

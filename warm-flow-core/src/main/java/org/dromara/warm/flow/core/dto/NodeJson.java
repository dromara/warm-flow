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
package org.dromara.warm.flow.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程节点对象Vo
 *
 * @author warm
 * @since 2023-03-29
 */
@Setter
@Getter
@Accessors(chain = true)
public class NodeJson {

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    private Integer nodeType;
    /**
     * 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制
     */
    private String nodeCode;
    /**
     * 流程节点名称
     */
    private String nodeName;
    /**
     * 权限标识（权限类型:权限标识，可以多个，用逗号隔开)
     */
    private String permissionFlag;
    /**
     * 流程签署比例值
     */
    private BigDecimal nodeRatio;
    /**
     * 流程节点坐标
     */
    private String coordinate;
    /**
     * 是否可以跳转任意节点（Y是 N否）
     */
    private String skipAnyNode;
    /**
     * 监听器类型
     */
    private String listenerType;
    /**
     * 监听器路径
     */
    private String listenerPath;
    /**
     * 处理器类型
     */
    private String handlerType;
    /**
     * 处理器路径
     */
    private String handlerPath;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String formPath;

    /**
     * 跳转条件
     */
    private List<SkipJson> skipList = new ArrayList<>();

}

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

import org.dromara.warm.flow.core.dto.DefChart;

import java.io.IOException;

/**
 * 流程图绘制Service接口
 *
 * @author warm
 * @since 2024-12-30
 */
public interface ChartService {


    /**
     * 根据流程实例ID,获取流程图的图片流(渲染颜色)
     *
     * @param instanceId 流程实例id
     * @return base64编码的图片流字符串
     */
    String chartIns(Long instanceId) throws IOException;

    /**
     * 根据流程定义ID,获取流程图的图片流(不渲染颜色)
     * @param definitionId 流程定义id
     * @return base64编码的图片流字符串
     */
    String chartDef(Long definitionId) throws IOException;

    /**
     * 根据流程实例ID,获取流程图对象
     *
     * @param instanceId 流程实例id
     * @return DefChart 流程图对象
     */
    DefChart chartInsObj(Long instanceId);

    /**
     * 根据流程定义ID,获取流程图对象
     * @param definitionId 流程定义id
     * @return DefChart 流程图对象
     */
    DefChart chartDefObj(Long definitionId);
}
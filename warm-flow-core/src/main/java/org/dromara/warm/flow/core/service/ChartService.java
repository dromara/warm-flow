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

import org.dromara.warm.flow.core.chart.FlowChartChain;
import org.dromara.warm.flow.core.dto.DefChart;
import org.dromara.warm.flow.core.dto.PathWayData;

import java.util.function.Consumer;

/**
 * 流程图绘制Service接口
 *
 * @author warm
 * @since 2024-12-30
 */
public interface ChartService {


    /**
     * 根据流程实例ID,获取流程图的图片流(渲染状态)
     *
     * @param instanceId 流程实例id
     * @return base64编码的图片流字符串
     */
    String chartIns(Long instanceId);

    /**
     * 根据流程定义ID,获取流程图的图片流(不渲染状态)
     * @param definitionId 流程定义id
     * @return base64编码的图片流字符串
     */
    String chartDef(Long definitionId);

    /**
     * 根据流程实例ID,获取流程图的图片流(渲染状态)
     *
     * @param instanceId 流程实例id
     * @param consumer 可获取流程图对象，可用于修改流程图样式或者新增内容
     * @return base64编码的图片流字符串
     */
    String chartIns(Long instanceId, Consumer<FlowChartChain> consumer);

    /**
     * 根据流程定义ID,获取流程图的图片流(不渲染状态)
     * @param definitionId 流程定义id
     * @param consumer 可获取流程图对象，可用于修改流程图样式或者新增内容
     * @return base64编码的图片流字符串
     */
    String chartDef(Long definitionId, Consumer<FlowChartChain> consumer);

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

    /**
     * 获取流程开启时的流程图元数据
     * @param pathWayData 办理过程中途径数据，用于渲染流程图
     * @return 流程图元数据json
     */
    String startMetadata(PathWayData pathWayData);

    /**
     * 获取流程运行时的流程图元数据
     * @param pathWayData 办理过程中途径数据，用于渲染流程图
     * @return 流程图元数据json
     */
    String skipMetadata(PathWayData pathWayData);
}

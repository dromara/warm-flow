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

import org.dromara.warm.flow.core.dto.PathWayData;

import java.util.List;

/**
 * 流程图绘制Service接口
 *
 * @author warm
 * @since 2024-12-30
 */
public interface ChartService {


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

    /**
     * 获取流程图三原色
     * @param modelValue 流程模型
     */
    List<String> getChartRgb(String modelValue);
}

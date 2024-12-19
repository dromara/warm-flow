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
package org.dromara.warm.flow.core.chart;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程图绘制
 *
 * @author warm
 */
@Getter
public class FlowChartChain {

    private final List<FlowChart> flowChartList = new ArrayList<>();

    public void addFlowChart(FlowChart flowChart) {
        flowChartList.add(flowChart);
    }

    public void draw(Graphics2D graphics, int n) {
        for (FlowChart flowChart : flowChartList) {
            flowChart.setN(n).draw(graphics);
        }
    }
}

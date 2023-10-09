package com.warm.flow.core.chart;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程图绘制
 *
 * @author warm
 */
public class FlowChartChain {

    private final List<FlowChart> flowChartList = new ArrayList<>();

    public FlowChartChain addFlowChart(FlowChart flowChart) {
        flowChartList.add(flowChart);
        return this;
    }

    public void draw(Graphics2D graphics) {
        for (FlowChart flowChart : flowChartList) {
            flowChart.draw(graphics);
        }
    }
}

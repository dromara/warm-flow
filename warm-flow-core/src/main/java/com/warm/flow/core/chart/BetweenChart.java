package com.warm.flow.core.chart;

import com.warm.flow.core.utils.DrawUtils;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;

import java.awt.*;

/**
 * 流程图中间节点
 */
public class BetweenChart implements FlowChart {
    private int xRect;
    private int yRect;
    private Color c = Color.BLACK;
    private TextChart textChart;

    public BetweenChart(int xRect, int yRect, Color c, TextChart textChart) {
        this.xRect = xRect;
        this.yRect = yRect;
        this.c = c;
        this.textChart = textChart;
    }


    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        graphics.drawRoundRect(xRect - 50, yRect - 40, 100, 80, 20, 20);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setxText(textChart.getxText() - DrawUtils.stringWidth(textChart.getTitle()) / 2);
            // 填充文字说明
            textChart.draw(graphics);
        }
    }
}

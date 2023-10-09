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

    private TextChart textChart;

    public TextChart getTextImage() {
        return textChart;
    }

    public BetweenChart setTextImage(TextChart textChart) {
        this.textChart = textChart;
        return this;
    }

    public BetweenChart(int xRect, int yRect) {
        this.xRect = xRect;
        this.yRect = yRect;
    }

    public BetweenChart(int xRect, int yRect, TextChart textChart) {
        this.xRect = xRect;
        this.yRect = yRect;
        this.textChart = textChart;
    }

    public int getxRect() {
        return xRect;
    }

    public BetweenChart setxRect(int xRect) {
        this.xRect = xRect;
        return this;
    }

    public int getyRect() {
        return yRect;
    }

    public BetweenChart setyRect(int yRect) {
        this.yRect = yRect;
        return this;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.drawRoundRect(xRect - 50, yRect - 40, 100, 80, 20, 20);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setxText(textChart.getxText() - DrawUtils.stringWidth(textChart.getTitle()) / 2);
            // 填充文字说明
            textChart.draw(graphics);
        }
    }
}

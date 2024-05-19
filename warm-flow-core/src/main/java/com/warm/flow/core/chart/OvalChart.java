package com.warm.flow.core.chart;

import com.warm.flow.core.utils.DrawUtils;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 流程图开始或者结束节点
 */
public class OvalChart implements FlowChart {
    private int xStartOval;

    private int yStartOval;

    private Color c;

    private TextChart textChart;

    public OvalChart(int xStartOval, int yStartOval, Color c, TextChart textChart) {
        this.xStartOval = xStartOval;
        this.yStartOval = yStartOval;
        this.c = c;
        this.textChart = textChart;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        graphics.drawOval(xStartOval - 20, yStartOval - 20, 40, 40);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setxText(textChart.getxText() - DrawUtils.stringWidth(graphics, textChart.getTitle()) / 2);
            textChart.setyText(textChart.getyText() + 5);
            // 填充文字说明
            textChart.draw(graphics);
        }
    }
}

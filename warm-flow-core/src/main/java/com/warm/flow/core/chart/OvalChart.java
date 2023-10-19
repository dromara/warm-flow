package com.warm.flow.core.chart;

import com.warm.flow.core.utils.DrawUtils;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;

import java.awt.*;

/**
 * 流程图开始或者结束节点
 */
public class OvalChart implements FlowChart {
    private int xStartOval;

    private int yStartOval;

    private Color c = Color.BLACK;

    private TextChart textChart;

    public OvalChart(int xStartOval, int yStartOval, Color c, TextChart textChart) {
        this.xStartOval = xStartOval;
        this.yStartOval = yStartOval;
        this.c = c;
        this.textChart = textChart;
    }

    public int getxStartOval() {
        return xStartOval;
    }

    public void setxStartOval(int xStartOval) {
        this.xStartOval = xStartOval;
    }

    public int getyStartOval() {
        return yStartOval;
    }

    public void setyStartOval(int yStartOval) {
        this.yStartOval = yStartOval;
    }

    public Color getC() {
        return c;
    }

    public void setC(Color c) {
        this.c = c;
    }

    public TextChart getTextChart() {
        return textChart;
    }

    public void setTextChart(TextChart textChart) {
        this.textChart = textChart;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        graphics.drawOval(xStartOval - 20, yStartOval - 20, 40, 40);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setxText(textChart.getxText() - DrawUtils.stringWidth(textChart.getTitle()) / 2);
            textChart.setyText(textChart.getyText() + 5);
            // 填充文字说明
            textChart.draw(graphics);
        }
    }
}

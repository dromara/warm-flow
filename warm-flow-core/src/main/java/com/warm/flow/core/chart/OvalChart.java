package com.warm.flow.core.chart;

import java.awt.*;

/**
 * 流程图开始或者结束节点
 */
public class OvalChart implements FlowChart {
    private int xStartOval;

    private int yStartOval;

    private boolean isStart;

    public OvalChart(int xStartOval, int yStartOval, boolean isStart) {
        this.xStartOval = xStartOval;
        this.yStartOval = yStartOval;
        this.isStart = isStart;
    }

    public int getxStartOval() {
        return xStartOval;
    }

    public OvalChart setxStartOval(int xStartOval) {
        this.xStartOval = xStartOval;
        return this;
    }

    public int getyStartOval() {
        return yStartOval;
    }

    public OvalChart setyStartOval(int yStartOval) {
        this.yStartOval = yStartOval;
        return this;
    }

    public boolean isStart() {
        return isStart;
    }

    public OvalChart setStart(boolean start) {
        isStart = start;
        return this;
    }

    @Override
    public void draw(Graphics2D graphics) {
        if (isStart) {
            graphics.setColor(Color.GREEN);
            graphics.drawString("开始", xStartOval - 9, yStartOval + 35);
        } else {
            graphics.setColor(Color.RED);
            graphics.drawString("结束", xStartOval - 9, yStartOval + 35);
        }
        graphics.fillOval(xStartOval - 20, yStartOval - 20, 40, 40);
    }
}

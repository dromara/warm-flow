package com.warm.flow.core.chart;

import java.awt.*;

/**
 * 流程图并行网关
 */
public class ParallelChart implements FlowChart {
    private int xParallel;

    private int yParallel;

    public ParallelChart(int xParallel, int yParallel) {
        this.xParallel = xParallel;
        this.yParallel = yParallel;
    }

    public int getxParallel() {
        return xParallel;
    }

    public void setxParallel(int xParallel) {
        this.xParallel = xParallel;
    }

    public int getyParallel() {
        return yParallel;
    }

    public void setyParallel(int yParallel) {
        this.yParallel = yParallel;
    }

    @Override
    public void draw(Graphics2D graphics) {
        int[] xParallels = {xParallel - 20, xParallel, xParallel + 20, xParallel};
        int[] yParallels = {yParallel, yParallel - 20, yParallel, yParallel + 20};
        graphics.drawPolygon(xParallels, yParallels, 4);

        int[] xPoints1 = {xParallel - 8, xParallel + 8};
        int[] yPoints1 = {yParallel, yParallel};
        graphics.drawPolyline(xPoints1, yPoints1, xPoints1.length);

        int[] xPoints2 = {xParallel, xParallel};
        int[] yPoints2 = {yParallel - 8, yParallel + 8};
        graphics.drawPolyline(xPoints2, yPoints2, xPoints2.length);
    }
}

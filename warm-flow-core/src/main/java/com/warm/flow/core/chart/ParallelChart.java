package com.warm.flow.core.chart;

import java.awt.*;

/**
 * 流程图并行网关
 */
public class ParallelChart implements FlowChart {
    private int xParallel;

    private int yParallel;

    private Color c;

    public ParallelChart(int xParallel, int yParallel) {
        this.xParallel = xParallel;
        this.yParallel = yParallel;
    }

    public ParallelChart(int xParallel, int yParallel, Color c) {
        this.xParallel = xParallel;
        this.yParallel = yParallel;
        this.c = c;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
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

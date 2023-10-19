package com.warm.flow.core.chart;

import java.awt.*;

/**
 * 流程图互斥网关
 */
public class SerialChart implements FlowChart {
    private int xSerial;

    private int ySerial;

    public SerialChart(int xSerial, int ySerial) {
        this.xSerial = xSerial;
        this.ySerial = ySerial;
    }

    public int getxSerial() {
        return xSerial;
    }

    public void setxSerial(int xSerial) {
        this.xSerial = xSerial;
    }

    public int getySerial() {
        return ySerial;
    }

    public void setySerial(int ySerial) {
        this.ySerial = ySerial;
    }


    @Override
    public void draw(Graphics2D graphics) {
        int[] xSerials = {xSerial - 20, xSerial, xSerial + 20, xSerial};
        int[] ySerials = {ySerial, ySerial - 20, ySerial, ySerial + 20};
        graphics.drawPolygon(xSerials, ySerials, 4);

        int[] xPoints1 = {xSerial - 6, xSerial + 6};
        int[] yPoints1 = {ySerial - 6, ySerial + 6};
        graphics.drawPolyline(xPoints1, yPoints1, xPoints1.length);

        int[] xPoints2 = {xSerial - 6, xSerial + 6};
        int[] yPoints2 = {ySerial + 6, ySerial - 6};
        graphics.drawPolyline(xPoints2, yPoints2, xPoints2.length);
    }
}

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
package com.warm.flow.core.chart;

import java.awt.*;

/**
 * 流程图互斥网关
 */
public class SerialChart implements FlowChart {
    private int xSerial;

    private int ySerial;

    private Color c;

    public SerialChart() {
    }

    public SerialChart(int xSerial, int ySerial, Color c) {
        this.xSerial = xSerial;
        this.ySerial = ySerial;
        this.c = c;
    }

    public int getxSerial() {
        return xSerial;
    }

    public SerialChart setxSerial(int xSerial) {
        this.xSerial = xSerial;
        return this;
    }

    public int getySerial() {
        return ySerial;
    }

    public SerialChart setySerial(int ySerial) {
        this.ySerial = ySerial;
        return this;
    }

    public Color getC() {
        return c;
    }

    public SerialChart setC(Color c) {
        this.c = c;
        return this;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
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

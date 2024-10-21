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
 * 流程图并行网关
 */
public class ParallelChart implements FlowChart {
    private int xParallel;

    private int yParallel;

    private Color c;

    public ParallelChart() {
    }

    public ParallelChart(int xParallel, int yParallel, Color c) {
        this.xParallel = xParallel;
        this.yParallel = yParallel;
        this.c = c;
    }

    public int getxParallel() {
        return xParallel;
    }

    public ParallelChart setxParallel(int xParallel) {
        this.xParallel = xParallel;
        return this;
    }

    public int getyParallel() {
        return yParallel;
    }

    public ParallelChart setyParallel(int yParallel) {
        this.yParallel = yParallel;
        return this;
    }

    public Color getC() {
        return c;
    }

    public ParallelChart setC(Color c) {
        this.c = c;
        return this;
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

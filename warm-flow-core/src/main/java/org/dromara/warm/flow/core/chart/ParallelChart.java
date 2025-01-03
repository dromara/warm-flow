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
package org.dromara.warm.flow.core.chart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.*;

/**
 * 流程图并行网关
 */
@Getter
@Setter
@Accessors(chain = true)
public class ParallelChart implements FlowChart {
    private int n;

    private int xParallel;

    private int yParallel;

    private Color c;

    public ParallelChart(int xParallel, int yParallel, Color c) {
        this.xParallel = xParallel;
        this.yParallel = yParallel;
        this.c = c;
    }


    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        int[] xParallels = {(xParallel - 20) * n, xParallel * n, (xParallel + 20) * n, xParallel * n};
        int[] yParallels = {yParallel * n, (yParallel - 20) * n, yParallel * n, (yParallel + 20) * n};
        graphics.drawPolygon(xParallels, yParallels, 4);

        int[] xPoints1 = {(xParallel - 8) * n, (xParallel + 8) * n};
        int[] yPoints1 = {yParallel * n, yParallel * n};
        graphics.drawPolyline(xPoints1, yPoints1, xPoints1.length);

        int[] xPoints2 = {xParallel * n, xParallel * n};
        int[] yPoints2 = {(yParallel - 8) * n, (yParallel + 8) * n};
        graphics.drawPolyline(xPoints2, yPoints2, xPoints2.length);
    }
}

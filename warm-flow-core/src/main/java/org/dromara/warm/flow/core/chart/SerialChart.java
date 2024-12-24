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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.*;

/**
 * 流程图互斥网关
 */
@Getter
@Setter
@Accessors(chain = true)
public class SerialChart implements FlowChart {
    private int n;

    private int xSerial;

    private int ySerial;

    private Color c;

    public SerialChart(int xSerial, int ySerial, Color c) {
        this.xSerial = xSerial;
        this.ySerial = ySerial;
        this.c = c;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        int[] xSerials = {(xSerial - 20) * n, xSerial * n, (xSerial + 20) * n, xSerial * n};
        int[] ySerials = {ySerial * n, (ySerial - 20) * n, ySerial * n, (ySerial + 20) * n};
        graphics.drawPolygon(xSerials, ySerials, 4);

        int[] xPoints1 = {(xSerial - 6) * n, (xSerial + 6) * n};
        int[] yPoints1 = {(ySerial - 6) * n, (ySerial + 6) * n};
        graphics.drawPolyline(xPoints1, yPoints1, xPoints1.length);

        int[] xPoints2 = {(xSerial - 6) * n, (xSerial + 6) * n};
        int[] yPoints2 = {(ySerial + 6) * n, (ySerial - 6) * n};
        graphics.drawPolyline(xPoints2, yPoints2, xPoints2.length);
    }
}

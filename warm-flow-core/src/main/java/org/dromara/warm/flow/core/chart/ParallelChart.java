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
import org.dromara.warm.flow.core.dto.NodeJson;

import java.awt.*;

/**
 * 流程图并行网关
 *
 * @see <a href="https://warm-flow.dromara.org/master/advanced/chart_manage.html">文档地址</a>
 */
@Getter
@Setter
@Accessors(chain = true)
public class ParallelChart extends FlowChart {

    public Color c;

    private int x;

    private int y;

    private NodeJson nodeJson;

    public ParallelChart(int x, int y, Color c, NodeJson nodeJson) {
        this.x = x;
        this.y = y;
        this.c = c;
        this.nodeJson = nodeJson;
    }


    @Override
    public void draw(Graphics2D graphics) {
        int[] xParallels = {(x - 20) * n, x * n, (x + 20) * n, x * n};
        int[] yParallels = {y * n, (y - 20) * n, y * n, (y + 20) * n};
        // 设置填充颜色
        graphics.setColor(lightColor(c));
        // 填充圆角矩形
        graphics.fillPolygon(xParallels, yParallels, 4);
        graphics.setColor(c);
        graphics.drawPolygon(xParallels, yParallels, 4);

        int[] xPoints1 = {(x - 8) * n, (x + 8) * n};
        int[] yPoints1 = {y * n, y * n};
        graphics.drawPolyline(xPoints1, yPoints1, xPoints1.length);

        int[] xPoints2 = {x * n, x * n};
        int[] yPoints2 = {(y - 8) * n, (y + 8) * n};
        graphics.drawPolyline(xPoints2, yPoints2, xPoints2.length);
    }

    @Override
    public void toOffset(int offsetW, int offsetH) {
        this.x += offsetW;
        this.y += offsetH;
    }
}

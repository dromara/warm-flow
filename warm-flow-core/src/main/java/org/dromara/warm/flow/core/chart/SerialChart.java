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
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 流程图互斥网关
 * @see <a href="https://warm-flow.dromara.org/master/advanced/chart_manage.html">文档地址</a>
 */
@Getter
@Setter
@Accessors(chain = true)
public class SerialChart extends FlowChart {

    public Color c;

    private int x;

    private int y;

    private TextChart textChart;

    private NodeJson nodeJson;

    public SerialChart(int x, int y, Color c, TextChart textChart, NodeJson nodeJson) {
        this.x = x;
        this.y = y;
        this.c = c;
        this.textChart = textChart;
        this.nodeJson = nodeJson;
    }

    @Override
    public void draw(Graphics2D graphics) {
        int[] xSerials = {(x - 20) * n, x * n, (x + 20) * n, x * n};
        int[] ySerials = {y * n, (y - 20) * n, y * n, (y + 20) * n};
        // 设置填充颜色
        graphics.setColor(lightColor(c));
        // 填充菱形矩形
        graphics.fillPolygon(xSerials, ySerials, 4);
        graphics.setColor(c);
        graphics.drawPolygon(xSerials, ySerials, 4);

        int[] xPoints1 = {(x - 6) * n, (x + 6) * n};
        int[] yPoints1 = {(y - 6) * n, (y + 6) * n};
        graphics.drawPolyline(xPoints1, yPoints1, xPoints1.length);

        int[] xPoints2 = {(x - 6) * n, (x + 6) * n};
        int[] yPoints2 = {(y + 6) * n, (y - 6) * n};
        graphics.drawPolyline(xPoints2, yPoints2, xPoints2.length);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setY(textChart.getY() - 5);
            // 填充文字说明
            textChart.setN(n).draw(graphics);
        }
    }

    @Override
    public void toOffset(int offsetW, int offsetH) {
        this.x += offsetW;
        this.y += offsetH;
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.offset(offsetW, offsetH);
        }
    }
}

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
import org.dromara.warm.flow.core.enums.ChartStatus;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 右上角示例
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExampleChart implements FlowChart {
    private int n;

    private int xRect;

    private int yRect;

    private Color c;

    private TextChart textChart;

    public ExampleChart(int xRect, int yRect, Color c, TextChart textChart) {
        this.xRect = xRect;
        this.yRect = yRect;
        this.c = c;
        this.textChart = textChart;
    }

    @Override
    public void draw(Graphics2D graphics) {
        // 设置填充颜色
        graphics.setColor(lightColor(c));
        // 填充圆角矩形
        graphics.fillRoundRect(xRect * n, yRect * n, 60 * n, 20 * n, 5 * n, 5 * n);
        graphics.setColor(c);
        // 保存当前的画笔样式
        Stroke originalStroke = graphics.getStroke();
        // 设置虚线样式，例如 [10, 5] 表示 10 像素的线段和 5 像素的间隔
        if (ChartStatus.TO_DO.getColor().equals(c)) {
            float[] dashPattern = {10f, 5f};
            BasicStroke dashedStroke = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashPattern, 0f);
            graphics.setStroke(dashedStroke);
        }
        graphics.drawRoundRect(xRect * n, yRect  * n, 60 * n, 20 * n, 5 * n, 5 * n);
        graphics.setStroke(originalStroke);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setXText(textChart.getXText() - stringWidth(graphics, textChart.getTitle()) / 2);
            // 填充文字说明
            textChart.setN(n).draw(graphics);
        }
    }
}

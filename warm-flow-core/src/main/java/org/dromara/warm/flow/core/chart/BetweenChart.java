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
import org.dromara.warm.flow.core.utils.DrawUtils;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 流程图中间节点
 */
@Getter
@Setter
@Accessors(chain = true)
public class BetweenChart implements FlowChart {
    private int n;

    private int xRect;

    private int yRect;

    private Color c;

    private TextChart textChart;

    public BetweenChart(int xRect, int yRect, Color c, TextChart textChart) {
        this.xRect = xRect;
        this.yRect = yRect;
        this.c = c;
        this.textChart = textChart;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        graphics.drawRoundRect((xRect - 50) * n, (yRect - 40)  * n, 100 * n, 80 * n, 20 * n, 20 * n);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setXText(textChart.getXText() - DrawUtils.stringWidth(graphics, textChart.getTitle()) / 2);
            // 填充文字说明
            textChart.setN(n).draw(graphics);
        }
    }
}

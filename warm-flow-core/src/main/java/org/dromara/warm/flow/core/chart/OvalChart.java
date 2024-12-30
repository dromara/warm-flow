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
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 流程图开始或者结束节点
 */
@Getter
@Setter
@Accessors(chain = true)
public class OvalChart implements FlowChart {
    private int n;

    private int xStartOval;

    private int yStartOval;

    private Color c;

    private TextChart textChart;

    public OvalChart(int xStartOval, int yStartOval, Color c, TextChart textChart) {
        this.xStartOval = xStartOval;
        this.yStartOval = yStartOval;
        this.c = c;
        this.textChart = textChart;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        graphics.drawOval((xStartOval - 20) * n, (yStartOval - 20) * n, 40 * n, 40 * n);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setXText(textChart.getXText() - stringWidth(graphics, textChart.getTitle()) / 2);
            textChart.setYText(textChart.getYText() + 5);
            // 填充文字说明
            textChart.setN(n).draw(graphics);
        }
    }
}

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

import com.warm.flow.core.utils.DrawUtils;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 流程图中间节点
 */
public class BetweenChart implements FlowChart {
    private int xRect;
    private int yRect;
    private Color c;
    private TextChart textChart;

    public BetweenChart() {
    }

    public BetweenChart(int xRect, int yRect, Color c, TextChart textChart) {
        this.xRect = xRect;
        this.yRect = yRect;
        this.c = c;
        this.textChart = textChart;
    }

    public int getxRect() {
        return xRect;
    }

    public BetweenChart setxRect(int xRect) {
        this.xRect = xRect;
        return this;
    }

    public int getyRect() {
        return yRect;
    }

    public BetweenChart setyRect(int yRect) {
        this.yRect = yRect;
        return this;
    }

    public Color getC() {
        return c;
    }

    public BetweenChart setC(Color c) {
        this.c = c;
        return this;
    }

    public TextChart getTextChart() {
        return textChart;
    }

    public BetweenChart setTextChart(TextChart textChart) {
        this.textChart = textChart;
        return this;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        graphics.drawRoundRect(xRect - 50, yRect - 40, 100, 80, 20, 20);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setxText(textChart.getxText() - DrawUtils.stringWidth(graphics, textChart.getTitle()) / 2);
            // 填充文字说明
            textChart.draw(graphics);
        }
    }
}

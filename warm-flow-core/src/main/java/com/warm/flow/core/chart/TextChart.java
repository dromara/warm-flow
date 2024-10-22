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

import com.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 流程图文字
 */
public class TextChart implements FlowChart {
    private int xText;
    private int yText;

    private String title;

    public TextChart() {
    }

    public TextChart(int xText, int yText, String title) {
        this.xText = xText;
        this.yText = yText;
        this.title = title;
    }

    public int getxText() {
        return xText;
    }

    public TextChart setxText(int xText) {
        this.xText = xText;
        return this;
    }

    public int getyText() {
        return yText;
    }

    public TextChart setyText(int yText) {
        this.yText = yText;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TextChart setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.drawString(StringUtils.isEmpty(title) ? "" : title, xText, yText);
    }
}

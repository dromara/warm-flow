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

import org.dromara.warm.flow.core.utils.DrawUtils;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * 流程图开始或者结束节点
 */
public class SkipChart implements FlowChart {
    private int n;

    private int[] xPoints;

    private int[] yPoints;

    private Color c;

    private TextChart textChart;

    public SkipChart() {
    }

    public SkipChart(int[] xPoints, int[] yPoints, Color c, TextChart textChart) {
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.c = c;
        this.textChart = textChart;
    }

    public int getN() {
        return n;
    }

    public SkipChart setN(int n) {
        this.n = n;
        return this;
    }

    public int[] getxPoints() {
        return xPoints;
    }

    public SkipChart setxPoints(int[] xPoints) {
        this.xPoints = xPoints;
        return this;
    }

    public int[] getyPoints() {
        return yPoints;
    }

    public SkipChart setyPoints(int[] yPoints) {
        this.yPoints = yPoints;
        return this;
    }

    public Color getC() {
        return c;
    }

    public SkipChart setC(Color c) {
        this.c = c;
        return this;
    }

    public TextChart getTextChart() {
        return textChart;
    }

    public SkipChart setTextChart(TextChart textChart) {
        this.textChart = textChart;
        return this;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c);
        // 画跳转线
        xPoints = Arrays.stream(xPoints).map(x -> x * n).toArray();
        yPoints = Arrays.stream(yPoints).map(y -> y * n).toArray();

        graphics.drawPolyline(xPoints, yPoints, xPoints.length);
        // 画箭头， 判断箭头朝向
        int[] xArrow;
        int[] yArrow;
        int xEndOne = xPoints[xPoints.length - 1];
        int xEndTwo = xPoints[xPoints.length - 2];
        int yEndOne = yPoints[yPoints.length - 1];
        int yEndTwo = yPoints[yPoints.length - 2];
        int xArrowLength = 5 * n;
        int yArrowLength = 10 * n;
        if (xEndOne == xEndTwo) {
            xArrow = new int[]{xEndOne - xArrowLength, xEndOne, xEndOne + xArrowLength};
            // 1、 如果最后两个左边x相同，最后一个 < 倒数第二个，朝下
            if (yEndOne > yEndTwo) {
                yArrow = new int[]{yEndOne - yArrowLength, yEndOne, yEndOne - yArrowLength};
            } else {
                // 2、 如果最后两个左边x相同，最后一个 > 倒数第二个，朝上
                yArrow = new int[]{yEndOne + yArrowLength, yEndOne, yEndOne + yArrowLength};
            }
        } else {
            yArrow = new int[]{yEndOne - xArrowLength, yEndOne, yEndOne + xArrowLength};
            // 3、 如果最后两个左边y相同，最后一个 < 倒数第二个，朝左
            if (xEndOne < xEndTwo) {
                xArrow = new int[]{xEndOne + yArrowLength, xEndOne, xEndOne + yArrowLength};
            } else {
                // 4、 如果最后两个左边y相同，最后一个 > 倒数第二个，朝右
                xArrow = new int[]{xEndOne - yArrowLength, xEndOne, xEndOne - yArrowLength};
            }
        }
        graphics.fillPolygon(xArrow, yArrow, 3);
        if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
            textChart.setxText(textChart.getxText() - DrawUtils.stringWidth(graphics, textChart.getTitle()) / 2);
            textChart.setyText(textChart.getyText() - 10);
            // 填充文字说明
            textChart.setN(n).draw(graphics);
        }
    }
}

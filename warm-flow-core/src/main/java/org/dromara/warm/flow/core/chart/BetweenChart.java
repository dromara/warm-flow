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
import org.dromara.warm.flow.core.enums.ChartStatus;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程图中间节点
 *
 * @see <a href="https://warm-flow.dromara.org/master/advanced/chart_manage.html">文档地址</a>
 */
@Getter
@Setter
@Accessors(chain = true)
public class BetweenChart extends FlowChart {

    public Color c;

    /**
     * 矩形左上角顶点的 x 坐标
     */
    private int x;

    /**
     * 矩形左上角顶点的 y 坐标
     */
    private int y;

    private int width = 100;

    private int height = 80;

    private int arcWidth = 20;

    private List<TextChart> textCharts;

    private NodeJson nodeJson;

    public BetweenChart(int x, int y, Color c, List<TextChart> textCharts, NodeJson nodeJson) {
        this.x = x;
        this.y = y;
        this.c = c;
        if (CollUtil.isNotEmpty(textCharts)) {
            this.textCharts = textCharts;
        } else {
            this.textCharts = new ArrayList<>();
        }
         this.nodeJson = nodeJson;
    }

    @Override
    public void draw(Graphics2D graphics) {
        // 设置填充颜色
        graphics.setColor(lightColor(c));
        // 填充圆角矩形
        graphics.fillRoundRect((x - 50) * this.n, (y - 40) * n, width * n, height * n, arcWidth * n, arcWidth * n);
        graphics.setColor(c);
        // 保存当前的画笔样式
        Stroke originalStroke = graphics.getStroke();
        // 设置虚线样式，例如 [10, 5] 表示 10 像素的线段和 5 像素的间隔
        if (ChartStatus.getToDo().equals(c)) {
            float[] dashPattern = {10f, 5f};
            BasicStroke dashedStroke = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashPattern, 0f);
            graphics.setStroke(dashedStroke);
        }
        graphics.drawRoundRect((x - 50) * n, (y - 40) * n, width * n, height * n, arcWidth * n, arcWidth * n);
        graphics.setStroke(originalStroke);
        if (CollUtil.isNotEmpty(textCharts)) {
            for (int i = 0; i < textCharts.size(); i++) {
                TextChart textChart = textCharts.get(i);
                if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
                    String[] lines = textChart.getTitle().split("\n");
                    for (int j = 0; j < lines.length; j++) {
                        TextChart textChartNew = copyText(lines[j], textChart);
                        if (j == 0) {
                            textCharts.set(i, textChartNew);
                        } else {
                            textCharts.add(++i, textChartNew);
                        }
                    }

                }
            }
            int y = this.y - height / 2;
            int unitHeight = height / (textCharts.size() + 1);
            for (int i = 0; i < textCharts.size(); i++) {
                TextChart textChart = textCharts.get(i);
                if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
                    if (textChart.getX() == null) {
                        textChart.setX(this.x);
                    }
                    if (textChart.getY() == null) {
                        textChart.setY(y + (i + 1) * unitHeight);
                    }
                }
            }
            textCharts.forEach(textChart -> {
                if (ObjectUtil.isNotNull(textChart) && StringUtils.isNotEmpty(textChart.getTitle())) {
                    // 填充文字说明
                    textChart.setN(n).draw(graphics);
                }
            });
        }
    }

    @Override
    public void toOffset(int offsetW, int offsetH) {
        this.x += offsetW;
        this.y += offsetH;
    }

    public TextChart copyText(String title, TextChart orgText) {
        TextChart textChart = new TextChart(title, orgText.getFont());
        if (orgText.getX() != null) {
            textChart.setX(orgText.getX());
        }
        if (orgText.getY() != null) {
            textChart.setY(orgText.getY());
        }
        textChart.setN(orgText.n);
        textChart.setC(orgText.c);
        return textChart;
    }

    /**
     * 给节点顶部增加文字说明
     *
     * @param title 文字说明
     */
    public void topText(String title) {
        textCharts.add(new TextChart(title).setY(this.y - height / 2 - 10));
    }

    /**
     * 给节点顶部增加文字说明
     *
     * @param title 文字说明
     * @param c 文字颜色
     */
    public void topText(String title, Color c) {
        textCharts.add(new TextChart(title).setC(c).setY(this.y - height / 2 - 10));
    }

    /**
     * 给节点中追加文字
     *
     * @param title 文字说明
     */
    public void addText(String title) {
        textCharts.add(new TextChart(title));
    }

    /**
     * 给节点中追加文字
     *
     * @param title 文字说明
     * @param c 文字颜色
     */
    public void addText(String title, Color c) {
        textCharts.add(new TextChart(title).setC(c));
    }

}

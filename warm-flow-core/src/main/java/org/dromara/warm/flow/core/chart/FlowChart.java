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

import java.awt.*;

/**
 * 流程图接口
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/advanced/chart_manage.html">文档地址</a>
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class FlowChart {

    public int n;

    public boolean offsetEnable = true;

    /**
     * 绘制流程图
     *
     * @param graphics 画笔
     */
    abstract void draw(Graphics2D graphics);

    public Color lightColor(Color c) {
        if (ChartStatus.getNotDone().equals(c)) {
            return Color.WHITE;
        }
        float red = c.getRed() / 255.0f;
        float green = c.getGreen() / 255.0f;
        float blue = c.getBlue() / 255.0f;
        float alpha = 0.15f; // 透明度

        // 创建带有透明度的颜色
        return new Color(red, green, blue, alpha);
    }

    public void offset(int offsetW, int offsetH) {
        if (offsetEnable) {
            toOffset(offsetW, offsetH);
        }
    }

    public void toOffset(int offsetW, int offsetH) {

    }
}

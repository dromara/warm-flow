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
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;

/**
 * 流程图文字
 */
@Getter
@Setter
@Accessors(chain = true)
public class TextChart implements FlowChart {
    private int n;

    private int x;

    private int y;

    private String title;

    private Font font;

    private float alpha = 1.0f;

    private Color c;

    public TextChart(int x, int y, String title) {
        this.x = x;
        this.y = y;
        this.title = title;
    }

    public TextChart(int x, int y, String title, Font font) {
        this.x = x;
        this.y = y;
        this.title = title;
        this.font = font;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(c == null? Color.BLACK : c);
        if (font != null) {
            graphics.setFont(font);
        }
        this.setX(this.getX() - stringWidth(graphics, this.getTitle()) / 2);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        graphics.drawString(StringUtils.isEmpty(title) ? "" : title, x * n, y * n);
    }

    @Override
    public void offset(int offsetW, int offsetH) {
        this.x += offsetW;
        this.y += offsetH;
    }
}

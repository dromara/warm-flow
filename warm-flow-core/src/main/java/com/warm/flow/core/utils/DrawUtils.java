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
package com.warm.flow.core.utils;

import java.awt.*;

/**
 * @author warm
 * @description: 流程图绘制工具类
 * @date 2023/6/25 16:27
 */
public class DrawUtils {


    private DrawUtils() {

    }

    /**
     * 获取文字的宽度
     *
     * @param graphics
     * @param str
     */
    public static int stringWidth(Graphics2D graphics, String str) {
        FontMetrics fm = graphics.getFontMetrics(new Font("宋体", Font.PLAIN, 12));
        return fm.stringWidth(str);
    }
}

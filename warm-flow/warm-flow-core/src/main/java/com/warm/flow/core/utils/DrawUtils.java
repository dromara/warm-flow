package com.warm.flow.core.utils;

import java.awt.*;

/**
 * @author minliuhua
 * @description: 流程图绘制工具类
 * @date 2023/6/25 16:27
 */
public class DrawUtils {


    private DrawUtils() {

    }

    public static void test(Graphics2D graphics) {
        // 对指定的矩形区域填充颜色
        graphics.setColor(Color.ORANGE);    // GREEN:绿色；  红色：RED;   灰色：GRAY
        graphics.fillRect(0, 0, 360, 480);
        // 对指定的矩形区域填充颜色
        graphics.setColor(Color.PINK);
        graphics.fillRect(360, 0, 360, 480);
    }
}

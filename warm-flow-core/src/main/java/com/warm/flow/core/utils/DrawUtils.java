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

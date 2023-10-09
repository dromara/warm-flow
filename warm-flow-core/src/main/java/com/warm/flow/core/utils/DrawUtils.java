package com.warm.flow.core.utils;

import sun.font.FontDesignMetrics;

import java.awt.*;

/**
 * @author minliuhua
 * @description: 流程图绘制工具类
 * @date 2023/6/25 16:27
 */
public class DrawUtils {


    private DrawUtils() {

    }

    /**
     * 获取文字的宽度
     * @param str
     */
    public static int stringWidth(String str) {
        FontDesignMetrics fm = FontDesignMetrics.getMetrics(new Font("宋体", Font.PLAIN, 12));
        return fm.stringWidth(str);
    }
}

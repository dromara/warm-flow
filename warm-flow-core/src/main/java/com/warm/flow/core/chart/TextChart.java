package com.warm.flow.core.chart;

import com.warm.tools.utils.StringUtils;

import java.awt.*;

/**
 * 流程图文字
 */
public class TextChart implements FlowChart {
    private int xText;
    private int yText;

    private String title;

    public TextChart(String title) {
        this.title = title;
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
        graphics.drawString(StringUtils.isEmpty(title)? "": title, xText, yText);
    }
}

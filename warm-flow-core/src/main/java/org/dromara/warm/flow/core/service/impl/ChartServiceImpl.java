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
package org.dromara.warm.flow.core.service.impl;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.chart.*;
import org.dromara.warm.flow.core.dto.DefChart;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.dto.SkipJson;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.service.ChartService;
import org.dromara.warm.flow.core.utils.Base64;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程图绘制Service业务层处理
 *
 * @author warm
 * @since 2024-12-30
 */
public class ChartServiceImpl implements ChartService {

    private static final Logger log = LoggerFactory.getLogger(ChartServiceImpl.class);

    @Override
    public String chartIns(Long instanceId) {
        DefChart flowChart = chartInsObj(instanceId);
        return basicFlowChart(flowChart.getNodeJsonList(), flowChart.getSkipJsonList());
    }

    @Override
    public String chartDef(Long definitionId) {
        DefChart flowChart = chartDefObj(definitionId);
        return basicFlowChart(flowChart.getNodeJsonList(), flowChart.getSkipJsonList());
    }

    @Override
    public DefChart chartInsObj(Long instanceId) {
        String defJsonStr = FlowEngine.insService().getById(instanceId).getDefJson();
        DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);
        return DefJson.copyChart(defJson);
    }

    @Override
    public DefChart chartDefObj(Long definitionId) {
        DefJson defJson = FlowEngine.defService().queryDesign(definitionId);
        return DefJson.copyChart(defJson);
    }

    /**
     * DefService 根据流程实例ID获取流程图的图片流(渲染颜色)
     * @param nodeJsonList 流程节点对象Vo
     * @param skipJsonList  节点跳转关联对象Vo
     * @return   流程图base64字符串
     */
    public String basicFlowChart(List<NodeJson> nodeJsonList, List<SkipJson> skipJsonList) {

        try {

            Map<String, Integer> nodeXY = new HashMap<>();
            nodeXY.put("minX", 5000);
            nodeXY.put("minY", 5000);
            nodeXY.put("maxX", 0);
            nodeXY.put("maxY", 0);

            FlowChartChain flowChartChain = new FlowChartChain();
            addNodeChart(nodeXY, nodeJsonList, flowChartChain);
            addSkipChart(skipJsonList, flowChartChain);

            // 清晰度
            int n = 2;
            int width = (nodeXY.get("maxX") + nodeXY.get("minX")) * n;
            int height = (nodeXY.get("maxY") + nodeXY.get("minY")) * n;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // 获取图形上下文,graphics想象成一个画笔
            Graphics2D graphics = image.createGraphics();
            graphics.setStroke(new BasicStroke((2 * n) + 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            Font font = new Font("宋体", Font.BOLD, 12 * n);
            graphics.setFont(font);
            // 消除线条锯齿
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // 对指定的矩形区域填充颜色: GREEN:绿色；  红色：RED;   灰色：GRAY
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);

            flowChartChain.draw(graphics, n);
            graphics.setPaintMode();
            graphics.dispose();// 释放此图形的上下文并释放它所使用的所有系统资源

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            return Base64.encode(os.toByteArray());
        } catch (IOException e) {
            log.error("获取流程图异常", e);
            throw new FlowException("获取流程图异常");
        }

    }


    /**
     * 添加节点流程图
     *
     * @param nodeXY 流程图坐标边界
     * @param nodeJsonList 流程节点对象Vo
     */
    private void addNodeChart(Map<String, Integer> nodeXY, List<NodeJson> nodeJsonList
            , FlowChartChain flowChartChain) {
        for (NodeJson nodeJson : nodeJsonList) {
            if (StringUtils.isNotEmpty(nodeJson.getCoordinate())) {
                String[] coordinateSplit = nodeJson.getCoordinate().split("\\|");
                String[] nodeSplit = coordinateSplit[0].split(",");
                int nodeX = Integer.parseInt(nodeSplit[0].split("\\.")[0]);
                int nodeY = Integer.parseInt(nodeSplit[1].split("\\.")[0]);
                if (nodeX > nodeXY.get("maxX")) {
                    nodeXY.put("maxX", nodeX);
                }
                if (nodeX < nodeXY.get("minX")) {
                    nodeXY.put("minX", nodeX);
                }
                if (nodeY > nodeXY.get("maxY")) {
                    nodeXY.put("maxY", nodeY);
                }
                if (nodeY < nodeXY.get("minY")) {
                    nodeXY.put("minY", nodeY);
                }
                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0].split("\\.")[0]);
                    int textY = Integer.parseInt(textSplit[1].split("\\.")[0]);
                    textChart = new TextChart(textX, textY, nodeJson.getNodeName());
                }
                Color c = Color.BLACK;
                if (NodeType.isStart(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, c, textChart));
                } else if (NodeType.isBetween(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new BetweenChart(nodeX, nodeY, c, textChart));
                } else if (NodeType.isGateWaySerial(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new SerialChart(nodeX, nodeY, c));
                } else if (NodeType.isGateWayParallel(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new ParallelChart(nodeX, nodeY, c));
                } else if (NodeType.isEnd(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, c, textChart));
                }
            }
        }
    }

    /**
     * 添加跳转流程图
     *
     * @param skipJsonList 节点跳转关联对象Vo
     * @param flowChartChain 流程图链
     */
    private void addSkipChart(List<SkipJson> skipJsonList, FlowChartChain flowChartChain) {
        for (SkipJson skipJson : skipJsonList) {
            if (StringUtils.isNotEmpty(skipJson.getCoordinate())) {
                String[] coordinateSplit = skipJson.getCoordinate().split("\\|");
                String[] skipSplit = coordinateSplit[0].split(";");
                int[] skipX = new int[skipSplit.length];
                int[] skipY = new int[skipSplit.length];
                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0].split("\\.")[0]);
                    int textY = Integer.parseInt(textSplit[1].split("\\.")[0]);
                    textChart = new TextChart(textX, textY, skipJson.getSkipName());
                }

                for (int i = 0; i < skipSplit.length; i++) {
                    skipX[i] = Integer.parseInt(skipSplit[i].split(",")[0].split("\\.")[0]);
                    skipY[i] = Integer.parseInt(skipSplit[i].split(",")[1].split("\\.")[0]);
                }
                Color c = Color.BLACK;
                flowChartChain.addFlowChart(new SkipChart(skipX, skipY, c, textChart));
            }
        }
    }

}

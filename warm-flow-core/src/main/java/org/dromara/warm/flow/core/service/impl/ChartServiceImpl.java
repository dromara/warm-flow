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
import org.dromara.warm.flow.core.dto.*;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.enums.ChartStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.service.ChartService;
import org.dromara.warm.flow.core.utils.Base64;
import org.dromara.warm.flow.core.utils.StreamUtils;
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
import java.util.stream.Collectors;

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
        initStatus(defJson);
        return DefJson.copyChart(defJson);
    }

    @Override
    public String startMetadata(PathWayData pathWayData) {

        DefJson defJson = FlowEngine.defService().queryDesign(pathWayData.getDefId());
        List<NodeJson> nodeList = defJson.getNodeList();

        Map<String, NodeJson> nodeMap = StreamUtils.toMap(nodeList, NodeJson::getNodeCode
                , node -> node.setStatus(ChartStatus.NOT_DONE.getKey()));
        Map<String, SkipJson> skipMap = nodeList.stream().map(NodeJson::getSkipList).flatMap(List::stream)
                .collect(Collectors.toMap(skip -> skip.getNowNodeCode() + ":" + skip.getSkipType() + ":" + skip.getNextNodeCode(),
                        skip -> skip.setStatus(ChartStatus.NOT_DONE.getKey())));

        pathWayData.getPathWayNodes().forEach(node -> nodeMap.get(node.getNodeCode()).setStatus(ChartStatus.DONE.getKey()));
        pathWayData.getPathWaySkips().forEach(skip -> skipMap.get(skip.getNowNodeCode() + ":" + skip.getSkipType()
                + ":" + skip.getNextNodeCode()).setStatus(ChartStatus.DONE.getKey()));
        pathWayData.getTargetNodes().forEach(node -> nodeMap.get(node.getNodeCode()).setStatus(ChartStatus.TO_DO.getKey()));

        return FlowEngine.jsonConvert.objToStr(defJson);
    }

    @Override
    public String skipMetadata(PathWayData pathWayData) {
        Instance instance = FlowEngine.insService().getById(pathWayData.getInsId());
        String defJsonStr = instance.getDefJson();
        DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);
        List<NodeJson> nodeList = defJson.getNodeList();
        List<SkipJson> skipList = nodeList.stream().map(NodeJson::getSkipList).flatMap(List::stream)
                .collect(Collectors.toList());

        // 如果是之前驳回过的节点，则他对应的跳转线要重新变为未办状态
        Map<String, List<SkipJson>> skipNextMap = StreamUtils.groupByKey(skipList, SkipJson::getNowNodeCode);
        List<SkipJson> skipJsons = skipNextMap.get(pathWayData.getPathWayNodes().get(0).getNodeCode());
        skipJsons.forEach(skipJson -> {
            if (ChartStatus.REJECT.getKey().equals(skipJson.getStatus())) {
                skipJson.setStatus(ChartStatus.NOT_DONE.getKey());
            }
        });

        Map<String, NodeJson> nodeMap = StreamUtils.toMap(nodeList, NodeJson::getNodeCode, node -> node);
        Map<String, SkipJson> skipMap = StreamUtils.toMap(skipList, skip -> skip.getNowNodeCode()
                + ":" + skip.getSkipType() + ":" + skip.getNextNodeCode(), skip -> skip);

        pathWayData.getPathWayNodes().forEach(node -> {
            NodeJson nodeJson = nodeMap.get(node.getNodeCode());
            if (SkipType.isPass(pathWayData.getSkipType())) {
                nodeJson.setStatus(ChartStatus.DONE.getKey());
            } else if (SkipType.isReject(pathWayData.getSkipType())){
                nodeJson.setStatus(ChartStatus.REJECT.getKey());
            }
        });
        pathWayData.getPathWaySkips().forEach(skip -> {
            SkipJson skipJson = skipMap.get(skip.getNowNodeCode() + ":" + skip.getSkipType()+ ":" + skip.getNextNodeCode());
            if (SkipType.isPass(pathWayData.getSkipType())) {
                skipJson.setStatus(ChartStatus.DONE.getKey());
            } else if (SkipType.isReject(pathWayData.getSkipType())){
                skipJson.setStatus(ChartStatus.REJECT.getKey());
            }
        });
        pathWayData.getTargetNodes().forEach(node -> {
            NodeJson nodeJson = nodeMap.get(node.getNodeCode());
            if (NodeType.isEnd(node.getNodeType())) {
                nodeJson.setStatus(ChartStatus.DONE.getKey());
            } else {
                if (SkipType.isPass(pathWayData.getSkipType())) {
                    nodeJson.setStatus(ChartStatus.TO_DO.getKey());
                } else if (SkipType.isReject(pathWayData.getSkipType())){
                    nodeJson.setStatus(ChartStatus.REJECT.getKey());
                }
            }
        });

        return FlowEngine.jsonConvert.objToStr(defJson);
    }

    private void initStatus(DefJson defJson) {
        List<NodeJson> nodeList = defJson.getNodeList();
        List<SkipJson> skipList = nodeList.stream().map(NodeJson::getSkipList).flatMap(List::stream)
                .collect(Collectors.toList());
        nodeList.forEach(node -> node.setStatus(ChartStatus.NOT_DONE.getKey()));
        skipList.forEach(skip -> skip.setStatus(ChartStatus.NOT_DONE.getKey()));
    }

    /**
     * DefService 根据流程实例ID获取流程图的图片流(渲染颜色)
     *
     * @param nodeJsonList 流程节点对象Vo
     * @param skipJsonList 节点跳转关联对象Vo
     * @return 流程图base64字符串
     */
    private String basicFlowChart(List<NodeJson> nodeJsonList, List<SkipJson> skipJsonList) {

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
     * @param nodeXY       流程图坐标边界
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
                Color c = ChartStatus.getColorByKey(nodeJson.getStatus());
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
     * @param skipJsonList   节点跳转关联对象Vo
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
                Color c = ChartStatus.getColorByKey(skipJson.getStatus());
                flowChartChain.addFlowChart(new SkipChart(skipX, skipY, c, textChart));
            }
        }
    }

}

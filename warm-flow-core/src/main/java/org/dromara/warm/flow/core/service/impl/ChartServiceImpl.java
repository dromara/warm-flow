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
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.ChartStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.service.ChartService;
import org.dromara.warm.flow.core.utils.*;
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
import java.util.function.Consumer;
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
        return chartIns(instanceId, chartChain -> {});
    }

    @Override
    public String chartDef(Long definitionId) {
        return chartDef(definitionId, chartChain -> {});
    }

    @Override
    public String chartIns(Long instanceId, Consumer<FlowChartChain> consumer) {
        DefChart flowChart = chartInsObj(instanceId);
        return basicFlowChart(flowChart.getNodeJsonList(), flowChart.getSkipJsonList(), consumer);
    }

    @Override
    public String chartDef(Long definitionId, Consumer<FlowChartChain> consumer) {
        DefChart flowChart = chartDefObj(definitionId);
        return basicFlowChart(flowChart.getNodeJsonList(), flowChart.getSkipJsonList(), consumer);
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
                .collect(Collectors.toMap(this::getSkipKey, skip -> skip.setStatus(ChartStatus.NOT_DONE.getKey())));

        pathWayData.getPathWayNodes().forEach(node -> nodeMap.get(node.getNodeCode()).setStatus(ChartStatus.DONE.getKey()));
        pathWayData.getPathWaySkips().forEach(skip -> skipMap.get(getSkipKey(skip)).setStatus(ChartStatus.DONE.getKey()));
        pathWayData.getTargetNodes().forEach(node -> nodeMap.get(node.getNodeCode()).setStatus(
                NodeType.isEnd(node.getNodeType()) ? ChartStatus.DONE.getKey() : ChartStatus.TO_DO.getKey()
        ));

        return FlowEngine.jsonConvert.objToStr(defJson);
    }

    @Override
    public String skipMetadata(PathWayData pathWayData) {
        Instance instance = FlowEngine.insService().getById(pathWayData.getInsId());
        DefJson defJson = FlowEngine.jsonConvert.strToBean(instance.getDefJson(), DefJson.class);

        List<NodeJson> nodeList = defJson.getNodeList();
        List<SkipJson> skipList = StreamUtils.toListAll(defJson.getNodeList(), NodeJson::getSkipList);
        Map<String, NodeJson> nodeMap = StreamUtils.toMap(nodeList, NodeJson::getNodeCode, node -> node);
        Map<String, SkipJson> skipMap = StreamUtils.toMap(skipList, this::getSkipKey, skip -> skip);

        pathWayData.getPathWayNodes().forEach(node -> {
            NodeJson nodeJson = nodeMap.get(node.getNodeCode());
            if (SkipType.isPass(pathWayData.getSkipType())) {
                nodeJson.setStatus(ChartStatus.DONE.getKey());
            } else if (SkipType.isReject(pathWayData.getSkipType())){
                nodeJson.setStatus(ChartStatus.NOT_DONE.getKey());
            }
        });
        pathWayData.getPathWaySkips().forEach(skip -> {
            SkipJson skipJson = skipMap.get(getSkipKey(skip));
            if (SkipType.isPass(pathWayData.getSkipType())) {
                skipJson.setStatus(ChartStatus.DONE.getKey());
            } else if (SkipType.isReject(pathWayData.getSkipType())){
                skipJson.setStatus(ChartStatus.NOT_DONE.getKey());
            }
        });
        pathWayData.getTargetNodes().forEach(node -> {
            NodeJson nodeJson = nodeMap.get(node.getNodeCode());
            if (NodeType.isEnd(node.getNodeType())) {
                nodeJson.setStatus(ChartStatus.DONE.getKey());
            } else {
                nodeJson.setStatus(ChartStatus.TO_DO.getKey());
            }
        });

        if (SkipType.isReject(pathWayData.getSkipType())) {
            Map<String, List<SkipJson>> skipNextMap = StreamUtils.groupByKeyFilter(skip ->
                    !SkipType.isReject(skip.getSkipType()), skipList, SkipJson::getNowNodeCode);
            pathWayData.getTargetNodes().forEach(node -> rejectReset(node.getNodeCode(), skipNextMap, nodeMap));
        }

        pathWayData.getTargetNodes().forEach(node -> {
            if (NodeType.isEnd(node.getNodeType())) {
                nodeList.forEach(nodeJson -> {
                    if (ChartStatus.isToDo(nodeJson.getStatus())) {
                        nodeJson.setStatus(ChartStatus.NOT_DONE.getKey());
                    }
                });
            }
        });


        return FlowEngine.jsonConvert.objToStr(defJson);
    }

    private String getSkipKey(SkipJson skip) {
        return skip.getNowNodeCode() + ":" + skip.getSkipType() + ":" + skip.getSkipCondition()
                + ":" + skip.getNextNodeCode();
    }

    private String getSkipKey(Skip skip) {
        return skip.getNowNodeCode() + ":" + skip.getSkipType() + ":" + skip.getSkipCondition()
                + ":" + skip.getNextNodeCode();
    }

    private void rejectReset(String nodeCode, Map<String, List<SkipJson>> skipNextMap, Map<String, NodeJson> nodeMap) {
        List<SkipJson> oneNextSkips = skipNextMap.get(nodeCode);
        if (CollUtil.isNotEmpty(oneNextSkips)) {
            oneNextSkips.forEach(oneNextSkip -> {
                if (ObjectUtil.isNotNull(oneNextSkip) &&  !ChartStatus.isNotDone(oneNextSkip.getStatus())) {
                    oneNextSkip.setStatus(ChartStatus.NOT_DONE.getKey());
                    NodeJson nodeJson = nodeMap.get(oneNextSkip.getNextNodeCode());
                    if (ObjectUtil.isNotNull(nodeJson) && !ChartStatus.isNotDone(nodeJson.getStatus())) {
                        nodeJson.setStatus(ChartStatus.NOT_DONE.getKey());
                        rejectReset(nodeJson.getNodeCode(), skipNextMap, nodeMap);
                    }
                }
            });
        }
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
     * @param consumer 可获取流程图对象，可用于修改流程图样式或者新增内容
     * @return 流程图base64字符串
     */
    private String basicFlowChart(List<NodeJson> nodeJsonList, List<SkipJson> skipJsonList, Consumer<FlowChartChain> consumer) {

        try {

            Map<String, Integer> chartXY = new HashMap<>();
            chartXY.put("minX", 5000);
            chartXY.put("minY", 5000);
            chartXY.put("maxX", 0);
            chartXY.put("maxY", 0);

            FlowChartChain flowChartChain = new FlowChartChain();
            addNodeChart(chartXY, nodeJsonList, flowChartChain);
            addSkipChart(chartXY, skipJsonList, flowChartChain);

            // 清晰度
            int n = 2;
            int offset = 100;
            int offsetW = 0;
            int offsetH = 0;
            // 如果有坐标小于0，则设置偏移量
            if (chartXY.get("minX") <0) {
                offsetW = offset - chartXY.get("minX");
                chartXY.put("maxX", chartXY.get("maxX") + offsetW);
                chartXY.put("minX", 0);
            }
            if (chartXY.get("minY") <0) {
                offsetH = offset - chartXY.get("minY");
                chartXY.put("maxY", chartXY.get("maxY") + offsetH);
                chartXY.put("minY", 0);
            }
            int width = (chartXY.get("maxX") + chartXY.get("minX")) * n + offset;;
            int height = (chartXY.get("maxY") + chartXY.get("minY")) * n + offset;

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

            // 提供外部扩展
            consumer.accept(flowChartChain);
            flowChartChain.draw(width, height, offsetW, offsetH, graphics, n);

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
     * @param chartXY       流程图坐标边界
     * @param nodeJsonList 流程节点对象Vo
     * @param flowChartChain 流程图链
     */
    private void addNodeChart(Map<String, Integer> chartXY, List<NodeJson> nodeJsonList
            , FlowChartChain flowChartChain) {
        for (NodeJson nodeJson : nodeJsonList) {
            if (StringUtils.isNotEmpty(nodeJson.getCoordinate())) {
                String[] coordinateSplit = nodeJson.getCoordinate().split("\\|");
                String[] nodeSplit = coordinateSplit[0].split(",");
                int nodeX = Integer.parseInt(nodeSplit[0].split("\\.")[0]);
                int nodeY = Integer.parseInt(nodeSplit[1].split("\\.")[0]);
                setChartXy(chartXY, nodeX, nodeY);
                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0].split("\\.")[0]);
                    int textY = Integer.parseInt(textSplit[1].split("\\.")[0]);
                    if (NodeType.isBetween(nodeJson.getNodeType())) {
                        textChart = new TextChart(nodeJson.getNodeName());
                    } else {
                        textChart = new TextChart(textX, textY, nodeJson.getNodeName());
                    }
                }
                Color c = ChartStatus.getColorByKey(nodeJson.getStatus());
                if (NodeType.isStart(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, c, textChart, nodeJson));
                } else if (NodeType.isBetween(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new BetweenChart(nodeX, nodeY, c, CollUtil.toList(textChart), nodeJson));
                } else if (NodeType.isGateWaySerial(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new SerialChart(nodeX, nodeY, c, textChart, nodeJson));
                } else if (NodeType.isGateWayParallel(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new ParallelChart(nodeX, nodeY, c, nodeJson));
                } else if (NodeType.isEnd(nodeJson.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, c, textChart, nodeJson));
                }
            }
        }
    }

    private static void setChartXy(Map<String, Integer> chartXY, int nodeX, int nodeY) {
        if (nodeX > chartXY.get("maxX")) {
            chartXY.put("maxX", nodeX);
        }
        if (nodeX < chartXY.get("minX")) {
            chartXY.put("minX", nodeX);
        }
        if (nodeY > chartXY.get("maxY")) {
            chartXY.put("maxY", nodeY);
        }
        if (nodeY < chartXY.get("minY")) {
            chartXY.put("minY", nodeY);
        }
    }

    /**
     * 添加跳转流程图
     *
     * @param chartXY       流程图坐标边界
     * @param skipJsonList   节点跳转关联对象Vo
     * @param flowChartChain 流程图链
     */
    private void addSkipChart(Map<String, Integer> chartXY, List<SkipJson> skipJsonList, FlowChartChain flowChartChain) {
        for (SkipJson skipJson : skipJsonList) {
            if (StringUtils.isNotEmpty(skipJson.getCoordinate())) {
                String[] coordinateSplit = skipJson.getCoordinate().split("\\|");

                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0].split("\\.")[0]);
                    int textY = Integer.parseInt(textSplit[1].split("\\.")[0]);
                    textChart = new TextChart(textX, textY, skipJson.getSkipName());
                }

                String[] skipSplit = coordinateSplit[0].split(";");
                int[] skipX = new int[skipSplit.length];
                int[] skipY = new int[skipSplit.length];
                for (int i = 0; i < skipSplit.length; i++) {
                    skipX[i] = Integer.parseInt(skipSplit[i].split(",")[0].split("\\.")[0]);
                    skipY[i] = Integer.parseInt(skipSplit[i].split(",")[1].split("\\.")[0]);
                    setChartXy(chartXY, skipX[i], skipY[i]);
                }
                Color c = ChartStatus.getColorByKey(skipJson.getStatus());
                flowChartChain.addFlowChart(new SkipChart(skipX, skipY, c, textChart));
            }
        }
    }

}

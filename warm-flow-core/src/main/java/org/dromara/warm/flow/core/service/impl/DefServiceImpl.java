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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.dom4j.Document;
import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.chart.*;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.dao.FlowDefinitionDao;
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.ActivityStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.utils.Base64;
import org.dromara.warm.flow.core.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程定义Service业务层处理
 *
 * @author warm
 * @since 2023-03-29
 */
public class DefServiceImpl extends WarmServiceImpl<FlowDefinitionDao<Definition>, Definition> implements DefService {

    private static final Logger log = LoggerFactory.getLogger(DefServiceImpl.class);

    @Override
    public DefService setDao(FlowDefinitionDao<Definition> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public Definition importXml(InputStream is) throws Exception {
        return importFlow(readXml(is));
    }

    @Override
    public FlowCombine readXml(InputStream is) throws Exception {
        if (ObjectUtil.isNull(is)) {
            return null;
        }
        return FlowConfigUtil.readConfig(is);
    }


    @Override
    public Definition importFlow(FlowCombine combine) {
        // 流程定义
        Definition definition = combine.getDefinition();
        // 所有的流程节点
        List<Node> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<Skip> allSkips = combine.getAllSkips();
        // 根据不同策略进行新增
        insertFlow(definition, allNodes, allSkips);
        return definition;
    }

    @Override
    public void insertFlow(Definition definition, List<Node> allNodes, List<Skip> allSkips) {
        definition.setVersion(getNewVersion(definition));
        for (Node node : allNodes) {
            node.setVersion(definition.getVersion());
        }
        FlowFactory.defService().save(definition);
        FlowFactory.nodeService().saveBatch(allNodes);
        FlowFactory.skipService().saveBatch(allSkips);
    }

    @Override
    public void saveXml(Definition def) throws Exception {
        saveXml(def.getId(), def.getXmlString());
    }

    @Override
    public void saveXml(Long id, String xmlString) throws Exception {
        if (ObjectUtil.isNull(id) || StringUtils.isEmpty(xmlString)) {
            return;
        }
        FlowCombine combine = FlowConfigUtil.readConfig(new ByteArrayInputStream
                (xmlString.getBytes(StandardCharsets.UTF_8)));
        // 所有的流程节点
        saveNodeAndSkip(id, combine);
    }

    @Override
    public Document exportXml(Long id) {
        Definition definition = getAllDataDefinition(id);
        return FlowConfigUtil.createDocument(definition);
    }

    @Override
    public String xmlString(Long id) {
        Definition definition = getAllDataDefinition(id);
        Document document = FlowConfigUtil.createDocument(definition);
        return document.asXML();
    }

    @Override
    public List<Definition> queryByCodeList(List<String> flowCodeList) {
        return getDao().queryByCodeList(flowCodeList);
    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        getDao().updatePublishStatus(ids, publishStatus);
    }

    @Override
    public boolean checkAndSave(Definition definition) {
        return save(definition.setVersion(getNewVersion(definition)));
    }

    @Override
    public boolean saveAndInitNode(Definition definition) {
        definition.setVersion(getNewVersion(definition));
        FlowFactory.dataFillHandler().idFill(definition);
        List<Node> nodeList = new ArrayList<>();
        List<Skip> skipList = new ArrayList<>();

        Node startNode = FlowFactory.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode(NodeType.START.getValue())
                .setNodeName("开始")
                .setNodeType(NodeType.START.getKey())
                .setCoordinate("260,200|260,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(startNode);

        Node betweenOneNode = FlowFactory.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode("submit")
                .setNodeName("中间节点-或签1")
                .setNodeType(NodeType.BETWEEN.getKey())
                .setCoordinate("420,200|420,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(betweenOneNode);

        Node betweenTwoNode = FlowFactory.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode("approval")
                .setNodeName("中间节点-或签2")
                .setNodeType(NodeType.BETWEEN.getKey())
                .setCoordinate("600,200|600,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(betweenTwoNode);

        Node endNode = FlowFactory.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode(NodeType.END.getValue())
                .setNodeName("结束")
                .setNodeType(NodeType.END.getKey())
                .setCoordinate("760,200|760,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(endNode);

        skipList.add(FlowFactory.newSkip()
                .setDefinitionId(definition.getId())
                .setNowNodeCode(startNode.getNodeCode())
                .setNextNodeType(startNode.getNodeType())
                .setNextNodeCode(betweenOneNode.getNodeCode())
                .setNextNodeType(betweenOneNode.getNodeType())
                .setSkipType(SkipType.PASS.getKey())
                .setCoordinate("280,200;370,200"));

        skipList.add(FlowFactory.newSkip()
                .setDefinitionId(definition.getId())
                .setNowNodeCode(betweenOneNode.getNodeCode())
                .setNextNodeType(betweenOneNode.getNodeType())
                .setNextNodeCode(betweenTwoNode.getNodeCode())
                .setNextNodeType(betweenTwoNode.getNodeType())
                .setSkipType(SkipType.PASS.getKey())
                .setCoordinate("470,200;550,200"));

        skipList.add(FlowFactory.newSkip()
                .setDefinitionId(definition.getId())
                .setNowNodeCode(betweenTwoNode.getNodeCode())
                .setNextNodeType(betweenTwoNode.getNodeType())
                .setNextNodeCode(endNode.getNodeCode())
                .setNextNodeType(endNode.getNodeType())
                .setSkipType(SkipType.PASS.getKey())
                .setCoordinate("650,200;740,200"));
        FlowFactory.nodeService().saveBatch(nodeList);
        FlowFactory.skipService().saveBatch(skipList);
        return save(definition);
    }

    /**
     * 删除流程定义
     *
     * @param ids 流程定义id
     */
    @Override
    public boolean removeDef(List<Long> ids) {
        ids.forEach(id -> {
            List<Instance> instances = FlowFactory.insService().list(FlowFactory.newIns().setDefinitionId(id));
            AssertUtil.isNotEmpty(instances, ExceptionCons.EXIST_START_TASK);
        });
        FlowFactory.nodeService().deleteNodeByDefIds(ids);
        FlowFactory.skipService().deleteSkipByDefIds(ids);
        return removeByIds(ids);
    }

    @Override
    public boolean publish(Long id) {
        Definition definition = getById(id);
        List<Definition> definitions = list(FlowFactory.newDef().setFlowCode(definition.getFlowCode()));
        // 已发布流程定义，改为已失效或者未发布状态
        List<Long> otherDefIds = definitions.stream()
                .filter(item -> !Objects.equals(definition.getId(), item.getId())
                        && PublishStatus.PUBLISHED.getKey().equals(item.getIsPublish()))
                .map(Definition::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(otherDefIds)) {
            List<Instance> instanceList = FlowFactory.insService().listByDefIds(otherDefIds);
            if (CollUtil.isNotEmpty(instanceList)) {
                // 已发布已使用过的流程定义
                Set<Long> useDefIds = StreamUtils.toSet(instanceList, Instance::getDefinitionId);
                if (CollUtil.isNotEmpty(useDefIds)) {
                    // 已发布已使用过的流程定义，改为已失效
                    updatePublishStatus(new ArrayList<>(useDefIds), PublishStatus.EXPIRED.getKey());

                    // 已发布未使用的流定义
                    otherDefIds.removeIf(useDefIds::contains);
                }

            }
            if (CollUtil.isNotEmpty(otherDefIds)) {
                // 已发布未使用过的流程定义，改为未发布
                updatePublishStatus(otherDefIds, PublishStatus.UNPUBLISHED.getKey());
            }
        }

        Definition flowDefinition = FlowFactory.newDef();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.PUBLISHED.getKey());
        return updateById(flowDefinition);
    }

    @Override
    public boolean unPublish(Long id) {
        List<Instance> instances = FlowFactory.insService().list(FlowFactory.newIns().setDefinitionId(id));
        AssertUtil.isNotEmpty(instances, ExceptionCons.EXIST_START_TASK);
        Definition definition = FlowFactory.newDef().setId(id);
        definition.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(definition);
    }

    @Override
    public boolean copyDef(Long id) {
        Definition definition = getById(id).copy();
        definition.setVersion(getNewVersion(definition));
        AssertUtil.isNull(definition, ExceptionCons.NOT_FOUNT_DEF);

        List<Node> nodeList = FlowFactory.nodeService().list(FlowFactory.newNode().setDefinitionId(id))
                .stream().map(Node::copy).collect(Collectors.toList());
        List<Skip> skipList = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(id))
                .stream().map(Skip::copy).collect(Collectors.toList());
        FlowFactory.dataFillHandler().idFill(definition.setId(null));
        definition.setIsPublish(PublishStatus.UNPUBLISHED.getKey())
                .setCreateTime(null)
                .setUpdateTime(null);

        nodeList.forEach(node -> node.setId(null)
                .setDefinitionId(definition.getId())
                .setVersion(definition.getVersion())
                .setCreateTime(null)
                .setUpdateTime(null));
        FlowFactory.nodeService().saveBatch(nodeList);

        skipList.forEach(skip -> skip.setId(null)
                .setDefinitionId(definition.getId())
                .setCreateTime(null)
                .setUpdateTime(null));
        FlowFactory.skipService().saveBatch(skipList);
        return save(definition);
    }

    @Override
    public boolean active(Long id) {
        Definition definition = getById(id);
        AssertUtil.isTrue(definition.getActivityStatus().equals(ActivityStatus.ACTIVITY.getKey()), ExceptionCons.DEFINITION_ALREADY_ACTIVITY);
        definition.setActivityStatus(ActivityStatus.ACTIVITY.getKey());
        return updateById(definition);
    }

    @Override
    public boolean unActive(Long id) {
        Definition definition = getById(id);
        AssertUtil.isTrue(definition.getActivityStatus().equals(ActivityStatus.SUSPENDED.getKey()), ExceptionCons.DEFINITION_ALREADY_SUSPENDED);
        definition.setActivityStatus(ActivityStatus.SUSPENDED.getKey());
        return updateById(definition);
    }

    @Override
    public String flowChart(Long instanceId) {
        Long definitionId = FlowFactory.insService().getById(instanceId).getDefinitionId();
        return basicFlowChart(instanceId, definitionId);
    }

    @Override
    public List<FlowChart> flowChartData(Long instanceId) {
        Long definitionId = FlowFactory.insService().getById(instanceId).getDefinitionId();
        FlowChartChain flowChartChain = new FlowChartChain();
        basicFlowChart(instanceId, definitionId, flowChartChain);
        return flowChartChain.getFlowChartList();
    }

    @Override
    public String flowChartNoColor(Long definitionId) {
        return basicFlowChart(null, definitionId);
    }

    @Override
    public List<FlowChart> flowChartNoColorData(Long definitionId) {
        FlowChartChain flowChartChain = new FlowChartChain();
        basicFlowChart(null, definitionId, flowChartChain);
        return flowChartChain.getFlowChartList();
    }

    /**
     * DefService 根据流程实例ID获取流程图的图片流(渲染颜色)
     * @param instanceId 实例id
     * @param definitionId  流程定义id
     * @return   流程图base64字符串
     */
    public String basicFlowChart(Long instanceId, Long definitionId) {

        try {
            FlowChartChain flowChartChain = new FlowChartChain();
            Map<String, Integer> nodeXY = basicFlowChart(instanceId, definitionId, flowChartChain);

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
     * 获取流程图基本元数据
     * @param instanceId 实例id
     * @param definitionId  流程定义id
     * @param flowChartChain 存储流程图基本元数据
     * @return Map<String, Integer> 节点坐标信息
     */
    public Map<String, Integer> basicFlowChart(Long instanceId, Long definitionId, FlowChartChain flowChartChain) {
        Instance instance;
        if (ObjectUtil.isNotNull(instanceId)) {
            instance = FlowFactory.insService().getById(instanceId);
        } else {
            instance = null;
        }
        Map<String, Color> colorMap = new HashMap<>();
        Map<String, Integer> nodeXY = addNodeChart(colorMap, instance, definitionId, flowChartChain);
        addSkipChart(colorMap, instance, definitionId, flowChartChain);

        return nodeXY;
    }
    /**
     * 添加跳转流程图
     *
     * @param colorMap 颜色映射
     * @param instance 流程实例
     * @param flowChartChain 流程图链
     */
    private void addSkipChart(Map<String, Color> colorMap, Instance instance, Long definitionId, FlowChartChain flowChartChain) {
        List<Skip> skipList = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(definitionId));
        for (Skip skip : skipList) {
            if (StringUtils.isNotEmpty(skip.getCoordinate())) {
                String[] coordinateSplit = skip.getCoordinate().split("\\|");
                String[] skipSplit = coordinateSplit[0].split(";");
                int[] skipX = new int[skipSplit.length];
                int[] skipY = new int[skipSplit.length];
                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0].split("\\.")[0]);
                    int textY = Integer.parseInt(textSplit[1].split("\\.")[0]);
                    textChart = new TextChart(textX, textY, skip.getSkipName());
                }

                for (int i = 0; i < skipSplit.length; i++) {
                    skipX[i] = Integer.parseInt(skipSplit[i].split(",")[0].split("\\.")[0]);
                    skipY[i] = Integer.parseInt(skipSplit[i].split(",")[1].split("\\.")[0]);
                }
                Color c;
                if (ObjectUtil.isNotNull(instance)) {
                    c = colorGet(colorMap, "skip:" + skip.getId().toString());
                } else {
                    c = Color.BLACK;
                }
                flowChartChain.addFlowChart(new SkipChart(skipX, skipY, c, textChart));
            }
        }
    }

    /**
     * 添加节点流程图
     *
     * @param instance 流程实例
     * @param flowChartChain 流程图链
     */
    private Map<String, Integer> addNodeChart(Map<String, Color> colorMap, Instance instance, Long definitionId
            , FlowChartChain flowChartChain) {
        List<Node> nodeList = FlowFactory.nodeService().list(FlowFactory.newNode().setDefinitionId(definitionId));
        List<Skip> allSkips = FlowFactory.skipService().list(FlowFactory.newSkip()
                .setDefinitionId(definitionId).setSkipType(SkipType.PASS.getKey()));
        if (ObjectUtil.isNotNull(instance)) {
            // 流程图渲染，过滤掉所有后置节点
            List<Node> needChartNodes = filterNodes(instance, allSkips, nodeList);
            setColorMap(colorMap, instance, allSkips, needChartNodes);
        }

        Map<String, Integer> maxR = new HashMap<>();
        maxR.put("minX", 5000);
        maxR.put("minY", 5000);
        maxR.put("maxX", 0);
        maxR.put("maxY", 0);

        for (Node node : nodeList) {
            if (StringUtils.isNotEmpty(node.getCoordinate())) {
                String[] coordinateSplit = node.getCoordinate().split("\\|");
                String[] nodeSplit = coordinateSplit[0].split(",");
                int nodeX = Integer.parseInt(nodeSplit[0].split("\\.")[0]);
                int nodeY = Integer.parseInt(nodeSplit[1].split("\\.")[0]);
                if (nodeX > maxR.get("maxX")) {
                    maxR.put("maxX", nodeX);
                }
                if (nodeX < maxR.get("minX")) {
                    maxR.put("minX", nodeX);
                }
                if (nodeY > maxR.get("maxY")) {
                    maxR.put("maxY", nodeY);
                }
                if (nodeY < maxR.get("minY")) {
                    maxR.put("minY", nodeY);
                }
                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0].split("\\.")[0]);
                    int textY = Integer.parseInt(textSplit[1].split("\\.")[0]);
                    textChart = new TextChart(textX, textY, node.getNodeName());
                }
                Color c;
                if (ObjectUtil.isNotNull(instance)) {
                    c = colorGet(colorMap, "node:" + node.getNodeCode());
                } else {
                    c = Color.BLACK;
                }
                if (NodeType.isStart(node.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, ObjectUtil.isNotNull(instance) ? Color.GREEN : Color.BLACK, textChart));
                } else if (NodeType.isBetween(node.getNodeType())) {
                    flowChartChain.addFlowChart(new BetweenChart(nodeX, nodeY, c, textChart));
                } else if (NodeType.isGateWaySerial(node.getNodeType())) {
                    flowChartChain.addFlowChart(new SerialChart(nodeX, nodeY, c));
                } else if (NodeType.isGateWayParallel(node.getNodeType())) {
                    flowChartChain.addFlowChart(new ParallelChart(nodeX, nodeY, c));
                } else if (NodeType.isEnd(node.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, c, textChart));
                }
            }
        }
        return maxR;
    }

    /**
     * 流程图渲染，过滤掉当前任务后的节点
     *
     * @param instance 流程实例
     * @param allSkips 所有跳转
     * @param nodeList 节点集合
     * @return 过滤后的节点列表
     */
    private List<Node> filterNodes(Instance instance, List<Skip> allSkips, List<Node> nodeList) {
        List<String> allNextNode = new ArrayList<>();
        Map<String, List<Skip>> skipNextMap = StreamUtils.groupByKey(allSkips, Skip::getNowNodeCode);
        if (NodeType.isEnd(instance.getNodeType())) {
            return nodeList;
        }
        List<Task> curTasks = FlowFactory.taskService().list(FlowFactory.newTask().setInstanceId(instance.getId()));
        for (Task curTask : curTasks) {
            List<Skip> nextSkips = skipNextMap.get(curTask.getNodeCode());
            getAllNextNode(nextSkips, allNextNode, skipNextMap);
        }
        return StreamUtils.filter(nodeList, node -> !allNextNode.contains(node.getNodeCode()));
    }

    /**
     * 获取待办任务节点后的所有节点
     *
     * @param nextSkips 当前节点对应的所有跳转
     * @param allNextNode 所有下个任务节点
     */
    private void getAllNextNode(List<Skip> nextSkips, List<String> allNextNode, Map<String, List<Skip>> skipMap) {
        if (CollUtil.isNotEmpty(nextSkips)) {
            for (Skip nextSkip : nextSkips) {
                allNextNode.add(nextSkip.getNextNodeCode());
                List<Skip> nextNextSkips = skipMap.get(nextSkip.getNextNodeCode());
                getAllNextNode(nextNextSkips, allNextNode, skipMap);
            }
        }
    }

    /**
     * 设置节点和跳转对应的颜色
     *
     * @param colorMap 颜色map
     * @param instance 流程实例
     * @param allSkips 所有跳转
     * @param nodeList 节点集合
     */
    public void setColorMap(Map<String, Color> colorMap, Instance instance, List<Skip> allSkips
            , List<Node> nodeList) {
        final Color color = Color.PINK;
        Map<String, List<Skip>> skipLastMap = StreamUtils.groupByKey(allSkips, Skip::getNextNodeCode);
        Map<String, List<Skip>> skipNextMap = StreamUtils.groupByKey(allSkips, Skip::getNowNodeCode);
        List<HisTask> hisTaskList = FlowFactory.hisTaskService().getNoReject(instance.getId());
        for (Node node : nodeList) {
            List<Skip> oneNextSkips = skipNextMap.get(node.getNodeCode());
            List<Skip> oneLastSkips = skipLastMap.get(node.getNodeCode());
            if (NodeType.isStart(node.getNodeType())) {
                colorPut(colorMap, "node:" + node.getNodeCode(), Color.GREEN);
                if (CollUtil.isNotEmpty(oneNextSkips)) {
                    oneNextSkips.forEach(oneNextSkip -> colorPut(colorMap, "skip:" + oneNextSkip.getId().toString(), Color.GREEN));
                }
                continue;
            }
            if (NodeType.isGateWay(node.getNodeType())) {
                continue;
            }
            Task task = FlowFactory.taskService()
                    .getOne(FlowFactory.newTask().setNodeCode(node.getNodeCode()).setInstanceId(instance.getId()));
            HisTask curHisTask = FlowFactory.hisTaskService()
                    .getNoReject(node.getNodeCode(), null, hisTaskList);

            if (CollUtil.isNotEmpty(oneLastSkips)) {
                for (Skip oneLastSkip : oneLastSkips) {
                    Color c = null;
                    if (NodeType.isStart(oneLastSkip.getNowNodeType()) && task == null) {
                        colorPut(colorMap, "node:" + node.getNodeCode(), Color.GREEN);
                        setNextColorMap(colorMap, oneNextSkips, Color.GREEN);
                    } else if (NodeType.isGateWay(oneLastSkip.getNowNodeType())) {
                        // 如果前置节点是网关，那网关前任意一个任务完成就算完成
                        List<Skip> twoLastSkips = skipLastMap.get(oneLastSkip.getNowNodeCode());
                        for (Skip twoLastSkip : twoLastSkips) {
                            HisTask twoLastHisTask = FlowFactory.hisTaskService()
                                    .getNoReject(twoLastSkip.getNowNodeCode(), node.getNodeCode(), hisTaskList);

                            // 前前置节点完成时间是否早于前置节点，如果是串行网关，那前前置节点必须只有一个完成，如果是并行网关都要完成
                            if (task != null) {
                                c = color;
                                if (ObjectUtil.isNotNull(twoLastHisTask)) {
                                    colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), Color.GREEN);
                                    colorPut(colorMap, "node:" + oneLastSkip.getNowNodeCode(), Color.GREEN);
                                }
                            } else {
                                if (NodeType.isEnd(node.getNodeType()) && NodeType.isEnd(instance.getNodeType())) {
                                    HisTask curHisTaskN = FlowFactory.hisTaskService()
                                            .getNoReject(null, node.getNodeCode(), hisTaskList);
                                    if (ObjectUtil.isNotNull(curHisTaskN)) {
                                        c = Color.GREEN;
                                        curHisTaskN = FlowFactory.hisTaskService()
                                                .getNoReject(twoLastSkip.getNowNodeCode(), node.getNodeCode(), hisTaskList);
                                        if (ObjectUtil.isNotNull(curHisTaskN)) {
                                            colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), c);
                                            colorPut(colorMap, "node:" + oneLastSkip.getNowNodeCode(), c);
                                        }
                                    }
                                } else if (curHisTask != null && ObjectUtil.isNotNull(twoLastHisTask) && (twoLastHisTask.getUpdateTime()
                                        .before(curHisTask.getUpdateTime()) || twoLastHisTask.getUpdateTime()
                                        .equals(curHisTask.getUpdateTime()))) {
                                    c = Color.GREEN;
                                    colorPut(colorMap, "node:" + oneLastSkip.getNowNodeCode(), c);
                                } else {
                                    c = Color.BLACK;
                                }
                                if (ObjectUtil.isNotNull(twoLastHisTask)) {
                                    colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), c);
                                }
                            }
                            colorPut(colorMap, "node:" + node.getNodeCode(), c);
                            setNextColorMap(colorMap, oneNextSkips, c);
                        }
                    } else if (NodeType.isEnd(node.getNodeType()) && NodeType.isEnd(instance.getNodeType())) {
                        HisTask curHisTaskN = FlowFactory.hisTaskService()
                                .getNoReject(null, node.getNodeCode(), hisTaskList);
                        if (ObjectUtil.isNotNull(curHisTaskN)) {
                            colorPut(colorMap, "node:" + node.getNodeCode(), Color.GREEN);
                            curHisTaskN = FlowFactory.hisTaskService()
                                    .getNoReject(oneLastSkip.getNowNodeCode(), node.getNodeCode(), hisTaskList);
                            if (ObjectUtil.isNotNull(curHisTaskN)) {
                                colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), Color.GREEN);
                            }
                        }
                    } else {
                        HisTask oneLastHisTask = FlowFactory.hisTaskService()
                                .getNoReject(oneLastSkip.getNowNodeCode(), node.getNodeCode(), hisTaskList);
                        // 前前置节点完成时间是否早于前置节点，如果是串行网关，那前前置节点必须只有一个完成，如果是并行网关都要完成
                        if (task != null) {
                            c = color;
                        } else if (curHisTask != null && ObjectUtil.isNotNull(oneLastHisTask) && (oneLastHisTask.getUpdateTime()
                                .before(curHisTask.getUpdateTime()) || oneLastHisTask.getUpdateTime()
                                .equals(curHisTask.getUpdateTime()))) {
                            c = Color.GREEN;
                        } else {
                            c = Color.BLACK;
                        }

                        if (ObjectUtil.isNotNull(oneLastHisTask)) {
                            colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), c);
                        }
                        colorPut(colorMap, "node:" + node.getNodeCode(), c);
                        setNextColorMap(colorMap, oneNextSkips, c);
                    }
                }
            }
        }
    }

    /**
     * 设置下个节点的颜色
     *
     * @param colorMap 颜色map
     * @param oneNextSkips 下一个跳转
     * @param c 颜色
     */
    private void setNextColorMap(Map<String, Color> colorMap, List<Skip> oneNextSkips, Color c) {
        if (CollUtil.isNotEmpty(oneNextSkips)) {
            oneNextSkips.forEach(oneNextSkip -> {
                colorPut(colorMap, "skip:" + oneNextSkip.getId().toString(), c);
                if (NodeType.isGateWay(oneNextSkip.getNextNodeType()) && (c == Color.GREEN || c == Color.BLACK)) {
                    colorPut(colorMap, "node:" + oneNextSkip.getNextNodeCode(), c);
                }
            });
        }
    }

    /**
     * 优先绿色
     *
     * @param colorMap 颜色map
     * @param key key
     * @param c color
     */
    private void colorPut(Map<String, Color> colorMap, String key, Color c) {
        Color color = colorMap.get(key);
        if (c == Color.GREEN) {
            colorMap.put(key, c);
        } else if (color == null || color == Color.BLACK) {
            colorMap.put(key, c);
        }
    }

    private Color colorGet(Map<String, Color> colorMap, String key) {
        Color color = colorMap.get(key);
        if (color == null) {
            color = Color.BLACK;
        }
        return color;
    }

    public Definition getAllDataDefinition(Long id) {
        Definition definition = getDao().selectById(id);
        List<Node> nodeList = FlowFactory.nodeService().list(FlowFactory.newNode().setDefinitionId(id));
        definition.setNodeList(nodeList);
        List<Skip> skips = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(id));
        Map<String, List<Skip>> flowSkipMap = skips.stream()
                .collect(Collectors.groupingBy(Skip::getNowNodeCode));
        nodeList.forEach(flowNode -> flowNode.setSkipList(flowSkipMap.get(flowNode.getNodeCode())));
        return definition;
    }


    private String getNewVersion(Definition definition) {
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        List<Definition> definitions = getDao().queryByCodeList(flowCodeList);
        int highestVersion = 0;
        String latestNonPositiveVersion = null;
        long latestTimestamp = Long.MIN_VALUE;

        for (Definition otherDef : definitions) {
            if (definition.getFlowCode().equals(otherDef.getFlowCode())) {
                try {
                    int version = Integer.parseInt(otherDef.getVersion());
                    if (version > highestVersion) {
                        highestVersion = version;
                    }
                } catch (NumberFormatException e) {
                    long timestamp = otherDef.getCreateTime().getTime();
                    if (timestamp > latestTimestamp) {
                        latestTimestamp = timestamp;
                        latestNonPositiveVersion = otherDef.getVersion();
                    }
                }
            }
        }

        String version = "1";
        if (highestVersion > 0) {
            version = String.valueOf(highestVersion + 1);
        } else if (latestNonPositiveVersion != null) {
            version = latestNonPositiveVersion + "_1";
        }

        return version;
    }

    @Override
    public void saveJson(Long defId, String jsonString) {
        if (ObjectUtil.isNull(defId) || StringUtils.isEmpty(jsonString)) {
            return;
        }
        FlowCombine combine = JSON.parseObject(jsonString, FlowCombine.class);
        // 校验流程定义合法性
        checkFlowLegal(combine);
        // 保存流程节点和跳转
        saveNodeAndSkip(defId, combine);
    }

    private void checkFlowLegal(FlowCombine combine) {
        Definition definition = combine.getDefinition();
        String flowName = definition.getFlowName();
        AssertUtil.isEmpty(definition.getFlowCode(), "【" + flowName + "】流程flowCode为空!");
        // 节点校验
        List<Node> allNodes = combine.getAllNodes();
        List<Skip> allSkips = combine.getAllSkips();
        Map<String, List<Skip>> skipMap = StreamUtils.groupByKey(allSkips, Skip::getNowNodeCode);
        allNodes.forEach(node -> {
            node.setSkipList(skipMap.get(node.getNodeCode()));
            skipMap.remove(node.getNodeCode());
        });
        AssertUtil.isNotEmpty(skipMap, "[" + flowName + "]" + ExceptionCons.FLOW_HAVE_USELESS_SKIP);
        // 每一个流程的开始节点个数
        Set<String> nodeCodeSet = new HashSet<>();
        // 便利一个流程中的各个节点
        int startNum = 0;
        for (Node node : allNodes) {
            FlowConfigUtil.initNodeAndCondition(node, definition.getId(), definition.getVersion());
            startNum = FlowConfigUtil.checkStartAndSame(node, startNum, flowName, nodeCodeSet);
        }
        AssertUtil.isTrue(startNum == 0, "[" + flowName + "]" + ExceptionCons.LOST_START_NODE);
        // 校验跳转节点的合法性
        FlowConfigUtil.checkSkipNode(allSkips);
        // 校验所有目标节点是否都存在
        FlowConfigUtil.validaIsExistDestNode(allSkips, nodeCodeSet);
    }

    private void saveNodeAndSkip(Long defId, FlowCombine combine) {
        // 所有的流程节点
        List<Node> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<Skip> allSkips = combine.getAllSkips();
        // 删除所有节点和连线
        FlowFactory.nodeService().remove(FlowFactory.newNode().setDefinitionId(defId));
        FlowFactory.skipService().remove(FlowFactory.newSkip().setDefinitionId(defId));

        allNodes.forEach(node -> node.setId(null)
                .setDefinitionId(defId)
                .setCreateTime(null)
                .setUpdateTime(null));

        allSkips.forEach(skip -> skip.setId(null)
                .setDefinitionId(defId)
                .setCreateTime(null)
                .setUpdateTime(null));

        // 保存节点，流程连线，权利人
        FlowFactory.nodeService().saveBatch(allNodes);
        FlowFactory.skipService().saveBatch(allSkips);
    }

    @Override
    public String jsonString(Long defId) {
        return getFlowCombine(defId);
    }

    @Override
    public Definition importJson(InputStream inputStream) throws Exception {
        if (ObjectUtil.isNull(inputStream)) {
            return null;
        }
        FlowCombine flowCombine = JSON.parseObject(readJsonStream(inputStream), FlowCombine.class);
        // 流程定义
        Definition definition = flowCombine.getDefinition();
        FlowFactory.dataFillHandler().idFill(definition);
        // 所有的流程节点
        List<Node> allNodes = flowCombine.getAllNodes();
        allNodes.forEach(node -> node.setDefinitionId(definition.getId()));
        // 所有的流程连线
        List<Skip> allSkips = flowCombine.getAllSkips();
        allSkips.forEach(skip -> skip.setDefinitionId(definition.getId()));
        // 根据不同策略进行新增
        insertFlow(definition, allNodes, allSkips);
        return definition;
    }

    private String readJsonStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } catch (IOException e) {
            throw new FlowException("读取导入的JSON输入流异常：" + e.getMessage(), e);
        }
        if (builder.length() > 0 && builder.charAt(builder.length() - 1) == '\n') {
            // 判断去掉最后一个换行符
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    @Override
    public InputStream exportJson(Long defId) {
        String flowCombine = getFlowCombine(defId);
        try {
            // 格式化 JSON 字符串
            String formattedJson = JSON.toJSONString(JSON.parse(flowCombine), JSONWriter.Feature.PrettyFormat,
                    JSONWriter.Feature.WriteMapNullValue);
            return new ByteArrayInputStream(formattedJson.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("生成 JSON 输入流时发生异常: " + e.getMessage(), e);
        }
    }

    private String getFlowCombine(Long defId) {
        FlowCombine flowCombine = new FlowCombine();
        Definition definition = getDao().selectById(defId);
        definition.setId(null);
        flowCombine.setDefinition(definition);
        List<Node> nodeList = FlowFactory.nodeService().list(FlowFactory.newNode().setDefinitionId(defId));
        nodeList.forEach(node -> {
            node.setId(null);
            node.setDefinitionId(null);
        });
        flowCombine.setAllNodes(nodeList);
        List<Skip> skipList = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(defId));
        skipList.forEach(skip -> {
            skip.setId(null);
            skip.setDefinitionId(null);
        });
        flowCombine.setAllSkips(skipList);
        return JSON.toJSONString(flowCombine);
    }
}

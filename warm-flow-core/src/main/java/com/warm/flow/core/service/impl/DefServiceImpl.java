package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.chart.*;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.dto.FlowCombine;
import com.warm.flow.core.entity.*;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.utils.Base64;
import com.warm.flow.core.utils.*;
import org.dom4j.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程定义Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class DefServiceImpl extends WarmServiceImpl<FlowDefinitionDao<Definition>, Definition> implements DefService {

    @Override
    public DefService setDao(FlowDefinitionDao<Definition> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public Definition importXml(InputStream is) throws Exception {
        if (ObjectUtil.isNull(is)) {
            return null;
        }
        FlowCombine combine = FlowConfigUtil.readConfig(is);
        // 流程定义
        Definition definition = combine.getDefinition();
        // 所有的流程节点
        List<Node> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<Skip> allSkips = combine.getAllSkips();
        // 根据不同策略进行新增或者更新
        updateFlow(definition, allNodes, allSkips);
        return definition;
    }

    @Override
    public void saveXml(Definition def) throws Exception {
        if (ObjectUtil.isNull(def)) {
            return;
        }
        FlowCombine combine = FlowConfigUtil.readConfig(new ByteArrayInputStream(def.getXmlString()
                .getBytes(StandardCharsets.UTF_8)));
        // 所有的流程节点
        List<Node> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<Skip> allSkips = combine.getAllSkips();

        FlowFactory.nodeService().remove(FlowFactory.newNode().setDefinitionId(def.getId()));
        FlowFactory.skipService().remove(FlowFactory.newSkip().setDefinitionId(def.getId()));
        allNodes.forEach(node -> node.setDefinitionId(def.getId()));
        allSkips.forEach(skip -> skip.setDefinitionId(def.getId()));
        // 保存节点，流程连线，权利人
        FlowFactory.nodeService().saveBatch(allNodes);
        FlowFactory.skipService().saveBatch(allSkips);
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
    public void closeFlowByCodeList(List<String> flowCodeList) {
        getDao().closeFlowByCodeList(flowCodeList);
    }

    @Override
    public boolean checkAndSave(Definition definition) {
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        List<Definition> definitions = queryByCodeList(flowCodeList);
        for (Definition otherDef : definitions) {
            if (definition.getFlowCode().equals(otherDef.getFlowCode())
                    && definition.getVersion().equals(otherDef.getVersion())) {
                throw new FlowException(definition.getFlowCode() + "(" + definition.getVersion() + ")" + ExceptionCons.ALREADY_EXIST);
            }
        }
        return save(definition);
    }

    /**
     * 删除流程定义
     *
     * @param ids
     */
    @Override
    public boolean removeDef(List<Long> ids) {
        FlowFactory.nodeService().deleteNodeByDefIds(ids);
        FlowFactory.skipService().deleteSkipByDefIds(ids);
        return removeByIds(ids);
    }

    @Override
    public boolean publish(Long id) {
        Definition definition = getById(id);
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        // 把之前的流程定义改为已失效
        closeFlowByCodeList(flowCodeList);

        Definition flowDefinition = FlowFactory.newDef();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.PUBLISHED.getKey());
        return updateById(flowDefinition);
    }

    @Override
    public boolean unPublish(Long id) {
        List<Task> tasks = FlowFactory.taskService().list(FlowFactory.newTask().setDefinitionId(id));
        AssertUtil.isTrue(CollUtil.isNotEmpty(tasks), ExceptionCons.NOT_PUBLISH_TASK);
        Definition definition = FlowFactory.newDef().setId(id);
        definition.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(definition);
    }

    @Override
    public boolean copyDef(Long id) {
        Definition definition = ClassUtil.clone(getById(id));
        AssertUtil.isTrue(ObjectUtil.isNull(definition), ExceptionCons.NOT_FOUNT_DEF);

        List<Node> nodeList = FlowFactory.nodeService().list(FlowFactory.newNode().setDefinitionId(id))
                .stream().map(ClassUtil::clone).collect(Collectors.toList());
        List<Skip> skipList = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(id))
                .stream().map(ClassUtil::clone).collect(Collectors.toList());
        FlowFactory.dataFillHandler().idFill(definition.setId(null));
        definition.setVersion(definition.getVersion() + "_copy")
                .setIsPublish(PublishStatus.UNPUBLISHED.getKey())
                .setCreateTime(null)
                .setUpdateTime(null);

        nodeList.forEach(node -> node.setId(null)
                .setDefinitionId(definition.getId())
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
    public String flowChart(Long instanceId) throws IOException {
        FlowChartChain flowChartChain = new FlowChartChain();
        Instance instance = FlowFactory.insService().getById(instanceId);
        Map<String, Color> colorMap = new HashMap<>();
        Map<String, Integer> nodeXY = addNodeChart(colorMap, instance, flowChartChain);
        addSkipChart(colorMap, instance, flowChartChain);

        int width = nodeXY.get("maxX") + nodeXY.get("minX");
        int height = nodeXY.get("maxY") + nodeXY.get("minY");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文,graphics想象成一个画笔
        Graphics2D graphics = image.createGraphics();
        graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        graphics.setFont(new Font("宋体", Font.BOLD, 12));
        // 消除线条锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 对指定的矩形区域填充颜色: GREEN:绿色；  红色：RED;   灰色：GRAY
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        flowChartChain.draw(graphics);

        graphics.setPaintMode();
        graphics.dispose();// 释放此图形的上下文并释放它所使用的所有系统资源

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ImageIO.write(image, "jpg", os);
        return Base64.encode(os.toByteArray());

    }

    /**
     * 添加跳转流程图
     *
     * @param colorMap
     * @param instance
     * @param flowChartChain
     */
    private void addSkipChart(Map<String, Color> colorMap, Instance instance, FlowChartChain flowChartChain) {
        List<Skip> skipList = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(instance.getDefinitionId()));
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
                Color c = colorGet(colorMap, "skip:" + skip.getId().toString());
                flowChartChain.addFlowChart(new SkipChart(skipX, skipY, c, textChart));
            }
        }
    }

    /**
     * 添加节点流程图
     *
     * @param instance
     * @param flowChartChain
     */
    private Map<String, Integer> addNodeChart(Map<String, Color> colorMap, Instance instance, FlowChartChain flowChartChain) {
        List<Node> nodeList = FlowFactory.nodeService().list(FlowFactory.newNode().setDefinitionId(instance.getDefinitionId()));
        List<Skip> allSkips = FlowFactory.skipService().list(FlowFactory.newSkip()
                .setDefinitionId(instance.getDefinitionId()).setSkipType(SkipType.PASS.getKey()));
        // 流程图渲染，过滤掉所有后置节点
        List<Node> needChartNodes = filterNodes(instance, allSkips, nodeList);
        setColorMap(colorMap, instance, allSkips, needChartNodes);

        Map<String, Integer> maxR = new HashMap<>();
        maxR.put("minX", 999999999);
        maxR.put("minY", 999999999);
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
                Color c = colorGet(colorMap, "node:" + node.getNodeCode());
                if (NodeType.isStart(node.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, Color.GREEN, textChart));
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
     * @param instance
     * @param allSkips
     * @param nodeList
     * @return
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
     * 获取代办任务节点后的所有节点
     *
     * @param nextkips
     * @param allNextNode
     */
    private void getAllNextNode(List<Skip> nextkips, List<String> allNextNode, Map<String, List<Skip>> skipMap) {
        if (CollUtil.isNotEmpty(nextkips)) {
            for (Skip nextSkip : nextkips) {
                allNextNode.add(nextSkip.getNextNodeCode());
                List<Skip> nextNextSkips = skipMap.get(nextSkip.getNextNodeCode());
                getAllNextNode(nextNextSkips, allNextNode, skipMap);
            }
        }
    }

    /**
     * 设置节点和跳转对应的颜色
     *
     * @param colorMap
     * @param instance
     * @param allSkips
     * @param nodeList
     * @return
     */
    public void setColorMap(Map<String, Color> colorMap, Instance instance, List<Skip> allSkips
            , List<Node> nodeList) {
        final Color color = new Color(255, 145, 158);
        Map<String, List<Skip>> skipLastMap = StreamUtils.groupByKey(allSkips, Skip::getNextNodeCode);
        Map<String, List<Skip>> skipNextMap = StreamUtils.groupByKey(allSkips, Skip::getNowNodeCode);
        for (Node node : nodeList) {
            List<Skip> oneNextSkips = skipNextMap.get(node.getNodeCode());
            if (NodeType.isStart(node.getNodeType())) {
                colorPut(colorMap, "node:" + node.getNodeCode(), Color.GREEN);
                if (CollUtil.isNotEmpty(oneNextSkips)) {
                    oneNextSkips.forEach(oneNextSkip -> colorPut(colorMap, "skip:" + oneNextSkip.getId().toString(), Color.GREEN));
                }
                continue;
            }
            if (NodeType.isEnd(node.getNodeType()) && NodeType.isEnd(instance.getNodeType())) {
                colorPut(colorMap, "node:" + node.getNodeCode(), Color.GREEN);
                continue;
            }
            if (NodeType.isGateWay(node.getNodeType())) {
                continue;
            }
            Task task = FlowFactory.taskService()
                    .getOne(FlowFactory.newTask().setNodeCode(node.getNodeCode()).setInstanceId(instance.getId()));
            HisTask curHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                    .getNoReject(node.getNodeCode(), null, instance.getId()));

            List<Skip> oneLastSkips = skipLastMap.get(node.getNodeCode());
            if (CollUtil.isNotEmpty(oneLastSkips)) {
                for (Skip oneLastSkip : oneLastSkips) {
                    if (NodeType.isStart(oneLastSkip.getNowNodeType()) && task == null) {
                        colorPut(colorMap, "node:" + node.getNodeCode(), Color.GREEN);
                        colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), Color.GREEN);
                        oneNextSkips.forEach(oneNextSkip -> colorPut(colorMap, "skip:" + oneNextSkip.getId().toString(), Color.GREEN));
                    } else if (NodeType.isGateWay(oneLastSkip.getNowNodeType())) {
                        // 如果前置节点是网关，那网关前任意一个任务完成就算完成
                        List<Skip> twoLastSkips = skipLastMap.get(oneLastSkip.getNowNodeCode());
                        for (Skip twoLastSkip : twoLastSkips) {
                            HisTask twoLastHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                                    .getNoReject(twoLastSkip.getNowNodeCode(), node.getNodeCode(), instance.getId()));

                            Color c;
                            // 前前置节点完成时间是否早于前置节点，如果是串行网关，那前前置节点必须只有一个完成，如果是并行网关都要完成
                            if (task != null) {
                                c = color;
                                if (ObjectUtil.isNotNull(twoLastHisTask)) {
                                    colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), Color.GREEN);
                                }
                            } else {
                                if (curHisTask != null && ObjectUtil.isNotNull(twoLastHisTask) && twoLastHisTask.getCreateTime()
                                        .before(curHisTask.getCreateTime())) {
                                    c = Color.GREEN;
                                } else {
                                    c = Color.BLACK;
                                }
                                if (ObjectUtil.isNotNull(twoLastHisTask)) {
                                    colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), c);
                                }
                            }
                            colorPut(colorMap, "node:" + node.getNodeCode(), c);
                            setNextColorMap(colorMap, oneNextSkips, c, skipNextMap);
                        }
                    } else {
                        HisTask oneLastHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                                .getNoReject(oneLastSkip.getNowNodeCode(), node.getNodeCode(), instance.getId()));
                        Color c;
                        // 前前置节点完成时间是否早于前置节点，如果是串行网关，那前前置节点必须只有一个完成，如果是并行网关都要完成
                        if (task != null) {
                            c = color;
                        } else if (curHisTask != null && ObjectUtil.isNotNull(oneLastHisTask) && oneLastHisTask.getCreateTime()
                                .before(curHisTask.getCreateTime())) {
                            c = Color.GREEN;
                        } else {
                            c = Color.BLACK;
                        }

                        if (ObjectUtil.isNotNull(oneLastHisTask)) {
                            colorPut(colorMap, "skip:" + oneLastSkip.getId().toString(), c);
                        }
                        colorPut(colorMap, "node:" + node.getNodeCode(), c);
                        setNextColorMap(colorMap, oneNextSkips, c, skipNextMap);
                    }
                }
            }
        }
    }

    /**
     * 设置下个节点的颜色
     *
     * @param colorMap
     * @param oneNextSkips
     * @param c
     * @param skipNextMap
     */
    private void setNextColorMap(Map<String, Color> colorMap, List<Skip> oneNextSkips, Color c, Map<String, List<Skip>> skipNextMap) {
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
     * @param colorMap
     * @param key
     * @param c
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

    /**
     * 每次只做新增操作,保证新增的flowCode+version是唯一的
     *
     * @param definition
     * @param allNodes
     * @param allSkips
     */
    private void updateFlow(Definition definition, List<Node> allNodes, List<Skip> allSkips) {
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        List<Definition> definitions = getDao().queryByCodeList(flowCodeList);
        for (Definition otherDef : definitions) {
            if (definition.getFlowCode().equals(otherDef.getFlowCode())
                    && definition.getVersion().equals(otherDef.getVersion())) {
                throw new FlowException(definition.getFlowCode() + "(" + definition.getVersion() + ")" + ExceptionCons.ALREADY_EXIST);
            }
        }
        FlowFactory.defService().save(definition);
        FlowFactory.nodeService().saveBatch(allNodes);
        FlowFactory.skipService().saveBatch(allSkips);
    }
}

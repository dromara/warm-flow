package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.chart.*;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.domain.dto.FlowCombine;
import com.warm.flow.core.domain.entity.*;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.mapper.FlowDefinitionMapper;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.core.utils.FlowConfigUtil;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;
import com.warm.tools.utils.*;
import org.dom4j.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程定义Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class DefServiceImpl extends WarmServiceImpl<FlowDefinitionMapper, FlowDefinition> implements DefService {

    @Override
    public Class<FlowDefinitionMapper> getMapperClass() {
        return FlowDefinitionMapper.class;
    }

    @Override
    public void importXml(InputStream is) throws Exception {
        FlowCombine combine = FlowConfigUtil.readConfig(is);
        // 流程定义
        FlowDefinition definition = combine.getDefinition();
        // 所有的流程节点
        List<FlowNode> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<FlowSkip> allSkips = combine.getAllSkips();
        // 根据不同策略进行新增或者更新
        updateFlow(definition, allNodes, allSkips);
    }

    @Override
    public Document exportXml(Long id) {
        FlowDefinition definition = getAllDataDefinition(id);
        return FlowConfigUtil.createDocument(definition);
    }

    public FlowDefinition getAllDataDefinition(Long id) {
        FlowDefinition definition = getMapper().selectById(id);
        FlowNode node = new FlowNode();
        node.setDefinitionId(id);
        List<FlowNode> nodeList = FlowFactory.nodeService().list(node);
        definition.setNodeList(nodeList);
        FlowSkip flowSkip = new FlowSkip();
        flowSkip.setDefinitionId(id);
        List<FlowSkip> flowSkips = FlowFactory.skipService().list(flowSkip);
        Map<Long, List<FlowSkip>> flowSkipMap = flowSkips.stream()
                .collect(Collectors.groupingBy(FlowSkip::getNodeId));
        nodeList.forEach(flowNode -> flowNode.setSkipList(flowSkipMap.get(flowNode.getId())));

        return definition;
    }

    /**
     * 每次只做新增操作,保证新增的flowCode+version是唯一的
     *
     * @param definition
     * @param allNodes
     * @param allSkips
     */
    private void updateFlow(FlowDefinition definition, List<FlowNode> allNodes, List<FlowSkip> allSkips) {
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        List<FlowDefinition> flowDefinitions = getMapper().queryByCodeList(flowCodeList);
        for (int j = 0; j < flowDefinitions.size(); j++) {
            FlowDefinition beforeDefinition = flowDefinitions.get(j);
            if (definition.getFlowCode().equals(beforeDefinition.getFlowCode()) && definition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(definition.getFlowCode() + "(" + definition.getVersion() + ")" + ExceptionCons.ALREADY_EXIST);
            }
        }
        FlowFactory.defService().save(definition);
        FlowFactory.nodeService().saveBatch(allNodes);
        FlowFactory.skipService().saveBatch(allSkips);
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        return getMapper().queryByCodeList(flowCodeList);
    }

    @Override
    public void closeFlowByCodeList(List<String> flowCodeList) {
        getMapper().closeFlowByCodeList(flowCodeList);
    }

    @Override
    public boolean checkAndSave(FlowDefinition flowDefinition) {
        List<String> flowCodeList = Collections.singletonList(flowDefinition.getFlowCode());
        List<FlowDefinition> flowDefinitions = queryByCodeList(flowCodeList);
        for (FlowDefinition beforeDefinition : flowDefinitions) {
            if (flowDefinition.getFlowCode().equals(beforeDefinition.getFlowCode()) && flowDefinition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(flowDefinition.getFlowCode() + "(" + flowDefinition.getVersion() + ")" + ExceptionCons.ALREADY_EXIST);
            }
        }
        return save(flowDefinition);
    }

    /**
     * 删除流程定义
     *
     * @param ids
     */
    @Override
    public boolean removeDef(List<Long> ids) {
        getMapper().deleteNodeByDefIds(ids);
        getMapper().deleteSkipByDefIds(ids);
        return removeByIds(ids);
    }

    @Override
    public boolean publish(Long id) {
        FlowDefinition definition = getById(id);
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        // 把之前的流程定义改为已失效
        closeFlowByCodeList(flowCodeList);

        FlowDefinition flowDefinition = new FlowDefinition();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.PUBLISHED.getKey());
        return updateById(flowDefinition);
    }

    @Override
    public boolean unPublish(Long id) {
        List<FlowTask> flowTasks = FlowFactory.taskService().list(new FlowTask().setDefinitionId(id));
        AssertUtil.isTrue(CollUtil.isNotEmpty(flowTasks), ExceptionCons.NOT_PUBLISH_TASK);
        FlowDefinition flowDefinition = new FlowDefinition();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(flowDefinition);
    }

    @Override
    public String flowChart(Long instanceId) throws IOException {
        // 绘制宽=480，长=640的图板
        int width = 1500, height = 500;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文,graphics想象成一个画笔
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        // 消除线条锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 对指定的矩形区域填充颜色
        graphics.setColor(Color.WHITE);    // GREEN:绿色；  红色：RED;   灰色：GRAY
        graphics.fillRect(0, 0, width, height);

        FlowChartChain flowChartChain = new FlowChartChain();
        FlowInstance instance = FlowFactory.insService().getById(instanceId);
        addNodeChart(instance, flowChartChain);
        addSkipChart(instance, flowChartChain);
        flowChartChain.draw(graphics);

        graphics.setPaintMode();
        graphics.translate(400, 600);
        graphics.dispose();// 释放此图形的上下文并释放它所使用的所有系统资源

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        return Base64.encode(os.toByteArray());

    }

    /**
     * 添加跳转流程图
     *
     * @param instance
     * @param flowChartChain
     */
    private void addSkipChart(FlowInstance instance, FlowChartChain flowChartChain) {
        List<FlowSkip> skipList = FlowFactory.skipService().list(new FlowSkip().setDefinitionId(instance.getDefinitionId()));
        for (FlowSkip flowSkip : skipList) {
            if (StringUtils.isNotEmpty(flowSkip.getCoordinate())) {
                String[] coordinateSplit = flowSkip.getCoordinate().split("\\|");
                String[] skipSplit = coordinateSplit[0].split(";");
                int[] skipX = new int[skipSplit.length];
                int[] skipY = new int[skipSplit.length];
                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0]);
                    int textY = Integer.parseInt(textSplit[1]);
                    textChart = new TextChart(textX, textY, StringUtils.isEmpty(flowSkip.getSkipCondition()) ?
                            SkipType.getValueByKey(flowSkip.getSkipType()) : flowSkip.getSkipCondition());
                }

                for (int i = 0; i < skipSplit.length; i++) {
                    skipX[i] = Integer.parseInt(skipSplit[i].split(",")[0]);
                    skipY[i] = Integer.parseInt(skipSplit[i].split(",")[1]);
                }
                flowChartChain.addFlowChart(new SkipChart(skipX, skipY, textChart));
            }
        }
    }

    /**
     * 添加节点流程图
     *
     * @param instance
     * @param flowChartChain
     */
    private void addNodeChart(FlowInstance instance, FlowChartChain flowChartChain) {
        List<FlowNode> nodeList = FlowFactory.nodeService().list(new FlowNode().setDefinitionId(instance.getDefinitionId()));
        List<FlowSkip> allSkips = FlowFactory.skipService().list(new FlowSkip()
                .setDefinitionId(instance.getDefinitionId()));
        for (FlowNode flowNode : nodeList) {
            if (StringUtils.isNotEmpty(flowNode.getCoordinate())) {
                String[] coordinateSplit = flowNode.getCoordinate().split("\\|");
                String[] nodeSplit = coordinateSplit[0].split(",");
                int nodeX = Integer.parseInt(nodeSplit[0]);
                int nodeY = Integer.parseInt(nodeSplit[1]);
                TextChart textChart = null;
                if (coordinateSplit.length > 1) {
                    String[] textSplit = coordinateSplit[1].split(",");
                    int textX = Integer.parseInt(textSplit[0]);
                    int textY = Integer.parseInt(textSplit[1]);
                    textChart = new TextChart(textX, textY, flowNode.getNodeName());
                }
                if (NodeType.isStart(flowNode.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, true));
                } else if (NodeType.isBetween(flowNode.getNodeType())) {
                    Color c = nodeIsFinish(flowNode.getNodeCode(), allSkips, instance.getId());
                    flowChartChain.addFlowChart(new BetweenChart(nodeX, nodeY, c, textChart));
                } else if (NodeType.isEnd(flowNode.getNodeType())) {
                    flowChartChain.addFlowChart(new OvalChart(nodeX, nodeY, false));
                }
            }
        }
    }

    /**
     * 判断节点是否完成,是否代办，显示对应的颜色
     *
     * @param nodeCode
     * @param allSkips
     * @param instanceId
     * @return
     */
    public Color nodeIsFinish(String nodeCode, List<FlowSkip> allSkips, Long instanceId) {
        FlowTask flowTask = FlowFactory.taskService()
                .getOne(new FlowTask().setNodeCode(nodeCode).setInstanceId(instanceId));
        // 查询前置节点是否完成
        if (ObjectUtil.isNotNull(flowTask)) {
            return Color.RED;
        }
        FlowHisTask curHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                .getNoReject(nodeCode, instanceId));
        // 查询前置节点是否完成
        if (ObjectUtil.isNull(curHisTask)) {
            return Color.BLACK;
        }

        Map<String, List<FlowSkip>> skipNextMap = StreamUtils.groupByKey(allSkips, FlowSkip::getNextNodeCode);
        List<FlowSkip> oneLastSkips = skipNextMap.get(nodeCode);
        if (CollUtil.isNotEmpty(oneLastSkips)) {
            for (FlowSkip oneLastSkip : oneLastSkips) {
                if (NodeType.isStart(oneLastSkip.getNowNodeType())) {
                    return Color.GREEN;
                } else if (NodeType.isGateWay(oneLastSkip.getNowNodeType())) {
                    // 如果前置节点是网关，那网关前任意一个任务完成就算完成
                    List<FlowSkip> twoLastSkips = skipNextMap.get(oneLastSkip.getNowNodeCode());
                    for (FlowSkip twoLastSkip : twoLastSkips) {
                        FlowHisTask twoLastHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                                .getNoReject(twoLastSkip.getNowNodeCode(), instanceId));
                        if (ObjectUtil.isNotNull(twoLastHisTask) && twoLastHisTask.getCreateTime()
                                .before(curHisTask.getCreateTime())) {
                            return Color.GREEN;
                        }
                    }
                } else {
                    FlowHisTask twoLastHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                            .getNoReject(oneLastSkip.getNowNodeCode(), instanceId));
                    // 前前置节点完成时间是否早于前置节点，如果是串行网关，那前前置节点必须只有一个完成，如果是并行网关都要完成
                    if (ObjectUtil.isNotNull(twoLastHisTask) && twoLastHisTask.getCreateTime()
                            .before(curHisTask.getCreateTime())) {
                        return Color.GREEN;
                    }
                }
            }
        }
        return Color.BLACK;
    }
}

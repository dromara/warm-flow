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
import org.dromara.warm.flow.core.dto.*;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.ChartStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.ChartService;

import org.dromara.warm.flow.core.utils.*;

import java.awt.*;
import java.util.ArrayList;
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
            } else if (SkipType.isReject(pathWayData.getSkipType())) {
                nodeJson.setStatus(ChartStatus.NOT_DONE.getKey());
            }
        });
        pathWayData.getPathWaySkips().forEach(skip -> {
            SkipJson skipJson = skipMap.get(getSkipKey(skip));
            if (SkipType.isPass(pathWayData.getSkipType())) {
                skipJson.setStatus(ChartStatus.DONE.getKey());
            } else if (SkipType.isReject(pathWayData.getSkipType())) {
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

    @Override
    public List<String> getChartRgb() {
        List<String> chartStatusColor = new ArrayList<>();
        Color done = ChartStatus.getDone();
        chartStatusColor.add(done.getRed() + "," + done.getGreen() + "," + done.getBlue());
        Color toDo = ChartStatus.getToDo();
        chartStatusColor.add(toDo.getRed() + "," + toDo.getGreen() + "," + toDo.getBlue());
        Color notDone = ChartStatus.getNotDone();
        chartStatusColor.add(notDone.getRed() + "," + notDone.getGreen() + "," + notDone.getBlue());
        return chartStatusColor;
    }

    private String getSkipKey(SkipJson skip) {
        return StringUtils.join(new String[]{
                skip.getNowNodeCode(),
                skip.getSkipType(),
                skip.getSkipCondition(),
                skip.getNextNodeCode()}, ":");
    }

    private String getSkipKey(Skip skip) {
        return StringUtils.join(new String[]{
                skip.getNowNodeCode(),
                skip.getSkipType(),
                skip.getSkipCondition(),
                skip.getNextNodeCode()}, ":");
    }

    private void rejectReset(String nodeCode, Map<String, List<SkipJson>> skipNextMap, Map<String, NodeJson> nodeMap) {
        List<SkipJson> oneNextSkips = skipNextMap.get(nodeCode);
        if (CollUtil.isNotEmpty(oneNextSkips)) {
            oneNextSkips.forEach(oneNextSkip -> {
                if (ObjectUtil.isNotNull(oneNextSkip) && !ChartStatus.isNotDone(oneNextSkip.getStatus())) {
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
}

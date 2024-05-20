package com.warm.flow.core.utils;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dto.FlowCombine;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.enums.UserType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流程配置帮助类
 *
 * @author zhoukai
 */
public class FlowConfigUtil {
    /**
     * 无参构造
     */
    private FlowConfigUtil() {

    }

    /**
     * 读取配置
     *
     * @param is
     * @throws Exception
     */
    public static FlowCombine readConfig(InputStream is) throws Exception {
        Definition definition = readDocument(is);
        return structureFlow(definition);
    }

    @SuppressWarnings("unchecked")
    public static Definition readDocument(InputStream is) throws Exception {
        AssertUtil.isNull(is, "文件不存在！");
        // 获取流程节点
        Element definitionElement = new SAXReader().read(is).getRootElement();
        AssertUtil.isTrue(ObjectUtil.isNull(definitionElement), "流程为空！");

        // 读取流程定义
        Definition definition = FlowFactory.newDef();
        definition.setFlowCode(definitionElement.attributeValue("flowCode"));
        definition.setFlowName(definitionElement.attributeValue("flowName"));
        definition.setVersion(definitionElement.attributeValue("version"));
        definition.setFromCustom(definitionElement.attributeValue("fromCustom"));
        definition.setFromPath(definitionElement.attributeValue("fromPath"));

        List<Element> nodesElement = definitionElement.elements();
        // 遍历一个流程中的各个节点
        List<Node> nodeList = definition.getNodeList();
        List<User> userList = definition.getUserList();
        for (Element nodeElement : nodesElement) {
            Node node = initNodeAndCondition(nodeElement);
            nodeList.add(node);
            // 初始化节点的权限用户
            List<User> users = initUser(nodeElement, node);
            if (CollUtil.isNotEmpty(users)){
                userList.addAll(users);
            }
        }
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return definition;
    }

    /**
     * 读取工作节点和跳转条件
     *
     * @param nodeElement
     * @return
     */
    private static Node initNodeAndCondition(Element nodeElement) {
        Node node = FlowFactory.newNode();
        node.setNodeType(NodeType.getKeyByValue(nodeElement.attributeValue("nodeType")));
        node.setNodeCode(nodeElement.attributeValue("nodeCode"));
        node.setNodeName(nodeElement.attributeValue("nodeName"));
        node.setCoordinate(nodeElement.attributeValue("coordinate"));
        node.setSkipAnyNode(nodeElement.attributeValue("skipAnyNode"));
        node.setListenerType(nodeElement.attributeValue("listenerType"));
        node.setListenerPath(nodeElement.attributeValue("listenerPath"));
        FlowFactory.dataFillHandler().idFill(node);

        List<Element> skipsElement = nodeElement.elements();
        List<Skip> skips = node.getSkipList();
        // 遍历节点下的跳转条件
        for (Element skipElement : skipsElement) {
            Skip skip = FlowFactory.newSkip();
            if ("skip".equals(skipElement.getName())) {
                skip.setNowNodeCode(node.getNodeCode());
                skip.setNowNodeType(node.getNodeType());
                skip.setNextNodeCode(skipElement.getText());
                // 条件约束
                skip.setSkipName(skipElement.attributeValue("skipName"));
                skip.setSkipType(skipElement.attributeValue("skipType"));
                skip.setCoordinate(skipElement.attributeValue("coordinate"));
                skip.setSkipCondition(skipElement.attributeValue("skipCondition"));
                skips.add(skip);
            }
        }
        return node;
    }

    private static List<User> initUser(Element nodeElement, Node node){
        // 初始化流程节点的权限
        List<String> permissions = CollUtil.strToColl(nodeElement.attributeValue("permissionFlag"), ",");
        if(CollUtil.isNotEmpty(permissions)){
            return StreamUtils.toList(permissions, permission -> FlowFactory.userService()
                    .getUser(node.getId(), permission, UserType.PROPOSE.getKey()));
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public static Document createDocument(Definition definition) {
        // 创建document对象
        Document document = DocumentHelper.createDocument();
        // 创建根节点bookRoot
        Element definitionElement = document.addElement("definition");
        // 向子节点中添加属性
        definitionElement.addAttribute("flowCode", definition.getFlowCode());
        definitionElement.addAttribute("flowName", definition.getFlowName());
        definitionElement.addAttribute("version", definition.getVersion());
        definitionElement.addAttribute("fromCustom", definition.getFromCustom());
        definitionElement.addAttribute("fromPath", definition.getFromPath());

        List<Node> nodeList = definition.getNodeList();
        for (Node node : nodeList) {
            // 向节点中添加子节点
            Element nodeElement = definitionElement.addElement("node");
            nodeElement.addAttribute("nodeType", NodeType.getValueByKey(node.getNodeType()));
            nodeElement.addAttribute("nodeCode", node.getNodeCode());
            nodeElement.addAttribute("nodeName", node.getNodeName());
            List<String> permission = FlowFactory.userService().getPermission(node.getId());
            if (CollUtil.isNotEmpty(permission)) {
                nodeElement.addAttribute("permissionFlag", CollUtil.strListToStr(permission, ","));
            }
            nodeElement.addAttribute("coordinate", node.getCoordinate());
            nodeElement.addAttribute("skipAnyNode", node.getSkipAnyNode());
            nodeElement.addAttribute("listenerType", node.getListenerType());
            nodeElement.addAttribute("listenerPath", node.getListenerPath());

            List<Skip> skipList = node.getSkipList();
            if (CollUtil.isNotEmpty(skipList)) {
                for (Skip skip : skipList) {
                    Element skipElement = nodeElement.addElement("skip");
                    skipElement.addAttribute("coordinate", skip.getCoordinate());
                    if (StringUtils.isNotEmpty(skip.getSkipType())) {
                        AssertUtil.isFalse(StringUtils.isNotEmpty(skip.getNextNodeCode()), "下一个流程节点编码为空");
                        skipElement.addAttribute("skipType", skip.getSkipType());
                    }
                    if (StringUtils.isNotEmpty(skip.getSkipName())) {
                        skipElement.addAttribute("skipName", skip.getSkipName());
                    }
                    if (StringUtils.isNotEmpty(skip.getSkipCondition())) {
                        skipElement.addAttribute("skipCondition", skip.getSkipCondition());
                    }
                    skipElement.addText(skip.getNextNodeCode());
                }
            }
        }
        return document;
    }


    private static FlowCombine structureFlow(Definition definition) {
        // 获取流程
        FlowCombine combine = new FlowCombine();
        // 流程定义
        combine.setDefinition(definition);
        // 所有的流程节点
        List<Node> allNodes = combine.getAllNodes();
        // 所有的流程节点权限人
        List<User> allUsers = combine.getAllUsers();
        // 所有的流程连线
        List<Skip> allSkips = combine.getAllSkips();

        String flowName = definition.getFlowName();
        AssertUtil.isBlank(definition.getFlowCode(), "【" + flowName + "】流程flowCode为空!");
        AssertUtil.isBlank(definition.getVersion(), "【" + flowName + "】流程version为空!");
        // 发布
        definition.setIsPublish(0);
        definition.setUpdateTime(new Date());
        FlowFactory.dataFillHandler().idFill(definition);

        // 处理节点权限人
        List<User> userList = definition.getUserList();
        allUsers.addAll(userList);

        List<Node> nodeList = definition.getNodeList();
        // 每一个流程的开始节点个数
        int startNum = 0;
        Set<String> nodeCodeSet = new HashSet<String>();
        // 便利一个流程中的各个节点
        for (Node node : nodeList) {
            initNodeAndCondition(node, definition.getId(), definition.getVersion());
            if (NodeType.isStart(node.getNodeType())) {
                startNum++;
                AssertUtil.isTrue(startNum > 1, "[" + flowName + "]" + ExceptionCons.MUL_START_NODE);
            }
            // 保证不存在重复的nodeCode
            AssertUtil.isTrue(nodeCodeSet.contains(node.getNodeCode()),
                    "【" + flowName + "】" + ExceptionCons.SAME_NODE_CODE);
            nodeCodeSet.add(node.getNodeCode());
            allNodes.add(node);
            allSkips.addAll(node.getSkipList());
        }
        Map<String, Integer> skipMap = StreamUtils.toMap(allNodes, Node::getNodeCode, Node::getNodeType);
        allSkips.forEach(allSkip -> allSkip.setNextNodeType(skipMap.get(allSkip.getNextNodeCode())));
        AssertUtil.isTrue(startNum == 0, "[" + flowName + "]" + ExceptionCons.LOST_START_NODE);
        // 校验跳转节点的合法性
        checkSkipNode(allSkips, nodeList);
        // 校验所有目标节点是否都存在
        validaIsExistDestNode(allSkips, nodeCodeSet);
        return combine;
    }

    /**
     * 校验跳转节点的合法性
     *
     * @param allSkips
     * @param nodeList
     */
    private static void checkSkipNode(List<Skip> allSkips, List<Node> nodeList) {
        Map<String, Node> flowNodeMap = StreamUtils.toMap(nodeList, Node::getNodeCode, node -> node);
        Map<String, List<Skip>> allSkipMap = StreamUtils.groupByKey(allSkips, Skip::getNowNodeCode);
        List<Skip> gatewaySkips = new ArrayList<>();
        // TODO min 2023-10-20 ,之前定义的规则，现在已经不适用了，后面观察，会有啥影响
        // for (FlowSkip allSkip : allSkips) {
        //     allSkip.setNextNodeType(flowNodeMap.get(allSkip.getNextNodeCode()).getNodeType());
        //     if (NodeType.isGateWay(allSkip.getNowNodeType())) {
        //         gatewaySkips.add(allSkip);
        //     }
        //     // 中间节点不可退回到网关节点
        //     AssertUtil.isTrue(!NodeType.isGateWay(allSkip.getNowNodeType())
        //                     && SkipType.isReject(allSkip.getSkipType())
        //                     && NodeType.isGateWay(allSkip.getNextNodeType())
        //             , ExceptionCons.BETWEEN_REJECT_GATEWAY);
        // }
        // 校验网关节点不可直连
        if (CollUtil.isNotEmpty(gatewaySkips)) {
            for (Skip gatewaySkip1 : gatewaySkips) {
                for (Skip gatewaySkip2 : gatewaySkips) {
                    AssertUtil.isTrue(gatewaySkip1.getNextNodeCode().equals(gatewaySkip2.getNowNodeCode())
                                    && gatewaySkip1.getNowNodeType().equals(gatewaySkip2.getNowNodeType())
                            , ExceptionCons.GATEWAY_NOT_CONNECT);
                }
            }
        }
        // 不可同时通过或者退回到多个中间节点，必须先流转到网关节点
        allSkipMap.forEach((key, values) -> {
            AtomicInteger passNum = new AtomicInteger();
            AtomicInteger rejectNum = new AtomicInteger();
            for (Skip value : values) {
                if (NodeType.isBetween(value.getNowNodeType()) && NodeType.isBetween(value.getNextNodeType())) {
                    if (SkipType.isPass(value.getSkipType())) {
                        passNum.getAndIncrement();
                    } else {
                        rejectNum.getAndIncrement();
                    }
                }
            }
            AssertUtil.isTrue(passNum.get() > 1 || rejectNum.get() > 1, ExceptionCons.MUL_SKIP_BETWEEN);
        });
    }

    /**
     * 校验所有的目标节点是否存在
     *
     * @param allSkips
     * @param nodeCodeSet
     */
    private static void validaIsExistDestNode(List<Skip> allSkips, Set<String> nodeCodeSet) {
        for (int i = 0; i < allSkips.size(); i++) {
            String nextNodeCode = allSkips.get(i).getNextNodeCode();
            AssertUtil.isTrue(!nodeCodeSet.contains(nextNodeCode), "【" + nextNodeCode + "】" + ExceptionCons.NULL_NODE_CODE);
        }
    }


    /**
     * 读取工作节点和跳转条件
     *
     * @param node
     * @param definitionId
     * @param version
     * @return
     */
    private static void initNodeAndCondition(Node node, Long definitionId, String version) {
        String nodeName = node.getNodeName();
        String nodeCode = node.getNodeCode();
        List<Skip> skipList = node.getSkipList();
        if (!NodeType.isEnd(node.getNodeType())) {
            AssertUtil.isTrue(CollUtil.isEmpty(skipList), "开始和中间节点必须有跳转规则");
        }
        AssertUtil.isBlank(nodeCode, "[" + nodeName + "]" + ExceptionCons.LOST_NODE_CODE);

        node.setVersion(version);
        node.setDefinitionId(definitionId);
        node.setUpdateTime(new Date());

        // 中间节点的集合， 跳转类型和目标节点不能重复
        Set<String> betweenSet = new HashSet<>();
        // 网关的集合 跳转条件和下目标节点不能重复
        Set<String> gateWaySet = new HashSet<>();
        int skipNum = 0;
        // 遍历节点下的跳转条件
        for (Skip skip : skipList) {
            if (NodeType.isStart(node.getNodeType())) {
                skipNum++;
                AssertUtil.isTrue(skipNum > 1, "[" + node.getNodeName() + "]" + ExceptionCons.MUL_START_SKIP);
            }
            AssertUtil.isBlank(skip.getNextNodeCode(), "【" + nodeName + "】" + ExceptionCons.LOST_DEST_NODE);
            FlowFactory.dataFillHandler().idFill(skip);
            // 流程id
            skip.setDefinitionId(definitionId);
            // 节点id
            skip.setNodeId(node.getId());
            if (NodeType.isGateWaySerial(node.getNodeType())) {
                String target = skip.getSkipCondition() + ":" + skip.getNextNodeCode();
                AssertUtil.isTrue(gateWaySet.contains(target), "[" + nodeName + "]" + ExceptionCons.SAME_CONDITION_NODE);
                gateWaySet.add(target);
            } else if (NodeType.isGateWayParallel(node.getNodeType())) {
                String target = skip.getNextNodeCode();
                AssertUtil.isTrue(gateWaySet.contains(target), "[" + nodeName + "]" + ExceptionCons.SAME_DEST_NODE);
                gateWaySet.add(target);
            } else {
                String value = skip.getSkipType() + ":" + skip.getNextNodeCode();
                AssertUtil.isTrue(betweenSet.contains(value), "[" + nodeName + "]" + ExceptionCons.SAME_CONDITION_VALUE);
                betweenSet.add(value);
            }
        }
    }

}

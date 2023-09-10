package com.warm.flow.core.utils;

import com.warm.flow.core.constant.FlowConstant;
import com.warm.flow.core.domain.dto.FlowCombine;
import com.warm.flow.core.domain.entity.FlowDefinition;
import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.flow.core.domain.entity.FlowSkip;
import com.warm.flow.core.enums.NodeType;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.IdUtils;
import com.warm.tools.utils.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        FlowDefinition definition = readDocument(is);
        FlowCombine combine = structureFlow(definition);
        return combine;
    }

    @SuppressWarnings("unchecked")
    public static FlowDefinition readDocument(InputStream is) throws Exception {
        AssertUtil.isNull(is, "文件不存在！");
        // 获取流程结点
        List<Element> flowElements = new SAXReader().read(is).getRootElement().elements();
        AssertUtil.isTrue(CollUtil.isNotEmpty(flowElements) && flowElements.size() == 1,
                "流程为空，或者一次只能导入一条流程定义！");

        Element definitionElement = flowElements.get(0);
        // 读取流程定义
        FlowDefinition definition = new FlowDefinition();
        definition.setFlowCode(definitionElement.attributeValue("flowCode"));
        definition.setFlowName(definitionElement.attributeValue("flowName"));
        definition.setVersion(definitionElement.attributeValue("version"));
        definition.setFromCustom(definitionElement.attributeValue("fromCustom"));
        definition.setFromPath(definitionElement.attributeValue("fromPath"));

        List<Element> nodesElement = definitionElement.elements();
        // 遍历一个流程中的各个结点
        List<FlowNode> nodeList = definition.getNodeList();
        for (Element nodeElement : nodesElement) {
            FlowNode node = initNodeAndCondition(nodeElement);
            nodeList.add(node);
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
     * 读取工作结点和跳转条件
     *
     * @param nodeElement
     * @return
     */
    private static FlowNode initNodeAndCondition(Element nodeElement) {
        FlowNode node = new FlowNode();
        node.setNodeType(NodeType.getKeyByValue(nodeElement.attributeValue("nodeType")));
        node.setNodeCode(nodeElement.attributeValue("nodeCode"));
        node.setNodeName(nodeElement.attributeValue("nodeName"));
        node.setPermissionFlag(nodeElement.attributeValue("permissionFlag"));

        List<Element> skipsElement = nodeElement.elements();
        List<FlowSkip> skips = node.getSkipList();
        // 遍历结点下的跳转条件
        for (Element skipElement : skipsElement) {
            FlowSkip skip = new FlowSkip();
            if ("skip".equals(skipElement.getName())) {
                skip.setNextNodeCode(skipElement.getText());
                // 条件约束
                skip.setSkipType(skipElement.attributeValue("skipType"));
                skip.setSkipMode(skipElement.attributeValue("skipMode"));
                skip.setSkipCondition(skipElement.attributeValue("skipCondition"));
                skips.add(skip);
            }
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    public static Document createDocument(FlowDefinition definition) {
        // 创建document对象
        Document document = DocumentHelper.createDocument();
        // 创建根节点bookRoot
        Element root = document.addElement("definitionConfig");
        // 向根节点中添加第一个节点
        Element definitionElement = root.addElement("definition");
        // 向子节点中添加属性
        definitionElement.addAttribute("flowCode", definition.getFlowCode());
        definitionElement.addAttribute("flowName", definition.getFlowName());
        definitionElement.addAttribute("version", definition.getVersion());
        definitionElement.addAttribute("fromCustom", definition.getFromCustom());
        definitionElement.addAttribute("fromPath", definition.getFromPath());

        List<FlowNode> nodeList = definition.getNodeList();
        for (FlowNode node : nodeList) {
            // 向节点中添加子节点
            Element nodeElement = definitionElement.addElement("node");
            nodeElement.addAttribute("nodeType", NodeType.getValueByKey(node.getNodeType()));
            nodeElement.addAttribute("nodeCode", node.getNodeCode());
            nodeElement.addAttribute("nodeName", node.getNodeName());
            nodeElement.addAttribute("permissionFlag", node.getPermissionFlag());

            List<FlowSkip> skipList = node.getSkipList();
            if (CollUtil.isNotEmpty(skipList)) {
                for (FlowSkip skip : skipList) {
                    Element skipElement = nodeElement.addElement("skip");
                    if (StringUtils.isNotEmpty(skip.getSkipType())) {
                        AssertUtil.isTrue(StringUtils.isNotEmpty(skip.getSkipMode()), "跳转方式不能为空");
                        AssertUtil.isTrue(StringUtils.isNotEmpty(skip.getSkipType()), "跳转类型不能为空");
                        AssertUtil.isTrue(StringUtils.isNotEmpty(skip.getNextNodeCode()), "下一个流程结点编码为空");
                        skipElement.addAttribute("skipType", skip.getSkipType());
                        skipElement.addAttribute("skipMode", skip.getSkipMode());
                        skipElement.addAttribute("skipCondition", skip.getSkipCondition());
                    }
                    skipElement.addText(skip.getNextNodeCode());
                }
            }
        }
        return document;
    }


    private static FlowCombine structureFlow(FlowDefinition definition) {
        // 获取流程
        FlowCombine combine = new FlowCombine();
        // 流程定义
        combine.setDefinition(definition);
        // 所有的流程结点
        List<FlowNode> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<FlowSkip> allSkips = combine.getAllSkips();

        String flowName = definition.getFlowName();
        AssertUtil.isBlank(definition.getFlowCode(), "【" + flowName + "】流程flowCode为空!");
        AssertUtil.isBlank(definition.getVersion(), "【" + flowName + "】流程version为空!");
        Long id = IdUtils.nextId();
        // 发布
        definition.setIsPublish(0);
        definition.setUpdateTime(new Date());
        definition.setId(id);

        List<FlowNode> nodeList = definition.getNodeList();
        // 每一个流程的开始结点个数
        int startNum = 0;
        Set<String> nodeCodeSet = new HashSet<String>();
        // 便利一个流程中的各个结点
        for (FlowNode node : nodeList) {
            initNodeAndCondition(node, id, definition.getVersion());
            if (NodeType.START.getKey().equals(node.getNodeType())) {
                startNum++;
                AssertUtil.isFalse(startNum > 1, "[" + flowName + "]" + FlowConstant.MUL_START_NODE);
            }
            // 保证不存在重复的nodeCode
            AssertUtil.isFalse(nodeCodeSet.contains(node.getNodeCode()),
                    "【" + flowName + "】" + FlowConstant.SAME_NODE_CODE);
            nodeCodeSet.add(node.getNodeCode());
            allNodes.add(node);
            allSkips.addAll(node.getSkipList());
        }

        AssertUtil.isFalse(startNum == 0, "[" + flowName + "]" + FlowConstant.LOST_START_NODE);
        // 校验所有目标结点是否都存在
        validaIsExistDestNode(allSkips, nodeCodeSet);
        return combine;
    }

    /**
     * 校验所有的目标结点是否存在
     *
     * @param allSkips
     * @param nodeCodeSet
     */
    private static void validaIsExistDestNode(List<FlowSkip> allSkips, Set<String> nodeCodeSet) {
        for (int i = 0; i < allSkips.size(); i++) {
            String nextNodeCode = allSkips.get(i).getNextNodeCode();
            AssertUtil.isFalse(!nodeCodeSet.contains(nextNodeCode), "【" + nextNodeCode + "】" + FlowConstant.NULL_NODE_CODE);
        }
    }


    /**
     * 读取工作结点和跳转条件
     *
     * @param node
     * @param definitionId
     * @param version
     * @return
     */
    private static void initNodeAndCondition(FlowNode node, Long definitionId, String version) {
        String nodeName = node.getNodeName();
        String nodeCode = node.getNodeCode();
        List<FlowSkip> skipList = node.getSkipList();
        if (!NodeType.END.getKey().equals(node.getNodeType())) {
            AssertUtil.isFalse(CollUtil.isEmpty(skipList), "开始和中间结点必须有跳转规则");
        }
        AssertUtil.isBlank(nodeCode, "[" + nodeName + "]" + FlowConstant.LOST_NODE_CODE);

        node.setId(IdUtils.nextId());
        node.setVersion(version);
        node.setDefinitionId(definitionId);
        node.setUpdateTime(new Date());

        // 跳转条件的集合
        Set<String> conditionSet = new HashSet<>();
        // 目标结点的集合 这两个集合都不能重复
        Set<String> targetSet = new HashSet<>();
        // 遍历结点下的跳转条件
        for (int i = 0; i < skipList.size(); i++) {
            FlowSkip skip = skipList.get(i);
            skip.setId(IdUtils.nextId());
            // 流程id
            skip.setDefinitionId(definitionId);
            // 结点id
            skip.setNodeId(node.getId());
            // 起始结点
            skip.setNowNodeCode(nodeCode);
            // 目标结点
            String target = skip.getSkipCondition() + ":" + skip.getNextNodeCode();
            AssertUtil.isBlank(target, "【" + nodeName + "】" + FlowConstant.LOST_DEST_NODE);
            AssertUtil.isFalse(targetSet.contains(target), "[" + nodeName + "]" + FlowConstant.SAME_DEST_NODE);
            targetSet.add(target);
            String value = skip.getSkipType() + ":" + skip.getSkipCondition();
            AssertUtil.isFalse(conditionSet.contains(value), "[" + nodeName + "]" + FlowConstant.SAME_CONDITION_VALUE);
            conditionSet.add(value);
        }
    }

}

const NODE_TYPE_MAP = {0: 'start', 1: 'between', 2: 'end', 3: 'serial', 4: 'parallel', 5: 'inclusive'}

/**
 * 将warm-flow的定义json数据转成LogicFlow支持的数据格式
 * @param {*} json
 * @returns LogicFlow的数据
 */
export const json2LogicFlowJson = (definition) => {
  const graphData = {
    nodes: [],
    edges: []
  }
  // 解析definition属性
  graphData.flowCode = definition.flowCode
  graphData.flowName = definition.flowName
  graphData.modelValue = definition.modelValue
  graphData.category = definition.category
  graphData.version = definition.version
  graphData.formCustom = definition.formCustom
  graphData.formPath = definition.formPath
  graphData.listenerType = definition.listenerType
  graphData.listenerPath = definition.listenerPath

  if (!definition.nodeList) {
    return graphData;
  }
  // 解析节点
  const allSkips = definition.nodeList.reduce((acc, node) => {
    if (node.skipList && Array.isArray(node.skipList)) {
      acc.push(...node.skipList);
    }
    return acc;
  }, [])
  const allNodes = definition.nodeList;
  // 解析节点
  if (allNodes.length) {
    for (var i = 0, len = allNodes.length; i < len; i++) {
      let node = allNodes[i]
      let lfNode = {
        text:{},
        properties: {}
      }
      // 处理节点
      lfNode.type = NODE_TYPE_MAP[node.nodeType]
      lfNode.id = node.nodeCode
      let coordinate = node.coordinate
      if (coordinate) {
        const attr = coordinate.split('|')
        const nodeXy = attr[0].split(',')
        lfNode.x = parseInt(nodeXy[0])
        lfNode.y = parseInt(nodeXy[1])
        if (attr.length === 2) {
          const textXy = attr[1].split(',')
          lfNode.text.x = parseInt(textXy[0])
          lfNode.text.y = parseInt(textXy[1])
        }
      }
      lfNode.text.value = node.nodeName
      lfNode.properties.nodeRatio = node.nodeRatio.toString()
      lfNode.properties.permissionFlag = node.permissionFlag
      lfNode.properties.anyNodeSkip = node.anyNodeSkip
      lfNode.properties.listenerType = node.listenerType
      lfNode.properties.listenerPath = node.listenerPath
      lfNode.properties.formCustom = node.formCustom
      lfNode.properties.formPath = node.formPath
      lfNode.properties.status = node.status
      lfNode.properties.chartStatusColor = definition.chartStatusColor
      lfNode.properties.promptContent = node.promptContent
      lfNode.properties.ext = {};
      if (node.ext && typeof node.ext === "string") {
        try {
          node.ext = JSON.parse(node.ext);
          node.ext.forEach(e => {
            lfNode.properties.ext[e.code] = String(e.value).includes(",") ? e.value.split(",") : String(e.value);
          });
        } catch (error) {
          console.error("Error parsing JSON:", error);
        }
      }
      graphData.nodes.push(lfNode)
    }
  }
  if (allSkips.length) {
    // 处理边
    let skipEle = null
    let edge = {}
    for (var j = 0, lenn = allSkips.length; j < lenn; j++) {
      skipEle = allSkips[j]
      edge = {
        text: {},
        properties: {},
      }
      edge.id = skipEle.id
      edge.type = 'skip'
      edge.sourceNodeId = skipEle.nowNodeCode
      edge.targetNodeId = skipEle.nextNodeCode
      edge.text = { value: skipEle.skipName }
      edge.properties.skipCondition = skipEle.skipCondition
      edge.properties.skipName = skipEle.skipName
      edge.properties.skipType = skipEle.skipType
      edge.properties.status = skipEle.status
      edge.properties.chartStatusColor = definition.chartStatusColor
      const expr = skipEle.expr
      if (expr) {
        edge.properties.expr = skipEle.expr
      }
      const coordinate = skipEle.coordinate
      if (coordinate) {
        const coordinateXy = coordinate.split('|')
        edge.pointsList = []
        coordinateXy[0].split(';').forEach((item) => {
          const pointArr = item.split(',')
          edge.pointsList.push({
            x: parseInt(pointArr[0]),
            y: parseInt(pointArr[1])
          })
        })
        edge.startPoint = edge.pointsList[0]
        edge.endPoint = edge.pointsList[edge.pointsList.length - 1]
        if (coordinateXy.length > 1) {
          let textXy = coordinateXy[1].split(",");
          edge.text.x = parseInt(textXy[0])
          edge.text.y = parseInt(textXy[1])
        }
      }
      graphData.edges.push(edge)
    }
  }

  return graphData
}

/**
 * 将LogicFlow的数据转成warm-flow的json定义文件
 * @param {*} data(...definitionInfo,nodes,edges)
 * @returns
 */
export const logicFlowJsonToWarmFlow = (data) => {
  // 先构建成流程对象
  const definition = {
    nodeList : []
  }

  /**
   * 根据节点的类型值，获取key
   * @param {*} mapValue 节点类型映射
   * @returns
   */
  const getNodeTypeValue = (mapValue) => {
    for (const key in NODE_TYPE_MAP) {
      if (NODE_TYPE_MAP[key] === mapValue) {
        return key
      }
    }
  }
  /**
   * 根据节点的编码，获取节点的类型
   * @param {*} nodeCode 当前节点名称
   * @returns
   */
  const getNodeType = (nodeCode) => {
    for (const node of definition.nodeList) {
      if (nodeCode === node.nodeCode) {
        return node.nodeType
      }
    }
  }
  /**
   * 拼接skip坐标
   * @param {*} edge logicFlow的edge
   * @returns
   */
  const getCoordinate = (edge) => {
    let coordinate = ''
    for (let i = 0; i < edge.pointsList.length; i++) {
      coordinate = coordinate + parseInt(edge.pointsList[i].x) + ',' + parseInt(edge.pointsList[i].y)
      if (i !== edge.pointsList.length - 1) {
        coordinate = coordinate + ';'
      }
    }
    if (edge.text) {
      coordinate = coordinate + '|' + parseInt(edge.text.x) + ',' + parseInt(edge.text.y)
    }
    return coordinate
  }

  // 流程定义
  definition.id = data.id
  definition.flowCode = data.flowCode
  definition.flowName = data.flowName
  definition.modelValue = data.modelValue
  definition.category = data.category
  definition.version = data.version
  definition.formCustom = data.formCustom
  definition.formPath = data.formPath
  definition.listenerType = data.listenerType
  definition.listenerPath = data.listenerPath
  // 流程节点
  data.nodes.forEach(anyNode => {
    let node = {}
    node.nodeType = getNodeTypeValue(anyNode.type)
    node.nodeCode = anyNode.id
    if (anyNode.text) {
      node.nodeName = anyNode.text.value
    }
    node.permissionFlag = anyNode.properties.permissionFlag
    node.nodeRatio = anyNode.properties.nodeRatio
    node.anyNodeSkip = anyNode.properties.anyNodeSkip
    node.listenerType = anyNode.properties.listenerType
    node.listenerPath = anyNode.properties.listenerPath
    node.formCustom = anyNode.properties.formCustom
    if (anyNode.properties.formPath && String(anyNode.properties.formPath).trim()) {
      node.formPath = anyNode.properties.formPath.trim()
    }
    node.ext = [];
    for (const key in anyNode.properties.ext) {
      if (Object.prototype.hasOwnProperty.call(anyNode.properties.ext, key)) {
        let e = anyNode.properties.ext[key];
        node.ext.push({ code: key, value: Array.isArray(e) ? e.join(",") : e });
      }
    }
    // node.ext中value为空则移除这条记录
    node.ext = node.ext.filter(item => {
      const value = item.value
      return value !== undefined && value !== null && value !== '' && value !== 'null' && value !== 'undefined'
    });
    node.ext = JSON.stringify(node.ext);
    node.coordinate = anyNode.x + ',' + anyNode.y
    if (anyNode.text && anyNode.text.x && anyNode.text.y) {
      node.coordinate = node.coordinate + '|' + anyNode.text.x + ',' + anyNode.text.y
    }
    node.handlerType = anyNode.properties.handlerType
    node.handlerPath = anyNode.properties.handlerPath
    node.version = definition.version
    node.skipList = []
    data.edges.forEach(anyEdge => {
      if (anyEdge.sourceNodeId === anyNode.id) {
        let skip = {}
        skip.skipType = anyEdge.properties.skipType
        skip.skipCondition = anyEdge.properties.skipCondition
        skip.skipName = anyEdge?.text?.value || anyEdge.properties.skipName
        skip.nowNodeCode = anyEdge.sourceNodeId
        skip.nowNodeType = getNodeType(skip.nowNodeCode)
        skip.nextNodeCode = anyEdge.targetNodeId
        skip.nextNodeType = getNodeType(skip.nextNodeCode)
        skip.coordinate = getCoordinate(anyEdge)
        node.skipList.push(skip)
      }
    })
    definition.nodeList.push(node)
  })
  return JSON.stringify(definition)
}

export const setCommonStyle = (style, properties, nodeType, type) => {
  // 从 chartStatusColor 数组中提取颜色值（后台传三原色则用后台的，否则用默认值）
  const [doneColor, todoColor, notDoneColor] = (properties.chartStatusColor &&
      properties.chartStatusColor.length === 3) ? properties.chartStatusColor :
      ["56,161,105", "245,158,11", "107,114,128"]; // 工作流语义色

  let currentColor = notDoneColor;
  // 检测暗黑模式
  const isDark = typeof document !== 'undefined' && document.documentElement.classList.contains('dark');

  if (properties.status === 2) {
    // 使用活跃状态的 RGB 颜色
    if (nodeType === 'node' && type !== "mimic") {
      style.fill = `rgba(${doneColor}, 0.08)`;}
    style.stroke = `rgb(${doneColor})`;
    currentColor = doneColor;
  } else if (properties.status === 1) {
    // 使用非活跃状态的 RGB 颜色
    if (nodeType === 'node' && type !== "mimic") {
      style.fill = `rgba(${todoColor}, 0.08)`;
    }
    style.stroke = `rgb(${todoColor})`;
    style.strokeDasharray = "5 4";
    currentColor = todoColor;
  } else {
    // 默认状态（暗黑模式适配：深色背景替代纯白）
    if (nodeType === 'node' && type !== "mimic") {
      style.fill = isDark ? `rgba(${notDoneColor}, 0.04)` : 'rgba(255, 255, 255, 0.95)';
    }
    style.stroke = `rgb(${notDoneColor})`;
    currentColor = notDoneColor;
  }

  // ========== 现代化样式增强 ==========
  style.strokeWidth = 1.5;
  style.cursor = 'pointer';
  style.strokeLinejoin = 'round';
  style.strokeLinecap = 'round';

  // 柔和阴影（所有节点统一）
  style.shadow = `drop-shadow(0 2px 8px rgba(${currentColor}, 0.12)) drop-shadow(0 1px 3px rgba(0, 0, 0, 0.06))`;

  // 存储当前状态色供节点 View 层使用（用于渐变、图标着色等）
  style._statusColorRGB = currentColor;
  style._statusHex = `rgb(${currentColor})`;
  style._statusRgba = (alpha) => `rgba(${currentColor}, ${alpha})`;

  return style;
}

/**
 * 经典模式「设计态」语义色增强
 *
 * 设计器里节点没有运行状态（status 为空），原本统一渲染成中性灰（notDoneColor），整体偏灰、辨识度低。
 * 这里在「设计器页面内 + 节点无在办/已办状态」时，按节点类型赋予清爽的语义色（开始绿 / 审批蓝 / 结束红 / 网关橙青紫），
 * 让各类节点一眼可辨，同时把发灰的投影替换成同色系柔和阴影。
 *
 * 安全边界（不破坏既有契约）：
 * - 仅在设计器页面生效（window.__WF_FLOW_DESIGN_MODE__），业务侧渲染的流程实例进度图不受影响；
 * - status 为「在办(1) / 已办(2)」时直接返回，继续沿用后端 chartStatusColor 的状态语义；
 * - 仅经典模式节点调用本函数，仿钉钉模式不调用、行为完全不变。
 *
 * @param {Object} style setCommonStyle 处理后的样式对象
 * @param {Object} properties 节点 / 边属性
 * @param {string} rgb 该节点类型的语义色，形如 '64,158,255'
 * @returns {Object} 处理后的样式对象
 */
export const applyClassicDesignColor = (style, properties, rgb) => {
  const inDesigner = typeof window !== 'undefined' && window.__WF_FLOW_DESIGN_MODE__;
  // 非设计器页面，或节点已有运行状态：保持 setCommonStyle 的状态语义色不变
  if (!inDesigner || properties.status === 1 || properties.status === 2) {
    return style;
  }
  style.stroke = `rgb(${rgb})`;
  style._statusColorRGB = rgb;
  style._statusHex = `rgb(${rgb})`;
  style._statusRgba = (alpha) => `rgba(${rgb}, ${alpha})`;
  // 同色系柔和阴影，替代原先发灰的投影
  style.shadow = `drop-shadow(0 2px 6px rgba(${rgb}, 0.14)) drop-shadow(0 1px 2px rgba(0, 0, 0, 0.04))`;
  return style;
};

export function getPreviousNodes(nodes, skips, nowNodeCode) {
  let previousCode = getPreviousCode(skips, nowNodeCode, new Set());
  // 使用 Set 去重后再转换为数组
  const uniquePreviousCode = [...new Set(previousCode)];
  return nodes.filter(node => uniquePreviousCode.includes(node.id)).reverse();
}

function getPreviousCode(skips, nowNodeCode, visited = new Set()) {
  // 防止循环引用导致的无限递归
  if (visited.has(nowNodeCode)) {
    return [];
  }

  visited.add(nowNodeCode);
  let passSkip = skips.filter(skip => skip.properties.skipType === "PASS");
  const previousCode = [];

  for (const skip of passSkip) {
    if (skip.targetNodeId === nowNodeCode) {
      previousCode.push(skip.sourceNodeId);
      // 递归获取更前面的节点
      const ancestors = getPreviousCode(passSkip, skip.sourceNodeId, visited);
      previousCode.push(...ancestors);
    }
  }

  return previousCode;
}

/**
 * 判断是否经典模式
 */
export function isClassics(modelValue) {
  return "CLASSICS" === modelValue
}

/**
 * 判断是否网关节点
 */
export function isGateWay(nodeType) {
  return ['serial', 'parallel', 'inclusive'].includes(nodeType);

}

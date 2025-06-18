const NODE_TYPE_MAP = {0: 'start', 1: 'between', 2: 'end', 3: 'serial', 4: 'parallel'}

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
  graphData.version = definition.version
  graphData.fromCustom = definition.fromCustom
  graphData.fromPath = definition.fromPath
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
  definition.version = data.version
  definition.fromCustom = data.fromCustom
  definition.fromPath = data.fromPath
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
    node.formPath = anyNode.properties.formPath
    node.ext = [];
    for (const key in anyNode.properties.ext) {
      if (Object.prototype.hasOwnProperty.call(anyNode.properties.ext, key)) {
        let e = anyNode.properties.ext[key];
        node.ext.push({ code: key, value: Array.isArray(e) ? e.join(",") : e });
      }
    }
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

export const setCommonStyle = (style, properties, type) => {
  // 从 chartStatusColor 数组中提取颜色值
  const [doneColor, todoColor, notDoneColor] = properties.chartStatusColor
  || ["157,255,0", "255,205,23", "0,0,0"]; // 提供默认值

  if (properties.status === 2) {
    // 使用活跃状态的 RGB 颜色
    if (type === 'node') {
      style.fill = `rgba(${doneColor}, 0.15)`;  // 带透明度
    }
    style.stroke = `rgb(${doneColor})`;      // 纯色
  } else if (properties.status === 1) {
    // 使用非活跃状态的 RGB 颜色
    if (type === 'node') {
      style.fill = `rgba(${todoColor}, 0.15)`;
    }
    style.stroke = `rgb(${todoColor})`;
  } else {
    // 默认状态
    if (type === 'node') {
      style.fill = `rgba(255, 255, 255)`;
    }
    style.stroke = `rgb(${notDoneColor})`;
  }

  style.strokeWidth = 1
  return style;
}

function addNode(lf, nodeType, x, y, textFlag) {
  return lf.addNode({
    type: nodeType,
    x: x,
    y: y,
    text: textFlag ? {
      value: "中间节点",
      x: x,
      y: y,
    } : null,
  });
}

function addEdge(lf, nodeType, sourceNodeId, targetNodeId, pointsList) {
  return lf.addEdge({
    type: nodeType,
    sourceNodeId: sourceNodeId,
    targetNodeId: targetNodeId,
    pointsList: pointsList,
  });
}

function addEdgeAll(lf, nodeType, sourceNodeId, targetNodeId, properties, text, pointsList) {
  return lf.addEdge({
    type: nodeType,
    sourceNodeId: sourceNodeId,
    targetNodeId: targetNodeId,
    properties: properties,
    text: text,
    pointsList: pointsList,
    startPoint: pointsList ? pointsList[0]: null,
    endPoint: pointsList ? pointsList[pointsList.length - 1]: null,
  });
}

function deleteAndAddEdge(sourceNode, nodes, edges, lf, nodeMap) {
  const nextEdges = findNextEdges(sourceNode.id, edges)
  if (!nextEdges || nextEdges.length === 0) {
    return
  }
  if (nextEdges.length > 1) {
    for (let i = 0; i < nextEdges.length; i++) {
      const edge = nextEdges[i]
      lf.deleteEdge(edge.id)
      const targetNode = nodeMap.get(edge.targetNodeId)
      if (targetNode.x > sourceNode.x) {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x + sourceNode.properties.width / 2, y: sourceNode.y},
          {x: targetNode.x, y: sourceNode.y},
          {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2},
        ])
      } else {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x - sourceNode.properties.width / 2, y: sourceNode.y},
          {x: targetNode.x, y: sourceNode.y},
          {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2},
        ])
      }
      deleteAndAddEdge(targetNode, nodes, edges, lf, nodeMap)
    }
  } else {
    const edge = nextEdges[0]
    lf.deleteEdge(edge.id)
    const targetNode = nodeMap.get(edge.targetNodeId)
    const lastEdges = findLastEdges(targetNode.id, edges)
    if (lastEdges.length > 1) {
      if (targetNode.x > sourceNode.x) {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
          {x: sourceNode.x, y: targetNode.y},
          {x: targetNode.x + targetNode.properties.width / 2, y: targetNode.y}
        ])
      } else {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
          {x: sourceNode.x, y: targetNode.y},
          {x: targetNode.x - targetNode.properties.width / 2, y: targetNode.y}
        ])
      }
    } else {
      addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
        {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
        {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2}
      ])
    }

    deleteAndAddEdge(targetNode, nodes, edges, lf, nodeMap)
  }
}

// 更新边
function updateEdges(lf) {
  const nodes = lf.getGraphData().nodes;
  const startNode = nodes.find(node => node.type === "start")
  const nodeMap = new Map()
  nodes.forEach((node) => nodeMap.set(node.id, node));
  const edges = lf.getGraphData().edges;
  // 获取源节点的后置节点
  deleteAndAddEdge(startNode, nodes, edges, lf, nodeMap);
  console.log(lf.getGraphData().edges)
  const edgeAll = lf.getGraphData().edges;
  // 如果存在重复的边，则删除
  const edgeMap = new Map()
  edgeAll.forEach(edge => {
      if (!edgeMap.has(edge.sourceNodeId + edge.targetNodeId)) {
        edgeMap.set(edge.sourceNodeId + edge.targetNodeId, edge)
      } else {
        lf.deleteEdge(edge.id)
      }
  })

  console.log(lf.getGraphData().edges)
}

export const addBetweenNode = (lf, edge, nodeType) => {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;
  const sourceNode = nodes.find(node => node.id === edge.sourceNodeId);
  const targetNode = nodes.find(node => node.id === edge.targetNodeId);

  // 偏移距离
  const offsetY = 120;

  // 添加中间节点
  const betweenNode = addNode(lf, nodeType, sourceNode.x, sourceNode.y + offsetY, true)
  addEdge(lf, "skip", sourceNode.id, betweenNode.id)

  // 找到并删除开始节点和结束节点之间的直接连接
  lf.deleteEdge(edge.id);

  const allNextNodes = findAllNextNodes(sourceNode.id, nodes, edges);
  allNextNodes.forEach(node => {
    if (node) {
      lf.graphModel.moveNode(node.id, 0, offsetY, true);
    }
  });
  addEdgeAll(lf, "skip", betweenNode.id, targetNode.id, edge.properties, edge.text)
  updateEdges(lf)
  // 自动调整画布大小
  // adjustCanvasSize();
}

export const addGatewayNode = (lf, edge, nodeType) => {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;
  const sourceNode = nodes.find(node => node.id === edge.sourceNodeId);
  const targetNode = nodes.find(node => node.id === edge.targetNodeId);

  // 偏移距离
  const offsetX = 100;
  const offsetY = 100;

  // 添加互斥网关开始节点
  const gatewayNode = addNode(lf, nodeType, sourceNode.x, sourceNode.y + offsetY)
  // 连接父节点与互斥网关开始节点
  addEdge(lf, "skip", sourceNode.id, gatewayNode.id)

  // 添加两个中间节点
  const between1Node = addNode(lf, "between", sourceNode.x - offsetX, sourceNode.y + offsetY * 2, true)
  const between2Node = addNode(lf, "between", sourceNode.x + offsetX, sourceNode.y + offsetY * 2, true)
  addEdge(lf, "skip", gatewayNode.id, between1Node.id)
  addEdge(lf, "skip", gatewayNode.id, between2Node.id)

  // 添加互斥网关结束节点
  const gatewayNode2 = addNode(lf, nodeType, sourceNode.x, sourceNode.y + offsetY * 3)
  // 连接两个中间节点与互斥网关结束节点
  addEdge(lf, "skip", between1Node.id, gatewayNode2.id)
  addEdge(lf, "skip", between2Node.id, gatewayNode2.id)

  // 找到并删除开始节点和目标节点之间的直接连接
  lf.deleteEdge(edge.id);
  const allNextNodes = findAllNextNodes(sourceNode.id, nodes, edges);
  allNextNodes.forEach(node => lf.graphModel.moveNode(node.id, 0, offsetY * 3, true));

  // 连接互斥网关结束节点到目标节点
  addEdge(lf, "skip", gatewayNode2.id, targetNode.id)
  updateEdges(lf)

  // 自动调整画布大小
  // adjustCanvasSize();
}

export const gatewayAddNode = (lf, gatewayNode) => {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;

  // 新中间节点的位置（在互斥网关开始节点的正右边）
  const offsetX = 150; // 偏移量

  // 获取网关节点的所有直接子节点
  const directChildren = findNextNodes(gatewayNode.id, nodes, edges);

  // 在直接子节点中找到最右边的节点
  const rightmostChild = getRightmostNode(directChildren);

  // 连接互斥网关开始节点到新的中间节点
  const betweenNode = addNode(lf, "between", rightmostChild.x + offsetX, rightmostChild.y, true)

  // 连接网关开始节点到新的中间节点
  addEdge(lf, "skip", gatewayNode.id, betweenNode.id)

  // 找到互斥网关结束节点
  const gatewayEndNode = nodes.find(node => ['serial', 'parallel'].includes(node.type) && node.id !== gatewayNode.id);

  if (gatewayEndNode) {
    // 连接新的中间节点到互斥网关结束节点
    addEdge(lf, "skip", betweenNode.id, gatewayEndNode.id)
  }
  updateEdges(lf)
};

function findAllNextNodes(nodeId, nodes, edges, visited = new Set()) {
  if (visited.has(nodeId)) {
    return [];
  }
  visited.add(nodeId);

  const targetNode = nodes.find(node => node.id === nodeId);
  if (!targetNode) {
    return [];
  }

  const directChildren = edges
      .filter(edge => edge.sourceNodeId === nodeId)
      .map(edge => nodes.find(node => node.id === edge.targetNodeId))
      .filter(Boolean);

  let allNextNodes = [];

  for (const child of directChildren) {
    // 先添加当前子节点
    allNextNodes.push(child);
    // 再递归添加子节点的后继节点
    const childNextNodes = findAllNextNodes(child.id, nodes, edges, visited);
    allNextNodes = [...allNextNodes, ...childNextNodes];
  }

  // 不再返回 targetNode 本身，只返回其所有后继节点
  return allNextNodes;
}

function findNextEdges(nodeId, edges) {
  return edges.filter(edge => edge.sourceNodeId === nodeId);
}

function findLastEdges(nodeId, edges) {
  return edges.filter(edge => edge.targetNodeId === nodeId);
}

function findNextNodes(nodeId, nodes, edges) {
  return edges
      .filter(edge => edge.sourceNodeId === nodeId)
      .map(edge => nodes.find(node => node.id === edge.targetNodeId))
      .filter(Boolean);
}

function adjustCanvasSize() {
  const nodes = lf.getGraphData().nodes;
  let maxX = 0;
  let maxY = 0;

  nodes.forEach((node) => {
    if (node.x > maxX) maxX = node.x;
    if (node.y > maxY) maxY = node.y;
  });

  // 扩展画布边界
  const padding = 200;
  const containerWidth = maxX + padding;
  const containerHeight = maxY + padding;

  // 动态修改容器大小
  proxy.$refs.container.style.width = `${containerWidth}px`;
  proxy.$refs.container.style.height = `${containerHeight}px`;

  // 可选：重置视图为合适的位置和缩放比例
  lf.translateCenter(); // 居中显示
  lf.zoomToFit();       // 自动缩放以适应画布
}

function getRightmostNode(nodes) {
  if (nodes.length === 0) return null;

  return nodes.reduce((rightmost, current) => {
    return (current.x > rightmost.x ? current : rightmost);
  }, nodes[0]);
}

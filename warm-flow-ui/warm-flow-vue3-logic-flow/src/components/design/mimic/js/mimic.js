const OFFSET_X = 150;
const OFFSET_Y = 150;

function addNode(lf, nodeType, x, y, name) {
  let text = null;
  if (name) {
    text = {
      value: name,
      x: x,
      y: y,
    }
  }
  return lf.addNode({
    type: nodeType,
    x: x,
    y: y,
    text: text,
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

function deleteAndAddEdge(sourceNode, nodes, edges, lf, nodeMap, edgeMap, visitedNodes = new Set()) {
  // 如果该节点已经被访问过，则直接返回，防止死循环
  if (visitedNodes.has(sourceNode.id)) {
    return;
  }
  visitedNodes.add(sourceNode.id);

  const nextEdges = findNextEdges(sourceNode.id, edges);
  if (!nextEdges || nextEdges.length === 0) {
    return;
  }

  if (nextEdges.length > 1) {
    for (let i = 0; i < nextEdges.length; i++) {
      const edge = nextEdges[i];
      lf.deleteEdge(edge.id);
      const targetNode = nodeMap.get(edge.targetNodeId);
      if (!targetNode) continue;

      // 边重复检测
      if (["serial", "parallel"].includes(sourceNode.type) && edgeMap.has(edge.sourceNodeId + ":" + edge.targetNodeId)) {
        continue; // 跳过重复边
      }

      // 添加新边
      if (targetNode.x > sourceNode.x) {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x + sourceNode.properties.width / 2, y: sourceNode.y},
          {x: targetNode.x, y: sourceNode.y},
          {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2},
        ]);
      } else {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x - sourceNode.properties.width / 2, y: sourceNode.y},
          {x: targetNode.x, y: sourceNode.y},
          {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2},
        ]);
      }

      edgeMap.set(edge.sourceNodeId + ":" + edge.targetNodeId, edge);
      // 继续处理子节点，带上 visitedNodes
      deleteAndAddEdge(targetNode, nodes, edges, lf, nodeMap, edgeMap, visitedNodes);
    }
  } else {
    const edge = nextEdges[0];
    lf.deleteEdge(edge.id);
    const targetNode = nodeMap.get(edge.targetNodeId);
    if (!targetNode) return;

    if (["serial", "parallel"].includes(sourceNode.type) && edgeMap.has(edge.sourceNodeId + ":" + edge.targetNodeId)) {
      return;
    }

    const lastEdges = findLastEdges(targetNode.id, edges);
    if (lastEdges.length > 1) {
      if (targetNode.x > sourceNode.x) {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
          {x: sourceNode.x, y: targetNode.y},
          {x: targetNode.x - targetNode.properties.width / 2, y: targetNode.y}
        ]);
      } else {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
          {x: sourceNode.x, y: targetNode.y},
          {x: targetNode.x + targetNode.properties.width / 2, y: targetNode.y}
        ]);
      }
    } else {
      edgeMap.set(edge.sourceNodeId + ":" + edge.targetNodeId, edge);
      addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
        {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
        {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2}
      ]);
    }

    // 带着 visitedNodes 递归
    deleteAndAddEdge(targetNode, nodes, edges, lf, nodeMap, edgeMap, visitedNodes);
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
  deleteAndAddEdge(startNode, nodes, edges, lf, nodeMap, new Map(), new Set());
}

export const addBetweenNode = (lf, edge) => {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;
  const sourceNode = nodes.find(node => node.id === edge.sourceNodeId);
  const targetNode = nodes.find(node => node.id === edge.targetNodeId);

  // 添加中间节点
  const betweenNode = addNode(lf, 'between', ["serial", "parallel"].includes(sourceNode.type) ? targetNode.x :sourceNode.x,
      sourceNode.y + OFFSET_Y, "中间节点")
  addEdge(lf, "skip", sourceNode.id, betweenNode.id)

  // 找到并删除开始节点和结束节点之间的直接连接
  lf.deleteEdge(edge.id);

  // 当目标节点减新增中间节点差，小于竖向偏移量，才移动节点
  if (targetNode.y - betweenNode.y < OFFSET_Y) {
    const allNextNodes = findAllNextNodes(sourceNode.id, nodes, edges);
    allNextNodes.forEach(node => {
      if (node) {
        lf.graphModel.moveNode(node.id, 0, OFFSET_Y, true);
      }
    });
  }

  addEdgeAll(lf, "skip", betweenNode.id, targetNode.id, edge.properties)
  updateEdges(lf)
  // 自动调整画布大小
  // adjustCanvasSize();
}

export const addGatewayNode = (lf, edge, nodeType) => {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;
  const sourceNode = nodes.find(node => node.id === edge.sourceNodeId);
  const targetNode = nodes.find(node => node.id === edge.targetNodeId);

  // 添加互斥网关开始节点
  const gatewayNode = addNode(lf, nodeType, sourceNode.x, sourceNode.y + OFFSET_Y, "添加节点")
  // 连接父节点与互斥网关开始节点
  addEdge(lf, "skip", sourceNode.id, gatewayNode.id)

  // 添加两个中间节点
  const between1Node = addNode(lf, "between", sourceNode.x - OFFSET_X, sourceNode.y + OFFSET_Y * 2, "中间节点")
  const between2Node = addNode(lf, "between", sourceNode.x + OFFSET_X, sourceNode.y + OFFSET_Y * 2, "中间节点")
  addEdge(lf, "skip", gatewayNode.id, between1Node.id)
  addEdge(lf, "skip", gatewayNode.id, between2Node.id)

  // 添加互斥网关结束节点
  const gatewayNode2 = addNode(lf, nodeType, sourceNode.x, sourceNode.y + OFFSET_Y * 3, "添加节点")
  // 连接两个中间节点与互斥网关结束节点
  addEdge(lf, "skip", between1Node.id, gatewayNode2.id)
  addEdge(lf, "skip", between2Node.id, gatewayNode2.id)

  // 找到并删除开始节点和目标节点之间的直接连接
  lf.deleteEdge(edge.id);

  // 当目标节点减新增互斥网关结束节点差，小于竖向偏移量，才移动节点
  if (targetNode.y - gatewayNode2.y < OFFSET_Y) {
    const allNextNodes = findAllNextNodes(sourceNode.id, nodes, edges);
    allNextNodes.forEach(node => lf.graphModel.moveNode(node.id, 0, OFFSET_Y * 3, true));
  }

  // 连接互斥网关结束节点到目标节点
  addEdge(lf, "skip", gatewayNode2.id, targetNode.id)

  // 找出所有节点nodes中的x坐标，减去sourceNode.x的最小的差值，小于横向偏移量，才移动节点
  let needMoveLeftNode = []
  let needMoveRightNode = []
  let LeftMove = false;
  let rightMove = false;
  // 过滤nodes在between1Node和between2Node之间的节点
  nodes.forEach(node => {
    if (node.x <= between1Node.x) {
      needMoveLeftNode.push(node)
      if (between1Node.x - node.x < OFFSET_X) {
        LeftMove = true
      }
    } else if (node.x >= between2Node.x) {
      needMoveRightNode.push(node)
      if (node.x - between2Node.x < OFFSET_X) {
        rightMove = true
      }
    }
  })

  // 水平自动布局
  if (LeftMove) {
    needMoveLeftNode.forEach(node => {
      lf.graphModel.moveNode(node.id, -OFFSET_X, 0, true);
    });
  }
  if (rightMove) {
    needMoveRightNode.forEach(node => {
      lf.graphModel.moveNode(node.id, OFFSET_X, 0, true);
    });
  }

  updateEdges(lf)

  // 自动调整画布大小
  // adjustCanvasSize();
}

export const gatewayAddNode = (lf, gatewayNode) => {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;

  // 找到互斥网关结束节点
  let allSuccessors = findAllNextNodes(gatewayNode.id, nodes, edges);
  let n = 1
  const gatewayEndNode = allSuccessors.find(node => {
    if (gatewayNode.type.includes(node.type)) {
      if (n === 1) {
        return node
      }
      if (findNextEdges(node.id, edges).length > 1) {
        n++
      } else {
        n--
      }
    }
  })

  // 在直接子节点中找到最右边的节点
  allSuccessors = findAllNextNodes(gatewayNode.id, nodes, edges, new Set(), [], gatewayEndNode);
  let rightmostChild = getRightmostNode(allSuccessors)

  // 连接互斥网关开始节点到新的中间节点
  const betweenNode = addNode(lf, "between", rightmostChild.x + OFFSET_X * 2, rightmostChild.y, "中间节点")

  // 连接网关开始节点到新的中间节点
  addEdge(lf, "skip", gatewayNode.id, betweenNode.id)


  if (gatewayEndNode) {
    // 连接新的中间节点到互斥网关结束节点
    addEdge(lf, "skip", betweenNode.id, gatewayEndNode.id)
  }
  updateEdges(lf)
};

function findAllNextNodes(nodeId, nodes, edges, visited = new Set(), result = [], endId) {
  if (visited.has(nodeId)) {
    return result;
  }
  visited.add(nodeId);

  const targetNode = nodes.find(node => node.id === nodeId);
  if (!targetNode) {
    return result;
  }

  const directChildren = edges
      .filter(edge => edge.sourceNodeId === nodeId)
      .map(edge => nodes.find(node => node.id === edge.targetNodeId))
      .filter(Boolean);

  for (const child of directChildren) {
    // 如果子节点还未添加到结果数组中，则添加
    if (!result.some(node => node.id === child.id)) {
      result.push(child);
    }
    if (!endId || endId !== child.id) {
      // 递归查找子节点的后继节点
      findAllNextNodes(child.id, nodes, edges, visited, result);
    }
  }

  return result;
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

export const hideText = (style) => {
  style.display = 'none';
  return style
}

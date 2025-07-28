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

function deleteAndAddEdge(sourceNode, nodes, edges, lf, edgeMap = new Map()
                          , visitedNodes = new Set(), nodeMap = new Map()) {
  if (nodeMap.size === 0) {
    nodes.forEach((node) => nodeMap.set(node.id, node));
  }
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
      } else if (targetNode.x < sourceNode.x) {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x - sourceNode.properties.width / 2, y: sourceNode.y},
          {x: targetNode.x, y: sourceNode.y},
          {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2},
        ]);
      } else {
        addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
          {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
          {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2},
        ]);
      }

      edgeMap.set(edge.sourceNodeId + ":" + edge.targetNodeId, edge);
      // 继续处理子节点，带上 visitedNodes
      deleteAndAddEdge(targetNode, nodes, edges, lf, edgeMap, visitedNodes, nodeMap);
    }
  } else {
    const edge = nextEdges[0];
    lf.deleteEdge(edge.id);
    const targetNode = nodeMap.get(edge.targetNodeId);
    if (!targetNode) return;

    if (["serial", "parallel"].includes(sourceNode.type) && edgeMap.has(edge.sourceNodeId + ":" + edge.targetNodeId)) {
      return;
    }

    if (targetNode.x > sourceNode.x) {
      addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
        {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
        {x: sourceNode.x, y: targetNode.y},
        {x: targetNode.x - targetNode.properties.width / 2, y: targetNode.y}
      ]);
    } else if (targetNode.x < sourceNode.x) {
      addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
        {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
        {x: sourceNode.x, y: targetNode.y},
        {x: targetNode.x + targetNode.properties.width / 2, y: targetNode.y}
      ]);
    } else {
      edgeMap.set(edge.sourceNodeId + ":" + edge.targetNodeId, edge);
      addEdgeAll(lf, "skip", sourceNode.id, edge.targetNodeId, edge.properties, edge.text, [
        {x: sourceNode.x, y: sourceNode.y + sourceNode.properties.height / 2},
        {x: targetNode.x, y: targetNode.y - targetNode.properties.height / 2}
      ]);
    }

    // 带着 visitedNodes 递归
    deleteAndAddEdge(targetNode, nodes, edges, lf, edgeMap, visitedNodes, nodeMap);
  }
}

/**
 * 水平自动布局，不需要移动的节点
 */
function LevelNotMoveNode(targetEdges, nodes, edges, lf, visitedIds, type = true
                  , nodeMap = new Map()) {
  if (nodeMap.size === 0) {
    nodeMap = new Map(nodes.map(node => [node.id, node]));
  }

  if (targetEdges.length > 1) {
    for (let i = 0; i < targetEdges.length; i++) {
      const edge = targetEdges[i];
      extracted(edge);
    }
  } else {
    const edge = targetEdges[0]
    extracted(edge);
  }

  function extracted(edge) {
    const targetNode = type ? nodeMap.get(edge.targetNodeId) : nodeMap.get(edge.sourceNodeId);
    // 如果该节点已经被访问过，则直接返回，防止死循环
    if (visitedIds.has(targetNode.id)) {
      return;
    }
    visitedIds.add(targetNode.id);
    const targetEdges = type ? findNextEdges(targetNode.id, edges) : findLastEdges(targetNode.id, edges);
    if (!targetEdges || targetEdges.length === 0) {
      return;
    }
    LevelNotMoveNode(targetEdges, nodes, edges, lf, visitedIds, type, nodeMap)
  }
}


// 更新边
function updateEdges(lf) {
  const nodes = lf.getGraphData().nodes;
  const startNode = nodes.find(node => node.type === "start")
  // 以上两行重构，改成一行

  const edges = lf.getGraphData().edges;
  // 获取源节点的后置节点
  deleteAndAddEdge(startNode, nodes, edges, lf);
}

export const addBetweenNode = (lf, edge) => {
  const nodes = lf.getGraphData().nodes;
  const sourceNode = nodes.find(node => node.id === edge.sourceNodeId);
  const targetNode = nodes.find(node => node.id === edge.targetNodeId);

  // 添加中间节点
  const betweenNode = addNode(lf, 'between', sourceNode.x, getMoveY(sourceNode, 'between'), "中间节点")
  addEdge(lf, "skip", sourceNode.id, betweenNode.id)

  // 找到并删除开始节点和结束节点之间的直接连接
  lf.deleteEdge(edge.id);

  addEdgeAll(lf, "skip", betweenNode.id, targetNode.id, edge.properties)

  // 当目标节点减新增中间节点差，小于竖向偏移量，才移动节点
  recursivelyMoveNodes(lf, betweenNode);

  updateEdges(lf)
}

export const addGatewayNode = (lf, edge, nodeType) => {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;
  const sourceNode = nodes.find(node => node.id === edge.sourceNodeId);
  const targetNode = nodes.find(node => node.id === edge.targetNodeId);

  // 添加互斥网关开始节点
  const gatewayNode = addNode(lf, nodeType, sourceNode.x, getMoveY(sourceNode, nodeType), "加节点")
  // 连接父节点与互斥网关开始节点
  addEdge(lf, "skip", sourceNode.id, gatewayNode.id)

  // 添加两个中间节点
  const between1Node = addNode(lf, "between", sourceNode.x - OFFSET_X, getMoveY(gatewayNode, "between"), "中间节点")
  const between2Node = addNode(lf, "between", sourceNode.x + OFFSET_X, getMoveY(gatewayNode, "between"), "中间节点")
  addEdge(lf, "skip", gatewayNode.id, between1Node.id)
  addEdge(lf, "skip", gatewayNode.id, between2Node.id)

  // 添加互斥网关结束节点
  const gatewayNode2 = addNode(lf, nodeType, sourceNode.x, getMoveY(between1Node, nodeType), "加节点")
  // 连接两个中间节点与互斥网关结束节点
  addEdge(lf, "skip", between1Node.id, gatewayNode2.id)
  addEdge(lf, "skip", between2Node.id, gatewayNode2.id)

  // 找到并删除开始节点和目标节点之间的直接连接
  lf.deleteEdge(edge.id);

  // 连接互斥网关结束节点到目标节点
  addEdge(lf, "skip", gatewayNode2.id, targetNode.id)

  // 当目标节点减新增互斥网关结束节点差，小于竖向偏移量，才移动节点
  recursivelyMoveNodes(lf, gatewayNode2);

  // 找到属于新增位置区域的节点，水平移动的时候排除它
  const targetEdges = [edges.find(edge1 => edge1.id === edge.id)];
  moveLevelNode(nodes, edges, targetEdges, lf, between1Node, between2Node);
  updateEdges(lf)
}

export const gatewayAddNode = (lf, gatewayNode) => {
  const incomingEdges = lf.getNodeIncomingEdge(gatewayNode.id)
  // 如果是网关的结束节点，则不新增节点
  if (incomingEdges.length > 1) {
    return
  }

  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges;

  // 找到互斥网关结束节点之间的节点
  const nextNodes = findNextNodes(gatewayNode.id, nodes, edges);
  const gatewayEnd = getGateWayEnd(gatewayNode, nodes, edges, nextNodes)
  const regionNodes = findAllNextNodes(gatewayNode.id, nodes, edges, gatewayEnd);
  let xSet = new Set()
  regionNodes.forEach(node => {
    if (node.type === 'between' && !xSet.has(node.x)) {
      xSet.add(node.x)
    }
  })

  regionNodes.forEach(node => {
    if (gatewayEnd.id !== node.id) {
      lf.graphModel.moveNode(node.id, -OFFSET_X, 0, true);
      node.x = node.x - OFFSET_X
    }
  })

  const mostChild = getRightmostNode(regionNodes)

  // 连接互斥网关开始节点到新的中间节点
  const betweenNode = addNode(lf, "between", mostChild.x + OFFSET_X * 2,
      getMoveY(gatewayNode, "between"), "中间节点")
  regionNodes.push(lf.getDataById(betweenNode.id))

  // 连接网关开始节点到新的中间节点
  addEdge(lf, "skip", gatewayNode.id, betweenNode.id)

  // 找到属于新增位置区域的节点，水平移动的时候排除它
  const targetEdges = [edges.find(edge1 => edge1.targetNodeId === gatewayNode.id)];
  moveLevelNode(nodes, edges, targetEdges, lf, getLeftmostNode(regionNodes) , getRightmostNode(regionNodes));

  // 连接新的中间节点到互斥网关结束节点
  addEdge(lf, "skip", betweenNode.id, gatewayEnd.id)
  updateEdges(lf)
};

function getGateWayEnd(startNode, nodes, edges, nextNodes, n = 1) {
  if (!nextNodes || nextNodes.length === 0) {
    nextNodes = findNextNodes(startNode.id, nodes, edges);
  }
  if (nextNodes && nextNodes.length > 0) {
    if (nextNodes[0].type === startNode.type) {
      if (n === 1 && nextNodes[0].x === startNode.x) {
        return nextNodes[0]
      }
      if (findNextEdges(nextNodes[0].id, edges).length > 1) {
        n++
      } else {
        n--
      }
    }
    startNode = {id : nextNodes[0].id, type : startNode.type, x : startNode.x}
    nextNodes = findNextNodes(startNode.id, nodes, edges);
    return getGateWayEnd(startNode, nodes, edges, nextNodes, n)
  }
}

export const removeNode = (lf, nodeModel) => {
  const sourceNodes = lf.getNodeIncomingNode(nodeModel.id)
  const targetNodes = lf.getNodeOutgoingNode(nodeModel.id)
  const [sourceNode, targetNode] = [sourceNodes[0], targetNodes[0]]
  let moveSourceNode = sourceNode
  let moveTargetNode = targetNode
  lf.deleteNode(nodeModel.id)
  let nodes = lf.getGraphData().nodes;
  let edges = lf.getGraphData().edges;

  if (['serial', 'parallel'].includes(sourceNode.type) && ['serial', 'parallel'].includes(targetNode.type)
      && sourceNode.type === targetNode.type) {
    let sourceEdges = lf.getNodeIncomingEdge(sourceNode.id)
    if (lf.getNodeOutgoingNode(sourceNode.id).length >= 2) {
      nodes = lf.getGraphData().nodes;
      edges = lf.getGraphData().edges;
      // 找到互斥网关结束节点之间的节点
      const nextNodes = findNextNodes(sourceNode.id, nodes, edges);
      const gatewayEnd = getGateWayEnd(sourceNode, nodes, edges, nextNodes)
      const regionNodes = findAllNextNodes(sourceNode.id, nodes, edges, gatewayEnd);
      let xSet = new Set()
      regionNodes.forEach(node => {
        if (node.type === 'between' && !xSet.has(node.x)) {
          xSet.add(node.x)
        }
      })
      // regionNodes按照x轴排序
      regionNodes.sort((a, b) => {
        return a.x - b.x
      })
      let mostLeftX = sourceNode.x - (xSet.size - 1) * OFFSET_X
      let moveX
      regionNodes.forEach(node => {
        if (gatewayEnd.id !== node.id) {
          if (moveX) {
            moveX = moveX + OFFSET_X * 2
          } else {
            moveX = mostLeftX
          }
          lf.graphModel.moveNode2Coordinate(node.id, moveX, node.y, true);
        }
      })
    }
    if (lf.getNodeOutgoingNode(sourceNode.id).length === 1) {
      moveSourceNode = lf.getNodeIncomingNode(sourceNode.id)[0]
      moveTargetNode = lf.getNodeOutgoingNode(targetNode.id)[0]
      const delNodes = findAllNextNodes(sourceNode.id, nodes, edges, targetNode);
      lf.deleteNode(sourceNode.id)
      delNodes.forEach(node => {
        lf.deleteNode(node.id)
      })
      sourceEdges = [addEdge(lf, "skip", moveSourceNode.id, moveTargetNode.id)]
    }

    nodes = lf.getGraphData().nodes;
    edges = lf.getGraphData().edges;
    let visitedIds = new Set()
    LevelNotMoveNode(sourceEdges, nodes, edges, lf, visitedIds)
    LevelNotMoveNode(sourceEdges, nodes, edges, lf, visitedIds, false)
    // 查询nodes中visitedIds包含node.id的数组
    const visitedNodes = nodes.filter(node => visitedIds.has(node.id))

    moveLevelNode(nodes, edges, sourceEdges, lf, nodeModel.x < sourceNode.x ? getLeftmostNode(visitedNodes) : null
        , nodeModel.x < sourceNode.x ? null : getRightmostNode(visitedNodes), false, visitedIds);
  } else {
    addEdge(lf, "skip", moveSourceNode.id, moveTargetNode.id)
  }

  // 当目标节点减新增互斥网关结束节点差，小于竖向偏移量，才移动节点
  recursivelyMoveNodes(lf, moveSourceNode, false);
  updateEdges(lf)
}

/**
 * 水平移动节点
 */
function moveLevelNode(nodes, edges, targetEdges, lf, betweenLeft, betweenRight, addFlag = true, visitedIds = new Set()) {
  if (!visitedIds || visitedIds.size === 0) {
    LevelNotMoveNode(targetEdges, nodes, edges, lf, visitedIds)
    LevelNotMoveNode(targetEdges, nodes, edges, lf, visitedIds, false)
  }

  // 找出所有节点nodes中的x坐标，减去sourceNode.x的最小的差值，小于横向偏移量，才移动节点
  let needMoveLeftNode = []
  let needMoveRightNode = []
  let LeftMove = !addFlag;
  let rightMove = !addFlag;
  // 过滤nodes在between1Node和between2Node之间的节点
  nodes.forEach(node => {
    if (visitedIds.has(node.id)) {
      return
    }
    if (betweenLeft && node.x <= betweenLeft.x) {
      needMoveLeftNode.push(node)
      if (addFlag && betweenLeft.x - node.x <= OFFSET_X) {
        LeftMove = true
      }
      if (!addFlag && betweenLeft.x - node.x <= OFFSET_X * 2) {
        LeftMove = false
      }
    } else if (betweenRight && node.x >= betweenRight.x) {
      needMoveRightNode.push(node)
      if (addFlag && node.x - betweenRight.x <= OFFSET_X) {
        rightMove = true
      }
      if (!addFlag && node.x - betweenRight.x <= OFFSET_X * 2) {
        rightMove = false
      }
    }
  })

  let n = betweenLeft && betweenRight ? 1 : 2;
  // 水平自动布局
  if (LeftMove) {
    needMoveLeftNode.forEach(node => {
      lf.graphModel.moveNode(node.id, addFlag ? -OFFSET_X * n : OFFSET_X * n, 0, true);
    });
  }
  if (rightMove) {
    needMoveRightNode.forEach(node => {
      lf.graphModel.moveNode(node.id, addFlag ? OFFSET_X * n : -OFFSET_X * n, 0, true);
    });
  }
}

function getMoveY(sourceNode, targetType) {
  let moveY;
  if (['start', 'between'].includes(sourceNode.type) && targetType === 'between') {
    moveY = sourceNode.y + OFFSET_Y;
  } else if (['start', 'between'].includes(sourceNode.type) && targetType === 'end') {
    moveY = sourceNode.y + OFFSET_Y - 20;
  } else if (['start', 'between'].includes(sourceNode.type) && ['serial', 'parallel'].includes(targetType)) {
    moveY = sourceNode.y + OFFSET_Y - 25;
  } else if (['serial', 'parallel'].includes(sourceNode.type) && ['serial', 'parallel'].includes(targetType)) {
    moveY = sourceNode.y + OFFSET_Y - 50;
  } else if (['serial', 'parallel'].includes(sourceNode.type) && targetType === 'between') {
    moveY = sourceNode.y + OFFSET_Y - 25;
  } else if (['serial', 'parallel'].includes(sourceNode.type) && targetType === 'end') {
    moveY = sourceNode.y + OFFSET_Y - 45;
  }
  return moveY;
}

/**
 * 递归处理节点的 Y 轴移动逻辑
 */
function recursivelyMoveNodes(lf, lastNode, addFlag = true) {
  const nodes = lf.getGraphData().nodes;
  const edges = lf.getGraphData().edges
  // 查找当前节点的后续节点并递归处理
  const nodesToMove = findNextNodes(lastNode.id, nodes, edges);

  if (!nodesToMove || nodesToMove.length === 0) return;

  nodesToMove.forEach(node => {
    let moveY = getMoveY(lastNode, node.type);
    let move = false;
    if (addFlag && node.y < moveY) {
      move = true;
    } else if (!addFlag) {
      if (['serial', 'parallel'].includes(node.type)) {
        const lastNodes = findLastNodes(node.id, nodes, edges)
        // 找出lastNodes中的最大的y坐标的节点
        const maxNode = lastNodes.reduce((prev, current) => {
          return (current.y > prev.y ? current : prev);
        }, lastNodes[0]);
        moveY = getMoveY(maxNode, node.type);
        if (node.y >= moveY) {
          move = true;
        }
      } else if (node.y >= moveY) {
        move = true;
      }
    }

    if (move) {
      // 移动当前节点
      lf.graphModel.moveNode2Coordinate(node.id, node.x, moveY, true);
      // 更新 lastNode
      const updatedNode = lf.graphModel.getNodeModelById(node.id);
      recursivelyMoveNodes(lf, updatedNode, addFlag);
    }
  });
}

function findAllNextNodes(nodeId, nodes, edges, gateWayEnd, visited = new Set(), result = []) {
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
    if (gateWayEnd && gateWayEnd.id === child.id) {
      gateWayEnd = child;
    } else {
      // 递归查找子节点的后继节点
      findAllNextNodes(child.id, nodes, edges, gateWayEnd, visited, result);
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
  return edges.filter(edge => edge.sourceNodeId === nodeId)
      .map(edge => nodes.find(node => node.id === edge.targetNodeId))
      .filter(Boolean);
}

function findLastNodes(nodeId, nodes, edges) {
  return edges.filter(edge => edge.targetNodeId === nodeId)
      .map(edge => nodes.find(node => node.id === edge.sourceNodeId))
      .filter(Boolean);
}

function getRightmostNode(nodes) {
  if (nodes.length === 0) return null;

  return nodes.reduce((rightmost, current) => {
    return (current.x > rightmost.x ? current : rightmost);
  }, nodes[0]);
}


function getLeftmostNode(nodes) {
  if (nodes.length === 0) return null;

  return nodes.reduce((leftmost, current) => {
    return (current.x < leftmost.x ? current : leftmost);
  }, nodes[0]);
}

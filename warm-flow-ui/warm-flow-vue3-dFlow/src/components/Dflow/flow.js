import { uuid } from '@/utils/tool';
function removeSubNode(nodeData, param) {
  for (const j in nodeData) {
    const childNodes = nodeData[j]
    for (const k in childNodes) {
      if (childNodes[k].id == param.id) {
        childNodes.splice(k, 1);
        if (k == 0) {
          //删除的为子节点的第一个，表明这个分支都要删除
          nodeData.splice(j, 1)
        }
      }
    }
  }
}

//移除节点， 同时删除子节点
export function removeNode(nodeData, param) {
  for (const i in nodeData) {
    if (nodeData[i].id === param.id) {
      nodeData.splice(i, 1);
      break;
    } else {
      let subNode = nodeData[i].childNodes;
      if (subNode) {
        removeSubNode(nodeData[i].childNodes, param);
        // 只有2个子节点， 设置允许删除
        if (subNode.length == 2 && subNode.type == 'serial') {
          subNode[1][0].enableDel = true;
        }
        if (subNode.length <= 1) {
          // 子节点是否只有2个(注意： 这里的数据结构为 [a,b],[c,d], 移除的子节点可能时 a或c, 但是数据长度不变)
          nodeData.splice(i, 1);
        } else {
          for (let m in subNode) {
            removeNode(subNode[m], param);
          }
        }
      }
    }
  }
}

// 添加节点，在 并行分支和 条件分支 也可以单独添加子节点
export function addNode(nodeData, param) {
  for (const i in nodeData) {
    if (nodeData[i].id === param.id) {
      if (param.type === 'serial') {
        nodeData.splice(parseInt(i) + 1, 0, {
          id: uuid(),
          type: 'serial',
          nodeName: "分支选择",
          childNodes: [
            [
              {
                id: uuid(),
                type: 'skip',
                text: { value: '条件1' },
                properties: {}
              },
            ],
            [
              {
                id: uuid(),
                type: 'skip',
                text: { value: '条件2' },
                properties: {}
              }
            ]
          ]
        })
      } else if (param.type === 'parallel') {
        nodeData.splice(parseInt(i) + 1, 0, {
          id: uuid(),
          type: 'parallel',
          childNodes: [
            [
              {
                text: { value: "中间节点1" },
                id: uuid(),
                type: 'between',
                properties: {}
              }
            ],
            [
              {
                text: { value: "中间节点2" },
                id: uuid(),
                type: 'between',
                properties: {}
              }
            ]
          ]

        })
      } else {
        nodeData.splice(parseInt(i) + 1, 0, {
          text: { value: "中间节点" },
          id: uuid(),
          type: 'between',
          properties: {}
        })
      }
      break;
    } else {
      let subNode = nodeData[i].childNodes;
      for (let m in subNode) {
        addNode(subNode[m], param);
      }
    }
  }
}

// 审批时，查看流程图，处理节点的状态， 暂时未用到
export function changeNodeStatus(nodeData, hisList) {
  // 修改节点状态
  for (const i in nodeData) {
    nodeData[i].nodeStatus = [];
    const hiss = hisList.filter(r => r.nodeCode == nodeData[i].id);
    if (hiss && hiss.length > 0) {
      for (const j in hiss) {
        nodeData[i].nodeStatus.push(hiss[j].flowStatus);
      }

    } else {
      let subNode = nodeData[i].childNodes;
      if (subNode) {
        for (let m in subNode) {
          changeNodeStatus(subNode[m], hisList);
        }
      }
    }
  }
}

export function nodeList(nodeData, list) {
  for (const i in nodeData) {
    const node = nodeData[i];
    if (node.type == 'start' || node.type == 'between'   // || node.type == 'parallel'  || node.type == 'serial' 
      || node.type == 'parallel-node') {
      list.push({
        id: node.id,
        text: { value: node.text.value },
        type: node.type,
      });
    }
    const childNodes = node.childNodes;
    if (childNodes != null && childNodes.length > 0) {
      for (const j in childNodes) {
        nodeList(childNodes[j], list);
      }
    }
  }
}

export function getNode(flowData, id) {
  for (const i in flowData) {
    const node = flowData[i];
    if (node.id == id) {
      return node;
    }
    const childNodes = node.childNodes;
    if (childNodes != null && childNodes.length > 0) {
      for (const j in childNodes) {
        return getNode(childNodes[j], id);
      }
    }
  }
  return null;
}

// 查找节点的父元素
function getBetweenNodes(nodeData, nodeList) {
  for (const i in nodeData) {
    if (nodeData[i].type === 'between' || nodeData[i].type === 'start' || nodeData[i].type === 'end') {
      nodeList.push(nodeData[i]);
    }
    let subNode = nodeData[i].childNodes;
    if (subNode) {
      for (const j in subNode) {
        getBetweenNodes(subNode[j], nodeList);
      }
    }
  }
}

export function backNodeList(flowData) {
  const nodeList = [];
  getBetweenNodes(flowData, nodeList);
  return nodeList;
}

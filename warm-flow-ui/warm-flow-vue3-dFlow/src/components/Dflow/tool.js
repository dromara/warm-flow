/**
 * 解析xml成Dom对象
 * @param {} xml
 * @returns
 */
export const parseXml2Dom = (xml) => {
  let xmlDoc = null
  if (window.DOMParser) {
    const parser = new DOMParser()
    xmlDoc = parser.parseFromString(xml, 'text/xml')
  } else { // Internet Explorer
    // eslint-disable-next-line no-undef
    xmlDoc = new ActiveXObject('Microsoft.XMLDOM')
    xmlDoc.async = false
    xmlDoc.loadXML(xml)
  }
  return xmlDoc
}

// 节点标签
const NODE_NAMES = ['start', 'between', 'serial', 'parallel', 'end']
// 流程节点属性
const DEFINITION_KEYS = ['flowCode', 'flowName', 'version', 'fromCustom', 'fromPath']
// 节点属性
const NODE_ATTR_KEYS = ['nodeType', 'nodeCode', 'nodeName', 'coordinate', 'nodeRatio', 'permissionFlag',
  "listenerType", "listenerPath", "formCustom", "formPath"]
// 变迁节点属性
const SKIP_ATTR_KEYS = ['skipName', 'skipType', 'coordinate', 'skipCondition']

const NODE_TYPE_MAP = { 0: 'start', 1: 'between', 2: 'end', 3: 'serial', 4: 'parallel' }
/**
 * 将warm-flow的定义文件转成LogicFlow支持的数据格式
 * @param {*} xml
 * @returns
 */
export const xml2LogicFlowJson = (xml) => {
  const graphData = {
    nodes: [],
    edges: []
  }
  const xmlDoc = parseXml2Dom(xml)
  const definitionDom = xmlDoc.getElementsByTagName('definition')
  if (!definitionDom.length) {
    return graphData
  }
  let value = null
  // 解析definition属性
  DEFINITION_KEYS.forEach(key => {
    value = definitionDom[0].getAttribute(key)
    if (value) {
      graphData[key] = value
    }
  })
  let nodeEles = null
  let node = null
  let lfNode = {}
  // 解析节点
  nodeEles = definitionDom[0].getElementsByTagName("node")
  if (nodeEles.length) {
    for (var i = 0, len = nodeEles.length; i < len; i++) {
      node = nodeEles[i]
      lfNode = {
        text: {},
        properties: {}
      }
      // 处理节点
      NODE_ATTR_KEYS.forEach(attrKey => {
        value = node.getAttribute(attrKey)
        if (value) {
          if (attrKey === 'nodeType') {
            lfNode.type = value
          } else if (attrKey === 'nodeCode') {
            lfNode.id = value
          } else if (attrKey === 'coordinate') {
            const attr = value.split('|')
            const nodeXy = attr[0].split(',')
            lfNode.x = parseInt(nodeXy[0])
            lfNode.y = parseInt(nodeXy[1])
            if (attr.length === 2) {
              const textXy = attr[1].split(',')
              lfNode.text.x = parseInt(textXy[0])
              lfNode.text.y = parseInt(textXy[1])
            }
          } else if (attrKey === 'nodeName') {
            lfNode.text.value = value
          } else {
            lfNode.properties[attrKey] = value
          }
        }
      })
      graphData.nodes.push(lfNode)
      // 处理边
      let skipEles = null
      let skipEle = null
      let edge = {}
      skipEles = node.getElementsByTagName('skip')
      for (var j = 0, lenn = skipEles.length; j < lenn; j++) {
        skipEle = skipEles[j]
        edge = {
          text: {},
          properties: {},
        }
        edge.id = skipEle.getAttribute('id')
        edge.type = 'skip'
        edge.sourceNodeId = lfNode.id
        edge.targetNodeId = skipEle.textContent
        edge.text = {
          value: skipEle.getAttribute('skipName')

        }
        edge.properties.skipCondition = skipEle.getAttribute('skipCondition')
        edge.properties.skipName = skipEle.getAttribute('skipName')
        edge.properties.skipType = skipEle.getAttribute('skipType')
        const expr = skipEle.getAttribute('expr')
        if (expr) {
          edge.properties.expr = expr
        }
        const coordinate = skipEle.getAttribute('coordinate')
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
  }
  return graphData
}

/**
 * 将warm-flow的定义json数据转成LogicFlow支持的数据格式
 * @param {*} json
 * @returns LogicFlow的数据
 */
export const json2LogicFlowJson = (json) => {
  const graphData = {
    nodes: [],
    edges: []
  }
  if (!json.length) {
    return graphData
  }
  const flowCombine = JSON.parse(json)
  // 解析definition属性
  const definition = flowCombine.definition
  graphData.flowCode = definition.flowCode
  graphData.flowName = definition.flowName
  graphData.version = definition.version
  graphData.fromCustom = definition.fromCustom
  graphData.fromPath = definition.fromPath
  // 解析节点
  const allSkips = flowCombine.allSkips
  const allNodes = flowCombine.allNodes
  let node = null
  let lfNode = {}
  let coordinate = null
  // 解析节点
  if (allNodes.length) {
    for (var i = 0, len = allNodes.length; i < len; i++) {
      node = allNodes[i]
      lfNode = {
        text: {},
        properties: {}
      }
      // 处理节点
      lfNode.type = NODE_TYPE_MAP[node.nodeType]
      lfNode.id = node.nodeCode
      coordinate = node.coordinate
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
      lfNode.properties.listenerType = node.listenerType
      lfNode.properties.listenerPath = node.listenerPath
      lfNode.properties.formCustom = node.formCustom
      lfNode.properties.formPath = node.formPath
      graphData.nodes.push(lfNode)
    }
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
 * 将LogicFlow的数据转成warm-flow的定义文件
 * @param {*} data(...definitionInfo,nodes,edges)
 * @returns
 */
export const logicFlowJsonToFlowXml = (data) => {
  let xml = ''
  // data的数据由流程定义文件信息+logicFlow数据构成
  // 先构建成流程对象
  const definitionObj = {
    flowCode: data.flowCode, // 流程定义编码
    flowName: data.flowName, // 流程定义名称
    version: data.version, // 流程定义版本号
    fromCustom: data.fromCustom, // 表单自定义
    fromPath: data.fromPath, // 表单自定义路径
  }
  /**
   * 获取开始节点
   * @returns
   */
  const getStartNode = () => {
    return data.nodes.find((node) => {
      return node.type === 'start'
    })
  }
  /**
   * 获取当前节点的所有下一个节点集合
   * @param {*} id 当前节点名称
   * @returns
   */
  const getNextNodes = (id) => {
    return data.edges.filter(edge => {
      return edge.sourceNodeId === id
    }).map(edge => {
      return data.nodes.find((node) => {
        return node.id === edge.targetNodeId
      })
    })
  }
  /**
   * 获取节点所有跳转
   * @param {*} id
   * @returns
   */
  const getSkip = (id) => {
    return data.edges.filter((edge) => {
      return edge.sourceNodeId === id
    }).map(edge => {
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
      return {
        skipType: edge.properties.skipType,
        skipCondition: edge.properties.skipCondition,
        skipName: edge?.text?.value || edge.properties.skipName,
        textContent: edge.targetNodeId, // 目地节点id
        coordinate: coordinate,
      }
    })
  }
  /**
   * 构建节点属性
   * @param {} node
   * @returns
   */
  const buildNode = (node) => {
    let textXy = '';
    if (node.text) {
      textXy = '|' + node.text.x + ',' + node.text.y;
    }
    // 监听器数据处理，类型路径其一为空则不保存
    let listenerType = node.properties.listenerType ? node.properties.listenerType.split(",") : [];
    let listenerPath = node.properties.listenerPath ? node.properties.listenerPath.split("@@") : [];
    let listenerRows = [];
    listenerType.map((e, i) => {
      if (e && listenerPath[i]) listenerRows.push({ listenerType: e, listenerPath: listenerPath[i] });
    });
    return {
      nodeType: node.type,
      nodeCode: node.id,
      nodeName: (node.text instanceof String || node.text === undefined) ? node.text : node.text.value,
      permissionFlag: node.properties.permissionFlag,
      nodeRatio: node.properties.nodeRatio,
      listenerType: listenerRows.map(e => e.listenerType).join(","),
      listenerPath: listenerRows.map(e => e.listenerPath).join("@@"),
      coordinate: node.x + ',' + node.y + textXy,
      skip: getSkip(node.id),
      formCustom: node.properties.formCustom,
      formPath: node.properties.formPath
    }
  }
  /**
   * 特殊字符转义
   * @param {*} text
   * @returns
   */
  const textEncode = (text) => {
    text = text.replace(/&/g, '&amp;')
      .replace(/"/g, '&quot;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
    return text
  }
  /**
   * 递归构建节点属性
   * @param {} node
   */
  const recursionBuildNode = (node) => {
    const nodeName = node.type
    if (!definitionObj[nodeName + '_' + node.id]) {
      definitionObj[nodeName + '_' + node.id] = buildNode(node)
      const nextNodes = getNextNodes(node.id)
      nextNodes.forEach(nextNode => {
        recursionBuildNode(nextNode)
      })
    }
  }
  // 开始执行
  const startNode = getStartNode()
  if (!startNode) {
    // 开始节点不存在，xml不合法
    return ''
  }
  recursionBuildNode(startNode)
  xml = '<?xml version="1.0" encoding="UTF-8"?>\n'
  xml += '<definition'
  Object.keys(definitionObj).forEach(key => {
    const value = definitionObj[key]
    if (DEFINITION_KEYS.includes(key) && value) {
      xml += ' ' + key + '=' + '"' + textEncode(value) + '"'
    }
  })
  xml += '>\n'
  // 生成节点xml
  Object.keys(definitionObj).forEach(key => {
    const value = definitionObj[key]
    let nodeName = key.split('_')[0]
    if (NODE_NAMES.includes(nodeName)) {
      xml += '\t<node'
      // 构造属性
      Object.keys(value).forEach(nodeAttrKey => {
        if (NODE_ATTR_KEYS.includes(nodeAttrKey) && value[nodeAttrKey]) {
          xml += ' ' + nodeAttrKey + '=' + '"' + textEncode(value[nodeAttrKey]) + '"'
        }
      })
      xml += '>\n\t'
      // 构建skip
      if (value.skip) {
        value.skip.forEach(skip => {
          xml += '\t<skip'
          // skip属性
          Object.keys(skip).forEach(skipAttrKey => {
            if (SKIP_ATTR_KEYS.includes(skipAttrKey) && skip[skipAttrKey]) {
              xml += ' ' + skipAttrKey + '=' + '"' + textEncode(skip[skipAttrKey]) + '"'
            }
          })
          xml += '>'
          xml += skip['textContent'] + '</skip>\n'
        })
      }
      xml += '\t</node>\n'
    }
  })
  xml += '</definition>'
  return xml
}
/**
 * 将LogicFlow的数据转成warm-flow的json定义文件
 * @param {*} data(...definitionInfo,nodes,edges)
 * @returns
 */
export const logicFlowJsonToFlowJson = (data) => {
  // 先构建成流程对象
  const flowCombine = {
    definition: {},
    allNodes: [],
    allSkips: []
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
    for (const node of flowCombine.allNodes) {
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
  flowCombine.definition.flowCode = data.flowCode
  flowCombine.definition.flowName = data.flowName
  flowCombine.definition.version = data.version
  flowCombine.definition.fromCustom = data.fromCustom
  flowCombine.definition.fromPath = data.fromPath
  // 流程节点
  data.nodes.forEach(anyNode => {
    let node = {}
    node.nodeType = getNodeTypeValue(anyNode.type)
    node.nodeCode = anyNode.id
    node.nodeName = anyNode.text.value
    node.permissionFlag = anyNode.properties.permissionFlag
    node.nodeRatio = anyNode.properties.nodeRatio
    node.listenerType = anyNode.properties.listenerType
    node.listenerPath = anyNode.properties.listenerPath
    node.formCustom = anyNode.properties.formCustom
    node.formPath = anyNode.properties.formPath
    node.coordinate = anyNode.x + ',' + anyNode.y
    if (anyNode.text.x && anyNode.text.y) {
      node.coordinate = node.coordinate + '|' + anyNode.text.x + ',' + anyNode.text.y
    }
    node.handlerType = anyNode.properties.handlerType
    node.handlerPath = anyNode.properties.handlerPath
    node.version = flowCombine.definition.version
    flowCombine.allNodes.push(node)
  })
  // 流程跳转
  data.edges.forEach(anyEdge => {
    let skip = {}
    skip.skipType = anyEdge.properties.skipType
    skip.skipCondition = anyEdge.properties.skipCondition
    skip.skipName = anyEdge?.text?.value || anyEdge.properties.skipName
    skip.nowNodeCode = anyEdge.sourceNodeId
    skip.nowNodeType = getNodeType(skip.nowNodeCode)
    skip.nextNodeCode = anyEdge.targetNodeId
    skip.nextNodeType = getNodeType(skip.nextNodeCode)
    skip.coordinate = getCoordinate(anyEdge)
    flowCombine.allSkips.push(skip)
  })
  return JSON.stringify(flowCombine)
}

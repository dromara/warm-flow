// 测试数据
const graphData = {
  flowName: "请假流程-串行1",
  flowCode: "leaveFlow-serial1",
  version: "1.0",
  formCustom: "N",
  formPath: "test/leave/approve",
  nodes: [
    {
      id: "node_id_1",
      type: "start",
      x: 200,
      y: 200,
      text: { x: 200, y: 200, value: "开始" },
      properties: {
        status: "approval"
      },
    },
    {
      id: "node_id_2",
      type: "between",
      x: 400,
      y: 200,
      text: { x: 400, y: 200, value: "待提交" },
      properties: {
        status: "pass"
      },
    },
    {
      id: "node_id_3",
      type: "between",
      x: 600,
      y: 200,
      text: { x: 600, y: 200, value: "组长审批" },
      properties: {
        status: "approval"
      },
    },
    {
      id: "node_id_4",
      type: "between",
      x: 800,
      y: 200,
      text: { x: 800, y: 200, value: "部门审批" },
      properties: {
        status: "approval"
      },
    },
    {
      id: "node_id_5",
      type: "between",
      x: 1000,
      y: 200,
      text: { x: 1000, y: 200, value: "hr审批" },
      properties: {
        status: "approval"
      },
    },
    {
      id: "node_id_6",
      type: "end",
      x: 1200,
      y: 200,
      text: { x: 1200, y: 200, value: "结束" },
      properties: {},
    },
  ],
  edges: [
    {
      id: "edge_id",
      type: "skip",
      sourceNodeId: "node_id_1",
      targetNodeId: "node_id_2",
      text: { x: 280, y: 200, value: "通过" },
      startPoint: { x: 220, y: 200 },
      endPoint: { x: 350, y: 200 },
      properties: {},
    },
    {
      id: "edge_id",
      type: "skip",
      sourceNodeId: "node_id_2",
      targetNodeId: "node_id_3",
      text: { x: 500, y: 200, value: "通过" },
      startPoint: { x: 450, y: 200 },
      endPoint: { x: 550, y: 200 },
      properties: {},
    },
    {
      id: "edge_id",
      type: "skip",
      sourceNodeId: "node_id_3",
      targetNodeId: "node_id_4",
      text: { x: 700, y: 200, value: "通过" },
      startPoint: { x: 650, y: 200 },
      endPoint: { x: 750, y: 200 },
      properties: {},
    },
    {
      id: "edge_id",
      type: "skip",
      sourceNodeId: "node_id_4",
      targetNodeId: "node_id_5",
      text: { x: 900, y: 200, value: "通过" },
      startPoint: { x: 850, y: 200 },
      endPoint: { x: 950, y: 200 },
      properties: {},
    },
    {
      id: "edge_id",
      type: "skip",
      sourceNodeId: "node_id_5",
      targetNodeId: "node_id_6",
      text: { x: 1120, y: 200, value: "通过" },
      startPoint: { x: 1050, y: 200 },
      endPoint: { x: 1180, y: 200 },
      properties: {},
    },

    {
      id: "edge_id",
      type: "skip",
      sourceNodeId: "node_id_4",
      targetNodeId: "node_id_2",
      text: { x: 600, y: 100, value: "退回" },
      startPoint: { x: 800, y: 160 },
      endPoint: { x: 400, y: 160 },
      pointsList: [
        { x: 800, y: 160 },
        { x: 800, y: 100 },
        { x: 400, y: 100 },
        { x: 400, y: 160 },
      ],
      properties: {},
    },
  ],
};

export default graphData

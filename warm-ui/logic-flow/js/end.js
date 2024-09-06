const { CircleNode, CircleNodeModel } = Core;

class endModel extends CircleNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.r = 20
  }
}

class endView extends CircleNode {}

export default {
  type: "end",
  model: endModel,
  view: endView,
};

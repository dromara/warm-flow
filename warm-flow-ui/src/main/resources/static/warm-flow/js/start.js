const { CircleNode, CircleNodeModel } = Core;

class StartModel extends CircleNodeModel {

  initNodeData(data) {
    console.log('initNodeData', data)
    super.initNodeData(data);
    this.r = 20
  }
}

class StartView extends CircleNode {}

export default {
  type: "start",
  model: StartModel,
  view: StartView,
};

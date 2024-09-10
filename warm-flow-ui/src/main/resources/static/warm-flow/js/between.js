const { RectNode, RectNodeModel } = Core;

class BetweenModel extends RectNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.width = 100;
    this.height = 80;
    this.radius = 5;
  }
  getNodeStyle() {
    const style = super.getNodeStyle();
    return style
  }
}

class BetweenView extends RectNode {}

export default {
  type: "between",
  model: BetweenModel,
  view: BetweenView,
};

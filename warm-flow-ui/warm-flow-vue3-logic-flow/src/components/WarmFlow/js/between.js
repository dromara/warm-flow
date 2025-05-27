import {RectNode, RectNodeModel} from "@logicflow/core";
import {getStatusStyle} from "@/components/WarmFlow/js/tool.js";

class BetweenModel extends RectNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.width = 100;
    this.height = 80;
    this.radius = 5;
  }
  getNodeStyle() {
    return getStatusStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class BetweenView extends RectNode {}

export default {
  type: "between",
  model: BetweenModel,
  view: BetweenView,
};

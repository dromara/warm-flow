import { CircleNode, CircleNodeModel } from "@logicflow/core";
import {getStatusStyle} from "@/components/WarmFlow/js/tool.js";

class StartModel extends CircleNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.r = 20
  }

  getNodeStyle() {
    return getStatusStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class StartView extends CircleNode {}

export default {
  type: "start",
  model: StartModel,
  view: StartView,
};

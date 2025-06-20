import { CircleNode, CircleNodeModel } from "@logicflow/core";
import {setCommonStyle} from "@/components/WarmFlow/js/tool.js";

class endModel extends CircleNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.r = 20
  }

  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class endView extends CircleNode {}

export default {
  type: "end",
  model: endModel,
  view: endView,
};

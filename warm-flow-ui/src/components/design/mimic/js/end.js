import { CircleNode, CircleNodeModel } from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class endModel extends CircleNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.r = 20
  }

  getNodeStyle() {
    const style = setCommonStyle(super.getNodeStyle(), this.properties, "node", "mimic");
    style.strokeWidth = 4;
    return style;
  }
}

class endView extends CircleNode {}

export default {
  type: "end",
  model: endModel,
  view: endView,
};

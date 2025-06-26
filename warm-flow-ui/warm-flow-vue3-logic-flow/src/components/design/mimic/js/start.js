import { CircleNode, CircleNodeModel } from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class StartModel extends CircleNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.r = 20
  }

  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class StartView extends CircleNode {}

export default {
  type: "start",
  model: StartModel,
  view: StartView,
};

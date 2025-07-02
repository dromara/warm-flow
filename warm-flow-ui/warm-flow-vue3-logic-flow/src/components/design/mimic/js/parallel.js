import { RectNode, RectNodeModel} from '@logicflow/core'
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class ParallelModel extends RectNodeModel {
  initNodeData(data) {
    super.initNodeData(data);
    this.width = 70;
    this.height = 30;
    this.radius = 10;
  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }

}

class ParallelView extends RectNode {}

export default {
  type: 'parallel',
  view: ParallelView,
  model: ParallelModel
};

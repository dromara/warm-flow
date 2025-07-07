import { RectNode, RectNodeModel} from '@logicflow/core'
import {setCommonStyle} from "@/components/design/common/js/tool.js";
import {createApp, h} from "vue";
import baseNode from "@/components/design/mimic/vue/baseNode.vue";

class SerialModel extends RectNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.width = 70;
    this.height = 30;
    this.radius = 10;
    this.graphModel.eventCenter.emit("edit:node", {id: this.id});

  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class SerialView extends RectNode {}

export default {
  type: 'serial',
  view: SerialView,
  model: SerialModel
};

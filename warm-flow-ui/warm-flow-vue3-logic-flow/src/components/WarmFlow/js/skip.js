import { PolylineEdge, PolylineEdgeModel } from "@logicflow/core";
import {getStatusStyle} from "@/components/WarmFlow/js/tool.js";

class SkipModel extends PolylineEdgeModel {
  setAttributes() {
    this.offset = 20;
  }

  getEdgeStyle() {
    const style = super.getEdgeStyle();
    const properties = this.properties;
    if (properties.status === 2) {
      style.stroke = '#9DFF00';
    }
    if (properties.status === 1) {
      style.stroke = '#FFCD17';
    }
    return style;
  }

  /**
   * 重写此方法，使保存数据是能带上锚点数据。
   */
  getData() {
    const data = super.getData();
    data.sourceAnchorId = this.sourceAnchorId;
    data.targetAnchorId = this.targetAnchorId;
    return data;
  }

}

export default {
  type: "skip",
  view: PolylineEdge,
  model: SkipModel,
};

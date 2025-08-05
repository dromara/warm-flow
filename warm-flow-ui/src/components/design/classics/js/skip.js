import { PolylineEdge, PolylineEdgeModel } from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class SkipModel extends PolylineEdgeModel {
  setAttributes() {
    this.offset = 20;
  }

  getEdgeStyle() {
    return setCommonStyle(super.getEdgeStyle(), this.properties, "skip");

  }

  getTextStyle() {
    const style = super.getTextStyle();
    style.background = {fill: "#fff"};
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

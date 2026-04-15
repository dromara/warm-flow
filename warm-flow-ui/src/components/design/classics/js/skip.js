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
    // 动态适配亮/暗模式的文本标签背景色
    const isDark = document.documentElement.classList.contains('dark');
    style.background = { fill: isDark ? '#1e1e1e' : '#ffffff' };
    style.fill = isDark ? '#cbd5e1' : '#374151';
    style.fontSize = 12;
    style.fontWeight = 400;
    // 胶囊式圆角
    if (!style.style) {
      style.style = {};
    }
    style.style.padding = '2px 8px';
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

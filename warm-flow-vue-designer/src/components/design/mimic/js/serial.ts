import {GatewayModel} from "./gatewayModel";
import {GatewayView} from "./gatewayView";
import {h} from "@logicflow/core";
import {applyClassicDesignColor} from "../../common/js/tool";
class SerialModel extends GatewayModel {
  // 设计态：串行(互斥)网关用橙色语义色，与经典模式一致；运行态进度图沿用状态色不受影响
  getNodeStyle() {
    const style: any = super.getNodeStyle();
    applyClassicDesignColor(style, this.properties, '245,158,11');
    const inDesigner = typeof window !== 'undefined' && (window as any).__WF_FLOW_DESIGN_MODE__;
    if (inDesigner && typeof style._statusRgba === 'function') {
      style.fill = style._statusRgba(0.15);
    }
    return style;
  }
}

class SerialView extends GatewayView {

  getSvg(x: number, y: number, width: number, height: number, textValue: string, style: { stroke: string, fill: string }): h.JSX.Element {
    return h('g', {}, [
      // 图标 SVG：垂直居中于胶囊，左内边距 9
      h('svg', {
        x: x - width / 2 + 8,
        y: y - 12,
        width: 24,
        height: 24,
        viewBox: '0 0 1024 1024',
      }, [
        // 单个输入边时的图标
        h('path', {
          fill: style.fill ? style.fill : '#ecf5ff',
          d: 'M85.418667 512L512.021333 85.397333 938.624 512 512.021333 938.602667z',
        }),
        h('path', {
          fill: style.stroke ? style.stroke : '#409eff',
          d: 'M512 970.24a66.346667 66.346667 0 0 1-47.146667-19.413333L72.96 558.72a66.346667 66.346667 0 0 1 0-94.08L464.853333 72.533333a68.266667 68.266667 0 0 1 94.293334 0l392.106666 392.106667a66.773333 66.773333 0 0 1 0 94.08L559.146667 950.826667a66.346667 66.346667 0 0 1-47.146667 19.413333z m0-853.333333a2.986667 2.986667 0 0 0-1.92 0L118.186667 509.866667a2.56 2.56 0 0 0 0 3.626666l391.893333 392.106667a3.2 3.2 0 0 0 3.84 0l391.893333-392.106667a2.56 2.56 0 0 0 0-3.626666L513.92 117.76a2.986667 2.986667 0 0 0-1.92-0.64z',
        }),
        h('path', {
          fill: style.stroke ? style.stroke : '#409eff',
          d: 'M554.666667 516.266667l107.946666-107.946667a30.506667 30.506667 0 0 0-42.666666-42.666667L512 473.173333l-107.946667-107.946666a30.506667 30.506667 0 0 0-42.666666 42.666666L469.333333 516.266667l-108.8 107.946666a30.506667 30.506667 0 1 0 42.666667 42.666667l108.8-107.306667 106.666667 107.733334a30.506667 30.506667 0 0 0 42.666666-42.666667z',
        }),
      ]),
      // 文本元素：随类型语义色着色 + 半粗，与图标同基线垂直居中
      textValue ? h('text', {
        x: x - width / 2 + 35,
        y: y + 4.5,
        fontSize: 12.5,
        style: {
          userSelect: 'none',
          fill: style.stroke || '#409eff',
          fontWeight: 600,
        }
      }, textValue) : null
    ]);
  }
}

export default {
  type: 'serial',
  view: SerialView,
  model: SerialModel
};

import {GatewayModel} from "./gatewayModel";
import {GatewayView} from "./gatewayView";
import {h} from "@logicflow/core";

class ParallelModel extends GatewayModel {}

class ParallelView extends GatewayView {

  getSvg(x: number, y: number, width: number, height: number, textValue: string, style: { stroke: string }) {
    // 创建一个包含图标和文本的容器
    return h('g', {}, [
      // 图标 SVG
      h('svg', {
        x: x - width / 2,
        y: y - height / 2 + 3,
        width: 25,
        height: 25,
        viewBox: '0 0 1024 1024',
      }, [
        h('path', {
          fill: '#FFFCEB',
          d: 'M85.418667 512L512.021333 85.397333 938.624 512 512.021333 938.602667z',
        }),
        h('path', {
          fill: '#FF8B00',
          d: 'M512 714.453333A32 32 0 0 1 480 682.666667v-138.666667H341.333333a32 32 0 1 1 0-64h138.666667V341.333333a32 32 0 0 1 64 0v138.666667H682.666667a32 32 0 0 1 0 64h-138.666667V682.666667a32 32 0 0 1-32 31.786666z',
        }),
        h('path', {
          fill: '#FF8B00',
          d: 'M512 970.453333a66.56 66.56 0 0 1-47.146667-19.626666L72.96 558.933333a66.346667 66.346667 0 0 1 0-94.293333L464.853333 72.533333a68.266667 68.266667 0 0 1 94.293334 0l391.893333 392.106667a66.346667 66.346667 0 0 1 0 94.293333L559.146667 950.826667a66.56 66.56 0 0 1-47.146667 19.626666z m0-853.333333a2.986667 2.986667 0 0 0-1.92 0L118.186667 509.866667a2.56 2.56 0 0 0 0 3.626666l391.893333 392.106667a3.2 3.2 0 0 0 3.84 0l391.893333-392.106667a2.986667 2.986667 0 0 0 0-3.626666L513.92 117.76a2.986667 2.986667 0 0 0-1.92-0.64z',
        }),
      ]),
      // 文本元素，相对于g元素定位
      textValue ? h('text', {
        x: x - width / 2 + 27,
        y: y - height / 2 + 20,
        fontSize: 13,
        fill: style.stroke,
        style: {
          userSelect: 'none',
        }
      }, textValue) : null
    ]);
  }
}

export default {
  type: 'parallel',
  view: ParallelView,
  model: ParallelModel
};

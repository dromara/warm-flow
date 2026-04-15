import { CircleNode, CircleNodeModel, h } from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class endModel extends CircleNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.r = 25
  }

  getNodeStyle() {
    const style = setCommonStyle(super.getNodeStyle(), this.properties, "node");
    style.strokeWidth = 3;
    return style;
  }
}

class endView extends CircleNode {

  getShape() {
    const { model } = this.props;
    const { x, y, r } = model;
    const style = model.getNodeStyle();
    const sc = style._statusColorRGB || '166,178,189';
    const isDark = typeof document !== 'undefined' && document.documentElement.classList.contains('dark');

    return h('g', {}, [
      // 定义渐变和滤镜
      h('defs', {}, [
        // 外环渐变
        h('linearGradient', { id: `end-grad-${model.id}`, x1: '0%', y1: '0%', x2: '100%', y2: '100%' }, [
          h('stop', { offset: '0%', stopColor: `rgb(${sc})` }),
          h('stop', { offset: '100%', stopColor: `rgba(${sc}, 0.45)` }),
        ]),
        // 内圈填充渐变（暗黑模式适配：加深底色让节点不刺眼）
        h('radialGradient', { id: `end-fill-${model.id}`, cx: '50%', cy: '50%', r: '60%' }, [
          h('stop', { offset: '0%', stopColor: isDark ? `rgba(${sc}, 0.15)` : `rgba(${sc}, 0.08)` }),
          h('stop', { offset: '100%', stopColor: isDark ? '#181a20' : 'rgba(248,250,252,0.98)' }),
        ]),
        // 双环阴影/发光
        h('filter', { id: `end-shadow-${model.id}`, x: '-30%', y: '-30%', width: '160%', height: '160%' }, [
          h('feDropShadow', { dx: 0, dy: 2, stdDeviation: 3, floodColor: `rgb(${sc})`, floodOpacity: isDark ? 0.25 : 0.15 }),
        ]),
      ]),
      // 外层光晕
      h('circle', {
        cx: x, cy: y, r: r + 5,
        fill: `rgba(${sc}, ${isDark ? 0.08 : 0.04})`,
      }),
      // 内圈填充（暗黑模式使用深色基底）
      h('circle', {
        cx: x, cy: y, r: r - 1,
        fill: `url(#end-fill-${model.id})`,
      }),
      // 粗外环（主视觉）
      h('circle', {
        cx: x, cy: y, r: r,
        fill: 'none',
        stroke: `url(#end-grad-${model.id})`,
        strokeWidth: 3.5,
        filter: `url(#end-shadow-${model.id})`,
        style: { cursor: 'pointer' },
      }),
      // 细内环装饰
      h('circle', {
        cx: x, cy: y, r: r * 0.72,
        fill: 'none',
        stroke: `rgba(${sc}, ${isDark ? 0.35 : 0.25})`,
        strokeWidth: 1,
      }),
    ]);
  }
}

export default {
  type: "end",
  model: endModel,
  view: endView,
};

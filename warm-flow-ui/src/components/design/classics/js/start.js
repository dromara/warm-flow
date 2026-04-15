import { CircleNode, CircleNodeModel, h } from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class StartModel extends CircleNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.r = 25
  }

  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class StartView extends CircleNode {

  getShape() {
    const { model } = this.props;
    const { x, y, r } = model;
    const style = model.getNodeStyle();
    const sc = style._statusColorRGB || '166,178,189';
    const isDark = typeof document !== 'undefined' && document.documentElement.classList.contains('dark');

    return h('g', {}, [
      // 定义渐变和滤镜
      h('defs', {}, [
        // 外环渐变描边
        h('linearGradient', { id: `start-grad-${model.id}`, x1: '0%', y1: '0%', x2: '100%', y2: '100%' }, [
          h('stop', { offset: '0%', stopColor: `rgb(${sc})` }),
          h('stop', { offset: '100%', stopColor: `rgba(${sc}, 0.5)` }),
        ]),
        // 内核径向渐变（实心圆点）
        h('radialGradient', { id: `start-core-${model.id}`, cx: '35%', cy: '35%', r: '65%' }, [
          h('stop', { offset: '0%', stopColor: `rgba(${sc}, 0.9)` }),
          h('stop', { offset: '70%', stopColor: `rgba(${sc}, 0.4)` }),
          h('stop', { offset: '100%', stopColor: `rgba(${sc}, 0.15)` }),
        ]),
        // 主外环背景填充（暗黑模式适配）
        h('radialGradient', { id: `start-bg-${model.id}`, cx: '50%', cy: '50%', r: '55%' }, [
          h('stop', { offset: '0%', stopColor: isDark ? '#252830' : '#ffffff' }),
          h('stop', { offset: '100%', stopColor: isDark ? '#181a20' : '#f0f2f5' }),
        ]),
        // 外发光滤镜
        h('filter', { id: `start-glow-${model.id}`, x: '-50%', y: '-50%', width: '200%', height: '200%' }, [
          h('feGaussianBlur', { stdDeviation: '3', result: 'blur' }),
          h('feFlood', { floodColor: `rgb(${sc})`, floodOpacity: 0.25, result: 'color' }),
          h('feComposite', { in: 'color', in2: 'blur', operator: 'in' }),
        ]),
      ]),
      // 外层发光效果（半透明大圆）
      h('circle', {
        cx: x, cy: y, r: r + 6,
        fill: `rgba(${sc}, ${isDark ? 0.10 : 0.06})`,
      }),
      // 主外环（渐变描边 + 暗黑适配背景）
      h('circle', {
        cx: x, cy: y, r: r,
        fill: `url(#start-bg-${model.id})`,
        stroke: `url(#start-grad-${model.id})`,
        strokeWidth: 2.5,
        filter: `url(#start-glow-${model.id})`,
        style: { cursor: 'pointer' },
      }),
      // 内核实心渐变圆点（放大）
      h('circle', {
        cx: x, cy: y, r: r * 0.72,
        fill: `url(#start-core-${model.id})`,
      }),
    ]);
  }
}

export default {
  type: "start",
  model: StartModel,
  view: StartView,
};

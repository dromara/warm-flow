import {RectNode, RectNodeModel, h} from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class BetweenModel extends RectNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.width = 100;
    this.height = 80;
    this.radius = 8;
  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class BetweenView extends RectNode {

  /** 现代化审批/任务图标 */
  getIconShape() {
    const {model} = this.props;
    const {x, y} = model;
    const style = model.getNodeStyle();
    const sc = style._statusColorRGB || '166,178,189';
    const iconX = x - 46; // 左上角
    const iconY = y - 34;

    return h(
        'svg',
        {
          x: iconX,
          y: iconY,
          width: 26,
          height: 26,
          viewBox: '0 0 24 24',
        },
        [
          // 圆角矩形卡片背景
          h('rect', {
            x: 2, y: 3,
            width: 18, height: 16,
            rx: 3, ry: 3,
            fill: `rgba(${sc}, 0.08)`,
            stroke: `rgb(${sc})`,
            strokeWidth: 1.2,
            strokeLinejoin: 'round',
          }),
          // 对勾或用户标识
          h('circle', {
            cx: 11, cy: 10,
            r: 3.5,
            fill: `rgba(${sc}, 0.15)`,
            stroke: `rgb(${sc})`,
            strokeWidth: 1,
          }),
          h('path', {
            d: 'M9 10 l2 2 l4 -4',
            fill: 'none',
            stroke: `rgb(${sc})`,
            strokeWidth: 1.5,
            strokeLinecap: 'round',
            strokeLinejoin: 'round',
          }),
          // 底部线条装饰（模拟文档）
          h('line', {
            x1: 6, y1: 16.5, x2: 16, y2: 16.5,
            stroke: `rgba(${sc}, 0.25)`,
            strokeWidth: 1,
            strokeLinecap: 'round',
          }),
        ]
    );
  }

  // 自定义节点外观
  getShape() {
    const { model } = this.props;
    const { x, y, width, height, radius } = model;
    const style = model.getNodeStyle();
    const sc = style._statusColorRGB || '166,178,189';

    return h('g', {style: {cursor: 'pointer'}}, [
      // 定义渐变和滤镜
      h('defs', {}, [
        // 卡片阴影
        h('filter', { id: `card-shadow-${model.id}`, x: '-20%', y: '-20%', width: '140%', height: '140%' }, [
          h('feDropShadow', { dx: 0, dy: 3, stdDeviation: 6, floodColor: '#000', floodOpacity: 0.08 }),
          h('feDropShadow', { dx: 0, dy: 1, stdDeviation: 2, floodColor: '#000', floodOpacity: 0.04 }),
        ]),
        // 顶部光泽渐变
        h('linearGradient', { id: `card-top-${model.id}`, x1: '0%', y1: '0%', x2: '100%', y2: '0%' }, [
          h('stop', { offset: '0%', stopColor: `rgba(${sc}, 0.12)` }),
          h('stop', { offset: '50%', stopColor: `rgba(${sc}, 0.03)` }),
          h('stop', { offset: '100%', stopColor: `rgba(255,255,255,0)` }),
        ]),
      ]),

      // 主卡片矩形
      h('rect', {
        x: x - width / 2,
        y: y - height / 2,
        rx: radius,
        ry: radius,
        width,
        height,
        fill: style.fill,
        stroke: style.stroke,
        strokeWidth: style.strokeWidth || 1.5,
        strokeLinejoin: 'round',
        filter: `url(#card-shadow-${model.id})`,
      }),

      // 顶部渐变光泽条
      h('rect', {
        x: x - width / 2 + 1,
        y: y - height / 2 + 1,
        rx: radius,
        ry: radius,
        width: width - 2,
        height: height * 0.18,
        fill: `url(#card-top-${model.id})`,
        clipPath: `inset(0 0 ${height * 0.82}px 0 round ${radius}px)`,
      }),


      // 图标
      this.getIconShape(),
    ]);
  }
}

export default {
  type: "between",
  model: BetweenModel,
  view: BetweenView,
};

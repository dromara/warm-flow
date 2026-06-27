import { h, PolygonNode, PolygonNodeModel } from '@logicflow/core'
import {setCommonStyle, applyClassicDesignColor} from "@/components/design/common/js/tool";

class InclusiveModel extends PolygonNodeModel {
  static extendKey = 'InclusiveModel';
  constructor (data, graphModel) {
    if (!data.text) {
      data.text = ''
    }
    if (data.text && typeof data.text === 'string') {
      data.text = {
        value: data.text,
        x: data.x,
        y: data.y + 40
      }
    }
    super(data, graphModel)
    this.points = [
      [25, 0],
      [50, 25],
      [25, 50],
      [0, 25]
    ]
  }

  getNodeStyle() {
    const style = setCommonStyle(super.getNodeStyle(), this.properties, "node");
    // 设计态语义色：包含网关用紫色（包容多选）
    applyClassicDesignColor(style, this.properties, '146,84,222');
    style.fill = style._statusRgba ? style._statusRgba(0.06) : 'rgba(166,178,189,0.06)';
    style.strokeWidth = 2;
    return style;
  }
}

class InclusiveView extends PolygonNode {
  static extendKey = 'InclusiveNode';
  getShape (): h.JSX.Element {
    const { model } = this.props
    const { x, y, width, height, points } = model
    const style = model.getNodeStyle()
    const sc = style._statusColorRGB || '166,178,189';

    return h(
      'g',
      {
        transform: `matrix(1 0 0 1 ${x - width / 2} ${y - height / 2})`
      },
      [
        // 定义滤镜
        h('defs', {}, [
          h('filter', { id: `gw-shadow-i-${model.id}`, x: '-30%', y: '-30%', width: '160%', height: '160%' }, [
            h('feDropShadow', { dx: 0, dy: 2, stdDeviation: 3, floodColor: '#000', floodOpacity: 0.05 }),
          ]),
        ]),
        // 菱形底座
        h('polygon', {
          ...style,
          x,
          y,
          points,
          filter: `url(#gw-shadow-i-${model.id})`,
        }),
        // 同心双圆图标（替代原来的单圆）
        h('g', {},
          // 外圆
          h('circle', {
            cx: 25, cy: 25, r: 11,
            fill: 'none',
            stroke: style.stroke || '#666',
            strokeWidth: 2,
          }),
          // 内圆（增加识别度）
          h('circle', {
            cx: 25, cy: 25, r: 5.5,
            fill: sc ? `rgba(${sc}, 0.15)` : 'rgba(102,102,102,0.15)',
            stroke: style.stroke || '#666',
            strokeWidth: 1.5,
          }),
          // 内圆中心微点缀
          h('circle', {
            cx: 25, cy: 25, r: 1.8,
            fill: style._statusHex || '#666',
          }),
        ),
      ]
    )
  }
}

export default {
  type: 'inclusive',
  view: InclusiveView,
  model: InclusiveModel
};

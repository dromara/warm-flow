import { h, PolygonNode, PolygonNodeModel } from '@logicflow/core'
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class SerialModel extends PolygonNodeModel {
  static extendKey = 'SerialModel';
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
    style.fill = style._statusRgba ? style._statusRgba(0.06) : 'rgba(166,178,189,0.06)';
    style.strokeWidth = 2;
    return style;
  }
}

class SerialView extends PolygonNode {
  static extendKey = 'SerialNode';
  getShape () {
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
          h('filter', { id: `gw-shadow-${model.id}`, x: '-30%', y: '-30%', width: '160%', height: '160%' }, [
            h('feDropShadow', { dx: 0, dy: 2, stdDeviation: 3, floodColor: '#000', floodOpacity: 0.07 }),
          ]),
        ]),
        // 菱形底座
        h('polygon', {
          ...style,
          x,
          y,
          points,
          filter: `url(#gw-shadow-${model.id})`,
        }),
        // X 图标（加粗 + 两端圆点装饰）
        h('g', {},
          // 主 X 形
          h('path', {
            d: 'M14.5 13 L22 21 M22 13 L14.5 21',
            fill: 'none',
            stroke: style.stroke || '#666',
            strokeWidth: 2.5,
            strokeLinecap: 'round',
            strokeLinejoin: 'round',
          }),
          // 四端小装饰圆点
          h('circle', { cx: 13, cy: 11.5, r: 1.3, fill: style._statusHex || '#666' }),
          h('circle', { cx: 23.5, cy: 11.5, r: 1.3, fill: style._statusHex || '#666' }),
          h('circle', { cx: 23.5, cy: 22.5, r: 1.3, fill: style._statusHex || '#666' }),
          h('circle', { cx: 13, cy: 22.5, r: 1.3, fill: style._statusHex || '#666' }),
        ),
      ]
    )
  }
}

export default {
  type: 'serial',
  view: SerialView,
  model: SerialModel
};

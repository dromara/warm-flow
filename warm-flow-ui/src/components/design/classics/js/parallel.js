import { h, PolygonNode, PolygonNodeModel } from '@logicflow/core'
import {setCommonStyle} from "@/components/design/common/js/tool.js";

class ParallelModel extends PolygonNodeModel {
  static extendKey = 'ParallelModel';
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

class ParallelView extends PolygonNode {
  static extendKey = 'ParallelNode';
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
          h('filter', { id: `gw-shadow-p-${model.id}`, x: '-30%', y: '-30%', width: '160%', height: '160%' }, [
            h('feDropShadow', { dx: 0, dy: 2, stdDeviation: 3, floodColor: '#000', floodOpacity: 0.07 }),
          ]),
        ]),
        // 菱形底座
        h('polygon', {
          ...style,
          x,
          y,
          points,
          filter: `url(#gw-shadow-p-${model.id})`,
        }),
        // + 图标（加粗 + 四臂末端圆角装饰）
        h('g', {},
          // 横线（两端带圆角端点）
          h('path', {
            d: 'M12 25 L38 25',
            fill: 'none',
            stroke: style.stroke || '#666',
            strokeWidth: 3,
            strokeLinecap: 'round',
          }),
          // 竖线
          h('path', {
            d: 'M25 12 L25 38',
            fill: 'none',
            stroke: style.stroke || '#666',
            strokeWidth: 3,
            strokeLinecap: 'round',
          }),
          // 四臂末端装饰圆点
          h('circle', { cx: 25, cy: 10.5, r: 1.5, fill: style._statusHex || '#666' }),
          h('circle', { cx: 39.5, cy: 25, r: 1.5, fill: style._statusHex || '#666' }),
          h('circle', { cx: 25, cy: 39.5, r: 1.5, fill: style._statusHex || '#666' }),
          h('circle', { cx: 10.5, cy: 25, r: 1.5, fill: style._statusHex || '#666' }),
        ),
      ]
    )
  }
}

export default {
  type: 'parallel',
  view: ParallelView,
  model: ParallelModel
};

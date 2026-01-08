import { h, PolygonNode, PolygonNodeModel } from '@logicflow/core'
import {setCommonStyle} from "@/components/design/common/js/tool.js";

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
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class InclusiveView extends PolygonNode {
  static extendKey = 'InclusiveNode';
  getShape () {
    const { model } = this.props
    const { x, y, width, height, points } = model
    const style = model.getNodeStyle()
    return h(
      'g',
      {
        transform: `matrix(1 0 0 1 ${x - width / 2} ${y - height / 2})`
      },
      h('polygon', {
        ...style,
        x,
        y,
        points
      }),
      h('circle', {
        cx: 25,
        cy: 25,
        r: 12,
        ...style,
      })
    )
  }
}

export default {
  type: 'inclusive',
  view: InclusiveView,
  model: InclusiveModel
};

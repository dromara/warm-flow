import { RectNode, h} from '@logicflow/core'

export abstract class GatewayView extends RectNode {

  abstract getSvg(x: number, y: number, width: number, height: number, textValue: string, style: any): any

  getLabelShape() {
    const {model} = this.props;
    const {x, y, width, height, text} = model;
    const style = model.getNodeStyle();
    const incomingEdges = model.graphModel.getNodeIncomingEdge(model.id)

    // 创建一个包含图标和文本的容器
    if (incomingEdges.length > 1) {
      return h(
          'svg',
          {
            x: x - width / 2  + 23,
            y: y - height / 2 + 3,
            width: 25,
            height: 25,
            viewBox: '0 0 1024 1024',
          },
          [
            // 多个输入边时的图标
            h('path', {
              fill: '#FF8B00',
              d: 'M171.52 280.064H51.2c-28.672 0-51.2-22.528-51.2-51.2V108.544c0-28.672 22.528-51.2 51.2-51.2h120.32c28.672 0 51.2 22.528 51.2 51.2v120.32c0 28.672-22.528 51.2-51.2 51.2zM51.2 108.544v120.32h120.32V108.544H51.2zM572.416 280.064H451.584c-28.672 0-51.2-22.528-51.2-51.2V108.544c0-28.672 22.528-51.2 51.2-51.2h120.32c28.672 0 51.2 22.528 51.2 51.2v119.808c0.512 13.312-4.608 26.112-13.824 35.84-9.728 10.24-23.04 15.872-36.864 15.872z m-0.512-50.176zM451.584 108.544v120.32h120.32V108.544H451.584zM972.8 280.064h-120.32c-28.672 0-51.2-22.528-51.2-51.2V108.544c0-28.672 22.528-51.2 51.2-51.2H972.8c28.672 0 51.2 22.528 51.2 51.2v120.32c0 28.672-22.528 51.2-51.2 51.2z m-120.32-171.52v120.32H972.8V108.544h-120.32zM572.416 966.656H451.584c-28.672 0-51.2-22.528-51.2-51.2v-120.32c0-28.672 22.528-51.2 51.2-51.2h120.32c28.672 0 51.2 22.528 51.2 51.2v119.808c0.512 13.312-4.608 26.112-13.824 35.84-9.728 10.24-23.04 15.872-36.864 15.872z m-0.512-50.176z m-120.32-121.344v120.32h120.32v-120.32H451.584z',
            }),
            h('path', {
              fill: '#FF8B00',
              d: 'M861.696 566.272H162.816c-42.496 0-76.8-34.304-76.8-76.8V254.464c0-14.336 11.264-25.6 25.6-25.6s25.6 11.264 25.6 25.6v235.008c0 14.336 11.264 25.6 25.6 25.6h698.88c14.336 0 25.6-11.264 25.6-25.6V254.464c0-14.336 11.264-25.6 25.6-25.6s25.6 11.264 25.6 25.6v235.008c0 42.496-34.304 76.8-76.8 76.8z',
            }),
            h('path', {
              fill: '#FF8B00',
              d: 'M512 766.464c-14.336 0-25.6-11.264-25.6-25.6V283.136c0-14.336 11.264-25.6 25.6-25.6s25.6 11.264 25.6 25.6v457.728c0 14.336-11.264 25.6-25.6 25.6z',
            }),
          ],
      );
    }
    return this.getSvg(x, y, width, height, text.value, style)
  }

  // 自定义节点外观
  getShape() {
    const { model } = this.props;
    const { x, y, width, height, radius } = model;
    const style = model.getNodeStyle();
    return h('g', {style: {
        cursor: 'pointer'
      }}, [
      h('rect', {
        ...style,
        x: x - width / 2, // 矩形默认x，y代表左上角顶点坐标，切换为中心点
        y: y - height / 2,
        rx: radius,
        ry: radius,
        width,
        height,
      }),
      this.getLabelShape(),
    ]);
  }
}

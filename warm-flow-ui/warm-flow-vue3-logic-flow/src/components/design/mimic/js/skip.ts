import {setCommonStyle} from "@/components/design/common/js/tool";
import {CurvedEdge, CurvedEdgeModel} from "@logicflow/extension";
import {h} from "@logicflow/core";
import {getCurvedEdgePath} from "@logicflow/extension/src/materials/curved-edge";

class SkipModel extends CurvedEdgeModel  {

  setAttributes() {
    this.isHitable = false // 细粒度控制边是否对用户操作进行反应
  }

  getEdgeStyle() {
    let style = super.getEdgeStyle()
    return setCommonStyle(style, this.properties, "skip");

  }

  /**
   * 重写此方法，使保存数据是能带上锚点数据。
   */
  getData() {
    const data = super.getData();
    data.sourceAnchorId = this.sourceAnchorId;
    data.targetAnchorId = this.targetAnchorId;
    return data;
  }

}

class SkipView extends CurvedEdge {

  getEdge(): h.JSX.Element {
    const { model } = this.props;
    const { points: pointsStr, isAnimation, arrowConfig, radius = 5 } = model;
    const style = model.getEdgeStyle();
    const animationStyle = model.getEdgeAnimationStyle();
    let points = this.pointFilter(pointsStr.split(' ').map((p) => p.split(',').map((a) => +a)));

    // 主路径
    let mainPath = getCurvedEdgePath(points, radius as number);
    let plusElements: h.JSX.Element[] = [];

    // 直线中间增加加号
    if (points.length === 2) {
      const midPoint = [(points[0][0] + points[1][0]) / 2, (points[0][1] + points[1][1]) / 2];
      plusElements = this.getPlusElements(midPoint);
    }

    // 折线特定条件增加加号
    else {
      for (let i = 0; i < points.length - 2; i++) {
        const p0 = points[i];
        const p1 = points[i + 1];
        const p2 = points[i + 2];

        // 判断是否由竖线变为横线
        if (Math.abs(p0[0] - p1[0]) === 5 && p0[1] !== p1[1]) {
          const midPoint = [(p0[0] + p1[0]) / 2, (p0[1] + p1[1]) / 2];
          plusElements = this.getPlusElements(midPoint);
        }
        // 判断是否由横线变为竖线
        if (Math.abs(p0[1] - p1[1]) === 5 && p0[0] !== p1[0]) {
          const midPoint = [(p1[0] + p2[0]) / 2, (p1[1] + p2[1]) / 2];
          plusElements = this.getPlusElements(midPoint);
        }
      }
    }

    // 绘制主路径
    let mainPathElement: h.JSX.Element[] = [];
    debugger
    mainPathElement.push(h('path', {
      d: mainPath,
      style: isAnimation ? animationStyle : {},
      ...style,
      ...arrowConfig,
      fill: 'none',
    }));

    // 返回所有路径元素
    return h('g', {}, [mainPathElement, ...plusElements]);
  }

  private getPlusElements(midPoint: number[]) {
    let plusElements: h.JSX.Element[] = [];
    plusElements.push(
        h('g', {
          onMouseEnter: (e) => {
            this.props.graphModel.eventCenter.emit("show:EdgeTooltip", {e: e, id: this.props.model.id});
          },
          onMouseLeave: (e) => {
            setTimeout(() => {
              if (!(window as any).isTooltipHovered) {
                this.props.graphModel.eventCenter.emit("hide:EdgeTooltip", e);

              }
            }, 100);
          },
          style: {
            pointerEvents: 'auto'
          }
        }, [
          h('circle', {
            cx: midPoint[0],
            cy: midPoint[1],
            r: 16,
            fill: 'blue',
          }),
          h('line', {
            x1: midPoint[0] - 8,
            y1: midPoint[1],
            x2: midPoint[0] + 8,
            y2: midPoint[1],
            stroke: 'white',
            'stroke-width': '2',
          }),
          h('line', {
            x1: midPoint[0],
            y1: midPoint[1] - 8,
            x2: midPoint[0],
            y2: midPoint[1] + 8,
            stroke: 'white',
            'stroke-width': '2',
          })
        ])
    );
    return plusElements;
  }
  private pointFilter(points: number[][]) {
    const all = points
    let i = 1
    while (i < all.length - 1) {
      const [x, y] = all[i - 1]
      const [x1, y1] = all[i]
      const [x2, y2] = all[i + 1]
      if ((x === x1 && x1 === x2) || (y === y1 && y1 === y2)) {
        all.splice(i, 1)
      } else {
        i++
      }
    }
    return all
  }
}

export default {
  type: "skip",
  view: SkipView,
  model: SkipModel,
};

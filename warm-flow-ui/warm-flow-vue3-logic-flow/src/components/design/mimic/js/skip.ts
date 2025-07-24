import {setCommonStyle} from "@/components/design/common/js/tool";
import {CurvedEdge, CurvedEdgeModel} from "@logicflow/extension";
import {h} from "@logicflow/core";
import {getCurvedEdgePath} from "@logicflow/extension/src/materials/curved-edge";

class SkipModel extends CurvedEdgeModel  {

  setAttributes() {
    this.isHitable = false // 细粒度控制边是否对用户操作进行反应
  }

  getEdgeStyle() {
    return setCommonStyle(super.getEdgeStyle(), this.properties, "skip");

  }

  getTextPosition() {
    const position = super.getTextPosition();

    const currentPositionList = this.points.split(' ');
    if (!currentPositionList || currentPositionList.length <= 2) {
      return position;
    }

    // 取最后两个点，用于判断终点方向
    const lastTwoPoints = currentPositionList.slice(-2);
    const [x2, y2] = lastTwoPoints[1].split(',').map(Number);

    // 设置文本位置
    position.x = x2;
    position.y = y2 - 30;

    return position;
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
    const { points: pointsStr, isAnimation, arrowConfig, radius = 0 } = model;
    const style = model.getEdgeStyle();
    const animationStyle = model.getEdgeAnimationStyle();
    let points = this.pointFilter(pointsStr.split(' ').map((p) => p.split(',').map((a) => +a)));

    // 主路径
    let mainPath = getCurvedEdgePath(points, radius as number);
    let plusElements: h.JSX.Element[] = [];

    const offsetY = 30

    // 跳转线是直线
    if (points.length === 2) {
      let nextEdge = model.graphModel.edges.filter(edge => edge.sourceNodeId ===  model.sourceNode.id);
      // 如果上一个节点是互斥网关，并且网关后节点大于1个，也就是说是互斥网关结束节点时
      if ((model.sourceNode.type as string) === "serial" && nextEdge.length > 1) {
        const midPoint = [points[0][0], points[0][1] + offsetY - 10];
        plusElements = this.getForeignObject(midPoint);
      } else {
        const midPoint = [points[0][0], points[0][1] + offsetY];
        plusElements = this.getPlusElements(midPoint);
      }
    } else {
      const p0 = points[0];
      const p1 = points[1];
      const p2 = points[2];

      // 判断是否由竖线变为横线
      if (p0[0] === p1[0] && p0[1] !== p1[1]) {
        const midPoint = [p0[0] , p0[1] + offsetY];
        plusElements = this.getPlusElements(midPoint);
      }

      // 判断是否由横线变为竖线，并且是互斥网关
      if (model.sourceNode && (model.sourceNode.type as string) === "serial" && p0[1] === p1[1] && p0[0] !== p1[0]) {
        const midPoint = [p2[0], p1[1] + offsetY];
        plusElements = this.getForeignObject(midPoint);
      }
    }

    // 绘制主路径
    let mainPathElement: h.JSX.Element[] = [];
    mainPathElement.push(h('path', {
      d: mainPath,
      style: isAnimation ? animationStyle : {},
      ...style,
      ...arrowConfig,
      fill: 'none',
    }));

    // 返回所有路径元素
    return h('g', {}, [...mainPathElement, ...plusElements]);
  }

  private getForeignObject(midPoint: number[]) {
    const obj = [
      // 使用 SVG 图标代替原来的图形
      h('foreignObject', {
        x: midPoint[0] - 16,
        y: midPoint[1] - 20,
        width: 32,
        height: 32,
      }, [
        h('div', {
          style: {
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: '100%',
            height: '100%'
          },
          innerHTML: `
<div style="width: 32px; height: 32px; transform: rotate(0deg); display: flex; align-items: center; justify-content: center;">
  <svg t="1751615394607" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11883">
    <path d="M853.5 960.7h-683c-59.3 0-106-46.8-106-106v-683c0-59.3 46.8-106 106-106h682.9c59.3 0 106 46.8 106 106v682.9c0.1 59.3-49.8 106.1-105.9 106.1z" fill="#93C861" p-id="11884"></path>
    <path d="M666.4 564.7c31.2 9.4 49.9 37.4 46.8 68.6-3.1 31.2-31.2 53-62.4 53s-59.3-21.8-62.4-53c-3.1-31.2 15.6-59.3 46.8-68.6v-65.5c0-9.4-6.2-15.6-15.6-15.6H401.3c-9.4 0-15.6 6.2-15.6 15.6v65.5c31.2 9.4 49.9 37.4 46.8 68.6-3.1 31.2-31.2 53-62.4 53s-59.3-21.8-62.4-53c-3.1-31.2 15.6-59.3 46.8-68.6v-81.1c0-18.7 12.5-31.2 31.2-31.2h109.1v-81.1c-31.2-9.4-49.9-37.4-46.8-68.6 3.1-31.2 31.2-53 62.4-53s59.3 21.8 62.4 53-15.6 59.3-46.8 68.6v81.1h109.1c18.7 0 31.2 12.5 31.2 31.2v81.1z m-156-218.3c18.7 0 31.2-12.5 31.2-31.2S529.1 284 510.4 284s-31.2 12.5-31.2 31.2c0.1 15.6 15.6 31.2 31.2 31.2zM370.1 655.1c18.7 0 31.2-12.5 31.2-31.2s-12.5-31.2-31.2-31.2-31.2 12.5-31.2 31.2 15.6 31.2 31.2 31.2z m280.7 0c18.7 0 31.2-12.5 31.2-31.2s-12.5-31.2-31.2-31.2-31.2 12.5-31.2 31.2 12.5 31.2 31.2 31.2z" 
    fill="#FFFFFF" p-id="11885"></path>
  </svg>
</div>            
`,
        })
      ])
    ]

    return  this.getAddElements(midPoint, obj, true);
  }

  private getPlusElements(midPoint: number[]) {
    return this.getAddElements(midPoint, null);
  }

  private getAddElements(midPoint: number[], obj, isCondition: boolean = false) {
    if (!obj) {
      const x = midPoint[0]
      const y = midPoint[1]
      obj = [
        h('circle', {
          cx: x,
          cy: y,
          r: 13,
          fill: 'blue',
        }),
        h('line', {
          x1: x - 8,
          y1: y,
          x2: x + 8,
          y2: y,
          stroke: 'white',
          'stroke-width': '2',
        }),
        h('line', {
          x1: x,
          y1: y - 8,
          x2: x,
          y2: y + 8,
          stroke: 'white',
          'stroke-width': '2',
        })
      ]
    }
    let plusElements: h.JSX.Element[] = [];
    plusElements.push(
        h('g', {
          ...this.getEventHandlers(isCondition), // 根据 isCondition 动态选择事件处理器
          style: {
            pointerEvents: 'auto',
            cursor: 'pointer'
          }
        }, obj)
    );
    return plusElements;
  }

  // 新增方法：根据 isCondition 返回不同的事件处理器
  private getEventHandlers(isCondition: boolean): any {
    if (isCondition) {
      return {
        onClick: () => {
          this.props.graphModel.eventCenter.emit("show:EdgeSetting", { id: this.props.model.id });
        }
      };
    } else {
      return {
        onMouseEnter: (e) => {
          this.props.graphModel.eventCenter.emit("show:EdgeTooltip", { e: e, id: this.props.model.id });
        },
        onMouseLeave: (e) => {
          setTimeout(() => {
            if (!(window as any).isTooltipHovered) {
              this.props.graphModel.eventCenter.emit("hide:EdgeTooltip", e);
            }
          }, 100);
        }
      };
    }
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

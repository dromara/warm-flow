import {setCommonStyle} from "../../common/js/tool";
import {CurvedEdge, CurvedEdgeModel, getCurvedEdgePath} from "@logicflow/extension";
import {h} from "@logicflow/core";

class SkipModel extends CurvedEdgeModel  {

  setAttributes() {
    this.isHitable = false // 细粒度控制边是否对用户操作进行反应
  }

  getEdgeStyle() {
    return setCommonStyle(super.getEdgeStyle(), this.properties, "skip", "mimic");

  }

  // getTextPosition() {
  //   const position = super.getTextPosition();
  //
  //   const currentPositionList = this.points.split(' ');
  //
  //   // 取最后两个点，用于判断终点方向
  //   const lastTwoPoints = currentPositionList.slice(-2);
  //   const [x2, y2] = lastTwoPoints[1].split(',').map(Number);
  //
  //   // 设置文本位置
  //   position.x = x2;
  //   position.y = y2 - 30;
  //
  //   return position;
  // }

  getTextStyle() {
    const style = super.getTextStyle();
    style.display = 'none';
    style.background = {fill: "transparent"};
    return style;
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
    // 跳转条件图标底色：设计态用主蓝 chroma，运行态保留状态语义色（chartStatusColor）
    const condColor = model.properties.chartStatusColor ? style.stroke : '#409eff';
    let points = this.pointFilter(pointsStr.split(' ').map((p) => p.split(',').map((a) => +a)));

    // 主路径
    let mainPath = getCurvedEdgePath(points, radius as number);
    let plusElements: h.JSX.Element[] = [];

    const offsetY = 30

    // 跳转线是直线
    if (points.length === 2) {
      let nextEdge = model.graphModel.edges.filter(edge => edge.sourceNodeId ===  model.sourceNode.id);
      // 如果上一个节点是互斥网关，并且网关后节点大于1个，也就是说是互斥网关结束节点时
      if (['serial', 'inclusive'].includes(model.sourceNode.type as string) && nextEdge.length > 1) {
        // +4 让徽标与网关胶囊留出呼吸间距，避免描边环贴住胶囊底边
        const midPoint = [points[0][0], points[0][1] + offsetY + 4];
        plusElements = this.getForeignObject(midPoint, condColor, model.text.value);
      } else if (!model.properties.chartStatusColor) {
        const midPoint = [points[0][0], points[0][1] + offsetY];
        plusElements = this.getPlusElements(midPoint);
      }
    } else {
      const p0 = points[0];
      const p1 = points[1];
      const p2 = points[2];

      // 判断是否由竖线变为横线
      if (p0[0] === p1[0] && p0[1] !== p1[1] && !model.properties.chartStatusColor) {
        const midPoint = [p0[0] , p0[1] + offsetY];
        plusElements = this.getPlusElements(midPoint);
      }

      // 判断是否由横线变为竖线，并且是互斥网关
      if (model.sourceNode && ['serial', 'inclusive'].includes(model.sourceNode.type as string) && p0[1] === p1[1] && p0[0] !== p1[0]) {
        // +4 与直连分支保持同一垂直落点，且徽标不压横向连线
        const midPoint = [p2[0], p1[1] + offsetY + 4];
        plusElements = this.getForeignObject(midPoint, condColor, model.text.value);
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

  private getForeignObject(midPoint: number[], stroke: string, text: string) {
    // 设计态用主蓝渐变（chroma 规范），运行态（chartStatusColor）保留纯状态色
    const isRuntime = !!this.props.model.properties.chartStatusColor;
    const bg = isRuntime ? stroke : 'linear-gradient(135deg, #409eff 0%, #2b7de9 100%)';
    const shadow = isRuntime ? '0 2px 6px rgba(0, 0, 0, 0.18)' : '0 2px 6px rgba(64, 158, 255, 0.35)';
    let elements: h.JSX.Element[] = [];
    elements = [
      // 条件设置徽标：圆角 chip + 底色描边环 + 投影（替代原 32px 实心大方块）
      h('foreignObject', {
        x: midPoint[0] - 18,
        y: midPoint[1] - 20,
        width: 36,
        height: 36,
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
<div style="width: 31px; height: 31px; border-radius: 10px; display: flex; align-items: center; justify-content: center;
  background: ${bg}; border: 2px solid var(--wf-bg-page, #fff); box-sizing: border-box; box-shadow: ${shadow};">
  <!-- viewBox 裁至图形实际边界（原素材四周留白约 30%，不裁的话 19px 里只画出 ~9px 图形） -->
  <svg viewBox="310 262 416 416" width="17" height="17" xmlns="http://www.w3.org/2000/svg">
    <path d="M666.4 564.7c31.2 9.4 49.9 37.4 46.8 68.6-3.1 31.2-31.2 53-62.4 53s-59.3-21.8-62.4-53c-3.1-31.2 15.6-59.3 46.8-68.6v-65.5c0-9.4-6.2-15.6-15.6-15.6H401.3c-9.4 0-15.6 6.2-15.6 15.6v65.5c31.2 9.4 49.9 37.4 46.8 68.6-3.1 31.2-31.2 53-62.4 53s-59.3-21.8-62.4-53c-3.1-31.2 15.6-59.3 46.8-68.6v-81.1c0-18.7 12.5-31.2 31.2-31.2h109.1v-81.1c-31.2-9.4-49.9-37.4-46.8-68.6 3.1-31.2 31.2-53 62.4-53s59.3 21.8 62.4 53-15.6 59.3-46.8 68.6v81.1h109.1c18.7 0 31.2 12.5 31.2 31.2v81.1z m-156-218.3c18.7 0 31.2-12.5 31.2-31.2S529.1 284 510.4 284s-31.2 12.5-31.2 31.2c0.1 15.6 15.6 31.2 31.2 31.2zM370.1 655.1c18.7 0 31.2-12.5 31.2-31.2s-12.5-31.2-31.2-31.2-31.2 12.5-31.2 31.2 15.6 31.2 31.2 31.2z m280.7 0c18.7 0 31.2-12.5 31.2-31.2s-12.5-31.2-31.2-31.2-31.2 12.5-31.2 31.2 12.5 31.2 31.2 31.2z"
    fill="#FFFFFF"></path>
  </svg>
</div>
`,
        })
      ])
    ];

    // 只有当文本有值时才添加背景矩形和文本
    if (text && text.trim().length > 0) {
      // 由于我们无法直接在当前环境中测量文本，我们使用一个估算方法
      // 通常每个字符大约占用 8-10 像素宽度（取决于字体）
      const charWidth = 8; // 每个字符的估计宽度
      const padding = 14; // 左右内边距
      const minWidth = 40; // 最小宽度
      const textWidth = Math.max(minWidth, text.length * charWidth + padding);

      // 条件文本 pill：走 --wf-* token（暗黑自动翻转）；CSS 变量须放 style 而非 presentation 属性。
      // y 偏移与放大后的徽标（下沿 +16）保持间距，避免文字胶囊压住徽标描边环
      elements.push(
          h('rect', {
            x: midPoint[0] - textWidth / 2,
            y: midPoint[1] + 20,
            width: textWidth,
            height: 20,
            'stroke-width': 1,
            rx: 10, // 圆角胶囊
            ry: 10,
            style: {
              fill: 'var(--wf-bg-white, #fff)',
              stroke: 'var(--wf-border-lighter, #ebeef5)',
            }
          })
      );

      // 添加文本
      elements.push(
          h('text', {
            x: midPoint[0],
            y: midPoint[1] + 34,
            fontSize: 12,
            style: {
              fill: 'var(--wf-text-regular, #606266)',
              userSelect: 'none',
              textAnchor: 'middle' // 文本居中对齐
            }
          }, `${text}`)
      );
    }

    return this.getAddElements(midPoint, elements, true);
  }

  private getPlusElements(midPoint: number[]) {
    // 只读态（预览 / 已发布）不渲染边「+」加节点入口；条件徽标仍保留供查看
    if (typeof window !== 'undefined' && (window as any).__WF_DESIGNER_DISABLED__) {
      return [];
    }
    return this.getAddElements(midPoint, null);
  }

  private getAddElements(midPoint: number[], obj, isCondition: boolean = false) {
    const isPlus = !obj;
    if (!obj) {
      const x = midPoint[0]
      const y = midPoint[1]
      obj = [
        // 描边把按钮从连线上「抬起来」，配合 CSS 投影与 hover 放大（见 FlowDesigner 全局样式 .wf-edge-plus）
        // 注意：CSS 变量在 SVG presentation 属性里无效，必须走 style（暗黑模式自动切换描边底色）
        h('circle', {
          cx: x,
          cy: y,
          r: 13,
          fill: '#409eff',
          'stroke-width': '2',
          style: { stroke: 'var(--wf-bg-page, #fff)' },
        }),
        h('line', {
          x1: x - 6.5,
          y1: y,
          x2: x + 6.5,
          y2: y,
          stroke: 'white',
          'stroke-width': '2.2',
          'stroke-linecap': 'round',
        }),
        h('line', {
          x1: x,
          y1: y - 6.5,
          x2: x,
          y2: y + 6.5,
          stroke: 'white',
          'stroke-width': '2.2',
          'stroke-linecap': 'round',
        })
      ]
    }
    let plusElements: h.JSX.Element[] = [];
    plusElements.push(
        h('g', {
          ...this.getEventHandlers(isCondition), // 根据 isCondition 动态选择事件处理器
          className: isPlus ? 'wf-edge-plus' : 'wf-edge-cond',
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

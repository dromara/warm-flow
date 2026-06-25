/**
 * 全局类型补充：设计器在 window 上挂载的运行期标志与抽屉桥接回调。
 *
 * 这些标志由 FlowDesigner / propertySetting 等组件读写，集中声明以获得精确类型、
 * 避免散落各处的 `window as any` 断言。
 *
 * @author warm
 */
export {}

declare global {
  interface Window {
    /** 标记当前处于设计器页面（供经典节点设计态语义色判断） */
    __WF_FLOW_DESIGN_MODE__?: boolean
    /** 渲染前同步的只读标志（供自定义节点视图判断是否隐藏编辑/删除/改名控件） */
    __WF_DESIGNER_DISABLED__?: boolean
    /** 抽屉打开时标记（打开窗口期禁止画布 resize / 触摸事件桥接） */
    _markDrawerOpen?: () => void
    /** 抽屉关闭动画完成后解除标记，并触发一次移动端画布适配 */
    _markDrawerClosed?: () => void
    /** 边「+」浮层是否处于 hover 状态（避免误触关闭） */
    isTooltipHovered?: boolean
  }
}

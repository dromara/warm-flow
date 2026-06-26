import LogicFlow from '@logicflow/core'
import '@logicflow/core/lib/style/index.css'
import { InsertNodeInPolyline, Menu, Snapshot } from '@logicflow/extension'
import '@logicflow/extension/lib/style/index.css'
import { onUnmounted, ref, watch, type Ref } from 'vue'
import { isClassics, logicFlowJsonToWarmFlow } from '@/components/design/common/js/tool'
import StartC from '@/components/design/classics/js/start'
import BetweenC from '@/components/design/classics/js/between'
import SerialC from '@/components/design/classics/js/serial'
import ParallelC from '@/components/design/classics/js/parallel'
import InclusiveC from '@/components/design/classics/js/inclusive'
import EndC from '@/components/design/classics/js/end'
import SkipC from '@/components/design/classics/js/skip'
import StartM from '@/components/design/mimic/js/start'
import BetweenM from '@/components/design/mimic/js/between'
import SerialM from '@/components/design/mimic/js/serial'
import ParallelM from '@/components/design/mimic/js/parallel'
import InclusiveM from '@/components/design/mimic/js/inclusive'
import EndM from '@/components/design/mimic/js/end'
import SkipM from '@/components/design/mimic/js/skip'
import initMimicData from '@/components/design/mimic/initMimicData.json'
import { useDark } from '@/composables/useDark'
import type { FlowDesignerProps } from '@/designer/types'

/**
 * useLogicFlowCanvas 入参：组件把「画布需要、但归属向导/表单/属性面板」的依赖以
 * ref + 回调形式注入，让 composable 专注于 LogicFlow 实例的生命周期与视口/IO 操作。
 */
export interface UseLogicFlowCanvasOptions {
  /** FlowDesigner 的 props（读取 showGrid / lfOptions / customNodes / extraExtensions / onBeforeUse / onRegister，惰性读取保持响应式） */
  props: FlowDesignerProps
  /** 画布容器 DOM（模板 `ref="containerRef"`），LogicFlow 挂载点 */
  containerRef: Ref<HTMLElement | undefined>
  /** 当前流程 JSON（读取 modelValue / flowName / version，render 数据源；clear 在仿钉钉模式会重置它） */
  logicJson: Ref<Record<string, any>>
  /** 只读标志（静默模式 / __WF_DESIGNER_DISABLED__） */
  disabled: Ref<boolean>
  /** 收集基础信息表单到 logicJson（下载 JSON 前调用），实现由组件提供 */
  getBaseInfo: () => void
  /** 绑定画布事件（initEvent）：画布 → 属性面板 UI 状态的桥接，逻辑留在组件，由本钩子在合适时机调用 */
  bindEvents: () => void
  /** 画布渲染完成回调：组件据此显示侧边栏 + emit('ready') */
  onReady: (lf: any) => void
}

/**
 * LogicFlow 画布生命周期组合式。
 *
 * 从 FlowDesigner.vue 抽出，集中承载：
 * - 实例生命周期：use（扩展）→ new LogicFlow → setTheme → register（节点）→ menu → events → 触摸桥接 → render → fitView → 暗黑主题
 * - 视口操作：放大/缩小/自适应（zoomViewport）、撤销/重做、清空、下载图片 / JSON
 * - 自适应：移动端判定、fitView（限制最大 100%）、resize（防抖 + 抽屉屏蔽）、移动端真机延迟重绘
 * - 移动端触摸 → 鼠标事件桥接（Pointer Events）
 * - 暗黑主题随 isDark 切换
 *
 * 组件保留「向导 / 表单 / 属性面板」状态与事件桥接（initEvent），通过 options 注入。
 *
 * @author warm
 */
export function useLogicFlowCanvas(options: UseLogicFlowCanvasOptions) {
  const { props, containerRef, logicJson, disabled, getBaseInfo, bindEvents, onReady } = options

  const lf = ref<any>(null)
  // 使用统一的暗黑模式 composable（模块级单例 isDark，移入此处不改变全局状态）
  const { isDark, themeColors, applyDarkTheme } = useDark()

  /**
   * 判断当前是否为移动端/平板设备
   * 通过屏幕宽度 + 触摸能力综合判断（PC 端不生效自动 fitView）
   */
  const isMobileDevice = () => {
    // 屏幕宽度 <= 1024px（覆盖平板横屏及以下）或支持触摸且有窄屏
    return window.innerWidth <= 1024 || ('ontouchstart' in window && window.innerWidth <= 1280)
  }

  /** 仅在移动端/平板执行 fitView（用于新增节点等增量操作，避免 PC 编辑时被打断） */
  function fitViewIfMobile() {
    if (isMobileDevice() && lf.value?.fitView) {
      lf.value.fitView(40, 20)
    }
  }

  /**
   * 自适应显示全部节点（所有端）：打开 / 进入流程设计 / resize / 点击「自适应」按钮时使用。
   * fitView 会把节点较少的流程放大撑满画布，这里限制最大缩放 100%：
   * 流程大 -> 缩小铺满；流程小 -> 保持原始大小并居中，避免进来就糊脸。(PR#396 评审①)
   */
  function fitViewAll() {
    const lfInst: any = lf.value
    if (!lfInst?.fitView) return
    lfInst.fitView(40, 20)
    const scale = lfInst.getTransform ? lfInst.getTransform().SCALE_X : 1
    if (scale && scale > 1) {
      lfInst.resetZoom ? lfInst.resetZoom() : lfInst.zoom(1)
      if (lfInst.translateCenter) lfInst.translateCenter()
    }
  }

  // 提取初始化逻辑到单独的函数
  function initLogicFlow() {
    const container = containerRef.value
    if (container) {
      use()
      lf.value = new LogicFlow({
        container,
        // 只读（预览 / 已发布）时开启静默模式：禁止拖拽、连线、文本编辑、删除键等原生编辑交互
        isSilentMode: disabled.value,
        textEdit: false,      // 是否开启文本编辑。
        snapToGrid: true,   // 是否开启网格吸附，开启后拖动节点会有以网格大小为补步长移动
        hideAnchors: !isClassics(logicJson.value.modelValue),   // 是否隐藏节点的锚点，静默模式下默认隐藏。
        adjustNodePosition: isClassics(logicJson.value.modelValue),   // 是否允许拖动节点。
        hoverOutline: isClassics(logicJson.value.modelValue),   // 鼠标 hover 的时候是否显示节点的外框。
        nodeSelectedOutline: isClassics(logicJson.value.modelValue),    // 节点被选中时是否显示节点的外框。
        edgeSelectedOutline: isClassics(logicJson.value.modelValue),    //	边被选中时是否显示边的外框。
        grid: {
          size: 20,
          visible: props.showGrid,
          type: 'dot',
          config: {
            color: themeColors.value.gridColor,
            thickness: 1,
          },
          background: {
            backgroundColor: themeColors.value.bgPage,
          },
        },
        keyboard: isClassics(logicJson.value.modelValue) ? {
          enabled: true,
          shortcuts: [
            {
              keys: ["delete"],
              callback: () => {
                const elements = lf.value.getSelectElements(true)
                lf.value.clearSelectElements()
                elements.edges.forEach((edge: any) => lf.value.deleteEdge(edge.id))
                elements.nodes.forEach((node: any) => lf.value.deleteNode(node.id))
              },
            },
          ],
        } : {},
        // 消费方自定义 LogicFlow 初始化选项（顶层覆盖内置默认值，如 grid / keyboard / 交互开关）
        ...(props.lfOptions ?? {}),
        // container 由组件内部管理，强制覆盖，避免消费方误传破坏画布挂载
        container,
      })
      lf.value.setTheme({
        snapline: {
          stroke: themeColors.value.snaplineStroke,
          strokeWidth: 2,
        },
        nodeText: {
          color: themeColors.value.nodeTextColor,
          fill: themeColors.value.nodeTextFill,
          fontSize: 13,
          fontWeight: 500,
        },
        edgeText: {
          fontSize: 13,
          strokeWidth: 1,
          background: {
            fill: themeColors.value.edgeTextBg,
          },
        },
        // 选中/悬浮的包围框：默认是黑色虚线，改成柔和蓝实线（贴近基础信息页 chroma 与 Mac 风格）
        outline: {
          stroke: 'rgba(64, 158, 255, 0.7)',
          strokeWidth: 1,
          strokeDasharray: '',
          hover: {
            stroke: 'rgba(64, 158, 255, 0.35)',
          },
        },
      })

      register()
      initMenu()
      // 画布 → 属性面板 UI 状态的事件桥接，逻辑由组件提供
      bindEvents()
      // 画布触摸事件桥接：LogicFlow v2 不支持原生 touch 事件，
      // 将触摸事件转换为鼠标事件以支持手机/平板端拖动画布
      initTouchEventBridge()
      // 渲染前同步只读标志：供仿钉钉自定义节点视图（baseNode.vue）判断是否隐藏编辑 / 删除 / 改名控件
      window.__WF_DESIGNER_DISABLED__ = disabled.value
      if (logicJson.value) {
        lf.value.render(logicJson.value)
        // 打开即自适应显示全部节点（所有端，最大缩放 100%）
        fitViewAll()
      }
      // 初始化完成后，如果当前是暗黑模式，显式应用一次主题（解决 URL 参数初始化时序问题）
      if (isDark.value && lf.value) {
        applyDarkTheme(lf.value, true)
      }
      // 真机修复：延迟触发一次 resize 确保 SVG 画布正确渲染尺寸
      // 移动端 v-show 切换后容器可能还未完成布局计算
      scheduleMobileResize()

      // 透出底层 LogicFlow 实例 + 由组件接管渲染后副作用（显示侧边栏 + emit('ready')）
      onReady(lf.value)
    }
  }

  /**
   * 延迟触发 LogicFlow resize + fitView（所有端）
   * 覆盖移动端真机 / v-show 切换 / iframe 嵌入等容器尺寸延迟场景
   */
  function scheduleMobileResize() {
    const doFit = () => {
      if (lf.value && lf.value.resize) {
        lf.value.resize()
        // resize 后自适应显示全部节点（所有端，覆盖布局/iframe 时序）
        requestAnimationFrame(fitViewAll)
      }
    }
    // 多重延迟策略覆盖各种场景：移动端/v-show切换/iframe嵌入
    setTimeout(doFit, 50)
    setTimeout(doFit, 150)
    setTimeout(doFit, 300)
    setTimeout(doFit, 600)
    // 最后一次保底（覆盖极端慢速场景如低端手机首次加载）
    setTimeout(doFit, 1000)
  }

  // 监听窗口变化，真机旋转屏幕时重绘（带防抖 + 标志位屏蔽抽屉操作干扰）
  let _resizeTimer: ReturnType<typeof setTimeout> | null = null
  let _drawerActive = false // 抽屉操作期间禁止 resize 重绘画布 + 阻止触摸事件桥接
  let _lastCanvasSize = { w: 0, h: 0 }
  function handleMobileResize() {
    if (_drawerActive) return // 抽屉操作期间跳过
    clearTimeout(_resizeTimer)
    _resizeTimer = setTimeout(() => {
      const container = containerRef.value
      if (!container || !lf.value?.resize) return
      const w = container.clientWidth
      const h = container.clientHeight
      // 容器尺寸无显著变化（<5px 容差）则跳过，过滤掉抽屉/overlay 导致的微调
      if (Math.abs(w - _lastCanvasSize.w) < 5 && Math.abs(h - _lastCanvasSize.h) < 5 && _lastCanvasSize.w > 0) return
      _lastCanvasSize = { w, h }
      lf.value.resize()
    }, 300) // 提高到 300ms 确保布局稳定后再判断
  }
  window.addEventListener('resize', handleMobileResize)

  // 抽屉打开时标记
  window._markDrawerOpen = () => { _drawerActive = true }
  // 抽屉关闭动画完成后解除标记，并触发一次移动端画布适配
  // 延迟 350ms：Element Plus drawer 关闭 transition 默认 300ms，加 50ms 缓冲
  window._markDrawerClosed = () => {
    setTimeout(() => {
      _drawerActive = false
      // 抽屉关闭后一次性适配画布（仅移动端/平板），替代 propertySetting 中分散的 fitView 调用
      if (isMobileDevice() && lf.value?.resize) {
        lf.value.resize()
        requestAnimationFrame(() => {
          if (lf.value?.fitView) lf.value.fitView(40, 20)
        })
      }
    }, 350)
  }

  /**
   * 自定义拖拽面板：监听子组件 dragInNode 事件，调用 lf.dnd.startDrag
   */
  function handleDragInNode(type: string, properties: Record<string, any> = {}, text?: string) {
    if (lf.value) {
      lf.value.dnd.startDrag({
        text,
        type,
        properties,
      })
    }
  }

  /**
   * 移动端触摸事件桥接：确保手机/平板上 LogicFlow 节点/边的点击、拖拽正常工作
   *
   * 核心问题：
   * LogicFlow v2 内部使用 mousedown/mousemove/mouseup + click/dblclick 事件，
   * 移动端浏览器不会自动将 touch 事件转换为 mouse 事件（除非设置了兼容模式）。
   *
   * 解决方案：
   * 使用统一的 Pointer Events API（pointerdown/pointermove/pointerup），
   * 它能同时覆盖鼠标、触摸、触控笔三种输入方式，
   * 并且自动映射为浏览器原生的 mousedown/mousemove/mouseup/click 事件链。
   */
  function initTouchEventBridge() {
    const container = containerRef.value
    if (!container) return

    // 对容器及其所有后代元素启用 pointer-events 全局拦截
    // 这确保所有触摸输入都能正确转换为 mouse/click 事件
    setupPointerEventCapture(container)

    // 同时绑定到 canvas-overlay（确保画布拖拽也正常）
    const canvasOverlay = container.querySelector('.lf-canvas-overlay')
    if (canvasOverlay) {
      setupPointerEventCapture(canvasOverlay)
    } else {
      // 如果 CanvasOverlay 还未渲染，延迟绑定
      const observer = new MutationObserver(() => {
        const overlay = container.querySelector('.lf-canvas-overlay')
        if (overlay) {
          setupPointerEventCapture(overlay)
          observer.disconnect()
        }
      })
      observer.observe(container, { childList: true, subtree: true })
    }
  }

  /**
   * 使用 Pointer Events API 建立移动端触摸→鼠标事件的完整桥接
   *
   * Pointer Events 是 W3C 标准，现代浏览器均支持：
   * - pointerdown → 自动触发 mousedown（含 touches[0] 坐标）
   * - pointermove → 自动触发 mousemove
   * - pointerup   → 自动触发 mouseup + click
   * - pointercancel → 自动触发 mouseup（中断场景）
   *
   * 我们在此基础上额外补充：
   * 1. 阻止浏览器的默认手势行为（滚动/缩放）
   * 2. 长按模拟 contextmenu 右键菜单
   * 3. 双击检测并分发 dblclick
   *
   * @param {Element} el 目标 DOM 元素
   */
  function setupPointerEventCapture(el: Element) {
    // --- 双击检测状态 ---
    let tapCount = 0
    let lastTapTime = 0
    let doubleTapTimer: ReturnType<typeof setTimeout> | null = null
    const DOUBLE_TAP_GAP = 350

    // --- 长按右键菜单状态 ---
    let longPressTimer: ReturnType<typeof setTimeout> | null = null
    let isLongPressFired = false

    // --- 拖动检测 ---
    let startX = 0, startY = 0
    let isDragging = false
    let lastMoveX = 0, lastMoveY = 0

    /**
     * 检查抽屉是否处于打开状态（打开期间禁止向 LogicFlow 转发触摸事件）
     *
     * 三级判断：
     * 1. _drawerActive 标志位 — 代码显式标记的打开/关闭窗口期
     * 2. .el-drawer__open — Element Plus drawer 打开时的 DOM class
     * 3. 排除 .el-drawer__close — 抽屉正在执行关闭动画（此时不应拦截用户对画布的操作）
     */
    function isDrawerOpen() {
      if (_drawerActive) return true
      const drawerEl = document.querySelector('.el-drawer')
      if (!drawerEl) return false
      // 有 __open 且无 __close → 确实是打开状态
      if (drawerEl.classList.contains('el-drawer__open') &&
          !drawerEl.classList.contains('el-drawer__close')) {
        return true
      }
      return false
    }

    /**
     * 统一获取坐标参数（用于构造 MouseEvent）
     */
    function getEventOpts(pointerEvent: PointerEvent) {
      return {
        clientX: pointerEvent.clientX,
        clientY: pointerEvent.clientY,
        button: 0,
        bubbles: true,
        cancelable: true,
        view: window,
      }
    }

    // ========== pointerdown ==========
    el.addEventListener('pointerdown', (e: PointerEvent) => {
      // 只处理主指针（手指/鼠标左键/触控笔）
      if (e.pointerType === 'mouse' && e.button !== 0) return

      // 【修复】抽屉打开期间：阻止触摸事件转发给 LogicFlow，防止画布被意外拖拽
      if (isDrawerOpen() && e.pointerType === 'touch') {
        e.preventDefault()
        return
      }

      startX = e.clientX
      startY = e.clientY
      lastMoveX = e.clientX
      lastMoveY = e.clientY
      isDragging = false
      isLongPressFired = false

      // 仅对触摸事件阻止浏览器默认手势（滚动/缩放/选择）
      // 鼠标事件不干预，让 LogicFlow 正常处理拖拽/双击
      if (e.pointerType === 'touch') {
        e.preventDefault()
        // 主动分发一次 mousedown 确保 LogicFlow 收到
        el.dispatchEvent(new MouseEvent('mousedown', getEventOpts(e)))
      }

      // 启动长按计时器（500ms 无显著移动则视为右键菜单，仅触摸有效）
      longPressTimer = setTimeout(() => {
        if (isDragging) return // 已在拖动，取消长按
        isLongPressFired = true
        el.dispatchEvent(new MouseEvent('contextmenu', getEventOpts(e)))
      }, 500)
    }, { passive: false } as AddEventListenerOptions)

    // ========== pointermove ==========
    el.addEventListener('pointermove', (e: PointerEvent) => {
      // 【修复】抽屉打开期间：完全拦截触摸移动事件
      if (isDrawerOpen() && e.pointerType === 'touch') {
        e.preventDefault()
        return
      }

      const dx = Math.abs(e.clientX - startX)
      const dy = Math.abs(e.clientY - startY)

      if (!isDragging && (dx > 5 || dy > 5)) {
        // 首次超过阈值：进入拖动模式
        isDragging = true
        if (longPressTimer) {
          clearTimeout(longPressTimer)
          longPressTimer = null
        }
      }

      if (isDragging && e.pointerType === 'touch') {
        // 触摸拖动中：持续分发 mousemove 给 LogicFlow
        e.preventDefault()
        el.dispatchEvent(new MouseEvent('mousemove', getEventOpts(e)))
        lastMoveX = e.clientX
        lastMoveY = e.clientY
      } else if (e.pointerType === 'touch') {
        // 触摸微移动：阻止默认滚动行为
        e.preventDefault()
      }
    }, { passive: false } as AddEventListenerOptions)

    // ========== pointerup / pointercancel ==========
    function handlePointerEnd(e: PointerEvent) {
      // 【修复】抽屉打开期间：拦截触摸释放事件
      if (isDrawerOpen() && e.pointerType === 'touch') {
        resetTapState()
        isDragging = false
        return
      }

      // 清理长按计时器
      if (longPressTimer) {
        clearTimeout(longPressTimer)
        longPressTimer = null
      }

      // 拖动结束：分发 mouseup 给 LogicFlow 完成拖拽链路
      if (isDragging && e.pointerType === 'touch') {
        el.dispatchEvent(new MouseEvent('mouseup', getEventOpts(e)))
        resetTapState()
        isDragging = false
        return
      }

      // 长按已触发（右键菜单），不再处理 click/dblclick
      if (isLongPressFired) {
        resetTapState()
        return
      }

      // ---- 点击/双击检测（仅非拖动） ----
      const now = Date.now()
      tapCount++

      if (tapCount === 1) {
        // 第一次点击：立即触发 click
        lastTapTime = now
        el.dispatchEvent(new MouseEvent('click', getEventOpts(e)))

        // 设置双击等待窗口
        doubleTapTimer = setTimeout(() => {
          tapCount = 0
          doubleTapTimer = null
        }, DOUBLE_TAP_GAP)

      } else if (tapCount >= 2 && (now - lastTapTime < DOUBLE_TAP_GAP)) {
        // 第二次快速点击：触发 dblclick
        if (doubleTapTimer) {
          clearTimeout(doubleTapTimer)
          doubleTapTimer = null
        }
        tapCount = 0
        el.dispatchEvent(new MouseEvent('dblclick', getEventOpts(e)))
      } else {
        // 超出双击间隔，重新开始计数
        tapCount = 1
        lastTapTime = now
        el.dispatchEvent(new MouseEvent('click', getEventOpts(e)))

        doubleTapTimer = setTimeout(() => {
          tapCount = 0
          doubleTapTimer = null
        }, DOUBLE_TAP_GAP)
      }
    }

    el.addEventListener('pointerup', handlePointerEnd as EventListener)
    el.addEventListener('pointercancel', (e: PointerEvent) => {
      // 中断：如果正在拖动需要补发 mouseup
      if (isDragging && e.pointerType === 'touch') {
        el.dispatchEvent(new MouseEvent('mouseup', getEventOpts(e)))
        isDragging = false
      }
      if (longPressTimer) {
        clearTimeout(longPressTimer)
        longPressTimer = null
      }
      resetTapState()
    })

    function resetTapState() {
      if (doubleTapTimer) {
        clearTimeout(doubleTapTimer)
        doubleTapTimer = null
      }
      tapCount = 0
    }
  }

  /**
   * 初始化菜单
   */
  function initMenu() {
    // 只有仿钉钉模式才初始化菜单
    if (!isClassics(logicJson.value.modelValue)) {
      // 为菜单追加选项（必须在 lf.render() 之前设置）
      lf.value.extension.menu.setMenuConfig({
        nodeMenu: [],
        edgeMenu: [],
      })
    }
  }

  /**
   * 注册自定义节点和边
   */
  function register() {
    if (isClassics(logicJson.value.modelValue)) {
      lf.value.register(StartC)
      lf.value.register(BetweenC)
      lf.value.register(SerialC)
      lf.value.register(ParallelC)
      lf.value.register(InclusiveC)
      lf.value.register(EndC)
      lf.value.register(SkipC)
    } else {
      lf.value.register(StartM)
      lf.value.register(BetweenM)
      lf.value.register(SerialM)
      lf.value.register(ParallelM)
      lf.value.register(InclusiveM)
      lf.value.register(EndM)
      lf.value.register(SkipM)
    }
    // 消费方自定义节点：在内置节点之后注册，可新增节点类型或覆盖内置同名 type
    ;(props.customNodes ?? []).forEach((node) => {
      if (node) lf.value.register(node)
    })
    // 命令式节点注册钩子：透出已创建的 lf 实例，支持批量/条件注册、注册自定义边或渲染前额外设置
    props.onRegister?.(lf.value)
  }

  /**
   * 添加扩展
   */
  function use() {
    // 只有经典模式才有拖拽面板（已使用自定义 DiagramSidebar 替代内置 DndPanel）
    if (isClassics(logicJson.value.modelValue)) {
      LogicFlow.use(InsertNodeInPolyline)
    }
    LogicFlow.use(Menu)
    LogicFlow.use(Snapshot)
    // 消费方自定义 LogicFlow 扩展（MiniMap / Control / Group 等），在内置扩展之后注册
    ;(props.extraExtensions ?? []).forEach((ext) => {
      if (ext) LogicFlow.use(ext)
    })
    // 命令式扩展钩子：透出 LogicFlow 类，支持注册带配置的扩展（extraExtensions 无法传配置）或覆盖内置扩展
    props.onBeforeUse?.(LogicFlow)
  }

  /**
   * 缩放视口：放大/缩小/自适应
   * @param {boolean|string|number} zoom - true=放大(内置刻度), false=缩小(内置刻度), 'fit'=fitView自适应全部节点, number=直接设置缩放比例
   */
  const zoomViewport = async (zoom: boolean | number | string) => {
    if (zoom === true) {
      // 放大（使用 LogicFlow 内置刻度，每次按固定比例放大）
      lf.value.zoom(true)
    } else if (zoom === false) {
      // 缩小（使用 LogicFlow 内置刻度，每次按固定比例缩小）
      lf.value.zoom(false)
    } else if (zoom === 1) {
      // PC 端自适应：zoom(1) 重置缩放 + translateCenter 居中
      lf.value.zoom(1)
      if (lf.value.translateCenter) {
        lf.value.translateCenter()
      }
    } else if (zoom === 'fit') {
      // 自适应：显示全部节点并居中，最大缩放 100%（节点少不放大撑屏）
      if (lf.value.fitView) {
        fitViewAll()
      } else {
        // fallback
        if (lf.value.translateCenter) lf.value.translateCenter()
        lf.value.resetZoom ? lf.value.resetZoom() : lf.value.zoom(1)
      }
    } else if (typeof zoom === 'number') {
      // 直接设置缩放比例
      lf.value.zoom(zoom)
    }
  }

  const undoOrRedo = async (undo: boolean) => {
    if (undo) {
      lf.value.undo(undo)
    } else {
      lf.value.redo(undo)
    }
  }

  //清空
  const clear = async () => {
    if (isClassics(logicJson.value.modelValue)) {
      lf.value.clearData()
    } else {
      logicJson.value = {
        ...logicJson.value,
        ...initMimicData,
      }
      lf.value.render(logicJson.value)
    }
  }

  /**
   * 下载流程图
   */
  function downLoad() {
    lf.value.getSnapshot(logicJson.value.flowName, {
      fileType: 'png',        // 可选：'png'、'webp'、'jpeg'、'svg'
      backgroundColor: themeColors.value.snapshotBg,
    })
  }

  async function downJson() {
    try {
      getBaseInfo()
      if (lf.value) {
        let graphData = lf.value.getGraphData()
        logicJson.value['nodes'] = graphData['nodes']
        logicJson.value['edges'] = graphData['edges']
      }
      let jsonString = logicFlowJsonToWarmFlow(logicJson.value)

      // 创建 Blob 并触发下载
      const filename = `${logicJson.value.flowName}_${logicJson.value.version}.json`
      // 格式化用于下载
      const jsonPretty = JSON.stringify(JSON.parse(jsonString), null, 2) // 先 parse 成对象再 stringify
      const blob = new Blob([jsonPretty], { type: 'application/json' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(downloadUrl)
    } catch (error) {
      console.error('下载失败:', error)
    }
  }

  /** 获取当前画布图数据（LogicFlow getGraphData 原始结构），未初始化时为 null */
  function getGraphData() {
    return lf.value ? lf.value.getGraphData() : null
  }

  watch(isDark, (v) => {
    if (!lf.value) {
      return
    }
    applyDarkTheme(lf.value, v)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', handleMobileResize)
    // 复位渲染态只读标志（由本钩子在 initLogicFlow 设置）
    window.__WF_DESIGNER_DISABLED__ = false
  })

  return {
    lf,
    initLogicFlow,
    zoomViewport,
    undoOrRedo,
    clear,
    downLoad,
    downJson,
    fitViewAll,
    fitViewIfMobile,
    handleDragInNode,
    getGraphData,
  }
}

/**
 * 侧边栏节点图标定义
 * 形状与画布节点 (classics/js/*.js) 保持完全一致；
 * 配色与画布「设计态语义色」(applyClassicDesignColor) 一致，避免发灰、提升辨识度。
 * 所有图标均为内联 SVG 字符串，可直接通过 v-html 渲染
 */

/** 各节点类型语义色（与画布设计态完全一致） */
const COLOR = {
  start: '103,194,58',     // 绿：起点
  between: '64,158,255',   // 蓝：审批
  end: '245,108,108',      // 红：终止
  serial: '245,158,11',    // 橙：互斥
  parallel: '19,194,194',  // 青：并行
  inclusive: '146,84,222', // 紫：包含
}

const stroke = (rgb) => `rgb(${rgb})`
const rgba = (rgb, a) => `rgba(${rgb},${a})`

/** 图标背景色 — 用 CSS 变量支持暗黑模式自动切换 */
const ICON_BG = 'var(--wf-icon-bg, #ffffff)'

/**
 * 开始节点：圆环 + 内核实心渐变圆（与 start.js 一致）
 */
export const startIcon = `
<svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <radialGradient id="si-core" cx="35%" cy="35%" r="65%">
      <stop offset="0%" stop-color="${stroke(COLOR.start)}"/>
      <stop offset="70%" stop-color="${rgba(COLOR.start, 0.4)}"/>
    </radialGradient>
  </defs>
  <circle cx="50" cy="50" r="42" fill="${ICON_BG}" stroke="${stroke(COLOR.start)}" stroke-width="2.5"/>
  <circle cx="50" cy="50" r="30" fill="url(#si-core)"/>
</svg>`

/**
 * 中间节点：圆角矩形卡片 + 审批图标（与 between.js 一致）
 */
export const betweenIcon = `
<svg viewBox="0 0 100 80" xmlns="http://www.w3.org/2000/svg">
  <rect x="5" y="5" width="90" height="70" rx="8" fill="${ICON_BG}" stroke="${stroke(COLOR.between)}" stroke-width="1.5"/>
  <!-- 左上角审批小图标（放大，偏右下） -->
  <rect x="12" y="10" width="32" height="26" rx="5" ry="5" fill="${rgba(COLOR.between, 0.08)}" stroke="${stroke(COLOR.between)}" stroke-width="1.2"/>
  <circle cx="28" cy="23" r="5.5" fill="${rgba(COLOR.between, 0.15)}" stroke="${stroke(COLOR.between)}" stroke-width="1"/>
  <path d="M24 23l4 4l6-6" fill="none" stroke="${stroke(COLOR.between)}" stroke-width="1.5" stroke-linecap="square" stroke-linejoin="miter"/>
</svg>`

/**
 * 结束节点：双环圆 — 粗外环 + 细内环（与 end.js 一致）
 */
export const endIcon = `
<svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
  <circle cx="50" cy="50" r="42" fill="${ICON_BG}" stroke="${stroke(COLOR.end)}" stroke-width="3.5"/>
  <circle cx="50" cy="50" r="30" fill="none" stroke="${rgba(COLOR.end, 0.25)}" stroke-width="1"/>
</svg>`

// ========== 网关节点公共部分 ==========

/** 正方形外框（50x50 坐标系），绕中心点旋转45度，尺寸确保旋转后四角不超出viewBox */
const gwRect = `<rect x="8" y="8" width="34" height="34" rx="5" transform="rotate(45 25 25)"`

/**
 * 互斥网关：正方形 + X + 四端圆点
 */
export const serialIcon = `
<svg viewBox="0 0 50 50" xmlns="http://www.w3.org/2000/svg">
  ${gwRect} fill="${ICON_BG}" stroke="${stroke(COLOR.serial)}" stroke-width="2"/>
  <!-- X 图标（直线，无圆角端点） -->
  <path d="M17 17 L33 33 M33 17 L17 33" fill="none" stroke="${stroke(COLOR.serial)}" stroke-width="2.5" stroke-linecap="square" stroke-linejoin="miter"/>
  <!-- 四端装饰圆点 -->
  <circle cx="14.5" cy="14.5" r="1.3" fill="${stroke(COLOR.serial)}"/>
  <circle cx="34.5" cy="14.5" r="1.3" fill="${stroke(COLOR.serial)}"/>
  <circle cx="34.5" cy="34.5" r="1.3" fill="${stroke(COLOR.serial)}"/>
  <circle cx="14.5" cy="34.5" r="1.3" fill="${stroke(COLOR.serial)}"/>
</svg>`

/**
 * 并行网关：正方形 + 十字 + 四端圆点
 */
export const parallelIcon = `
<svg viewBox="0 0 50 50" xmlns="http://www.w3.org/2000/svg">
  ${gwRect} fill="${ICON_BG}" stroke="${stroke(COLOR.parallel)}" stroke-width="2"/>
  <!-- 十字图标（直线） -->
  <path d="M15 25 L35 25" fill="none" stroke="${stroke(COLOR.parallel)}" stroke-width="3" stroke-linecap="square"/>
  <path d="M25 15 L25 35" fill="none" stroke="${stroke(COLOR.parallel)}" stroke-width="3" stroke-linecap="square"/>
  <!-- 四端装饰圆点 -->
  <circle cx="25" cy="12" r="1.5" fill="${stroke(COLOR.parallel)}"/>
  <circle cx="38" cy="25" r="1.5" fill="${stroke(COLOR.parallel)}"/>
  <circle cx="25" cy="38" r="1.5" fill="${stroke(COLOR.parallel)}"/>
  <circle cx="12" cy="25" r="1.5" fill="${stroke(COLOR.parallel)}"/>
</svg>`

/**
 * 包含网关：正方形 + 同心双圆 + 中心点
 */
export const inclusiveIcon = `
<svg viewBox="0 0 50 50" xmlns="http://www.w3.org/2000/svg">
  ${gwRect} fill="${ICON_BG}" stroke="${stroke(COLOR.inclusive)}" stroke-width="2"/>
  <!-- 同心双圆 -->
  <circle cx="25" cy="25" r="11" fill="none" stroke="${stroke(COLOR.inclusive)}" stroke-width="2"/>
  <circle cx="25" cy="25" r="5.5" fill="${rgba(COLOR.inclusive, 0.15)}" stroke="${stroke(COLOR.inclusive)}" stroke-width="1.5"/>
  <!-- 中心点缀 -->
  <circle cx="25" cy="25" r="1.8" fill="${stroke(COLOR.inclusive)}"/>
</svg>`

/**
 * 节点图标映射表
 * key: node type, value: svg string
 */
export const nodeIcons = {
  start: startIcon,
  between: betweenIcon,
  end: endIcon,
  serial: serialIcon,
  parallel: parallelIcon,
  inclusive: inclusiveIcon,
}

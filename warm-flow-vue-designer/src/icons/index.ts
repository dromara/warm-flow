import { addCollection } from '@iconify/vue'
import { epIcons } from './epIcons'
import { wfIcons } from './wfIcons'

/**
 * 注册离线图标集（iconify），使设计器图标框架无关、不依赖 element-plus 图标与 svg sprite：
 *  - ep:*  Element Plus 官方图标子集（离线内联，仅设计器实际用到的 9 个，见 epIcons.ts）
 *  - wf:*  Warm-Flow 自定义图标（由原 svg 资产迁移），用于流程名 / 保存 / 节点类型等
 *
 * 图标数据在构建期内联进产物，运行时无网络请求；只内联用到的图标，避免整集打包膨胀。
 * 新增图标：ep 系登记到 epIcons.ts、wf 系登记到 wfIcons.ts 即可。
 *
 * @author warm
 */
addCollection(epIcons)
addCollection(wfIcons)

import { defineConfig } from 'vite'
import path from 'path'
import dts from 'vite-plugin-dts'
import createVitePlugins from './vite/plugins'

/**
 * Warm-Flow 设计器「组件库 / npm 包」构建配置。
 *
 * 与默认应用构建（vite.config.js → dist/，供 warm-flow-plugin-vue3-ui 打进 jar）完全隔离：
 *  - 入口为可复用层门面 src/designer/index.js（FlowDesigner + 数据层）
 *  - 产物输出到 dist-lib/，不触碰 dist/ 与 jar 集成链路
 *  - vue / element-plus / logicflow 等重型库 externalize 为 peerDependencies，
 *    避免包体膨胀与 Vue / Element Plus 单例重复；第三方 .css（如 LogicFlow 样式）仍打进库合并产物
 *
 * 应用构建仍走 `yarn build:prod`；库构建走 `yarn build:lib`。
 *
 * @author warm
 */
// 需要 externalize 的单例敏感 / 框架级依赖（由消费方以 peerDependencies 提供）
const externalDeps = [
  'vue',
  'vue-router',
  'pinia',
  'element-plus',
  '@logicflow/core',
  '@logicflow/extension'
]

// dts 产物落在 dist-lib/src/**。
const distLibRoot = path.resolve(__dirname, 'dist-lib')
const distLibSrcRoot = path.join(distLibRoot, 'src')

/**
 * 修复 unplugin-dts(vite-plugin-dts) 对「@/ 别名派生」相对 import 的「偏移一级」缺陷。
 *
 * 现象：源码用 `@/data`（别名=src）等导入时，插件把目标按 entryRoot=src 映射（无 src 前缀），
 * 却从带 `src/` 前缀的实际输出目录（dist-lib/src/**）计算相对路径，导致 `../../data` 越出到
 * dist-lib/data（不存在）→ 消费方解析失败、所有导出类型退化为 any。源码原生 `./xxx` 相对导入不受影响。
 *
 * 修法：只重锚定「解析后越出 dist-lib/src」的坏 specifier 回 dist-lib/src/**，正确导入原样保留——
 * 纯路径运算、确定性强，不依赖 api-extractor 打平（该工具链 bundleTypes/entryRoot 行为均异常）。
 */
function fixAliasDtsImports(filePath, content) {
  const fileDir = path.dirname(filePath)
  return content.replace(
    /(\bfrom\s+|\bimport\s*\(\s*|\brequire\s*\(\s*)(['"])(\.\.?\/[^'"]+)\2/g,
    (full, keyword, quote, spec) => {
      const resolved = path.resolve(fileDir, spec)
      // 已在 dist-lib/src 内 → 正确（含源码原生 ./ 相对导入），原样保留
      if (resolved === distLibSrcRoot || resolved.startsWith(distLibSrcRoot + path.sep)) {
        return full
      }
      // 越出到 dist-lib/<X>（少了一层 src）→ 重锚定为 dist-lib/src/<X>
      if (resolved.startsWith(distLibRoot + path.sep)) {
        const corrected = path.join(distLibSrcRoot, path.relative(distLibRoot, resolved))
        let newSpec = path.relative(fileDir, corrected).split(path.sep).join('/')
        if (!newSpec.startsWith('.')) newSpec = './' + newSpec
        return keyword + quote + newSpec + quote
      }
      return full
    }
  )
}

export default defineConfig(() => {
  return {
    plugins: [
      ...createVitePlugins({}, true),
      // 生成 .d.ts 类型声明。本库唯一的 dts 产出点：include 放宽到整个 src，覆盖三个入口
      // （designer/index、ui/elementPlusAdapter、ui/antdvAdapter）及 FlowDesigner 组件链路上的全部传递依赖
      //（ui / components / composables / data），避免漏产导致 import 悬空。
      // beforeWriteFile 修复 @/ 别名派生 import 的「偏移一级」缺陷（见 fixAliasDtsImports）。
      // ep / antdv 子构建不再各自产 dts，统一由本构建产出，避免 entryRoot 导致的 dist-lib/ui/src/ui/** 嵌套错乱。
      dts({
        include: ['src/**/*.ts', 'src/**/*.tsx', 'src/**/*.vue', 'src/**/*.d.ts'],
        outDir: 'dist-lib',
        tsconfigPath: './tsconfig.json',
        beforeWriteFile: (filePath, content) => ({ content: fixAliasDtsImports(filePath, content) })
      })
    ],
    resolve: {
      alias: {
        '~': path.resolve(__dirname, './'),
        '@': path.resolve(__dirname, './src')
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    build: {
      outDir: 'dist-lib',
      emptyOutDir: true,
      cssCodeSplit: false,
      chunkSizeWarningLimit: 3000,
      lib: {
        entry: path.resolve(__dirname, 'src/designer/index.ts'),
        name: 'WarmFlowDesigner',
        formats: ['es'],
        fileName: (format) => `warm-flow-designer.${format}.js`
      },
      rollupOptions: {
        // 第三方样式（.css）保留打进库合并产物，其余框架级依赖 externalize 为 peerDependencies
        external: (id) => {
          if (id.endsWith('.css')) return false
          return externalDeps.some((dep) => id === dep || id.startsWith(dep + '/'))
        },
        output: {
          assetFileNames: 'warm-flow-designer.[ext]'
        }
      }
    },
    css: {
      postcss: {
        plugins: [
          {
            postcssPlugin: 'internal:charset-removal',
            AtRule: {
              charset: (atRule) => {
                if (atRule.name === 'charset') {
                  atRule.remove()
                }
              }
            }
          }
        ]
      }
    }
  }
})

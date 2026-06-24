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
 *  - vue / element-plus / logicflow / form-create 等重型库 externalize 为 peerDependencies，
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
  '@logicflow/extension',
  '@form-create/designer',
  '@form-create/element-ui'
]

export default defineConfig(() => {
  return {
    plugins: [
      ...createVitePlugins({}, true),
      // 生成 .d.ts 类型声明（仅库出口层：designer + data + 类型 shim）
      dts({
        include: ['src/designer/**/*.ts', 'src/data/**/*.ts', 'src/types/**/*.d.ts'],
        outDir: 'dist-lib',
        tsconfigPath: './tsconfig.json'
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

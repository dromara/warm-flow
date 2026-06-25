import { defineConfig } from 'vite'
import path from 'path'
import createVitePlugins from './vite/plugins'

/**
 * Element Plus 适配器子入口构建：产物 dist-lib/element-plus.es.js，对应包导出 @dromara/warm-flow-designer/element-plus。
 *
 * 与主库构建（vite.lib.config.js）分离：主入口已做到 UI 库无关（不静态引任何 UI 组件库），
 * Element Plus 与 Ant Design Vue 均为「可选」适配器，仅对应消费方按需引入并 setUiAdapter。
 * 故 vue / element-plus 全部 externalize 为 peerDependencies，主入口不打包 element-plus。
 * emptyOutDir:false 以免擦除主库已产出的 dist-lib 内容（须在 build:lib 主构建之后运行）。
 *
 * @author warm
 */
const externalDeps = ['vue', 'element-plus']

export default defineConfig(() => {
  return {
    // 仅构建 JS 产物（element-plus.es.js）。dts 由主库构建（vite.lib.config.js）统一产出，
    // 此处不再重复生成，避免 entryRoot 行为异常导致的 dist-lib/ui/src/ui/** 嵌套错乱。
    plugins: [...createVitePlugins({}, true)],
    resolve: {
      alias: {
        '~': path.resolve(__dirname, './'),
        '@': path.resolve(__dirname, './src')
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    build: {
      outDir: 'dist-lib',
      emptyOutDir: false,
      cssCodeSplit: false,
      chunkSizeWarningLimit: 3000,
      lib: {
        entry: path.resolve(__dirname, 'src/ui/elementPlusAdapter.ts'),
        name: 'WarmFlowDesignerElementPlus',
        formats: ['es'],
        fileName: () => 'element-plus.es.js'
      },
      rollupOptions: {
        external: (id) => {
          if (id.endsWith('.css')) return false
          return externalDeps.some((dep) => id === dep || id.startsWith(dep + '/'))
        }
      }
    }
  }
})

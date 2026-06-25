import { defineConfig } from 'vite'
import path from 'path'
import createVitePlugins from './vite/plugins'

/**
 * Ant Design Vue 4 适配器子入口构建：产物 dist-lib/antdv.es.js，对应包导出 @dromara/warm-flow-designer/antdv。
 *
 * 与主库构建（vite.lib.config.js）分离：antd 适配器是「可选」的，仅 antd 消费方按需引入，
 * 故 vue / ant-design-vue 全部 externalize 为 peerDependencies，主入口不打包 antd。
 * emptyOutDir:false 以免擦除主库已产出的 dist-lib 内容（须在 build:lib 之后运行）。
 *
 * @author warm
 */
const externalDeps = ['vue', 'ant-design-vue']

export default defineConfig(() => {
  return {
    // 仅构建 JS 产物（antdv.es.js）。dts 由主库构建（vite.lib.config.js）统一产出，
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
        entry: path.resolve(__dirname, 'src/ui/antdvAdapter.ts'),
        name: 'WarmFlowDesignerAntdv',
        formats: ['es'],
        fileName: () => 'antdv.es.js'
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

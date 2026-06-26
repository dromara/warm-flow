import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

/**
 * Warm-Flow 设计器 · Naive UI 消费示例。
 *
 * 以「第三方业务工程」的姿势消费 npm 包 @dromara/warm-flow-designer（本地 link 到 ../../warm-flow-vue-designer 的 dist-lib），
 * 仅引入 vue + naive-ui + pinia + 包本身即跑通设计器（setUiAdapter(naiveAdapter)）。运行前确保库已 `pnpm build:lib`。
 *
 * @author warm
 */
export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 经 link 消费本地库时，确保 vue / pinia 单实例（避免重复实例报错）
    dedupe: ['vue', 'pinia']
  },
  optimizeDeps: {
    // 库本身已是预构建 ESM，无需再被预打包
    exclude: ['@dromara/warm-flow-designer']
  },
  server: {
    port: 5182,
    open: false,
    // 允许 dev server 访问仓库内 link 出去的库产物（dist-lib 在本工程目录之外）
    fs: { allow: [path.resolve(__dirname, '../../')] }
  }
})

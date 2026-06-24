import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

/**
 * Warm-Flow 设计器 · Ant Design Vue 4 消费示例（过渡态）。
 *
 * 以「第三方业务工程」的姿势消费 npm 包 @dromara/warm-flow-designer（本地 link 到 ../../warm-flow-vue-designer 的 dist-lib）。
 * 应用外壳用 antd4；设计器内部 UI 当前仍为 Element Plus（默认适配器），Phase 3 接入 antdv 适配后切换。
 * 运行前确保库已 `yarn build:lib`。
 *
 * @author warm
 */
export default defineConfig({
  plugins: [vue()],
  resolve: {
    dedupe: ['vue', 'pinia']
  },
  optimizeDeps: {
    exclude: ['@dromara/warm-flow-designer']
  },
  server: {
    port: 5181,
    open: false,
    fs: { allow: [path.resolve(__dirname, '../../')] }
  }
})

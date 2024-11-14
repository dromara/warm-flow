import cache from './cache'
import modal from './modal'

export default function installPlugins(app){
  // 缓存对象
  app.config.globalProperties.$cache = cache
  // 模态框对象
  app.config.globalProperties.$modal = modal
}

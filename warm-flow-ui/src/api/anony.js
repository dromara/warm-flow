import request from '@/utils/request'

const urlPrefix = import.meta.env.VITE_URL_PREFIX

// 保存流程定义
export function config() {
  return request({
    url: urlPrefix + 'warm-flow-ui/config',
    method: 'get'
  })
}



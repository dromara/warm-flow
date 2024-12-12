import request from '@/utils/request'

const urlPrefix = import.meta.env.VITE_URL_PREFIX

// 查询表单定义列表
export function getFormContent(id) {
  return request({
    url: urlPrefix + 'warm-flow/form-content/' + id,
    method: 'get'
  })
}

// 保存表单设计
export function saveFormContent(data) {
  return request({
    url: urlPrefix + 'warm-flow/form-content',
    method: 'post',
    data: data
  })
}

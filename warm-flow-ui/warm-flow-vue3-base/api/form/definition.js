import request from '@/utils/request'

// 查询表单定义列表
export function listDefinition(query) {
  return request({
    url: '/flow/form/list',
    method: 'get',
    params: query
  })
}

// 保存表单设计
export function saveFormContent(data) {
  return request({
    url: '/flow/form/saveFormContent',
    method: 'post',
    data: data
  })
}

import request from '@/utils/request'

// 查询表单定义列表
export function getFormContent(id) {
  return request({
    url: 'warm-flow-ui/form-content/' + id,
    method: 'get'
  })
}

// 保存表单设计
export function saveFormContent(data) {
  return request({
    url: 'warm-flow-ui/form-content',
    method: 'post',
    data: data
  })
}

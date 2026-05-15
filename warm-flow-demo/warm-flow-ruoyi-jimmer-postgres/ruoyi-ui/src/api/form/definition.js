import request from '@/utils/request'

// 查询表单定义列表
export function listDefinition(query) {
  return request({
    url: '/flow/form/list',
    method: 'get',
    params: query
  })
}

// 查询表单定义详细
export function getDefinition(id) {
  return request({
    url: '/flow/form/' + id,
    method: 'get'
  })
}

// 新增表单定义
export function addDefinition(data) {
  return request({
    url: '/flow/form/add',
    method: 'post',
    data: data
  })
}

// 修改表单定义
export function updateDefinition(data) {
  return request({
    url: '/flow/form/edit',
    method: 'put',
    data: data
  })
}

// 删除表单定义
export function delDefinition(id) {
  return request({
    url: '/flow/form/' + id,
    method: 'delete'
  })
}

// 发布表单定义
export function publish(id) {
  return request({
    url: '/flow/form/publish/' + id,
    method: 'get'
  })
}

// 取消发布表单定义
export function unPublish(id) {
  return request({
    url: '/flow/form/unPublish/' + id,
    method: 'get'
  })
}

// 复制表单定义
export function copyDef(id) {
  return request({
    url: '/flow/form/copyForm/' + id,
    method: 'post'
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

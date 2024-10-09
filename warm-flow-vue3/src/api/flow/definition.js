import request from '@/utils/request'

// 查询流程定义列表
export function listDefinition(query) {
  return request({
    url: '/flow/definition/list',
    method: 'get',
    params: query
  })
}

// 查询流程定义详细
export function getDefinition(id) {
  return request({
    url: '/flow/definition/' + id,
    method: 'get'
  })
}

// 获取流程定义xml字符串
export function saveXml(data) {
  return request({
    url: '/flow/definition/saveXml',
    method: 'post',
    data: data
  })
}

// 导出流程定义详细
export function exportDefinition(id) {
  return request({
    url: '/flow/definition/exportDefinition/' + id,
    method: 'get'
  })
}

// 获取流程定义xml字符串
export function getXmlString(id) {
  return request({
    url: '/warmjars/flow/definition/xmlString/' + id,
    method: 'get'
  })
}

// 新增流程定义
export function addDefinition(data) {
  return request({
    url: '/flow/definition',
    method: 'post',
    data: data
  })
}

// 修改流程定义
export function updateDefinition(data) {
  return request({
    url: '/flow/definition',
    method: 'put',
    data: data
  })
}

// 删除流程定义
export function delDefinition(id) {
  return request({
    url: '/flow/definition/' + id,
    method: 'delete'
  })
}

// 发布流程定义
export function publish(id) {
  return request({
    url: '/flow/definition/publish/' + id,
    method: 'get'
  })
}

// 取消发布流程定义
export function unPublish(id) {
  return request({
    url: '/flow/definition/unPublish/' + id,
    method: 'get'
  })
}

// 复制流程定义
export function copyDef(id) {
  return request({
    url: '/flow/definition/copyDef/' + id,
    method: 'get'
  })
}

// 查看流程图
export function flowChartNoColor(definitionId) {
  return request({
    url: '/flow/definition/flowChartNoColor/' + definitionId,
    method: 'get'
  })
}

// 查看流程图
export function flowImage(instanceId) {
  return request({
    url: '/flow/definition/flowChart/' + instanceId,
    method: 'get'
  })
}

// 激活流程
export function active(definitionId) {
  return request({
    url: '/flow/definition/active/' + definitionId,
    method: 'get'
  })
}

// 挂起流程
export function unActive(definitionId) {
  return request({
    url: '/flow/definition/unActive/' + definitionId,
    method: 'get'
  })
}

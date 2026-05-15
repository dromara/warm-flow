import request from '@/utils/request'

// 查询待办任务列表
export function toDoPage(query) {
  return request({
    url: '/flow/execute/toDoPage',
    method: 'get',
    params: query
  })
}

// 查询已办任务列表
export function donePage(query) {
  return request({
    url: '/flow/execute/donePage',
    method: 'get',
    params: query
  })
}

// 查询抄送任务列表
export function copyPage(query) {
  return request({
    url: '/flow/execute/copyPage',
    method: 'get',
    params: query
  })
}

// 查询已办任务列表
export function doneList(instanceId) {
  return request({
    url: '/flow/execute/doneList/' + instanceId,
    method: 'get'
  })
}

// 查询跳转任意节点列表
export function anyNodeList(instanceId) {
  return request({
    url: '/flow/execute/anyNodeList/' + instanceId,
    method: 'get'
  })
}

// 转办|加签|委派|减签
export function interactiveType(taskId, assigneePermission, operatorType) {
  return request({
    url: '/flow/execute/interactiveType',
    method: 'post',
    params: {
              taskId: taskId,
              addHandlers: assigneePermission,
              operatorType: operatorType
            }
  })
}

// 查询跳转任意节点列表
export function getTaskById(taskId) {
  return request({
    url: '/flow/execute/getTaskById/' + taskId,
    method: 'get'
  })
}

// 激活流程
export function active(instanceId) {
  return request({
    url: '/flow/execute/active/' + instanceId,
    method: 'get'
  })
}

// 挂起流程
export function unActive(instanceId) {
  return request({
    url: '/flow/execute/unActive/' + instanceId,
    method: 'get'
  })
}

// 查询用户列表-转办|加签|委派|减签
export function interactiveTypeSysUser(query) {
  return request({
    url: '/flow/execute/interactiveTypeSysUser',
    method: 'get',
    params: query
  })
}

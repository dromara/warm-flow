import request from '@/utils/request'

// 查询企业采购流程表列表
export function listSteps(query) {
  return request({
    url: '/system/steps/list',
    method: 'get',
    params: query
  })
}

// 查询企业采购流程表详细
export function getSteps(id) {
  return request({
    url: '/system/steps/' + id,
    method: 'get'
  })
}

// 新增企业采购流程表
export function addSteps(data) {
  return request({
    url: '/system/steps',
    method: 'post',
    data: data
  })
}

// 修改企业采购流程表
export function updateSteps(data) {
  return request({
    url: '/system/steps',
    method: 'put',
    data: data
  })
}

// 删除企业采购流程表
export function delSteps(id) {
  return request({
    url: '/system/steps/' + id,
    method: 'delete'
  })
}

// 办理采购流程
export function handle(data, taskId, skipType, message, nodeCode, flowStatus) {
  return request({
    url: '/system/steps/handle?taskId=' + taskId + '&skipType=' + skipType + '&message=' + message
      + '&nodeCode=' + nodeCode + '&flowStatus=' + flowStatus,
    data: data,
    method: 'post'
  })
}

// 驳回到上一个任务
export function rejectLast(data, taskId, message, flowStatus) {
  return request({
    url: '/system/steps/rejectLast?taskId=' + taskId + '&message=' + message + '&flowStatus=' + flowStatus,
    data: data,
    method: 'post'
  })
}

// 拿回到最近办理的任务
export function taskBack(data, taskId, message, flowStatus) {
  return request({
    url: '/system/steps/taskBack?taskId=' + taskId + '&message=' + message + '&flowStatus=' + flowStatus,
    data: data,
    method: 'post'
  })
}

// 撤销
export function revoke(id) {
  return request({
    url: '/system/steps/revoke/' + id,
    method: 'get'
  })
}

// 拿回到最近办理的任务
export function taskBackByInsId(id) {
  return request({
    url: '/system/steps/taskBackByInsId/' + id,
    method: 'get'
  })
}

// 终止流程
export function termination(data) {
  return request({
    url: '/system/steps/termination',
    method: 'post',
    data: data
  })
}

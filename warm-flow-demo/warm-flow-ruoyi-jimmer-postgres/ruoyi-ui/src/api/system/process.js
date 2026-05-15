import request from '@/utils/request'

// 查询合同流程列表
export function listProcess(query) {
  return request({
    url: '/system/process/list',
    method: 'get',
    params: query
  })
}

// 查询合同流程详细
export function getProcess(id) {
  return request({
    url: '/system/process/' + id,
    method: 'get'
  })
}

// 新增合同流程
export function addProcess(data) {
  return request({
    url: '/system/process',
    method: 'post',
    data: data
  })
}

// 修改合同流程
export function updateProcess(data) {
  return request({
    url: '/system/process',
    method: 'put',
    data: data
  })
}

// 删除合同流程
export function delProcess(id) {
  return request({
    url: '/system/process/' + id,
    method: 'delete'
  })
}

// 办理OA 请假申请
export function handle(data, taskId, skipType, message, nodeCode, flowStatus) {
  return request({
    url: '/system/process/handle?taskId=' + taskId + '&skipType=' + skipType + '&message=' + message
      + '&nodeCode=' + nodeCode + '&flowStatus=' + flowStatus,
    data: data,
    method: 'post'
  })
}

// 驳回到上一个任务
export function rejectLast(data, taskId, message, flowStatus) {
  return request({
    url: '/system/process/rejectLast?taskId=' + taskId + '&message=' + message + '&flowStatus=' + flowStatus,
    data: data,
    method: 'post'
  })
}

// 拿回到最近办理的任务
export function taskBack(data, taskId, message, flowStatus) {
  return request({
    url: '/system/process/taskBack?taskId=' + taskId + '&message=' + message + '&flowStatus=' + flowStatus,
    data: data,
    method: 'post'
  })
}

// 撤销
export function revoke(id) {
  return request({
    url: '/system/process/revoke/' + id,
    method: 'get'
  })
}

// 拿回到最近办理的任务
export function taskBackByInsId(id) {
  return request({
    url: '/system/process/taskBackByInsId/' + id,
    method: 'get'
  })
}

// 终止流程
export function termination(data) {
  return request({
    url: '/system/process/termination',
    method: 'post',
    data: data
  })
}

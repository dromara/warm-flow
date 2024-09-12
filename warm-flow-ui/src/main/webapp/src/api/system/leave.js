import request from '@/utils/request'

// 查询OA 请假申请列表
export function listLeave(query) {
  return request({
    url: '/system/leave/list',
    method: 'get',
    params: query
  })
}

// 查询OA 请假申请详细
export function getLeave(id) {
  return request({
    url: '/system/leave/' + id,
    method: 'get'
  })
}

// 新增OA 请假申请
export function addLeave(data, flowStatus) {
  return request({
    url: '/system/leave?flowStatus=' + flowStatus,
    method: 'post',
    data: data
  })
}

// 修改OA 请假申请
export function updateLeave(data) {
  return request({
    url: '/system/leave',
    method: 'put',
    data: data
  })
}

// 删除OA 请假申请
export function delLeave(id) {
  return request({
    url: '/system/leave/' + id,
    method: 'delete'
  })
}

// 提交审批OA 请假申请
export function submit(id, flowStatus) {
  return request({
    url: '/system/leave/submit?id=' + id + '&flowStatus=' + flowStatus,
    method: 'get'
  })
}

// 办理OA 请假申请
export function handle(data, taskId, skipType, message, nodeCode, flowStatus) {
  return request({
    url: '/system/leave/handle?taskId=' + taskId + '&skipType=' + skipType + '&message=' + message
      + '&nodeCode=' + nodeCode + '&flowStatus=' + flowStatus,
    data: data,
    method: 'post'
  })
}

// 终止流程
export function termination(instanceId) {
  return request({
    url: '/system/leave/termination/' + instanceId,
    method: 'get'
  })
}
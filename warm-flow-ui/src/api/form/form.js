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

// 获取表单设计详情及数据
export function executeLoad(id) {
  return request({
    url: urlPrefix + 'warm-flow/execute/load/' + id,
    method: 'get'
  })
}

// 办理OA 申请
export function executeHandle(data, taskId, skipType, message) {
  return request({
    url: urlPrefix + 'warm-flow/execute/handle?taskId=' + taskId + '&skipType=' + skipType + '&message=' + message,
    data: data,
    method: 'post'
  })
}

// 查看已办表单详情
export function hisLoad(taskId) {
  return request({
    url: urlPrefix + 'warm-flow/execute/hisLoad/' + taskId,
    method: 'get'
  })
}

import request from '@/utils/request'

const urlPrefix = import.meta.env.VITE_URL_PREFIX

// 保存json流程定义
export function saveJson(data) {
  return request({
    url: urlPrefix + 'warm-flow/save-json',
    method: 'post',
    data: data
  })
}

// 获取流程定义
export function queryDef(id) {
  return request({
    url: urlPrefix + 'warm-flow/query-def/' + id,
    method: 'get'
  })
}

// 办理人权限设置列表tabs页签
export function handlerType() {
  return request({
    url: urlPrefix + 'warm-flow/handler-type',
    method: 'get',
  })
}

// 办理人权限设置列表结果
export function handlerResult(query) {
  return request({
    url: urlPrefix + 'warm-flow/handler-result',
    method: 'get',
    params: query
  })
}

// 办理人权限名称回显
export function handlerFeedback(query) {
  return request({
    url: urlPrefix + 'warm-flow/handler-feedback',
    method: 'get',
    params: query
  })
}

// 办理人选择项
export function handlerDict() {
  return request({
    url: urlPrefix + 'warm-flow/handler-dict',
    method: 'get'
  })
}

// 查询已发布表单定义列表
export function publishedList() {
  return request({
    url: urlPrefix + 'warm-flow/published-form',
    method: 'get'
  })
}

// 办理人选择项
export function nodeExt() {
  return request({
    url: urlPrefix + 'warm-flow/node-ext',
    method: 'get'
  })
}

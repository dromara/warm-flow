import request from '@/utils/request'

const urlPrefix = import.meta.env.VITE_URL_PREFIX

// 保存xml流程定义
export function saveXml(data) {
  return request({
    url: urlPrefix + 'warm-flow-ui/save-xml',
    method: 'post',
    data: data
  })
}

// 获取流程定义xml字符串
export function getXmlString(id) {
  return request({
    url: urlPrefix + 'warm-flow-ui/xml-string/' + id,
    method: 'get'
  })
}

// 保存json流程定义
export function saveJson(data) {
  return request({
    url: urlPrefix + 'warm-flow-ui/save-json',
    method: 'post',
    data: data
  })
}

// 获取流程定义json字符串
export function getJsonString(id) {
  return request({
    url: urlPrefix + 'warm-flow-ui/json-string/' + id,
    method: 'get'
  })
}

// 办理人权限设置列表tabs页签
export function handlerType() {
  return request({
    url: urlPrefix + 'warm-flow-ui/handler-type',
    method: 'get',
  })
}

// 办理人权限设置列表结果
export function handlerResult(query) {
  return request({
    url: urlPrefix + 'warm-flow-ui/handler-result',
    method: 'get',
    params: query
  })
}


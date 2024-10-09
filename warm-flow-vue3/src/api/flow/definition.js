import request from '@/utils/request'


// 获取流程定义xml字符串
export function saveXml(data) {
  return request({
    url: '/warm_flow/definition/saveXml',
    method: 'post',
    data: data
  })
}

// 获取流程定义xml字符串
export function getXmlString(id) {
  return request({
    url: '/warm_flow/definition/xmlString/' + id,
    method: 'get'
  })
}


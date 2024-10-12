import request from '@/utils/request'


// 保存流程定义
export function saveXml(data) {
  return request({
    url: '/warm-flow-ui/save-xml',
    method: 'post',
    data: data
  })
}

// 获取流程定义xml字符串
export function getXmlString(id) {
  return request({
    url: '/warm-flow-ui/xml-string/' + id,
    method: 'get'
  })
}

// 获取设计器办理人组选择框数据
export function selectGroup() {
  return request({
    url: '/warm-flow-ui/select-group',
    method: 'get'
  })
}

// 办理人权限设置列表结果
export function handlerResult(query) {
  return request({
    url: '/warm-flow-ui/handler-result',
    method: 'get',
    params: query
  })
}

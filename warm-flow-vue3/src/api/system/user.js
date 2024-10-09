import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 根据用户id查用户名
export function idReverseDisplayName(ids) {
  return request({
    url: '/flow/execute/idReverseDisplayName/' + ids,
    method: 'get'
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/system/user/deptTree',
    method: 'get'
  })
}

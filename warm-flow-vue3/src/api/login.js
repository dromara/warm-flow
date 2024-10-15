import request from '@/utils/request'

// 退出方法
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

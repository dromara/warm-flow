import Cookies from 'js-cookie'

// 接收外部业务系统token，并存入cookie中
export const TokenKey = 'Warm-Token'
// 设置外部token保存到header中的名称
export const TokenName = 'Warm-TokenName'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function getTokenName() {
  return Cookies.get(TokenName)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

import Cookies from 'js-cookie'

// 接收外部业务系统token，并存入cookie中
export const TokenPrefix = 'Warm-'
// 设置外部token保存到header中的名称
export const TokenName = TokenPrefix + 'TokenName'

export function getToken(key) {
  return Cookies.get(TokenPrefix + key)
}

export function getTokenName() {
  return Cookies.get(TokenName)
}

export function removeToken(key) {
  return Cookies.remove(TokenPrefix + key)
}

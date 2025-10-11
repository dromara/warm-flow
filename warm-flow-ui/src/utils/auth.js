// 接收外部业务系统token，并存入localStorage中
export const TokenPrefix = 'Warm-'
// 设置外部token保存到header中的名称
export const TokenName = TokenPrefix + 'TokenName'

export function getToken(key) {
  return localStorage.getItem(TokenPrefix + key)
}

export function getTokenName() {
  return localStorage.getItem(TokenName)
}

export function removeToken(key) {
  return localStorage.removeItem(TokenPrefix + key)
}

// 添加 setToken 方法来存储 token
export function setToken(key, value) {
  return localStorage.setItem(TokenPrefix + key, value)
}

// 添加 setTokenName 方法来存储 token name
export function setTokenName(value) {
  return localStorage.setItem(TokenName, value)
}

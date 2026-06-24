// 接收外部业务系统token，并存入localStorage中
export const Prefix = 'Warm-'
// 设置外部token保存到header中的名称
export const TokenName = Prefix + 'TokenName'

export const FrameworkName = Prefix + 'Framework'

export function getToken(key) {
  return localStorage.getItem(Prefix + key)
}

export function getTokenName() {
  return localStorage.getItem(TokenName)
}

export function remove(key) {
  return localStorage.removeItem(key)
}

// 添加 setToken 方法来存储 token
export function setToken(key, value) {
  return localStorage.setItem(Prefix + key, value)
}

// 添加 setTokenName 方法来存储 token name
export function setTokenName(value) {
  return localStorage.setItem(TokenName, value)
}

// 添加 setFramework 方法来存储 Framework
export function setFramework(value) {
  return localStorage.setItem(FrameworkName, value)
}

export function getFramework() {
  return localStorage.getItem(FrameworkName)
}

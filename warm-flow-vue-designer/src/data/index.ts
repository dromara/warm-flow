import { setDataProvider, getDataProvider } from './provider'
import { createHttpProvider } from './httpProvider'
import { createMockProvider } from './mockProvider'
import type { DataProvider } from './provider'

/**
 * 数据层统一出口。
 *
 * - 默认数据源为内置 axios（createHttpProvider），与原有集成方式行为一致；
 * - setupDataProvider 在应用启动时按环境装配（默认 http，显式开启时切 mock）；
 * - 业务方 / 宿主可在启动后任意时机调用 setDataProvider 覆盖，实现数据层与后端解耦。
 *
 * @author warm
 */

/**
 * 是否启用 mock 数据源：构建期 env VITE_USE_MOCK=true，或运行期 URL 携带 ?mock=true。
 */
export function isMockEnabled(): boolean {
  let byUrl = false
  try {
    byUrl = new URLSearchParams(window.location.search).get('mock') === 'true'
  } catch (e) {
    byUrl = false
  }
  return import.meta.env.VITE_USE_MOCK === 'true' || byUrl
}

/**
 * 按当前环境装配默认数据源：开启 mock 时用内存实现，否则保持内置 axios。
 *
 * 在应用挂载前调用一次即可；业务方仍可随后通过 setDataProvider 再次覆盖。
 */
export function setupDataProvider(): void {
  if (isMockEnabled()) {
    setDataProvider(createMockProvider())
  }
}

export { setDataProvider, getDataProvider, createHttpProvider, createMockProvider }
export type { DataProvider }

import { getDataProvider } from '@/data/provider'

// 说明：保持原有导出名与签名不变，内部委托给「当前数据源」(dataProvider)。
// 默认实现为内置 axios（见 src/data/httpProvider.ts），调用方无需改动。

// 获取设计器配置
export function config(): Promise<any> {
  return getDataProvider().config()
}

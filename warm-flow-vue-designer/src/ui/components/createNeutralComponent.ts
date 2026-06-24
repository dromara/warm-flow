import { defineComponent, h, ref } from 'vue'
import { getUiAdapter } from '@/ui/uiAdapter'
import { useComponentSize } from '@/ui/designerOptions'

/**
 * 中性透传组件工厂：按「语义名」生成一个包装组件，运行时渲染当前 UI 适配器
 * （默认 Element Plus）注册的对应组件，实现「设计器视图与具体 UI 库解耦」。
 *
 * - props / 事件：经 `$attrs` 整体透传（inheritAttrs:false + 显式 v-bind），含 v-model。
 * - 插槽：默认 / 具名 / 作用域插槽通用转发。
 * - ref：通过 `expose` 代理把对包装组件的 ref 访问（如 form.validate() / input.focus()）
 *   转发到内部真实组件实例，调用方 ref 用法无感。
 *
 * 注意：对「父组件按子组件类型自省」的耦合组件（如 el-table/el-table-column 读取列定义），
 * 通用包装可能不被父组件识别，这类需单独验证或特殊处理，不可盲目套用。
 *
 * @author warm
 */
export function createNeutralComponent(name: string, registryKey: string) {
  return defineComponent({
    name,
    inheritAttrs: false,
    setup(_props, { attrs, slots, expose }) {
      const inner = ref<Record<string, unknown> | null>(null)
      const globalSize = useComponentSize()
      // 代理转发：对包装组件实例的属性 / 方法访问统一落到内部真实组件实例
      expose(
        new Proxy(
          {},
          {
            get(_t, key) {
              const target = inner.value as Record<string, unknown> | null
              if (!target) return undefined
              const value = target[key as string]
              return typeof value === 'function' ? (value as Function).bind(target) : value
            },
            has(_t, key) {
              const target = inner.value as Record<string, unknown> | null
              return target ? key in target : false
            }
          }
        )
      )
      return () => {
        const comp = getUiAdapter().components?.[registryKey]
        if (!comp) return null
        // 全局尺寸作为默认注入：local attrs.size 优先；default 不注入（让各 UI 库用自身自然默认，
        // 同时避免把 EP 专有的 'default' 传给 antd）。非 size 语义的组件由适配器 / 底层组件忽略。
        const size = globalSize.value
        const props =
          size !== 'default' && attrs.size == null
            ? { size, ...attrs, ref: inner }
            : { ...attrs, ref: inner }
        return h(comp as never, props, slots)
      }
    }
  })
}

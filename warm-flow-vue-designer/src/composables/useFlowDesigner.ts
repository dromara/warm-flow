import { ref, type Ref } from 'vue'
import type { FlowDesignerInstance } from '@/designer/types'

export type { FlowDesignerInstance }

/**
 * useFlowDesigner 返回值：模板 ref + 与 FlowDesignerInstance 同名的命令式方法（带空安全包装）。
 */
export interface UseFlowDesignerReturn extends FlowDesignerInstance {
  /** 绑定到 `<FlowDesigner ref="designerRef" />` 的模板 ref */
  designerRef: Ref<FlowDesignerInstance | null>
  /** 设计器是否已挂载（命令式 API 是否可用） */
  isReady: () => boolean
}

/**
 * 流程设计器命令式 API 组合式封装。
 *
 * 相比直接用模板 ref，本 hook 额外提供：
 * - 空安全：组件尚未挂载时调用方法不会抛错，仅 warn 并返回安全默认值；
 * - 解构友好：`const { designerRef, save, getFlowJson } = useFlowDesigner()`；
 * - 类型完备：方法签名与 FlowDesignerInstance 完全一致，IDE 自动补全。
 *
 * @example
 * ```vue
 * <script setup lang="ts">
 * import { FlowDesigner, useFlowDesigner } from '@dromara/warm-flow-designer'
 * const { designerRef, save, getFlowJson } = useFlowDesigner()
 * </script>
 * <template>
 *   <FlowDesigner ref="designerRef" :definition-id="id" />
 *   <button @click="save">保存</button>
 * </template>
 * ```
 *
 * @author warm
 */
export function useFlowDesigner(): UseFlowDesignerReturn {
  const designerRef = ref<FlowDesignerInstance | null>(null)

  /** 取实例，未挂载时 warn 并返回 null（调用方按需降级） */
  function ensure(method: string): FlowDesignerInstance | null {
    if (!designerRef.value) {
      console.warn(
        `[warm-flow] useFlowDesigner.${method}(): 设计器尚未挂载，调用被忽略。` +
        `请确认已将 designerRef 绑定到 <FlowDesigner ref="designerRef" /> 且组件已渲染。`
      )
      return null
    }
    return designerRef.value
  }

  return {
    designerRef,
    isReady: () => !!designerRef.value,
    save: async () => { await ensure('save')?.save() },
    validate: async () => (await ensure('validate')?.validate()) ?? false,
    getGraphData: () => ensure('getGraphData')?.getGraphData() ?? null,
    getFlowJson: () => ensure('getFlowJson')?.getFlowJson() ?? '',
    getFlowName: () => ensure('getFlowName')?.getFlowName() ?? '',
    getLogicFlow: () => ensure('getLogicFlow')?.getLogicFlow() ?? null,
    zoom: (z) => { ensure('zoom')?.zoom(z) },
    zoomIn: () => { ensure('zoomIn')?.zoomIn() },
    zoomOut: () => { ensure('zoomOut')?.zoomOut() },
    fitView: () => { ensure('fitView')?.fitView() },
    resetZoom: () => { ensure('resetZoom')?.resetZoom() },
    undo: () => { ensure('undo')?.undo() },
    redo: () => { ensure('redo')?.redo() },
    clear: () => { ensure('clear')?.clear() },
    downloadImage: () => { ensure('downloadImage')?.downloadImage() },
    downloadJson: () => { ensure('downloadJson')?.downloadJson() },
    isDirty: () => ensure('isDirty')?.isDirty() ?? false,
    resetDirty: () => { ensure('resetDirty')?.resetDirty() },
    validateStructure: () => ensure('validateStructure')?.validateStructure() ?? { valid: false, errors: ['设计器尚未挂载'] },
  }
}

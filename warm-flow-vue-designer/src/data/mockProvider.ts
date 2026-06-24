import type { DataProvider } from './provider'

/**
 * 内存 mock 数据源：让设计器脱离后端独立运行 / 联调 / 测试。
 *
 * 仅在显式开启时启用（构建期 VITE_USE_MOCK=true，或运行期 URL 携带 ?mock=true），
 * 默认不启用，因此对线上 iframe / webjars 零影响。
 *
 * 返回结构对齐后端 vo（FlowPage / Tree / HandlerAuth / HandlerSelectVo / Dict / ListenerVo / WarmFlowVo 等），
 * 提供最小可用示例数据，使办理人选择、监听器、表单等功能在脱后端下可正常体验、不报错。
 *
 * @author warm
 */

/** 构造一个成功响应（对齐后端 {code, msg, data} 结构）。 */
function ok(data?: any, msg?: string): Promise<any> {
  return Promise.resolve({ code: 200, msg: msg || 'ok(mock)', data: data })
}

// 办理人示例数据（按 角色 / 部门 / 用户 三类 tab；对齐 HandlerAuth 结构）
const HANDLER_ROWS: Record<string, any[]> = {
  角色: [
    { storageId: 'role:1', handlerCode: 'admin', handlerName: '系统管理员', groupName: '角色', createTime: '2024-01-01 10:00:00' },
    { storageId: 'role:2', handlerCode: 'leader', handlerName: '部门主管', groupName: '角色', createTime: '2024-01-02 10:00:00' }
  ],
  部门: [
    { storageId: 'dept:1', handlerCode: 'tech', handlerName: '研发部', groupName: '部门', createTime: '2024-01-01 10:00:00' },
    { storageId: 'dept:2', handlerCode: 'hr', handlerName: '人事部', groupName: '部门', createTime: '2024-01-02 10:00:00' }
  ],
  用户: [
    { storageId: 'user:1', handlerCode: 'zhangsan', handlerName: '张三', groupName: '用户', createTime: '2024-01-01 10:00:00' },
    { storageId: 'user:2', handlerCode: 'lisi', handlerName: '李四', groupName: '用户', createTime: '2024-01-02 10:00:00' }
  ]
}

// 左侧组织架构树（对齐 Tree 结构，配合办理人列表）
const HANDLER_TREE: any[] = [
  {
    id: '1', name: '总公司', parentId: '0', children: [
      { id: '2', name: '研发部', parentId: '1', children: [] },
      { id: '3', name: '人事部', parentId: '1', children: [] }
    ]
  }
]

// 全量办理人（用于名称回显匹配）
const ALL_HANDLERS: any[] = [].concat(HANDLER_ROWS.角色, HANDLER_ROWS.部门, HANDLER_ROWS.用户)

export function createMockProvider(): DataProvider {
  return {
    // ===== 流程定义 =====
    saveJson() {
      return ok(true, '保存成功(mock)')
    },
    // 新建态：返回空对象，设计器走本地 initData 渲染（res.data 需为对象，避免取属性报错）
    queryDef() {
      return ok({})
    },
    queryFlowChart() {
      return ok({})
    },
    // 办理人权限 tab（角色 / 部门 / 用户）
    handlerType() {
      return ok(['角色', '部门', '用户'])
    },
    // 办理人列表 + 左侧组织树（对齐 HandlerSelectVo: { handlerAuths: FlowPage, treeSelections: Tree[] }）
    handlerResult(query) {
      const type = (query && query.handlerType) || '角色'
      const rows = HANDLER_ROWS[type] || HANDLER_ROWS['角色']
      return ok({
        handlerAuths: { rows: rows, total: rows.length },
        treeSelections: HANDLER_TREE
      })
    },
    // 办理人名称回显（对齐 HandlerFeedBackVo: [{ storageId, handlerName }]）
    handlerFeedback(query) {
      const raw = query && query.storageIds ? String(query.storageIds) : ''
      const ids = raw ? raw.split(',') : []
      const list = ids.map((id) => {
        const hit = ALL_HANDLERS.find((r) => r.storageId === id)
        return { storageId: id, handlerName: hit ? hit.handlerName : id }
      })
      return ok(list)
    },
    // 办理人字典（对齐 Dict: [{ label, value, childList }]）
    handlerDict() {
      return ok([
        { label: '角色', value: 'role', childList: [] },
        { label: '部门', value: 'dept', childList: [] },
        { label: '用户', value: 'user', childList: [] }
      ])
    },
    // 已发布表单定义列表（自定义表单下拉）
    publishedList() {
      return ok([
        { id: '1001', formName: '请假申请表(mock)' },
        { id: '1002', formName: '报销申请表(mock)' }
      ])
    },
    // 节点扩展属性（空数组安全：消费方按 e.childs 遍历，留空表示无扩展属性）
    nodeExt() {
      return ok([])
    },
    // 监听器下拉（对齐 ListenerVo: [{ type, path, description }]）
    listenerList() {
      return ok([
        { type: 'start', path: 'com.example.flow.StartListener', description: '开始监听器(mock)' },
        { type: 'assignment', path: 'com.example.flow.AssignmentListener', description: '指派监听器(mock)' },
        { type: 'finish', path: 'com.example.flow.FinishListener', description: '完成监听器(mock)' }
      ])
    },

    // ===== 表单 =====
    getFormContent() {
      return ok({})
    },
    saveFormContent() {
      return ok(true, '保存成功(mock)')
    },
    executeLoad() {
      return ok({})
    },
    executeHandle() {
      return ok(true, '办理成功(mock)')
    },
    hisLoad() {
      return ok({})
    },

    // ===== 配置 =====
    // 设计器启动即调用，返回安全默认（无 token、默认框架 springboot），保证 mock 下可正常初始化
    config() {
      return ok({ tokenNameList: [], framework: 'springboot' })
    }
  }
}

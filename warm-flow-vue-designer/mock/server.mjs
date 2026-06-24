/**
 * Warm-Flow 设计器本地 Mock 服务（开发联调用 · 零依赖）。
 *
 * 起一个真实 HTTP 服务（默认 :8080），对接 vite 的 /dev-api 代理：
 * 前端正常 `yarn dev` 访问 http://localhost:8083/（无需 ?mock=true），
 * 请求经 axios → /dev-api 代理 → 本服务，走完整真实 HTTP 链路联调，
 * 而非前端内存 mock（mockProvider / ?mock=true 直接在 DataProvider 层替换、绕过网络）。
 *
 * 数据与 src/data/mockProvider.ts 对齐（同一套示例：办理人 / 组织树 / 监听器 / 字典 / 表单等）；
 * 二者分别服务于「库内存 mock（给 npm 消费方脱后端预览）」与「开发 HTTP 联调」，互不耦合：
 * 本目录是纯开发工具，不进 npm 包（package.json files 仅 dist-lib）、不进 jar（仅 dist），也不被 src 引用。
 *
 * 启动：yarn mock            （= node mock/server.mjs，零依赖、无需安装任何包）
 * 端口：环境变量 MOCK_PORT 可改，默认 8080（与 .env.development 的 /dev-api 代理目标一致）
 *
 * @author warm
 */
import { createServer } from 'node:http'

const PORT = Number(process.env.MOCK_PORT) || 8080

/* ============ 示例数据（与 src/data/mockProvider.ts 对齐） ============ */

// 办理人示例数据（按 角色 / 部门 / 用户 三类 tab；对齐 HandlerAuth 结构）
const HANDLER_ROWS = {
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
const HANDLER_TREE = [
  {
    id: '1', name: '总公司', parentId: '0', children: [
      { id: '2', name: '研发部', parentId: '1', children: [] },
      { id: '3', name: '人事部', parentId: '1', children: [] }
    ]
  }
]

// 全量办理人（用于名称回显匹配）
const ALL_HANDLERS = [].concat(HANDLER_ROWS.角色, HANDLER_ROWS.部门, HANDLER_ROWS.用户)

/** 统一成功响应（对齐后端 {code,msg,data}；前端 request 拦截器按 code===200 放行，否则弹错）。 */
const ok = (data, msg = 'ok(mock)') => ({ code: 200, msg, data })

/* ============ 路由表（method + path 对齐 src/data/httpProvider.ts，:param 为路径参数） ============ */
const routes = [
  // ===== 流程定义 =====
  ['POST', '/warm-flow/save-json', () => ok(true, '保存成功(mock)')],
  ['GET', '/warm-flow/query-def', () => ok({})],
  ['GET', '/warm-flow/query-def/:id', () => ok({})],
  ['GET', '/warm-flow/query-flow-chart/:id', () => ok({})],
  ['GET', '/warm-flow/handler-type', () => ok(['角色', '部门', '用户'])],
  ['GET', '/warm-flow/handler-result', (ctx) => {
    const type = ctx.query.get('handlerType') || '角色'
    const rows = HANDLER_ROWS[type] || HANDLER_ROWS['角色']
    return ok({ handlerAuths: { rows, total: rows.length }, treeSelections: HANDLER_TREE })
  }],
  ['GET', '/warm-flow/handler-feedback', (ctx) => {
    const raw = ctx.query.get('storageIds') || ''
    const ids = raw ? raw.split(',') : []
    return ok(ids.map((id) => {
      const hit = ALL_HANDLERS.find((r) => r.storageId === id)
      return { storageId: id, handlerName: hit ? hit.handlerName : id }
    }))
  }],
  ['GET', '/warm-flow/handler-dict', () => ok([
    { label: '角色', value: 'role', childList: [] },
    { label: '部门', value: 'dept', childList: [] },
    { label: '用户', value: 'user', childList: [] }
  ])],
  ['GET', '/warm-flow/published-form', () => ok([
    { id: '1001', formName: '请假申请表(mock)' },
    { id: '1002', formName: '报销申请表(mock)' }
  ])],
  ['GET', '/warm-flow/node-ext', () => ok([])],
  ['GET', '/warm-flow/listener-list', () => ok([
    { type: 'start', path: 'com.example.flow.StartListener', description: '开始监听器(mock)' },
    { type: 'assignment', path: 'com.example.flow.AssignmentListener', description: '指派监听器(mock)' },
    { type: 'finish', path: 'com.example.flow.FinishListener', description: '完成监听器(mock)' }
  ])],

  // ===== 表单 =====
  ['GET', '/warm-flow/form-content/:id', () => ok({})],
  ['POST', '/warm-flow/form-content', () => ok(true, '保存成功(mock)')],
  ['GET', '/warm-flow/execute/load/:id', () => ok({})],
  ['POST', '/warm-flow/execute/handle', () => ok(true, '办理成功(mock)')],
  ['GET', '/warm-flow/execute/hisLoad/:taskId', () => ok({})],

  // ===== 配置 =====
  ['GET', '/warm-flow-ui/config', () => ok({ tokenNameList: [], framework: 'springboot' })]
]

/* ============ 极简路由匹配（支持 :param） ============ */
function compile(pattern) {
  const keys = []
  const source = '^' + pattern.replace(/:[^/]+/g, (m) => {
    keys.push(m.slice(1))
    return '([^/]+)'
  }) + '$'
  return { keys, regexp: new RegExp(source) }
}

const compiled = routes.map(([method, pattern, handler]) => {
  const { keys, regexp } = compile(pattern)
  return { method, pattern, handler, keys, regexp }
})

const CORS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
  'Access-Control-Allow-Headers': '*'
}

function readBody(req) {
  return new Promise((resolve) => {
    let data = ''
    req.on('data', (chunk) => { data += chunk })
    req.on('end', () => {
      try { resolve(data ? JSON.parse(data) : {}) } catch (e) { resolve({}) }
    })
    req.on('error', () => resolve({}))
  })
}

function sendJson(res, status, payload) {
  res.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8', ...CORS })
  res.end(JSON.stringify(payload))
}

const server = createServer(async (req, res) => {
  const method = req.method || 'GET'
  const url = new URL(req.url || '/', `http://localhost:${PORT}`)
  const pathname = decodeURIComponent(url.pathname)

  if (method === 'OPTIONS') {
    res.writeHead(204, CORS)
    res.end()
    return
  }

  const route = compiled.find((r) => r.method === method && r.regexp.test(pathname))
  if (!route) {
    console.warn(`[mock] ${method} ${pathname} -> 404 未实现`)
    sendJson(res, 404, { code: 404, msg: `mock 未实现: ${method} ${pathname}`, data: null })
    return
  }

  const body = method === 'POST' || method === 'PUT' ? await readBody(req) : {}
  const match = route.regexp.exec(pathname)
  const params = {}
  route.keys.forEach((k, i) => { params[k] = match[i + 1] })

  try {
    const result = await route.handler({ params, query: url.searchParams, body, headers: req.headers })
    console.log(`[mock] ${method} ${pathname} -> ${result.code}`)
    sendJson(res, 200, result)
  } catch (e) {
    console.error(`[mock] ${method} ${pathname} -> 500`, e)
    sendJson(res, 500, { code: 500, msg: String((e && e.message) || e), data: null })
  }
})

server.listen(PORT, () => {
  console.log('\n  Warm-Flow Mock Server  (零依赖) 已启动')
  console.log(`  ->  http://localhost:${PORT}   （对接 vite /dev-api 代理 -> localhost:8080）`)
  console.log('  前端：yarn dev 后访问 http://localhost:8083/（无需 ?mock=true）即走真实 HTTP 链路')
  console.log(`  已注册 ${routes.length} 个接口：`)
  routes.forEach(([m, p]) => console.log(`    ${m.padEnd(4)} ${p}`))
  console.log('')
})

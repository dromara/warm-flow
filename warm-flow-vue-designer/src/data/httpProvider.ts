import request from '@/utils/request'
import type { DataProvider } from './provider'

const urlPrefix = import.meta.env.VITE_URL_PREFIX

/**
 * 默认数据源实现：基于内置 axios 调用 warm-flow REST 接口。
 *
 * 行为与原 api/* 完全一致，作为 dataProvider 的默认回退实现，保证现有 iframe / webjars
 * 集成方式零影响。业务方可通过 setDataProvider 覆盖其中任意方法，未覆盖的自动回退到此实现。
 *
 * @author warm
 */
export function createHttpProvider(): DataProvider {
  return {
    // ===== 流程定义（原 api/flow/definition.js） =====
    // 保存json流程定义
    saveJson(data, onlyNodeSkip) {
      return request({
        url: urlPrefix + 'warm-flow/save-json',
        method: 'post',
        data: data,
        headers: { onlyNodeSkip: onlyNodeSkip }
      })
    },
    // 获取流程定义
    queryDef(id) {
      const suffix = id ? '/' + id : ''
      return request({
        url: urlPrefix + 'warm-flow/query-def' + suffix,
        method: 'get'
      })
    },
    // 获取流程图
    queryFlowChart(id) {
      return request({
        url: urlPrefix + 'warm-flow/query-flow-chart/' + id,
        method: 'get'
      })
    },
    // 办理人权限设置列表tabs页签
    handlerType() {
      return request({ url: urlPrefix + 'warm-flow/handler-type', method: 'get' })
    },
    // 办理人权限设置列表结果
    handlerResult(query) {
      return request({ url: urlPrefix + 'warm-flow/handler-result', method: 'get', params: query })
    },
    // 办理人权限名称回显
    handlerFeedback(query) {
      return request({ url: urlPrefix + 'warm-flow/handler-feedback', method: 'get', params: query })
    },
    // 办理人选择项
    handlerDict() {
      return request({ url: urlPrefix + 'warm-flow/handler-dict', method: 'get' })
    },
    // 查询已发布表单定义列表
    publishedList() {
      return request({ url: urlPrefix + 'warm-flow/published-form', method: 'get' })
    },
    // 节点扩展属性
    nodeExt() {
      return request({ url: urlPrefix + 'warm-flow/node-ext', method: 'get' })
    },
    // 获取监听器列表
    listenerList() {
      return request({ url: urlPrefix + 'warm-flow/listener-list', method: 'get' })
    },

    // ===== 表单（原 api/form/form.js） =====
    // 查询表单定义内容
    getFormContent(id) {
      return request({ url: urlPrefix + 'warm-flow/form-content/' + id, method: 'get' })
    },
    // 保存表单设计
    saveFormContent(data) {
      return request({ url: urlPrefix + 'warm-flow/form-content', method: 'post', data: data })
    },
    // 获取表单设计详情及数据
    executeLoad(id) {
      return request({ url: urlPrefix + 'warm-flow/execute/load/' + id, method: 'get' })
    },
    // 办理OA申请
    executeHandle(data, taskId, skipType, message) {
      return request({
        url: urlPrefix + 'warm-flow/execute/handle?taskId=' + taskId + '&skipType=' + skipType + '&message=' + message,
        data: data,
        method: 'post'
      })
    },
    // 查看已办表单详情
    hisLoad(taskId) {
      return request({ url: urlPrefix + 'warm-flow/execute/hisLoad/' + taskId, method: 'get' })
    },

    // ===== 匿名 / 配置（原 api/anony.js） =====
    // 获取设计器配置
    config() {
      return request({ url: urlPrefix + 'warm-flow-ui/config', method: 'get' })
    }
  }
}

import { getUiAdapter } from '@/ui/uiAdapter'

let loadingInstance;

export default {
  // 消息提示
  msg(content) {
    getUiAdapter().message('info', content)
  },
  // 错误消息
  msgError(content) {
    getUiAdapter().message('error', content)
  },
  // 成功消息
  msgSuccess(content) {
    getUiAdapter().message('success', content)
  },
  // 警告消息
  msgWarning(content) {
    getUiAdapter().message('warning', content)
  },
  // 弹出提示
  alert(content) {
    getUiAdapter().alert(content)
  },
  // 错误提示
  alertError(content) {
    getUiAdapter().alert(content, { type: 'error' })
  },
  // 成功提示
  alertSuccess(content) {
    getUiAdapter().alert(content, { type: 'success' })
  },
  // 警告提示
  alertWarning(content) {
    getUiAdapter().alert(content, { type: 'warning' })
  },
  // 通知提示
  notify(content) {
    getUiAdapter().notify('info', content)
  },
  // 错误通知
  notifyError(content) {
    getUiAdapter().notify('error', content)
  },
  // 成功通知
  notifySuccess(content) {
    getUiAdapter().notify('success', content)
  },
  // 警告通知
  notifyWarning(content) {
    getUiAdapter().notify('warning', content)
  },
  // 确认窗体
  confirm(content) {
    return getUiAdapter().confirm(content)
  },
  // 提交内容
  prompt(content) {
    return getUiAdapter().prompt(content)
  },
  // 打开遮罩层
  loading(content) {
    loadingInstance = getUiAdapter().loading({ text: content, background: 'rgba(0, 0, 0, 0.7)' })
  },
  // 关闭遮罩层
  closeLoading() {
    if (loadingInstance) {
      loadingInstance.close();
    }
  }
}

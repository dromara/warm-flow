<template>
  <el-form ref="form" :model="form" :rules="rules" :disabled="disabled">
    <form-create
      class="formCreate"
      ref="FormCreate"
      :value.sync="formData"
      v-model="fApi"
      :rule="rule"
      :option="option"
      :disabled="disabled"
    ></form-create>
    <el-divider v-if="showApprovalFields"></el-divider>
    <el-form-item label="审批意见" prop="message" v-if="showApprovalFields">
      <el-input v-model="form.message" type="textarea" placeholder="请输入审批意见" :autosize="{ minRows: 4, maxRows: 4 }" />
    </el-form-item>
    <div class="dialog-footer" v-if="showApprovalFields">
      <el-button type="primary" @click="handleBtn('PASS')">审批通过</el-button>
      <el-button @click="handleBtn('REJECT')">退回</el-button>
    </div>
  </el-form>
</template>

<script setup name="formCreate">
import { getFormContent, executeLoad, executeHandle, hisLoad } from "&/api/form/definition";
import formCreate from "@form-create/element-ui";
const { proxy } = getCurrentInstance();
const disabled = ref(false);
const fApi = ref(null);
const showApprovalFields = ref(true); // 控制审批意见和按钮是否显示
const data = reactive({
  // 表单设计内容
  formData: {},
  rule: [],
  option: {},
  form: {
    message: ""
  },
  rules: {}
});
const { formData, rule, option, form, rules } = toRefs(data);
window.addEventListener("message", handleMessage);
window.parent.postMessage({ method: "formInit" }, "*");
onBeforeUnmount(() => {
  window.removeEventListener('message', handleMessage);
});
function handleMessage(event) {
  switch (event.data.method) {
    case "formInit":
      formInit(event.data.data);
      break;
    case "reset":
      reset(); // 表单重置
      break;
  }
}
// 表单重置
function reset() {
  formData.value = {};
  form.value.message = "";
};
/** 审核通过按钮 */
function handleBtn(skipType) {
  fApi.value.submit((formData, fApi) => {
    proxy.$refs["form"].validate(valid => {
      if (valid) {
        executeHandle(formData.value, props.taskId, skipType, form.value.message).then(response => {
          proxy.$modal.msgSuccess("办理成功");
          window.parent.postMessage({ method: "cancel" }, "*");
        });
      }
    });
  });
}
// 设计表单反显
async function formInit(data) {
  let response = null;
  let formContent = null;
  // type 来源：0待办-办理 1已办-流程历史记录 2已发布的表单设计
  if (data.type === "0") {
    reset();
    response = await executeLoad(data.taskId);
    if (!response.data) proxy.$modal.alertWarning("待办任务不存在");
    formContent = response.data;
  } else if (data.type === "1") {
    showApprovalFields.value = false;
    response = await hisLoad(data.taskId);
    if (!response.data) proxy.$modal.alertWarning("历史记录不存在");
    formContent = response.data;
  } else {
    showApprovalFields.value = false;
    response = await getFormContent(data.formId);
    formContent = JSON.parse(response.data);
  }

  disabled.value = data.disabled;
  rule.value = formContent.rule;
  option.value = formContent.option;
  if (option.value) option.value.submitBtn = false;
  formData.value = response.data; // 表单内容
  proxy.$nextTick(() => {
    window.parent.postMessage({ method: "getOffsetHeight", offsetHeight: proxy.$refs.FormCreate.$el.offsetHeight }, "*");
  });
};
</script>

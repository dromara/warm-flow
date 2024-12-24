<template>
  <div class="container" ref="container">
    <fc-designer class="fcDesigner" ref="designer" :config="config" @save="handleSave"/>
  </div>
</template>

<script setup name="formDesign">
import {getFormContent, saveFormContent} from '../api/form/definition';
const { proxy } = getCurrentInstance();
import useAppStore from "@/store/app";
const appParams = computed(() => useAppStore().appParams);

const definitionId = ref("");
const data = reactive({
  config: {
    //是否显示保存按钮
    showSaveBtn: true,
    //字段ID是否可编辑
    fieldReadonly: false
  }
});

const { config } = toRefs(data);

function handleSave() {
  //获取表单的生成规则
  const ruleJson = proxy.$refs.designer.getJson();
  //获取表单的配置
  const optionsJson =  JSON.stringify(proxy.$refs.designer.getOption());
  let data = {
    formContent: JSON.stringify({
      rule: formCreate.parseJson(ruleJson),
      option: formCreate.parseJson(optionsJson)
    }),
    id: definitionId.value
  };
  saveFormContent(data).then(response => {
    proxy.$modal.msgSuccess("保存成功");
    if (response.code === 200) {
      // const obj = { path: "/form/formDefinition", query: { t: Date.now(), pageNum: proxy.$route.query.pageNum } };
      // proxy.$tab.closeOpenPage(obj);
      window.parent.postMessage({ method: "close" }, "*");
    }
  });
}

// 获取详情
function getInfo() {
  definitionId.value = appParams.value?.id;
  if (!definitionId.value) {
    proxy.$modal.notifySuccess("流程id不能为空！");
  } else {
    getFormContent(definitionId.value).then(res => {
      let formContent = res.data;
      if (formContent) {
        nextTick(() => {
          formContent = JSON.parse(formContent);
          if (formContent.rule) proxy.$refs.designer.setRule(formContent.rule);
          if (formContent.option) proxy.$refs.designer.setOption(formContent.option);
        });
      }
    });
  }
}
getInfo();

</script>

<style scoped lang="scss">
::v-deep.container {
  width: 100%;
  .fcDesigner {
    height: 100vh;
    .el-aside {
      padding: 0;
      background: #ffffff;
      height: 100%;
    }
  }
}
</style>

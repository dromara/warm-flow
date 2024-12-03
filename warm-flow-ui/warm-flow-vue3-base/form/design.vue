<template>
  <div class="container" ref="container">
    <fc-designer class="fcDesigner" ref="designer" :config="config" @save="handleSave"/>
  </div>
</template>

<script>
import { listDefinition, saveFormContent } from '../api/form/from';

export default {
  name: 'formDesign',
  data() {
    return {
      config: {
        //是否显示保存按钮
        showSaveBtn: true,
        //字段ID是否可编辑
        fieldReadonly: false
      }
    }
  },
  created() {
    this.definitionId = this.$route.params && this.$route.params.id;
    if (this.definitionId) this.getInfo();
  },
  methods: {
    handleSave() {
      //获取表单的生成规则
      const ruleJson = this.$refs.designer.getJson();
      //获取表单的配置
      const optionsJson =  JSON.stringify(this.$refs.designer.getOption());
      let data = {
        formContent: JSON.stringify({
          rule: formCreate.parseJson(ruleJson),
          option: formCreate.parseJson(optionsJson)
        }),
        id: this.definitionId
      }
      saveFormContent(data).then(response => {
        this.$modal.msgSuccess("保存成功");
        if (response.code === 200) {
          const obj = { path: "/form/definition", query: { t: Date.now(), pageNum: this.$route.query.pageNum } };
          this.$tab.closeOpenPage(obj);
        }
      });
    },
    // 获取详情
    getInfo() {
      listDefinition({ id: this.definitionId }).then(res => {
        let formContent = res.rows[0].formContent;
        if (formContent) {
          this.$nextTick(() => {
            formContent = JSON.parse(formContent);
            if (formContent.rule) this.$refs.designer.setRule(formContent.rule);
            if (formContent.option) this.$refs.designer.setOption(formContent.option);
          });
        }
      });
    }
  }
};
</script>

<style scoped lang="scss">
::v-deep.container {
  width: 100%;
  .fcDesigner {
    height: calc(100vh - 90px);
    .el-aside {
      padding: 0;
      background: #ffffff;
      height: 100%;
    }
  }
}
</style>

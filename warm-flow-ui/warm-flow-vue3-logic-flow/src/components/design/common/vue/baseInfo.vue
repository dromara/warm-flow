<template>
  <div class="app-container">
    <el-form ref="formRef" :model="form" class="dialogForm" :rules="rules" label-width="150px" :disabled="disabled">
      <el-form-item label="流程编码" prop="flowCode">
        <el-input v-model="form.flowCode" placeholder="请输入流程编码" maxlength="40" show-word-limit />
      </el-form-item>

      <el-form-item label="流程名称" prop="flowName">
        <el-input v-model="form.flowName" placeholder="请输入流程名称" maxlength="100" show-word-limit @input="nameChange" />
      </el-form-item>

      <el-form-item label="设计器模型" prop="modelValue">
        <el-radio-group v-model="form.modelValue" :disabled="!!definitionId">
          <el-radio label="CLASSICS">经典模型</el-radio>
          <el-radio label="MIMIC">仿钉钉模型
            <span style="color: #ff4949; margin-left: 50px;">切换后重置节点，保存后不支持修改！</span>
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="流程类别" prop="category">
        <el-tree-select
            v-model="form.category"
            :data="categoryList"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            value-key="id"
            placeholder="请选择流程类别"
            check-strictly
        />
      </el-form-item>

      <el-form-item label="审批表单是否自定义" prop="formCustom">
        <el-select v-model="form.formCustom" clearable>
          <el-option label="表单路径" value="N"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="审批表单路径" prop="formPath" v-if="form.formCustom === 'N'">
        <el-input v-model="form.formPath" placeholder="请输入审批表单路径" maxlength="100" show-word-limit />
      </el-form-item>

      <el-form-item label="审批流程表单" v-else-if="form.formCustom === 'Y'">
        <el-select v-model="form.formPath">
          <el-option v-for="item in definitionList" :key="item.id" :label="`${item.formName} - v${item.version}`" :value="item.id"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item prop="listenerRows" class="listenerItem">
        <el-table :data="form.listenerRows" style="width: 100%">
          <el-table-column prop="listenerType" width="150" label="类型">
            <template #default="scope">
              <el-form-item :prop="`listenerRows.${scope.$index}.listenerType`" :rules="rules.listenerType">
                <el-select v-model="scope.row.listenerType" placeholder="请选择类型">
                  <el-option label="开始" value="start"></el-option>
                  <el-option label="分派" value="assignment"></el-option>
                  <el-option label="完成" value="finish"></el-option>
                  <el-option label="创建" value="create"></el-option>
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column prop="listenerPath" label="路径">
            <template #default="scope">
              <el-form-item :prop="`listenerRows.${scope.$index}.listenerPath`" :rules="rules.listenerPath">
                <el-input v-model="scope.row.listenerPath" placeholder="请输入路径" />
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="65" v-if="!disabled">
            <template #default="scope">
              <el-button size="small" type="danger" icon="Delete" @click="handleDeleteRow(scope.$index)" />
            </template>
          </el-table-column>
        </el-table>
        <el-button v-if="!disabled" style="margin-top: 10px;" type="primary" @click="handleAddRow">增加行</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup name="BaseInfo">
import { ref } from "vue";

const { proxy } = getCurrentInstance();
const props = defineProps({
  disabled: { // 是否禁止
    type: Boolean,
    default: false
  },
  logicJson: {
    type: Object,
    default () {
      return {}
    }
  },
  categoryList: {
    type: Array,
    default () {
      return []
    }
  },
  definitionId: {
    type: String,
    default () {
      return null
    }
  },
});

const form = ref({
  id: null,
  flowCode: "",
  flowName: "",
  modelValue: "",
  category: "",
  formCustom: "N",
  formPath: "",
  listenerRows: []
});

watch(() => props.logicJson, newValue => {
  if (newValue && Object.keys(newValue).length > 0) {
    Object.assign(form.value, newValue);
    setListenerData();
  }
});

const definitionList = ref([]);
const rules = {
  modelValue: [
    { required: true, message: "设计器模型不能为空", trigger: "blur" }
  ],
  flowCode: [
    { required: true, message: "流程编码不能为空", trigger: "blur" }
  ],
  flowName: [
    { required: true, message: "流程名称不能为空", trigger: "blur" }
  ],
  formCustom: [
    { required: true, message: "请选择审批表单是否自定义", trigger: "change" }
  ],
  listenerType: [
    { required: true, message: '监听器不能为空', trigger: ['change', 'blur'] }
  ],
  listenerPath: [
    { required: true, message: '监听器不能为空', trigger: ['change', 'blur'] }
  ]
};

// 表单引用（用于校验）
const formRef = ref();

function setListenerData() {
  // 处理监听器数据
  if (form.value.listenerType) {
    const listenerTypes = form.value.listenerType.split(",");
    const listenerPaths = form.value.listenerPath.split("@@");
    form.value.listenerRows = listenerTypes.map((type, index) => ({
      listenerType: type,
      listenerPath: listenerPaths[index]
    }));
  } else {
    form.value.listenerRows = [];
  }
}

// 增加行
function handleAddRow() {
  form.value.listenerRows.push({ listenerType: "", listenerPath: "" });
  formRef.value?.clearValidate("listenerRows");
}

// 删除行
function handleDeleteRow(index) {
  form.value.listenerRows.splice(index, 1);
}

// 表单必填校验
function validate() {
  return new Promise((resolve, reject) => {
    proxy.$nextTick(() => {
      proxy.$refs.formRef.validate(valid => {
        if (valid) {
          resolve(true);
        } else {
          resolve(false);
        }
      });
    });
  });
}

function nameChange(flowName) {
  // 可以在这里添加额外的逻辑，比如验证或格式化
  props.logicJson.flowName = flowName; // 更新 logicJson 中的流程名称
  proxy.$emit('update:flow-name', flowName); // 如果需要通知父组件
}

function getFormData() {
  return form.value;
}

defineExpose({ getFormData, validate });

</script>

<style scoped lang="scss">
:deep(.Tabs) {
  margin-top: -20px;
  box-shadow: none;
  border-bottom: 0;
  .el-tabs__content {
    display: none;
  }
  .el-tabs__item.is-active {
    margin-left: 0;
    border-top: 1px solid var(--el-border-color);
    margin-top: 0;
  }
}
.dialogForm {
  border-top: 0;
  border-top: 0;
  padding: 15px;
  width: 1000px;
  margin: 50px auto;
}
:deep(.listenerItem) {
  .el-form-item__label {
    float: none;
    display: inline-block;
    text-align: left;
  }
  .el-form-item__content {
    margin-left: 0 !important;
  }
}
</style>

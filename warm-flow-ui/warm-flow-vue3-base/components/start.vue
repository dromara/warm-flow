<template>
  <div>
    <el-tabs type="border-card" class="Tabs" v-model="tabsValue">
      <el-tab-pane label="基础设置" name="1"></el-tab-pane>
      <el-tab-pane label="监听器" name="2"></el-tab-pane>
    </el-tabs>
    <el-form ref="formRef" class="startForm" :model="form" label-width="120px" size="small" :disabled="disabled">
      <template v-if="tabsValue === '1'">
        <slot name="form-item-task-name" :model="form" field="nodeCode">
          <el-form-item label="节点编码">
            <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
          </el-form-item>
        </slot>
        <slot name="form-item-task-name" :model="form" field="nodeName">
          <el-form-item label="节点名称">
            <el-input v-model="form.nodeName" ref="nodeInput" :disabled="disabled" @change="nodeNameChange"></el-input>
          </el-form-item>
        </slot>
      </template>
      <template v-if="tabsValue === '2'">
        <slot name="form-item-task-listenerType" :model="form" field="listenerType">
          <el-form-item prop="listenerRows" class="listenerItem" label-width="0">
            <el-table :data="form.listenerRows" style="width: 100%">
              <el-table-column prop="listenerType" label="类型" width="80">
                <template #default="scope">
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerType'">
                    <el-select v-model="scope.row.listenerType" placeholder="请选择">
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
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerPath'">
                    <el-input v-model="scope.row.listenerPath" placeholder="请输入"></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="55" v-if="!disabled">
                <template #default="scope">
                  <el-button type="danger" :icon="Delete" @click="handleDeleteRow(scope.$index)"/>
                </template>
              </el-table-column>
            </el-table>
            <el-button v-if="!disabled" type="primary" style="margin-top: 10px;" @click="handleAddRow">增加行</el-button>
          </el-form-item>
        </slot>
      </template>
    </el-form>
  </div>
</template>

<script setup name="Start">
import { Delete } from '../../warm-flow-vue3-logic-flow/node_modules/@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default () {
      return {}
    }
  },
  disabled: { // 是否禁止
    type: Boolean,
    default: false
  },
});

const tabsValue = ref("1");
const form = ref(props.modelValue);
const emit = defineEmits(["change"]);
const { proxy } = getCurrentInstance();

watch(() => form, n => {
  if (n) {
    emit('change', n)
  }
},{ deep: true });

function nodeNameChange() {
  proxy.$refs.nodeInput.focus();
}

if (form.value.listenerType) {
  const listenerTypes = form.value.listenerType.split(",");
  const listenerPaths = form.value.listenerPath.split("@@");
  form.value.listenerRows = listenerTypes.map((type, index) => ({
    listenerType: type,
    listenerPath: listenerPaths[index]
  }));
}

// 增加行
function handleAddRow() {
  form.value.listenerRows.push({ listenerType: '', listenerPath: '' });
}

// 删除行
function handleDeleteRow(index) {
  form.value.listenerRows.splice(index, 1);
}
</script>

<style scoped lang="scss">
:deep(.Tabs) {
  border: 0;
  margin-top: -20px;
  .el-tabs__content {
    display: none;
  }
  .el-tabs__item.is-active {
    margin-left: 0;
    border-top: 1px solid var(--el-border-color);
    margin-top: 0;
  }
}
.startForm {
  border: 1px solid #e4e7ed;
  border-top: 0;
  padding: 15px;
}
</style>

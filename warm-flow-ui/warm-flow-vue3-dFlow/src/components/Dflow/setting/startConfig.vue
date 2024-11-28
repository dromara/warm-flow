<template>
  <div class="startConfig">
    <el-tabs type="border-card" class="demo-tabs" v-model="tabsValue">
      <el-form ref="formRef" :model="form" size="small" :disabled="disabled" :rules="rules">
        <el-tab-pane label="监听器" name="1">
          <el-form-item prop="listenerRows" class="listenerItem" label-width="0">
            <el-table :data="form.listenerRows" style="width: 100%">
              <el-table-column prop="listenerType" label="类型" width="80">
                <template #default="scope">
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerType'">
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
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerPath'">
                    <el-input v-model="scope.row.listenerPath" placeholder="请输入路径"></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="55" v-if="!disabled">
                <template #default="scope">
                  <el-button type="danger" :icon="Delete" @click="handleDeleteRow(scope.$index)" />
                </template>
              </el-table-column>
            </el-table>
            <el-button v-if="!disabled" type="primary" style="margin-top: 10px;" @click="handleAddRow">增加行</el-button>
          </el-form-item>
        </el-tab-pane>
      </el-form>
    </el-tabs>
  </div>
</template>

<script setup>
import { Delete } from '@element-plus/icons-vue'
import { defineProps, defineExpose, toRefs, watch, ref, reactive } from 'vue';
const props = defineProps({
  data: {},
  disabled: { // 是否禁止
    type: Boolean,
    default: false
  }
})
const tabsValue = ref('1');

const form = reactive({
  listenerRows: []
})

const rules = ref({})

const { data } = toRefs(props);
watch(() => data, (newv, oldv) => {
  alert(newv);
})

Object.assign(form, data.value.properties);

// 增加行
function handleAddRow() {
  form.listenerRows.push({ listenerType: '', listenerPath: '' });
}

// 删除行
function handleDeleteRow(index) {
  form.listenerRows.splice(index, 1);
}

const formConfig = () => {
  return new Promise((resolve, reject) => {
    resolve(form);
  });
}

defineExpose({
  formConfig
})
</script>

<style scoped lang="scss">
.demo-tabs {
  margin-top: -20px;
}
</style>

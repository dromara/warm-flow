<template>
  <div class="between">
    <el-tabs type="border-card" class="Tabs" v-model="tabsValue">
      <el-tab-pane label="基础设置" name="1"></el-tab-pane>
      <el-tab-pane label="监听器" name="2"></el-tab-pane>
    </el-tabs>
    <el-form ref="formRef" class="betweenForm" :model="form" label-width="135px" size="small" :rules="rules" :disabled="disabled">
      <template v-if="tabsValue === '1'">
        <slot name="form-item-task-nodeCode" :model="form" field="nodeCode">
          <el-form-item label="节点编码">
            <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
          </el-form-item>
        </slot>
        <slot name="form-item-task-nodeName" :model="form" field="nodeName">
          <el-form-item label="节点名称">
            <el-input v-model="form.nodeName" :disabled="disabled"></el-input>
          </el-form-item>
        </slot>
        <slot name="form-item-task-collaborativeWay" :model="form" field="collaborativeWay">
          <el-form-item label="协作方式">
            <el-radio-group v-model="form.collaborativeWay">
              <el-radio label="1" v-if="form.collaborativeWay ==='1'">
                <span class="flex-hc">
                  或签
                  <el-tooltip class="box-item" effect="dark" content="只需一个人审批">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
              </el-radio>
              <el-radio label="2" v-if="form.collaborativeWay ==='2'">
                <span class="flex-hc">
                  票签
                  <el-tooltip class="box-item" effect="dark" content="部分办理人审批，只支持选择用户">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
              </el-radio>
              <el-radio label="3" v-if="form.collaborativeWay ==='3'">
                <span class="flex-hc">
                  会签
                  <el-tooltip class="box-item" effect="dark" content="所有办理都需要审批，只支持选择用户">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </slot>
        <slot name="form-item-task-nodeRatio" :model="form" field="nodeRatio" v-if="form.collaborativeWay === '2'">
          <el-form-item label="票签占比" prop="nodeRatio">
            <el-input v-model="form.nodeRatio" type="number" placeholder="请输入"></el-input>
            <div class="placeholder mt5">票签比例范围：(0-100)的值</div>
          </el-form-item>
        </slot>
        <slot name="form-item-task-permissionFlag" :model="form" field="permissionFlag">
          <el-form-item label="办理人输入" class="permissionItem">
            <div v-for="(tag, index) in form.permissionFlag" :key="index" class="inputGroup">
              <el-input v-model="form.permissionFlag[index]" style="width: 200px;"></el-input>
              <Close class="Icon" v-if="form.permissionFlag.length !== 1 && !disabled" @click="delPermission(index)" />
              <Plus class="Icon" v-if="(index === form.permissionFlag.length - 1) && !disabled" @click="addPermission" />
              <el-button class="btn" v-if="(index === form.permissionFlag.length - 1) && !disabled" @click="initUser">选择</el-button>
            </div>
          </el-form-item>
        </slot>
        <slot name="form-item-task-formCustom" :model="form" field="formCustom">
          <el-form-item label="驳回到指定节点">
            <template #label>
              <span v-if="form.collaborativeWay === '2'"  class="mr5" style="color: red;">*</span>驳回到指定节点
            </template>
            <el-select v-model="form.anyNodeSkip" STYLE="width: 80%">
              <el-option label="" :value="''"></el-option>
              <el-option
                  v-for="dict in nodes"
                  :key="dict.nodeCode"
                  :label="dict.nodeName"
                  :value="dict.nodeCode"
              />
            </el-select>
            <div class="placeholder mt5">票签必须选择驳到指定节点！</div>
          </el-form-item>
        </slot>
        <slot name="form-item-task-formCustom" :model="form" field="formCustom">
          <el-form-item label="审批表单是否自定义">
            <el-select v-model="form.formCustom">
              <el-option label="使用流程表单" :value="''"></el-option>
              <!-- <el-option label="节点自定义表单" value="Y"></el-option> -->
              <el-option label="节点表单路径" value="N"></el-option>
            </el-select>
          </el-form-item>
        </slot>
        <slot name="form-item-task-formPath" :model="form" field="formPath" v-if="form.formCustom === 'N'">
          <el-form-item label="审批表单路径">
            <el-input v-model="form.formPath"></el-input>
          </el-form-item>
        </slot>
      </template>
      <template v-if="tabsValue === '2'">
        <slot name="form-item-task-listenerType" :model="form" field="listenerType">
          <el-form-item prop="listenerRows" class="listenerItem">
            <el-table :data="form.listenerRows" style="width: 100%">
              <el-table-column prop="listenerType" label="类型" width="90">
                <template #default="scope">
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerType'" :rules="rules.listenerType">
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
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerPath'" :rules="rules.listenerPath">
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

    <!-- 权限标识：会签票签选择用户 -->
    <el-dialog title="办理人选择" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
      <selectUser v-model:selectUser="form.permissionFlag" v-model:userVisible="userVisible" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script setup name="Between">
import selectUser from "@/views/components/selectUser";
import { Delete } from '@element-plus/icons-vue'
import {previousNodeList} from "../../../api/flow/definition.js";

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
  definitionId: {
    type: Number,
    default () {
      return []
    }
  }
});

const tabsValue = ref("1");
const form = ref(props.modelValue);
const nodes = ref([]);
const userVisible = ref(false);

const rules = reactive({
  nodeRatio: [
    { required: false, message: "请输入", trigger: "change" },
    { pattern: /^(?:[1-9]\d?|0\.\d{1,3}|[1-9]\d?\.\d{1,3})$/, message: "请输入(0, 100)的值，最多保留三位小数", trigger: ["change", "blur"] }
  ],
  listenerType: [{ required: true, message: '监听器类型不能为空', trigger: 'change' }],
  listenerPath: [{ required: true, message: '监听器路径不能为空', trigger: 'blur' }]
});

const emit = defineEmits(["change"]);

watch(() => form, n => {
  if (n) {
    emit('change', n)
  }
},{ deep: true });

// 删除办理人
function delPermission(index) {
  form.value.permissionFlag.splice(index, 1);
}
// 添加办理人
function addPermission() {
  form.value.permissionFlag.push("");
}

/** 选择角色权限范围触发 */
function getPreviousNodeList() {
  previousNodeList(props.definitionId, form.value.nodeCode).then(res => {
      if (res && res.data) {
        nodes.value = res.data
      }
  })

}

/** 选择角色权限范围触发 */
function getPermissionFlag() {
  form.value.permissionFlag = form.value.permissionFlag ? form.value.permissionFlag.split(",") : [""];
  if (form.value.listenerType) {
    const listenerTypes = form.value.listenerType.split(",");
    const listenerPaths = form.value.listenerPath.split("@@");
    form.value.listenerRows = listenerTypes.map((type, index) => ({
      listenerType: type,
      listenerPath: listenerPaths[index]
    }));
  }
}

// 打开用户选择弹窗
function initUser() {
  userVisible.value = true;
}

// 获取选中用户数据
function handleUserSelect(checkedItemList) {
  form.value.permissionFlag = checkedItemList.map(e => {
    return e.storageId;
  }).filter(n => n);
}

// 增加行
function handleAddRow() {
  form.value.listenerRows.push({ listenerType: '', listenerPath: '' });
}

// 删除行
function handleDeleteRow(index) {
  form.value.listenerRows.splice(index, 1);
}

getPermissionFlag();
getPreviousNodeList();
</script>

<style scoped lang="scss">
.dialogSelect {
  display: none;
}
.placeholder {
  color: #828f9e;
  font-size: 12px;
}
:deep(.listenerItem) {
  display: inline-block;
  width: 100%;
  .el-form-item__label {
    float: none;
    display: inline-block;
    text-align: left;
  }
  .el-form-item__content {
    margin-left: 0 !important;
  }
}
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
.betweenForm {
  border: 1px solid #e4e7ed;
  border-top: 0;
  padding: 15px;
}
:deep(.permissionItem) {
  .el-form-item__content {
    display: block;
  }
  .inputGroup {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    .el-input {
      width: 120px;
    }
    .Icon {
      width: 14px;
      height: 14px;
      margin-left: 5px;
      cursor: pointer;
      color: #fff;
      background: #94979d;
      border-radius: 50%;
      padding: 2px;
      &:hover {
        background: #f0f2f5;
        color: #94979d;
      }
    }
    .btn {
      height: 18px;
      margin-left: 5px;
      color: #fff;
      background: #94979d;
      border-radius: 5px;
      padding: 6px;
      font-size: 10px;
      border: 0;
      &:hover {
        background: #f0f2f5;
        color: #94979d;
      }
    }
  }
}
</style>

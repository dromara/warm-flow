<template>
  <div class="between">
    <el-form ref="formRef" :model="form" label-width="120px" size="small" :rules="rules" :disabled="disabled">
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
          <div class="placeholder">票签比例范围：(0-100)的值</div>
        </el-form-item>
      </slot>
      <slot name="form-item-task-permissionFlag" :model="form" field="permissionFlag">
        <el-form-item label="办理人输入" class="permissionItem">
          <div v-for="(tag, index) in form.permissionFlag" :key="index" class="inputGroup">
            <el-input v-model="form.permissionFlag[index]"></el-input>
            <Close class="Icon" v-if="form.permissionFlag.length !== 1" @click="delPermission(index)" />
            <Plus class="Icon" v-if="index === form.permissionFlag.length - 1" @click="addPermission" />
            <el-button class="btn" v-if="index === form.permissionFlag.length - 1" @click="initUser">选择</el-button>
          </div>
        </el-form-item>
      </slot>
      <slot name="form-item-task-skipAnyNode" :model="form" field="skipAnyNode">
        <el-form-item label="是否可以跳转任意节点">
          <el-radio-group v-model="form.skipAnyNode">
            <el-radio label="N">否</el-radio>
            <el-radio label="Y">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </slot>
      <slot name="form-item-task-listenerType" :model="form" field="listenerType">
        <el-form-item label="监听器类型">
          <el-select v-model="form.listenerType" multiple>
            <el-option label="任务创建" value="create"></el-option>
            <el-option label="任务开始办理" value="start"></el-option>
            <el-option label="分派监听器" value="assignment"></el-option>
            <el-option label="权限认证" value="permission"></el-option>
            <el-option label="任务完成" value="finish"></el-option>
          </el-select>
        </el-form-item>
      </slot>
      <slot name="form-item-task-listenerPath" :model="form" field="listenerPath">
        <el-form-item label="监听器路径" description="输入监听器的路径，以@@分隔,顺序与监听器类型一致">
          <el-input type="textarea" v-model="form.listenerPath" rows="8"></el-input>
          <el-tooltip class="box-item" effect="dark" content="输入监听器的路径，以@@分隔，顺序与监听器类型一致">
            <el-icon :size="14" class="mt5">
              <WarningFilled />
            </el-icon>
          </el-tooltip>
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
    </el-form>

    <!-- 权限标识：会签票签选择用户 -->
    <el-dialog title="办理人选择" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
      <selectUser v-model:selectUser="form.permissionFlag" v-model:userVisible="userVisible" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script setup name="Between">
import selectUser from "@/views/components/selectUser";
const { proxy } = getCurrentInstance();

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

const form = ref(props.modelValue);
const rules = reactive({
  nodeRatio: [
    { required: false, message: "请输入", trigger: "change" },
    { pattern: /^(?:[1-9]\d?|0\.\d{1,3}|[1-9]\d?\.\d{1,3})$/, message: "请输入(0, 100)的值，最多保留三位小数", trigger: ["change", "blur"] }
  ]
});
const userVisible = ref(false);
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
function getPermissionFlag() {
  form.value.permissionFlag = form.value.permissionFlag ? form.value.permissionFlag.split(",") : [""];
  if (form.value.listenerType) {
    form.value.listenerType = form.value.listenerType.split(",")
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

getPermissionFlag();
</script>

<style scoped lang="scss">
.dialogSelect {
  display: none;
}
.placeholder {
  color: #828f9e;
  font-size: 12px;
}
::v-deep.permissionItem {
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

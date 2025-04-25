<template>
  <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick" type="border-card" stretch>
    <el-form :model="nodeConfig" label-width="auto" size="small" :disabled="disabled">
      <el-tab-pane label="基础设置" name="first" style>
        <el-form-item label="协作方式" prop="nodeRatioType">
          <el-radio-group v-model="nodeConfig.nodeRatioType">
            <el-radio label="1">
              <span class="flex-hc">
                或签
                <el-tooltip class="box-item" effect="dark" content="只需一个人审批">
                  <el-icon :size="14" class="ml5">
                    <WarningFilled />
                  </el-icon>
                </el-tooltip>
              </span>
            </el-radio>
            <el-radio label="2">
              <span class="flex-hc">
                票签
                <el-tooltip class="box-item" effect="dark" content="部分办理人审批，只支持选择用户">
                  <el-icon :size="14" class="ml5">
                    <WarningFilled />
                  </el-icon>
                </el-tooltip>
              </span>
            </el-radio>
            <el-radio label="3">
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
        <el-form-item label="票签占比" prop="nodeRatio" v-show="nodeConfig.nodeRatioType == '2'">
          <el-tooltip class="box-item" effect="dark" content="票签比例范围：(0-100)的值">
            <el-input-number v-model="nodeConfig.nodeRatio" class="mx-4" :min="0" :max="100"
              controls-position="right" />
            <el-icon :size="14" class="mt5">
              <WarningFilled />
            </el-icon>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="办理人列表" class="permissionItem">
          <div v-for="(tag, index) in nodeConfig.permissionFlag" :key="index" class="inputGroup">
            <el-input v-model="nodeConfig.permissionFlag[index]"></el-input>
            <Close class="Icon" v-if="nodeConfig.permissionFlag.length !== 1 && !disabled"
              @click="delPermission(index)" />
            <Plus class="Icon" v-if="(index === nodeConfig.permissionFlag.length - 1) && !disabled"
              @click="addPermission" />
            <el-button class="btn" v-if="(index === nodeConfig.permissionFlag.length - 1) && !disabled"
              @click="initUser">选择</el-button>
          </div>
        </el-form-item>
        <el-form-item label="审批表单是否自定义">
          <el-select v-model="nodeConfig.formCustom">
            <el-option label="使用流程表单" :value="''"></el-option>
            <!-- <el-option label="节点自定义表单" value="Y"></el-option> -->
            <el-option label="节点表单路径" value="N"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审批表单路径" v-if="nodeConfig.formCustom === 'N'">
          <el-input v-model="nodeConfig.formPath"></el-input>
        </el-form-item>
      </el-tab-pane>
      <el-tab-pane label="监听器" name="third"><el-form-item prop="listenerRows" class="listenerItem" label-width="0">
          <el-table :data="nodeConfig.listenerRows" style="width: 100%">
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
  <el-dialog title="办理人选择" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
    <selectUser v-model:selectUser="nodeConfig.permissionFlag" v-model:userVisible="userVisible"
      @handleUserSelect="handleUserSelect"></selectUser>
  </el-dialog>

</template>

<script setup>
import { Delete } from '@element-plus/icons-vue'
import { defineProps, toRefs, watch, ref, onMounted, reactive } from 'vue';
import selectUser from "@/views/components/selectUser";
const roleSelectRef = ref(null);
const userSelectRef = ref(null);
const emptySelectRef = ref(null)
const props = defineProps({
  data: {},
  backNodeList: [],  // 回退节点选择列表
})

const userVisible = ref(false);

const nodeConfig = reactive({
  nodeRatioType: "1",
  backType: '1',
  nodeRatio: 0,
  permissionFlag: [""],
  formCustom: "",
  formPath: "",
  listenerRows: [],
  value: ''
})

// 协作方式值
watch(() => nodeConfig.nodeRatioType, (newV, oldv) => {
  if (newV == '1') {
    nodeConfig.nodeRatio = 0;
  } else if (newV == '3') {
    nodeConfig.nodeRatio = 100;
  } else {
    nodeConfig.nodeRatio = 70;
  }
})

const initUser = () => {
  userVisible.value = true;
}

const handleUserSelect = (checkedItemList) => {
  nodeConfig.permissionFlag = checkedItemList.map(e => {
    return e.storageId;
  }).filter(n => n);
}
// 删除办理人
function delPermission(index) {
  nodeConfig.permissionFlag.splice(index, 1);
}
// 添加办理人
function addPermission() {
  nodeConfig.permissionFlag.push("");
}
/** 选择角色权限范围触发 */
function getPermissionFlag() {
  if (nodeConfig.listenerType) {
    const listenerTypes = nodeConfig.listenerType.split(",");
    const listenerPaths = nodeConfig.listenerPath.split("@@");
    nodeConfig.listenerRows = listenerTypes.map((type, index) => ({
      listenerType: type,
      listenerPath: listenerPaths[index]
    }));
  }
}

/** 监听器 */
// 增加行
function handleAddRow() {
  nodeConfig.listenerRows.push({ listenerType: '', listenerPath: '' });
}
// 删除行
function handleDeleteRow(index) {
  nodeConfig.listenerRows.splice(index, 1);
}

// 流程图节点中显示的内容
const showValue = () => {
  let value = '';
  if (nodeConfig.permissionFlag) {
    const permissionFlags = nodeConfig.permissionFlag
    for (const i in permissionFlags) {
      value += permissionFlags[i] + ",";
    }
  }
  nodeConfig.value = value;
}

const activeName = ref('first');

const { data, backNodeList } = toRefs(props);

// 页面打开时， 参数初始化
Object.assign(nodeConfig, data.value.properties);


// 关闭页面时的验证和处理。
const formConfig = async () => {
  return new Promise((resolve, reject) => {
    showValue();
    if (nodeConfig.backTypeNode == null) {
      nodeConfig.backTypeNode = backNodeList.value[0].id;
    }
    resolve(nodeConfig);
    // reject();
  })
}

getPermissionFlag();

defineExpose({
  formConfig
})


</script>

<style scoped lang="scss">
.demo-tabs {
  margin-top: -20px;
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
      width: 160px !important;
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

.flow-user-select-checkbox {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  gap: 5px;
}

.flow-user-selected {
  display: flex;
  align-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  width: 100%;

}

.flow-operate-setting {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>

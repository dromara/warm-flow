<template>
  <div class="between">
    <el-form ref="formRef" class="betweenForm" :model="form" label-width="120px" size="small" :rules="rules" :disabled="disabled" label-position="left">
      <el-tabs type="border-card" class="Tabs" v-model="tabsValue" @tab-change="handleTabChange">
        <el-tab-pane v-for="item in tabsList" :key="item.name" :label="item.label" :name="item.name">
        </el-tab-pane>
      </el-tabs>
      <div v-if="tabsValue === '1'" class="tabPane">
        <el-form-item label="节点编码：" prop="nodeCode">
          <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="节点名称：" prop="nodeName">
          <el-input v-model="form.nodeName" type="textarea" :disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="协作方式：" prop="collaborativeWay">
          <el-radio-group v-model="form.collaborativeWay">
            <el-radio label="1" v-if="form.collaborativeWay ==='1' || showWays">
                <span class="flex-hc">
                  或签
                  <el-tooltip class="box-item" effect="dark" placement="top" content="只需一个人审批">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
            </el-radio>
            <el-radio label="2" v-if="form.collaborativeWay ==='2' || showWays">
                <span class="flex-hc">
                  票签
                  <el-tooltip class="box-item" effect="dark"  placement="top"
                              content="部分办理人审批，建议选择用户；如果选择角色或者部门等，需自行通过办理人表达式或者监听器，转成具体办理用户">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
            </el-radio>
            <el-radio label="3" v-if="form.collaborativeWay ==='3' || showWays">
                <span class="flex-hc">
                  会签
                  <el-tooltip class="box-item" effect="dark" placement="top"
                              content="所有办理都需要审批，建议选择用户；如果选择角色或者部门等，需自行通过办理人表达式或者监听器，转成具体办理用户">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="票签策略：" prop="nodeRatio" v-if="form.collaborativeWay === '2'">
          <el-select v-model="form.nodeRatioType" placeholder="请选择策略类型" style="width: 25%"
                     clearable @clear="handleClear">
              <el-option label="通过率" value="passRatio"/>
              <el-option label="固定通过人数" value="passCount"/>
              <el-option label="固定驳回人数" value="rejectCount"/>
              <el-option label="默认表达式" value="default"/>
              <el-option label="spel表达式" value="spel"/>
          </el-select>
          <el-input v-model="form.nodeRatioValue" :placeholder="getNodeRatioDescription()" style="width: 74%; margin-left: 1%"/>
        </el-form-item>
        <el-form-item label="驳回到指定节点" prop="formCustom">
          <template #label>
            <span v-if="form.collaborativeWay === '2'"  class="mr5" style="color: red;">*</span>驳回到指定节点
          </template>
          <el-select v-model="form.anyNodeSkip" style="width: 80%" clearable>
            <el-option
                v-for="dict in filteredNodes"
                :key="dict.id"
                :label="dict.text.value"
                :value="dict.id"
            />
          </el-select>
          <div class="placeholder mt5">【票签】必须选择驳到指定节点！</div>
        </el-form-item>
        <el-form-item label="自定义表单：" prop="formCustom">
            <el-radio-group v-model="form.formCustom">
                <el-radio label="N">
              <span class="flex-hc">
                  否
                  <el-tooltip class="box-item" effect="dark" placement="top"
                              content="填写页面地址：如system/process/approve">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
                </el-radio>
                <el-radio label="Y">
              <span class="flex-hc">
                  是
                  <el-tooltip class="box-item" effect="dark" placement="top"
                              content="填写自定义表单的唯一标识：如formCode+version">
                    <el-icon :size="14" class="ml5">
                      <WarningFilled />
                    </el-icon>
                  </el-tooltip>
                </span>
                </el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="表单路径：" prop="formPath" v-if="form.formCustom === 'N'">
          <el-input v-model="form.formPath"></el-input>
        </el-form-item>
        <el-form-item label="自定义表单唯一标识：" prop="formPath" v-else-if="form.formCustom === 'Y'">
            <el-tree-select
                v-model="form.formPath"
                :data="formPathList"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择流程类别"
                check-strictly/>
        </el-form-item>
        <el-divider content-position="center"/>
        <nodeExtList v-if="baseList.length > 0" ref="nodeBase" v-model="form.ext" :formList="baseList" :disabled="disabled"></nodeExtList>
      </div>
      <div v-else-if="tabsValue === '2'" class="tabPane">
          <el-form-item class="listenerItem" prop="permissionFlag">
              <el-table :data="permissionRows" style="width: 100%;margin-top: 10px;" class="inputGroup">
                  <el-table-column prop="storageId" label="入库主键" width="250">
                      <template #default="scope">
                          <el-form-item prop="storageId">
                              <el-input v-model="scope.row.storageId" style="width: 100%;" @blur="event => inputBlur(event, scope.$index)"></el-input>
                          </el-form-item>
                      </template>
                  </el-table-column>
                  <el-table-column prop="handlerName" label="权限名称"></el-table-column>
                  <el-table-column label="操作" width="55" v-if="!disabled">
                      <template #default="scope">
                          <el-button type="danger" v-if="!disabled" :icon="Delete" @click="delPermission(scope.$index)"/>
                      </template>
                  </el-table-column>
              </el-table>
              <el-button type="primary" v-if="!disabled" @click="addPermission">添加行</el-button>
              <el-button type="primary" v-if="!disabled" @click="initUser">选择</el-button>
          </el-form-item>
      </div>
      <div v-else-if="tabsValue === '3'" class="tabPane">
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
      </div>
      <div v-else class="tabPane">
        <nodeExtList v-if="buttonList[tabsValue].length > 0" :ref="`nodeExtList_${tabsValue}`" v-model="form.ext" :formList="buttonList[tabsValue]" :disabled="disabled"></nodeExtList>
      </div>
    </el-form>

    <!-- 权限标识：会签票签选择用户 -->
    <el-dialog title="人员选择" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
      <selectUser v-model:selectUser="form.permissionFlag" v-model:userVisible="userVisible" :permissionRows="permissionRows" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script setup name="Between">
import selectUser from "./selectUser";
import { Delete } from '@element-plus/icons-vue'
import {publishedList, handlerDict, nodeExt, handlerFeedback} from "@/api/flow/definition";
import nodeExtList from "./nodeExtList";
import {getPreviousNodes} from "@/components/design/common/js/tool.js";
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
  // 是否展示所有协作方式
  showWays: {
    type: Boolean,
    default: true
  },
  nodes: {
    type: Array,
    default () {
      return []
    }
  },
  skips: {
    type: Array,
    default () {
      return []
    }
  },
  formPathList: {
      type: Array,
      default () {
          return []
      }
  },
});

const tabsValue = ref("1");
const tabsList = ref([
  { label: "基础设置", name: "1" },
  { label: "办理人设置", name: "2" },
  { label: "监听器", name: "3" },
]);
const form = ref(props.modelValue);
const userVisible = ref(false);

//基础设置扩展属性
const baseList = ref([]);
//按钮权限
const buttonList = ref({});

const rules = reactive({
    nodeRatio: computed(() => {
        const type = form.value.nodeRatioType;
        if (!type) return [];

        if (type === 'passRatio') {
            console.log('passRatio')
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validatePassRatio, trigger: ["change", "blur"]}
            ];
        } else if (['passCount', 'rejectCount'].includes(type)) {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validatePassCount, trigger: ["change", "blur"]}
            ];
        } else if (type === 'default') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateDefault, trigger: ["change", "blur"]}
            ];
        }  else if (type === 'spel') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateSpel, trigger: ["change", "blur"]}
            ];
        }
        // 其他类型不作限制
        return [];
    }),
    listenerType: [{required: true, message: '监听器类型不能为空', trigger: 'change'}],
    listenerPath: [{required: true, message: '监听器路径不能为空', trigger: 'blur'}]
});

const definitionList = ref([]); // 流程表单列表
const dictList = ref(); // 办理人选项
const permissionRows = ref([]); // 办理人表格
const emit = defineEmits(['update:modelValue']);

watch(() => form.value, n => {
  if (n) {
    emit('update:modelValue', n)
  }
  if (n.nodeRatioType) {
      let nodeRatio = '';
      if (/^passCount|rejectCount/.test(n.nodeRatioType)) {
          nodeRatio = n.nodeRatioType + "=";
      } else if (/^spel|default/.test(n.nodeRatioType)) {
          nodeRatio = n.nodeRatioType + "@@";
      }
      n.nodeRatio = nodeRatio + (n.nodeRatioValue ? n.nodeRatioValue : '')
  }
},{ deep: true });

function validatePassRatio(rule, value, callback) {
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入通过率'));
    } else {
        const numValue = Number(value);
        if (isNaN(numValue)) {
            callback(new Error('请输入有效数字'));
        } else if (numValue < 0.001 || numValue > 100) {
            callback(new Error('通过率必须在0.001-100之间'));
        } else if (!/^\d+(\.\d{1,3})?$/.test(value)) {
            callback(new Error('通过率最多保留三位小数'));
        }  else {
            callback();
        }
    }
}

function validatePassCount(rule, value, callback) {
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入固定人数'));
    } else {
        value = value.replace('passCount', '').replace('rejectCount', '').replace('=', '').trim();
        console.log('validatePassCount', value)
        const reg = /^[1-9]\d*$/;
        if (!reg.test(value)) {
            callback(new Error('请输入正整数'));
        } else {
            callback();
        }
    }
}

function validateDefault(rule, value, callback) {
    value = value.replace('default@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入默认表达式'));
    } else if (!/^\$\{.*\}$/.test(value)) {
        callback(new Error('默认表达式必须以${开头，以}结尾'));
    } else {
        callback();
    }
}

function validateSpel(rule, value, callback) {
    value = value.replace('spel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入spel表达式'));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error('spel表达式必须以#{开头，以}结尾'));
    } else {
        callback();
    }
}

function getNodeRatioDescription() {
    const type = form.value.nodeRatioType;
    switch (type) {
        case 'passRatio':
            return '请输入通过率(0.001-100)';
        case 'passCount':
            return '请输入固定通过人数，类型为正整数';
        case 'rejectCount':
            return '请输入固定驳回人数，类型为正整数';
        case 'default':
            return '请输入默认表达式,格式如: ${flag > 4}';
        case 'spel':
            return '请输入spel表达式，格式如: #{@user.eval(#flag)}';
        default:
            return '请输入';
    }
}

function handleClear() {
    // 处理清空操作的逻辑
    form.value.nodeRatioType = '';
    form.value.nodeRatioValue = '';
    form.value.nodeRatio = '';
}

// 删除办理人
function delPermission(index) {
  form.value.permissionFlag.splice(index, 1);
  permissionRows.value.splice(index, 1);
}
// 添加办理人
function addPermission() {
  form.value.permissionFlag.push("");
  permissionRows.value.push({ storageId: "", handlerName: "" });
}
// 办理人手动输入，失焦获取权限名称
function inputBlur(event, index) {
  form.value.permissionFlag[index] = event.target.value;
}

// 添加在其他函数后面
function handleTabChange(activeTabName) {
    // 可以根据不同的 tab 做相应处理
    switch(activeTabName) {
        case '1':
            // 基础设置 tab
            break;
        case '2':
            // 办理人设置 tab
            getHandlerFeedback();
            break;
        case '3':
            // 监听器 tab
            break;
        default:
            // 自定义 tab
            break;
    }
}

/** 选择角色权限范围触发 */
function getPermissionFlag() {
  form.value.permissionFlag = form.value.permissionFlag ? form.value.permissionFlag.split("@@") : [""];
  if (form.value.listenerType) {
    const listenerTypes = form.value.listenerType.split(",");
    const listenerPaths = form.value.listenerPath.split("@@");
    form.value.listenerRows = listenerTypes.map((type, index) => ({
      listenerType: type,
      listenerPath: listenerPaths[index]
    }));
  }
}

/** 查询表单定义列表 */
function getDefinition() {
  publishedList().then(response => {
    definitionList.value = response.data;
  });
}

/** 查询表单定义列表 */
function getHandlerDict() {
  handlerDict().then(response => {
    if (response.code === 200 && response.data) {
      dictList.value = response.data;
    }
  });
}


/** 办理人权限名称回显 */
async function getHandlerFeedback() {
  if (form.value.permissionFlag) {
    handlerFeedback({storageIds: form.value.permissionFlag}).then(response => {
      if (response.code === 200 && response.data) {
        permissionRows.value = response.data;
      }
    });
  }
}

/** 查询节点扩展属性 */
function getNodeExt() {
  nodeExt().then(response => {
    if (response.code === 200 && response.data) {
      response.data.forEach(e => {
        // 设置默认值
        e.childs.forEach(cItem => {
          if ([3, 4].includes(cItem.type)) {
            if (cItem?.dict) {
              if (form.value.ext?.[cItem.code] === undefined) {
                if (cItem.multiple) {
                  form.value.ext = Object.assign({}, form.value.ext, { [cItem.code]: [] });
                  cItem.dict.forEach(e => {
                    if (e.selected) form.value.ext[cItem.code].push(String(e.value));
                  });
                } else {
                  let value = cItem.dict.find(e => e.selected)?.value;
                  form.value.ext = Object.assign({}, form.value.ext, { [cItem.code]: value ? String(value) : null });
                }
              } else {
                if (cItem.multiple) {
                  // 处理为空、只选择一项的多选
                  let value = form.value.ext[cItem.code];
                  form.value.ext[cItem.code] = value
                    ? Array.isArray(value) ? value : value.split(",")
                    : [];
                }
              }
            }
          }
        });
        if (e.type === 1) baseList.value.push(...e?.childs);
        else if (e.type === 2) {
          tabsList.value.push({ label: e.name, name: e.code })
          buttonList.value[e.code] = e?.childs;
        }
      });
    }
  });
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

  // 办理人表格展示
  permissionRows.value = checkedItemList;
}

// 增加行
function handleAddRow() {
  form.value.listenerRows.push({ listenerType: '', listenerPath: '' });
}

// 删除行
function handleDeleteRow(index) {
  form.value.listenerRows.splice(index, 1);
}

const filteredNodes = computed(() => {
  let previousNodes = getPreviousNodes(props.nodes, props.skips, form.value.nodeCode)
  return previousNodes.filter(node => !["start", "serial", "parallel", 'inclusive'].includes(node.type));
});


getPermissionFlag();
// TODO form 开发中
// getDefinition();

// getHandlerDict();

getNodeExt();

// 表单必填校验
function validate() {
  return new Promise(async (resolve, reject) => {
    tabsValue.value = "1";
    await proxy.$nextTick();
    proxy.$refs.formRef.validate((valid) => {
      if (!valid) reject(false);
    });
    if (proxy.$refs.nodeBase && proxy.$refs.nodeBase.length > 0) {
      if (await proxy.$refs.nodeBase[0].validate()) {
        tabsValidate(resolve, reject);
      } else reject(false);
    } else tabsValidate(resolve, reject);
  });
}

async function tabsValidate(resolve, reject) {
  let addTabsList = tabsList.value.slice(tabsList.value.length);
  if (addTabsList.length === 0) resolve(true);
  // 切换页签做校验
  for (const e of addTabsList) {
    tabsValue.value = e.name;
    await proxy.$nextTick();
    if (!await proxy.$refs[`nodeExtList_${e.name}`][0].validate()) break;
    else if (e.name === addTabsList[addTabsList.length - 1].name) resolve(true);
  }
}

defineExpose({
  validate
})
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
    padding: 0;
  }
  .el-tabs__item.is-active {
    margin-left: 0;
    border-top: 1px solid var(--el-border-color);
    margin-top: 0;
  }
}
.tabPane {
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-top: 0;
}
.betweenForm {
  border-top: 0;
}
:deep(.permissionItem) {
  display: inline-block;
  width: 100%;
  .el-form-item__content {
    display: block;
  }
  .inputGroup {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
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

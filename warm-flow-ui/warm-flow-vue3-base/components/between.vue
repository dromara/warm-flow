<template>
  <div class="between">
    <el-form ref="formRef" class="betweenForm" :model="form" label-width="100px" size="small" :rules="rules" :disabled="disabled" label-position="left">
      <el-tabs type="border-card" class="Tabs" v-model="tabsValue">
        <el-tab-pane v-for="item in tabsList" :key="item.name" :label="item.label" :name="item.name">
          <div v-if="tabsValue === '1'">
            <slot name="form-item-task-nodeCode" :model="form" field="nodeCode">
              <el-form-item label="节点编码：">
                <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
              </el-form-item>
            </slot>
            <slot name="form-item-task-nodeName" :model="form" field="nodeName">
              <el-form-item label="节点名称：">
                <el-input v-model="form.nodeName" type="textarea" ref="nodeInput" :disabled="disabled" @change="nodeNameChange"></el-input>
              </el-form-item>
            </slot>
            <slot name="form-item-task-collaborativeWay" :model="form" field="collaborativeWay">
              <el-form-item label="协作方式：">
                <el-radio-group v-model="form.collaborativeWay">
                  <el-radio label="1" v-if="form.collaborativeWay ==='1' || showWays">
                    <span class="flex-hc">
                      或签
                      <el-tooltip class="box-item" effect="dark" content="只需一个人审批">
                        <el-icon :size="14" class="ml5">
                          <WarningFilled />
                        </el-icon>
                      </el-tooltip>
                    </span>
                  </el-radio>
                  <el-radio label="2" v-if="form.collaborativeWay ==='2' || showWays">
                    <span class="flex-hc">
                      票签
                      <el-tooltip class="box-item" effect="dark" content="部分办理人审批，建议选择用户；如果选择角色或者部门等，需自行通过办理人变量表达式或者监听器，转成具体办理用户">
                        <el-icon :size="14" class="ml5">
                          <WarningFilled />
                        </el-icon>
                      </el-tooltip>
                    </span>
                  </el-radio>
                  <el-radio label="3" v-if="form.collaborativeWay ==='3' || showWays">
                    <span class="flex-hc">
                      会签
                      <el-tooltip class="box-item" effect="dark" content="所有办理都需要审批，建议选择用户；如果选择角色或者部门等，需自行通过办理人变量表达式或者监听器，转成具体办理用户">
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
              <el-form-item label="票签占比：" prop="nodeRatio">
                <el-input v-model="form.nodeRatio" type="number" placeholder="请输入"></el-input>
                <div class="placeholder mt5">票签比例范围：(0-100)的值</div>
              </el-form-item>
            </slot>
            <slot name="form-item-task-permissionFlag" :model="form" field="permissionFlag">
              <el-form-item label="办理人输入：" class="permissionItem">
                <div v-for="(tag, index) in form.permissionFlag" :key="index" class="inputGroup">
    <!--              <el-select v-if="dictList" v-model="form.permissionFlag[index]" placeholder="请选择">-->
    <!--                <el-option-->
    <!--                    v-for="dict in dictList"-->
    <!--                    :key="dict.value"-->
    <!--                    :label="dict.label"-->
    <!--                    :value="parseInt(dict.value)"-->
    <!--                ></el-option>-->
    <!--              </el-select>-->

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
            </slot>
            <slot name="form-item-task-formCustom" :model="form" field="formCustom">
              <el-form-item label="审批表单：" prop="formCustom">
                <el-select v-model="form.formCustom" clearable>
                  <el-option label="表单路径" value="N"></el-option>
                  <!--TODO form 开发中-->
    <!--              <el-option label="动态表单" value="Y"></el-option>-->
                </el-select>
              </el-form-item>
            </slot>
            <slot name="form-item-task-formPath" :model="form" field="formPath" v-if="form.formCustom === 'N'">
              <el-form-item label="审批表单路径：">
                <el-input v-model="form.formPath"></el-input>
              </el-form-item>
            </slot>
            <slot name="form-item-task-formPath" :model="form" field="formPath" v-else-if="form.formCustom === 'Y'">
              <el-form-item label="审批流程表单：">
                <el-select v-model="form.formPath">
                  <el-option v-for="item in definitionList" :key="id" :label="`${item.formName} - v${item.version}`" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </slot>
            <nodeExtList v-if="baseList.length > 0" ref="nodeBase" v-model="form.ext" :formList="baseList" :disabled="disabled"></nodeExtList>
          </div>
          <div v-else-if="tabsValue === '2'">
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
          </div>
          <div v-else-if="tabsValue === item.name">
            <nodeExtList v-if="buttonList[item.name].length > 0" :ref="`nodeExtList_${item.name}`" v-model="form.ext" :formList="buttonList[item.name]" :disabled="disabled"></nodeExtList>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <!-- 权限标识：会签票签选择用户 -->
    <el-dialog title="办理人选择" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
      <selectUser v-model:selectUser="form.permissionFlag" v-model:userVisible="userVisible" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script setup name="Between">
import selectUser from "./selectUser";
import { Delete } from '../../warm-flow-vue3-logic-flow/node_modules/@element-plus/icons-vue'
import {publishedList, handlerDict, nodeExt} from "../api/flow/definition";
import nodeExtList from "./nodeExtList";
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
  }
});

const tabsValue = ref("1");
const tabsList = ref([
  { label: "基础设置", name: "1" },
  { label: "监听器", name: "2" }
]);
const form = ref(props.modelValue);
const userVisible = ref(false);

//基础设置扩展属性
const baseList = ref([]);
//按钮权限
const buttonList = ref({});

const rules = reactive({
  nodeRatio: [
    { required: false, message: "请输入", trigger: "change" },
    { pattern: /^(?:[1-9]\d?|0\.\d{1,3}|[1-9]\d?\.\d{1,3})$/, message: "请输入(0, 100)的值，最多保留三位小数", trigger: ["change", "blur"] }
  ],
  listenerType: [{ required: true, message: '监听器类型不能为空', trigger: 'change' }],
  listenerPath: [{ required: true, message: '监听器路径不能为空', trigger: 'blur' }]
});
const definitionList = ref([]); // 流程表单列表
const dictList = ref(); // 办理人选项
const emit = defineEmits(["change"]);

watch(() => form, n => {
  if (n) {
    emit('change', n)
  }
},{ deep: true });

function nodeNameChange() {
  proxy.$refs.nodeInput.focus();
}

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

/** 查询节点扩展属性 */
function getNodeExt() {
  nodeExt().then(response => {
    if (response.code === 200 && response.data) {
//       response.data = [
// 	// {
// 	// 	"code": "base",
// 	// 	"desc": "基础设置扩展属性",
// 	// 	"type": 1,
// 	// 	"childs": [
// 	// 		{
// 	// 			"code": "base1",
// 	// 			"label": "输入框",
// 	// 			"desc": "基础设置扩展属性1",
// 	// 			"type": 1,
// 	// 			"must": true,
// 	// 			"value": ""
// 	// 		},
// 	// 		{
// 	// 			"code": "base2",
// 	// 			"label": "文本域",
// 	// 			"desc": "基础设置扩展属性2",
// 	// 			"type": 2,
// 	// 			"must": false,
// 	// 			"value": ""
// 	// 		},
// 	// 		{
// 	// 			"code": "base3",
// 	// 			"label": "下拉框-多选",
// 	// 			"desc": "基础设置扩展属性3",
// 	// 			"type": 3,
// 	// 			"must": true,
//   //       "multiple": true,
// 	// 			"value": "",
// 	// 			"dict": [
// 	// 				{
// 	// 					"label": "选项A",
// 	// 					"value": "1"
// 	// 				},
// 	// 				{
// 	// 					"label": "选项B",
//   //           "selected": false,
// 	// 					"value": "2"
// 	// 				},
// 	// 				{
// 	// 					"label": "选项C",
//   //           "selected": false,
// 	// 					"value": "3"
// 	// 				}
// 	// 			]
// 	// 		},
// 	// 		{
// 	// 			"code": "base4",
// 	// 			"label": "下拉框-单选",
// 	// 			"desc": "基础设置扩展属性4",
// 	// 			"type": 3,
// 	// 			"must": true,
//   //       "multiple": false,
// 	// 			"value": "",
// 	// 			"dict": [
// 	// 				{
// 	// 					"label": "选项A",
// 	// 					"value": "1"
// 	// 				},
// 	// 				{
// 	// 					"label": "选项B",
// 	// 					"value": "2"
// 	// 				},
// 	// 				{
// 	// 					"label": "选项C",
//   //           "selected": true,
// 	// 					"value": "3"
// 	// 				}
// 	// 			]
// 	// 		},
// 	// 		{
// 	// 			"code": "base5",
// 	// 			"label": "复选框",
// 	// 			"desc": "基础设置扩展属性5",
// 	// 			"type": 4,
// 	// 			"must": true,
// 	// 			"value": "",
//   //       "multiple": true,
// 	// 			"dict": [
// 	// 				{
// 	// 					"label": "是否弹窗选人",
//   //           "selected": true,
// 	// 					"value": 1
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能委托",
//   //           "selected": true,
// 	// 					"value": 2
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能转办",
// 	// 					"value": 3
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能抄送",
//   //           "selected": true,
// 	// 					"value": 4
// 	// 				},
// 	// 				{
// 	// 					"label": "是否显示退回",
// 	// 					"value": 5
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能加签",
// 	// 					"value": 6
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能减签",
// 	// 					"value": 7
// 	// 				}
// 	// 			]
// 	// 		},
// 	// 		{
// 	// 			"code": "base6",
// 	// 			"label": "单选框",
// 	// 			"desc": "基础设置扩展属性6",
// 	// 			"type": 4,
// 	// 			"must": true,
// 	// 			"value": "",
//   //       "multiple": false,
// 	// 			"dict": [
// 	// 				{
// 	// 					"label": "是否弹窗选人",
// 	// 					"value": 1
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能委托",
// 	// 					"value": 2
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能转办",
// 	// 					"value": 3
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能抄送",
// 	// 					"value": 4
// 	// 				},
// 	// 				{
// 	// 					"label": "是否显示退回",
// 	// 					"value": 5
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能加签",
// 	// 					"value": 6
// 	// 				},
// 	// 				{
// 	// 					"label": "是否能减签",
// 	// 					"value": 7
// 	// 				}
// 	// 			]
// 	// 		}
// 	// 	]
// 	// },
// 	{
// 		"code": "btn_auth2",
// 		"name": "按钮权限2",
// 		"desc": "按钮权限设置",
// 		"type": 2,
// 		"childs": [
// 			{
// 				"code": "btn_aut2",
// 				"label": "复选框2",
// 				"desc": "按钮权限1",
// 				"type": 4,
// 				"must": true,
// 				"value": "",
// 				"dict": [
// 					{
// 						"label": "是否弹窗选人",
// 						"value": 1
// 					},
// 					{
// 						"label": "是否能委托",
// 						"value": 2
// 					},
// 					{
// 						"label": "是否能转办",
// 						"value": 3
// 					},
// 					{
// 						"label": "是否能抄送",
// 						"value": 4
// 					},
// 					{
// 						"label": "是否显示退回",
// 						"value": 5
// 					},
// 					{
// 						"label": "是否能加签",
// 						"value": 6
// 					},
// 					{
// 						"label": "是否能减签",
// 						"value": 7
// 					}
// 				]
// 			}
// 		]
// 	},
// ]
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
  let skipList = props.skips.filter(skip => skip.properties.skipType === "PASS");

  let previousCode = getPreviousCode(skipList, form.value.nodeCode)
  return props.nodes.filter(node => !["serial", "parallel"].includes(node.type)
      && previousCode.includes(node.id)).reverse();
});

function getPreviousCode(skipList, nowNodeCode) {
  const previousCode = [];
  for (const skip of skipList) {
    if (skip.targetNodeId === nowNodeCode) {
      previousCode.push(skip.sourceNodeId);
      previousCode.push(...getPreviousCode(skipList, skip.sourceNodeId));
    }
  }
  return previousCode;
}

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
    if (proxy.$refs.nodeBase) {
      if (await proxy.$refs.nodeBase[0].validate()) {
        tabsValidate(resolve, reject);
      } else reject(false);
    } else tabsValidate(resolve, reject);
  });
}

async function tabsValidate(resolve, reject) {
  let addTabsList = tabsList.value.slice(2);
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
    border: 1px solid #e4e7ed;
    border-top: 0;
  }
  .el-tabs__item.is-active {
    margin-left: 0;
    border-top: 1px solid var(--el-border-color);
    margin-top: 0;
  }
}
.betweenForm {
  border-top: 0;
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

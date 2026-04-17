<template>
  <div class="between">
    <el-form ref="formRef" class="betweenForm" :model="form" label-width="120px" size="small" :rules="rules" :disabled="disabled" label-position="left">
      <!-- 页签区域 -->
      <div class="modern-tabs-wrapper">
        <div class="modern-tabs">
          <div
            v-for="(item, index) in tabsList"
            :key="item.name"
            class="modern-tab-item"
            :class="{ 'is-active': tabsValue === item.name, 'is-ext-tab': index >= 3 }"
            @click="tabsValue = item.name; handleTabChange(item.name)"
          >
            <span class="tab-label">{{ item.label }}</span>
            <span v-if="index >= 3" class="tab-ext-tag">扩展</span>
          </div>
        </div>
      </div>

      <!-- 基础设置 -->
      <div v-show="tabsValue === '1'" class="tabPane">
        <!-- 基础配置卡片 -->
        <div class="base-settings-section">
          <div class="base-settings-header">
            <span class="ext-attributes-icon ext-icon-base">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M19 3H5C3.89 3 3 3.89 3 5V19C3 20.11 3.89 21 5 21H19C20.11 21 21 20.11 21 19V5C21 3.89 20.11 3 19 3ZM12 6C13.93 6 15.5 7.57 15.5 9.5C15.5 11.43 13.93 13 12 13C10.07 13 8.5 11.43 8.5 9.5C8.5 7.57 10.07 6 12 6ZM18 18H6V17C6 14.67 10.33 13.34 12 13.34C13.67 13.34 18 14.67 18 17V18Z" fill="currentColor"/>
              </svg>
            </span>
            <span class="base-settings-title">基础配置</span>
          </div>
          <div class="base-settings-content">
        <el-form-item label="节点编码：" prop="nodeCode">
          <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="节点名称：" prop="nodeName">
          <el-input v-model="form.nodeName" type="textarea" :disabled="disabled"></el-input>
        </el-form-item>

        <!-- 协作方式 - 卡片式单选 -->
        <el-form-item label="协作方式：" prop="collaborativeWay">
          <div class="radio-card-group">
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.collaborativeWay === '1' }"
              @click="!disabled && (form.collaborativeWay = '1')"
              v-if="form.collaborativeWay === '1' || showWays"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">或签</span>
              <el-tooltip effect="dark" placement="top" content="只需一个人审批">
                <el-icon :size="13" class="radio-card-tip"><WarningFilled /></el-icon>
              </el-tooltip>
            </label>
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.collaborativeWay === '2' }"
              @click="!disabled && (form.collaborativeWay = '2')"
              v-if="form.collaborativeWay === '2' || showWays"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">票签</span>
              <el-tooltip effect="dark" placement="top" content="部分办理人审批，建议选择用户；如果选择角色或者部门等，需自行通过办理人表达式或者监听器，转成具体办理用户">
                <el-icon :size="13" class="radio-card-tip"><WarningFilled /></el-icon>
              </el-tooltip>
            </label>
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.collaborativeWay === '3' }"
              @click="!disabled && (form.collaborativeWay = '3')"
              v-if="form.collaborativeWay === '3' || showWays"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">会签</span>
              <el-tooltip effect="dark" placement="top" content="所有办理都需要审批，建议选择用户；如果选择角色或者部门等，需自行通过办理人表达式或者监听器，转成具体办理用户">
                <el-icon :size="13" class="radio-card-tip"><WarningFilled /></el-icon>
              </el-tooltip>
            </label>
          </div>
        </el-form-item>

        <el-form-item label="票签策略：" prop="nodeRatio" v-if="form.collaborativeWay === '2'">
          <el-select v-model="form.nodeRatioType" placeholder="请选择策略类型" style="width: 25%"
                     clearable @clear="handleClear">
              <el-option label="通过率" value="passRatio"/>
              <el-option label="固定通过人数" value="passCount"/>
              <el-option label="固定驳回人数" value="rejectCount"/>
              <el-option label="默认表达式" value="default" v-if="framework ==='SPRING_BOOT'"/>
              <el-option label="spel表达式" value="spel" v-if="framework ==='SPRING_BOOT'"/>
              <el-option label="snel表达式" value="snel" v-if="framework ==='SOLON'"/>
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

        <!-- 自定义表单 - 卡片式单选 -->
        <el-form-item label="自定义表单：" prop="formCustom">
          <div class="radio-card-group radio-card-sm">
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.formCustom === 'N' }"
              @click="!disabled && (form.formCustom = 'N')"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">否</span>
              <el-tooltip effect="dark" placement="top" content="填写页面地址：如system/process/approve">
                <el-icon :size="13" class="radio-card-tip"><WarningFilled /></el-icon>
              </el-tooltip>
            </label>
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.formCustom === 'Y' }"
              @click="!disabled && (form.formCustom = 'Y')"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">是</span>
              <el-tooltip effect="dark" placement="top" content="填写自定义表单的唯一标识：如formCode+version">
                <el-icon :size="13" class="radio-card-tip"><WarningFilled /></el-icon>
              </el-tooltip>
            </label>
          </div>
        </el-form-item>

        <el-form-item label="表单路径：" prop="formPath" v-if="form.formCustom === 'N'">
          <el-input v-model="form.formPath"></el-input>
        </el-form-item>
        <el-form-item label="表单唯一标识：" prop="formPath" v-else-if="form.formCustom === 'Y'">
            <el-tree-select
                v-model="form.formPath"
                :data="formPathList"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择流程类别"
                check-strictly/>
        </el-form-item>
          </div>
        </div>

        <!-- 节点扩展属性（基础设置中的）- 可折叠 -->
        <div v-if="baseList.length > 0" class="ext-attributes-section ext-primary" :class="{ 'is-collapsed': baseExtCollapsed }">
          <div class="ext-attributes-header ext-header-clickable" @click="baseExtCollapsed = !baseExtCollapsed">
            <span class="ext-attributes-icon ext-icon-star">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" fill="currentColor"/>
              </svg>
            </span>
            <span class="ext-attributes-title">节点扩展属性</span>
            <span class="ext-attributes-badge">{{ baseList.length }}项</span>
            <span class="ext-collapse-arrow">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" :class="{ 'arrow-rotated': !baseExtCollapsed }" fill="currentColor"/>
              </svg>
            </span>
          </div>
          <transition name="ext-collapse">
            <div class="ext-attributes-content" v-show="!baseExtCollapsed">
              <nodeExtList ref="nodeBase" v-model="form.ext" :formList="baseList" :disabled="disabled"></nodeExtList>
            </div>
          </transition>
        </div>
      </div>

      <!-- 办理人设置 -->
      <div v-show="tabsValue === '2'" class="tabPane tabPane-full">
        <div class="section-card section-blue">
          <div class="section-card-header">
            <span class="section-card-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z" fill="currentColor"/>
              </svg>
            </span>
            <span class="section-card-title">办理人设置</span>
          </div>
          <div class="section-card-body">
              <el-table :data="permissionRows" style="width: 100%;" class="inputGroup handler-table-mobile"
                  :table-layout="isMobile ? 'fixed' : 'auto'"
              >
                  <el-table-column prop="storageId" label="入库主键" :width="isMobile ? undefined : 250">
                      <template #default="scope">
                          <el-form-item prop="storageId">
                              <el-input v-model="scope.row.storageId" style="width: 100%;" @blur="event => inputBlur(event, scope.$index)"></el-input>
                          </el-form-item>
                      </template>
                  </el-table-column>
                  <el-table-column prop="handlerName" label="权限名称"></el-table-column>
                  <el-table-column label="操作" :width="isMobile ? undefined : 42" v-if="!disabled">
                      <template #default="scope">
                          <el-button type="danger" v-if="!disabled" :icon="Delete" @click="delPermission(scope.$index)"/>
                      </template>
                  </el-table-column>
              </el-table>
              <div class="action-buttons">
                <el-button v-if="!disabled" class="add-row-btn" @click="addPermission">添加行</el-button>
                <el-button v-if="!disabled" class="add-row-btn add-row-btn-secondary" @click="initUser">选择</el-button>
              </div>
          </div>
        </div>
      </div>

      <!-- 监听器 -->
      <div v-show="tabsValue === '3'" class="tabPane tabPane-full">
        <div class="section-card section-purple">
          <div class="section-card-header section-purple-header">
            <span class="section-card-icon section-purple-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 6H16V4C16 2.89 15.11 2 14 2H10C8.89 2 8 2.89 8 4V6H2V8H4V19C4 20.11 4.89 21 6 21H18C19.11 21 20 20.11 20 19V8H22V6ZM10 4H14V6H10V4ZM18 19H6V8H18V19ZM10 10H14V17H10V10Z" fill="currentColor"/>
              </svg>
            </span>
            <span class="section-card-title section-purple-title">监听器配置</span>
          </div>
          <div class="section-card-body">
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
              <el-table-column prop="listenerPath" label="监听器（可输入类路径）">
                <template #default="scope">
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerPath'" :rules="rules.listenerPath">
                      <el-select
                          v-model="scope.row.listenerPath"
                          placeholder="请输入或选择"
                          allow-create
                          filterable
                          clearable
                          style="width: 100%"
                          @change="(value) => handleListenerPathChange(value, scope.row)">
                          <el-option
                              v-for="item in ListenerVo"
                              :key="item.path"
                              :label="item.description"
                              :value="item.path"/>
                      </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="55" v-if="!disabled">
                <template #default="scope">
                  <el-button type="danger" :icon="Delete" @click="handleDeleteRow(scope.$index)"/>
                </template>
              </el-table-column>
            </el-table>
            <div class="action-buttons">
              <el-button v-if="!disabled" class="add-row-btn" @click="handleAddRow">增加行</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 动态页签（按钮权限等）- 都是节点扩展属性 -->
      <div v-show="tabsValue !== '1' && tabsValue !== '2' && tabsValue !== '3'" class="tabPane tabPane-full">
        <div v-if="buttonList[tabsValue] && buttonList[tabsValue].length > 0" class="ext-attributes-section ext-secondary">
          <div class="ext-attributes-header ext-secondary-header">
            <span class="ext-attributes-icon ext-icon-puzzle">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 6H16V4C16 2.89 15.11 2 14 2H10C8.89 2 8 2.89 8 4V6H4C2.89 6 2.01 6.89 2.01 8L2 19C2 20.11 2.89 21 4 21H20C21.11 21 22 20.11 22 19V8C22 6.89 21.11 6 20 6ZM10 4H14V6H10V4ZM12 17H6V15H12V17ZM18 13H6V11H18V13Z" fill="currentColor"/>
              </svg>
            </span>
            <span class="ext-attributes-title ext-secondary-title">{{ getCurrentTabLabel() }}</span>
            <span class="ext-attributes-badge ext-secondary-badge">节点扩展属性 · {{ buttonList[tabsValue].length }}项</span>
          </div>
          <div class="ext-attributes-content">
            <nodeExtList :ref="`nodeExtList_${tabsValue}`" v-model="form.ext" :formList="buttonList[tabsValue]" :disabled="disabled"></nodeExtList>
          </div>
        </div>
      </div>
    </el-form>

    <!-- 权限标识：会签票签选择用户 -->
    <el-dialog title="人员选择" v-if="userVisible" v-model="userVisible" :width="isMobile ? '96%' : '80%'" append-to-body
      :class="{ 'mobile-user-dialog': isMobile }"
    >
      <selectUser v-model:selectUser="form.permissionFlag" v-model:userVisible="userVisible" :permissionRows="permissionRows" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script setup name="Between">
import selectUser from "./selectUser";
import { Delete } from '@element-plus/icons-vue'
import {publishedList, handlerDict, nodeExt, handlerFeedback, listenerList} from "@/api/flow/definition";
import nodeExtList from "./nodeExtList";
import {getPreviousNodes} from "@/components/design/common/js/tool.js";
import {getFramework} from "@/utils/auth.js";
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
// 节点扩展属性折叠状态（默认收起）
const baseExtCollapsed = ref(true);
const tabsList = ref([
  { label: "基础设置", name: "1" },
  { label: "办理人设置", name: "2" },
  { label: "监听器", name: "3" },
]);
const form = ref(props.modelValue);
const userVisible = ref(false);
const framework = getFramework()
//基础设置扩展属性
const baseList = ref([]);
//按钮权限
const buttonList = ref({});
const definitionList = ref([]); // 流程表单列表
const dictList = ref(); // 办理人选项
const permissionRows = ref([]); // 办理人表格
const ListenerVo = ref([]); // 监听器列表
const emit = defineEmits(['update:modelValue']);

function getCurrentTabLabel() {
  const currentTab = tabsList.value.find(t => t.name === tabsValue.value);
  return currentTab ? currentTab.label : '节点扩展属性';
}

const rules = reactive({
    nodeRatio: computed(() => {
        const type = form.value.nodeRatioType;
        if (!type) return [];
        if (type === 'passRatio') {
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
        }  else if (type === 'snel') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateSnel, trigger: ["change", "blur"]}
            ];
        }
        // 其他类型不作限制
        return [];
    }),
    listenerType: [{required: true, message: '监听器类型不能为空', trigger: 'change'}],
    listenerPath: [{required: true, message: '监听器路径不能为空', trigger: 'blur'}]
});

watch(() => form.value, n => {
  if (n) {
    emit('update:modelValue', n)
  }
  if (n.nodeRatioType) {
      let nodeRatio = '';
      if (/^passCount|rejectCount/.test(n.nodeRatioType)) {
          nodeRatio = n.nodeRatioType + "=";
      } else if (/^spel|default|snel/.test(n.nodeRatioType)) {
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

function validateSnel(rule, value, callback) {
    value = value.replace('snel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入snel表达式'));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error('snel表达式必须以#{开头，以}结尾'));
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
        case 'snel':
            return '请输入snel表达式，格式如: #{@user.eval(flag)}';
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
            getHandlerFeedback()
            break;
        case '3':
            // 监听器 tab
            getListenerList()
            break;
        default:
            // 自定义 tab
            break;
    }
}

/** 选择角色权限范围触发 */
function getPermissionFlag() {
  const pf = form.value.permissionFlag;
  form.value.permissionFlag = (typeof pf === 'string' && pf) ? pf.split("@@") : [""];
  if (form.value.listenerType && typeof form.value.listenerType === 'string') {
    const listenerTypes = form.value.listenerType.split(",");
    const lp = form.value.listenerPath;
    const listenerPaths = (typeof lp === 'string' && lp) ? lp.split("@@") : [];
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
      let permissionFlag = form.value.permissionFlag.join(",");
      handlerFeedback({storageIds: permissionFlag}).then(response => {
          if (response.code === 200 && response.data) {
            permissionRows.value = response.data;
          }
    });
  }
}

/** 获取监听器列表 */
async function getListenerList() {
    listenerList().then(response => {
        if (response.code === 200 && response.data) {
            ListenerVo.value = response.data;
        }
    });
}


// 处理监听器路径变化，级联更新类型
function handleListenerPathChange(path, row) {
    if (!path) {
        // 清空时，也清空类型
        row.listenerType = '';
        return;
    }

    // 在下拉选项中查找匹配的项
    const matchedItem = ListenerVo.value.find(item => item.path === path);
    if (matchedItem && matchedItem.type) {
        // 如果找到了匹配项且有 type，则更新 listenerType
        row.listenerType = matchedItem.type;
    } else {
        // 如果是手动输入的，清空类型（或者保持原值，根据需求决定）
        row.listenerType = '';
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

// 移动端/平板检测
const isMobile = computed(() => {
  if (typeof window === 'undefined') return false;
  return window.innerWidth <= 768;
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
@import '@/assets/styles/_common.scss';

/* ========== 容器布局 ========== */
.between {
  width: 100%;
  html.dark & {
    background: var(--wf-bg-color, #141414);
    border-radius: var(--wf-radius-lg, 12px);
  }
}
.betweenForm {
  border-top: 0;
  width: 100%;
}

/* 引入公共样式：现代化页签 + 基础配置卡片 + 监听器/办理人卡片 + 表格表单左对齐 */
@include modern-tabs;
@include base-settings-card;
@include section-card;
@include table-form-align;
@include responsive-adaption;

/* ========== 手机端特有适配（between 独有） ========== */
@media (max-width: 768px) {
  /* 协作方式/自定义表单 radio-card：手机端保持一行横向排列，缩小间距 */
  .radio-card-group {
    gap: 6px;
    flex-wrap: nowrap;
    overflow-x: auto;
    &::-webkit-scrollbar { display: none; }
  }
  .radio-card-item {
    padding: 6px 12px;
    font-size: 12px;
    white-space: nowrap;
    flex-shrink: 0;
  }

  /* 基础设置表单 label 缩减（匹配 label-width=120px） */
  .betweenForm::v-deep(.el-form-item__label) {
    min-width: 95px !important;
    max-width: 115px !important;
    font-size: 12px !important;
    white-space: normal !important;
    word-break: break-word !important;
  }

  /* 票签策略：select + input 堆叠显示 */
  .betweenForm::v-deep(.el-form-item) {
    .el-form-item__content {
      display: flex !important;
      flex-wrap: wrap !important;
      gap: 6px;
    }
    .el-select,
    .el-input {
      width: 100% !important;
      margin-left: 0 !important;
      min-width: 100%;
    }
  }

  /* 驳回节点 select 缩小 */
  .ext-attributes-section { margin-top: 8px; }

  /* 办理人表格：三列平分宽度（入库主键44%+名称44%+操作12%），无横向滚动条 */
  .handler-table-mobile {
    :deep(.el-table) {
      table-layout: fixed !important;
      width: 100% !important;
    }
    :deep(.el-table__header-wrapper),
    :deep(.el-table__body-wrapper) {
      th:nth-child(1),
      td:nth-child(1) {
        width: 44% !important;
        min-width: 0 !important;
        max-width: none !important;
        & .cell { width: 100%; }
      }
      th:nth-child(2),
      td:nth-child(2) {
        width: 44% !important;
        min-width: 0 !important;
        max-width: none !important;
        & .cell { width: 100%; }
      }
      th:nth-child(3),
      td:nth-child(3) {
        width: 12% !important;
        min-width: 0 !important;
        max-width: none !important;
        & .cell {
          width: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          padding-left: 4px;
          padding-right: 4px;
          box-sizing: border-box;
        }
      }
    :deep(.el-table__header-wrapper) {
      th:nth-child(3) .cell {
        text-align: center;
        justify-content: center;
      }
    }
    }
    :deep(.el-table__header colgroup) {
      col:nth-child(1) { width: 44% !important; }
      col:nth-child(2) { width: 44% !important; }
      col:nth-child(3) { width: 12% !important; }
    }
  }

  /* 添加行 + 选择 */
  .action-buttons {
    margin-top: 10px;
  }

  /* 手机端：人员选择弹框占满行宽（用非 scoped 覆盖 append-to-body 弹框） */
  :global(.mobile-user-dialog) {
    width: 96% !important;
    max-width: 100vw !important;
    margin: 0 auto !important;

    .el-dialog__header {
      padding: 14px 16px;
      .el-dialog__title { font-size: 16px; }
      .el-dialog__headerbtn { top: 12px; right: 10px; }
    }

    .el-dialog__body {
      padding: 12px;
      max-height: calc(100vh - 56px);
      overflow-y: auto;
    }
  }
}

@media (max-width: 480px) {
  .betweenForm::v-deep(.el-form-item__label) {
    min-width: 80px !important;
    font-size: 11px !important;
    white-space: normal !important;
  }
  .radio-card-text {
    font-size: 12px;
  }
  .radio-card-tip {
    display: none;
  }
}

.placeholder { color: #828f9e; font-size: 12px; }
.mt5 { margin-top: 5px; }
.mr5 { margin-right: 5px; }
.ml5 { margin-left: 5px; }
.flex-hc { display: flex; align-items: center; }

/* ========== 虚线增加行按钮（与 baseInfo 风格一致） ========== */
.action-buttons {
  margin-top: 12px;
}

.add-row-btn {
  width: 100%;
  border: 1.5px dashed var(--wf-primary, #409eff) !important;
  color: var(--wf-primary, #409eff) !important;
  background: transparent !important;
  border-radius: var(--wf-radius, 8px);
  transition: all 0.3s ease;
  height: 38px;
  letter-spacing: 2px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: var(--wf-primary-light, #ecf5ff) !important;
    border-style: solid !important;
    border-color: var(--wf-primary, #409eff) !important;
    box-shadow: var(--wf-shadow-primary, 0 2px 8px rgba(64, 158, 255, 0.3));
  }

  &.add-row-btn-secondary {
    border-color: var(--wf-text-secondary, #909399) !important;
    color: var(--wf-text-secondary, #909399) !important;

    &:hover {
      border-color: var(--wf-primary, #409eff) !important;
      color: var(--wf-primary, #409eff) !important;
      background: var(--wf-primary-light, #ecf5ff) !important;
      border-style: solid !important;
      box-shadow: var(--wf-shadow-primary, 0 2px 8px rgba(64, 158, 255, 0.3));
    }
  }
}

/* 蓝色主题 - 办理人 */
.section-blue {
  border-color: rgba(64, 158, 255, 0.25);
  html.dark & { border-color: rgba(64, 158, 255, 0.2); }
  .section-card-header {
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.06) 0%, rgba(43, 125, 233, 0.03) 100%);
    border-bottom-color: rgba(64, 158, 255, 0.12);
    html.dark & {
      background: linear-gradient(135deg, rgba(64, 158, 255, 0.08) 0%, rgba(43, 125, 233, 0.05) 100%);
    }
  }
  .section-card-icon { color: var(--wf-primary, #409eff); }
  .section-card-title { color: var(--wf-primary, #409eff); }
  &:hover { box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1); }
}

/* 紫色主题 - 监听器 */
.section-purple {
  border-color: rgba(137, 96, 220, 0.25);
  html.dark & { border-color: rgba(137, 96, 220, 0.2); }
  .section-purple-header {
    background: linear-gradient(135deg, rgba(137, 96, 220, 0.06) 0%, rgba(115, 78, 190, 0.03) 100%);
    border-bottom-color: rgba(137, 96, 220, 0.12);
    html.dark & {
      background: linear-gradient(135deg, rgba(137, 96, 220, 0.08) 0%, rgba(115, 78, 190, 0.05) 100%);
    }
  }
  .section-purple-icon { color: #8960dc; }
  .section-purple-title { color: #8960dc; }
  &:hover { box-shadow: 0 2px 12px rgba(137, 96, 220, 0.1); }
}

/* ========== 卡片式单选框 ========== */
.radio-card-group {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  &.radio-card-sm { gap: 8px; }
}
.radio-card-item {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: var(--wf-radius, 8px);
  border: 1.5px solid var(--wf-border-color, #dcdfe6);
  background: var(--wf-bg-white, #fff);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  font-size: 13px;
  color: var(--wf-text-regular, #606266);
  html.dark & {
    background: var(--wf-bg-color, #1a1a1a);
    border-color: var(--wf-border-color, #333333);
    color: var(--wf-text-regular, #b0b0b0);
  }
  &:hover {
    border-color: var(--wf-primary, #409eff);
    background: var(--wf-primary-lighter, #f0f7ff);
    color: var(--wf-primary, #409eff);
    html.dark & { background: rgba(64, 158, 255, 0.08); }
    .radio-card-dot { border-color: var(--wf-primary, #409eff); background: transparent; }
  }
  &.is-checked {
    border-color: var(--wf-primary, #409eff);
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.06) 0%, rgba(64, 158, 255, 0.03) 100%);
    color: var(--wf-primary, #409eff);
    font-weight: 500;
    html.dark & { background: rgba(64, 158, 255, 0.12); }
    .radio-card-dot {
      border-color: var(--wf-primary, #409eff);
      background: var(--wf-primary, #409eff);
      box-shadow: inset 0 0 0 3px var(--wf-bg-white, #fff);
      html.dark & { box-shadow: inset 0 0 0 3px var(--wf-bg-color, #141414); }
    }
  }
  .radio-card-dot {
    width: 16px; height: 16px; min-width: 16px;
    border-radius: 50%; border: 2px solid var(--wf-border-color, #dcdfe6);
    transition: all 0.25s ease; background: var(--wf-bg-white, #fff);
    html.dark & { border-color: var(--wf-border-color, #444444); background: var(--wf-bg-color, #1a1a1a); }
  }
  .radio-card-text { line-height: 1; }
  .radio-card-tip {
    color: var(--wf-text-placeholder, #c0c4cc); transition: color 0.25s ease;
    .is-checked & { color: var(--wf-primary, #409eff); opacity: 0.7; }
  }
}

/* ========== 节点扩展属性区域（基础设置内 - 蓝色主题） ========== */
.ext-attributes-section {
  margin-top: 12px;
  border-radius: var(--wf-radius-lg, 12px);
  overflow: hidden;
  border: 1px solid var(--wf-border-light, #e4e7ed);
  background: var(--wf-bg-white, #fff);
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  transition: all 0.3s ease;
  html.dark & { border-color: var(--wf-border-color, #333333); background: var(--wf-bg-color, #141414); }
  &:hover {
    box-shadow: var(--wf-shadow, 0 2px 12px rgba(0, 0, 0, 0.06));
    html.dark & { box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3); }
  }
}
.ext-attributes-header {
  display: flex; align-items: center; gap: 8px; padding: 12px 16px;
  background: linear-gradient(135deg, var(--wf-primary-light, #ecf5ff) 0%, var(--wf-primary-lighter, #f0f7ff) 100%);
  border-bottom: 1px solid rgba(64, 158, 255, 0.12);
  html.dark & {
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.08) 0%, rgba(43, 125, 233, 0.05) 100%);
    border-bottom-color: rgba(64, 158, 255, 0.12);
  }
}
.ext-attributes-icon {
  width: 20px; height: 20px;
  display: flex; align-items: center; justify-content: center;
  color: var(--wf-primary, #409eff);
  svg { width: 16px; height: 16px; }
}
.ext-attributes-title {
  font-size: 13px; font-weight: 600; color: var(--wf-primary, #409eff); letter-spacing: 0.3px;
}
.ext-attributes-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 1px 7px; font-size: 10px; font-weight: 600;
  color: var(--wf-primary, #409eff); background: rgba(64, 158, 255, 0.12); border-radius: 8px;
  html.dark & { background: rgba(64, 158, 255, 0.2); color: var(--wf-primary, #409eff); }
}
.ext-attributes-content {
  padding: 14px 16px; background: var(--wf-bg-color, #fafbfc);
  html.dark & { background: var(--wf-bg-color, #141414); }
}

/* ========== 折叠功能样式 ========== */
.ext-header-clickable {
  cursor: pointer;
  user-select: none;
  transition: background 0.25s ease;

  &:hover {
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(43, 125, 233, 0.06) 100%);
    html.dark & {
      background: linear-gradient(135deg, rgba(64, 158, 255, 0.12) 0%, rgba(43, 125, 233, 0.08) 100%);
    }
  }

  &:active {
    opacity: 0.85;
  }
}

.ext-collapse-arrow {
  margin-left: auto;
  width: 20px; height: 20px;
  display: flex; align-items: center; justify-content: center;
  color: var(--wf-primary, #409eff);
  opacity: 0.6;
  transition: opacity 0.25s ease;

  svg { width: 18px; height: 18px; }

  .ext-header-clickable:hover & {
    opacity: 1;
  }
}

.arrow-rotated {
  transform: rotate(180deg);
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 折叠状态：去掉底部边框 */
.is-collapsed {
  border-bottom-width: 1px !important;

  .ext-attributes-header {
    border-bottom: none;
  }
}

/* 展开/折叠过渡动画 */
.ext-collapse-enter-active,
.ext-collapse-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}
.ext-collapse-enter-from,
.ext-collapse-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
.ext-collapse-enter-to,
.ext-collapse-leave-from {
  opacity: 1;
  max-height: 2000px;
}

/* ========== 绿色主题（动态页签） ========== */
.ext-secondary {
  border-color: rgba(103, 194, 58, 0.25);
  html.dark & { border-color: rgba(103, 194, 58, 0.2); }
  .ext-secondary-header {
    background: linear-gradient(135deg, rgba(103, 194, 58, 0.08) 0%, rgba(82, 155, 46, 0.04) 100%);
    border-bottom-color: rgba(103, 194, 58, 0.12);
    html.dark & {
      background: linear-gradient(135deg, rgba(103, 194, 58, 0.1) 0%, rgba(82, 155, 46, 0.06) 100%);
      border-bottom-color: rgba(103, 194, 58, 0.15);
    }
  }
  .ext-secondary-title { color: #67c23a; }
  .ext-secondary-badge { color: #67c23a; background: rgba(103, 194, 58, 0.12); html.dark & { background: rgba(103, 194, 58, 0.2); } }
  .ext-icon-puzzle { color: #67c23a; }
  &:hover { box-shadow: 0 2px 12px rgba(103, 194, 58, 0.1); html.dark & { box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3); } }
}

/* ========== 表格内表单元素左对齐（关键修复） ========== */
.section-card-body {
  /* 穿透表格内的所有 el-form-item，去掉 label 占位 */
  ::v-deep(.el-table .el-form-item) {
    margin-bottom: 0;

    /* 彻底隐藏 label 区域 */
    .el-form-item__label {
      display: none !important;
      width: 0 !important;
      padding-right: 0 !important;
      min-height: 0 !important;
      height: 0 !important;
      overflow: hidden !important;
    }

    /* 内容区域紧贴左侧 */
    .el-form-item__content {
      display: block !important;
      margin-left: 0 !important;
    }
  }
}

::v-deep(.permissionItem) {
  display: inline-block;
  width: 100%;
  .el-form-item__content { display: block; }
  .inputGroup {
    display: flex; align-items: center; margin-bottom: 10px;
    .Icon { width:14px;height:14px;margin-left:5px;cursor:pointer;color:#fff;background:#94979d;border-radius:50%;padding:2px;&:hover{background:#f0f2f5;color:#94979d;} }
    .btn { height:18px;margin-left:5px;color:#fff;background:#94979d;border-radius:5px;padding:6px;font-size:10px;border:0;&:hover{background:#f0f2f5;color:#94979d;} }
  }
}

::v-deep(.listenerItem) {
  display: inline-block;
  width: 100%;
  .el-form-item__label { display:none!important;width:0!important;min-height:0!important;height:0!important;overflow:hidden!important;padding-right:0!important; }
  .el-form-item__content { margin-left:0!important;display:block!important; }
  .el-table .el-form-item { margin-bottom:0; }
}
</style>

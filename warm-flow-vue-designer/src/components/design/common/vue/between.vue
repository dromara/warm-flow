<template>
  <div class="between">
    <wf-form ref="formRef" class="betweenForm" :model="form" label-width="110px" :rules="rules" :disabled="disabled">
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
            <svg class="tab-icon" viewBox="0 0 24 24"><path :d="item.iconPath || TAB_ICONS.ext" fill="currentColor"/></svg>
            <span class="tab-label">{{ item.label }}</span>
            <span v-if="index >= 3" class="tab-ext-tag">{{ t('between.extTag') }}</span>
          </div>
        </div>
      </div>

      <!-- 基础设置 -->
      <div v-show="tabsValue === '1'" class="tabPane">
        <!-- 基础配置卡片 -->
        <div class="base-settings-section">
          <div class="base-settings-content">
        <wf-form-item :label="t('node.codeLabel')" prop="nodeCode">
          <wf-input v-model="form.nodeCode" :disabled="disabled"></wf-input>
        </wf-form-item>
        <wf-form-item :label="t('node.nameLabel')" prop="nodeName">
          <wf-input v-model="form.nodeName" type="textarea" :disabled="disabled"></wf-input>
        </wf-form-item>

        <!-- 协作方式 - 卡片式单选 -->
        <wf-form-item :label="t('between.collaborativeWay')" prop="collaborativeWay">
          <div class="radio-card-group">
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.collaborativeWay === '1' }"
              @click="!disabled && (form.collaborativeWay = '1')"
              v-if="form.collaborativeWay === '1' || showWays"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">{{ t('between.wayOr') }}</span>
              <wf-tooltip effect="dark" placement="top" :content="t('between.wayOrTip')">
                <wf-icon :size="13" class="radio-card-tip"><svg-icon icon-class="ep:warning-filled" /></wf-icon>
              </wf-tooltip>
            </label>
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.collaborativeWay === '2' }"
              @click="!disabled && (form.collaborativeWay = '2')"
              v-if="form.collaborativeWay === '2' || showWays"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">{{ t('between.wayVote') }}</span>
              <wf-tooltip effect="dark" placement="top" :content="t('between.wayVoteTip')">
                <wf-icon :size="13" class="radio-card-tip"><svg-icon icon-class="ep:warning-filled" /></wf-icon>
              </wf-tooltip>
            </label>
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.collaborativeWay === '3' }"
              @click="!disabled && (form.collaborativeWay = '3')"
              v-if="form.collaborativeWay === '3' || showWays"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">{{ t('between.waySign') }}</span>
              <wf-tooltip effect="dark" placement="top" :content="t('between.waySignTip')">
                <wf-icon :size="13" class="radio-card-tip"><svg-icon icon-class="ep:warning-filled" /></wf-icon>
              </wf-tooltip>
            </label>
          </div>
        </wf-form-item>

        <wf-form-item :label="t('between.ratioStrategy')" prop="nodeRatio" v-if="form.collaborativeWay === '2'">
          <wf-select v-model="form.nodeRatioType" :placeholder="t('between.ratioStrategyPlaceholder')" style="width: 25%"
                     clearable @clear="handleClear">
              <wf-option :label="t('between.ratioPassRatio')" value="passRatio"/>
              <wf-option :label="t('between.ratioPassCount')" value="passCount"/>
              <wf-option :label="t('between.ratioRejectCount')" value="rejectCount"/>
              <wf-option :label="t('between.ratioDefaultExpr')" value="default" v-if="framework ==='SPRING_BOOT'"/>
              <wf-option :label="t('between.ratioSpelExpr')" value="spel" v-if="framework ==='SPRING_BOOT'"/>
              <wf-option :label="t('between.ratioSnelExpr')" value="snel" v-if="framework ==='SOLON'"/>
          </wf-select>
          <wf-input v-model="form.nodeRatioValue" :placeholder="getNodeRatioDescription()" style="width: 74%; margin-left: 1%"/>
        </wf-form-item>
        <wf-form-item :label="t('between.rejectToNode')" prop="formCustom">
          <template #label>
            <span v-if="form.collaborativeWay === '2'"  class="mr5" style="color: red;">*</span>{{ t('between.rejectToNode') }}
          </template>
          <wf-select v-model="form.anyNodeSkip" style="width: 80%" clearable>
            <wf-option
                v-for="dict in filteredNodes"
                :key="dict.id"
                :label="dict.text.value"
                :value="dict.id"
            />
          </wf-select>
          <div class="placeholder mt5">{{ t('between.voteRejectRequired') }}</div>
        </wf-form-item>

        <!-- 自定义表单 - 卡片式单选 -->
        <wf-form-item :label="t('between.formCustom')" prop="formCustom">
          <div class="radio-card-group radio-card-sm">
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.formCustom === 'N' }"
              @click="!disabled && (form.formCustom = 'N')"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">{{ t('common.no') }}</span>
              <wf-tooltip effect="dark" placement="top" :content="t('between.formCustomNoTip')">
                <wf-icon :size="13" class="radio-card-tip"><svg-icon icon-class="ep:warning-filled" /></wf-icon>
              </wf-tooltip>
            </label>
            <label
              class="radio-card-item"
              :class="{ 'is-checked': form.formCustom === 'Y' }"
              @click="!disabled && (form.formCustom = 'Y')"
            >
              <span class="radio-card-dot"></span>
              <span class="radio-card-text">{{ t('common.yes') }}</span>
              <wf-tooltip effect="dark" placement="top" :content="t('between.formCustomYesTip')">
                <wf-icon :size="13" class="radio-card-tip"><svg-icon icon-class="ep:warning-filled" /></wf-icon>
              </wf-tooltip>
            </label>
          </div>
        </wf-form-item>

        <wf-form-item :label="t('between.formPath')" prop="formPath" v-if="form.formCustom === 'N'">
          <wf-input v-model="form.formPath"></wf-input>
        </wf-form-item>
        <wf-form-item :label="t('between.formKey')" prop="formPath" v-else-if="form.formCustom === 'Y'">
            <wf-tree-select
                v-model="form.formPath"
                :data="formPathList"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                :placeholder="t('baseInfo.categoryPlaceholder')"
                check-strictly/>
        </wf-form-item>
        <!-- 自定义扩展点：消费方可注入额外表单项（透出 { form, disabled }） -->
        <slot name="node-form-extra" :form="form" :disabled="disabled" />
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
            <span class="ext-attributes-title">{{ t('between.extAttributes') }}</span>
            <span class="ext-attributes-badge">{{ t('between.extCount', { n: baseList.length }) }}</span>
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
          <div class="section-card-body">
              <wf-table :data="permissionRows" style="width: 100%;" class="inputGroup handler-table-mobile"
                  :table-layout="isMobile ? 'fixed' : 'auto'"
              >
                  <wf-table-column prop="storageId" :label="t('between.handlerStorageId')" :width="isMobile ? undefined : 250">
                      <template #default="scope">
                          <wf-form-item prop="storageId">
                              <wf-input v-model="scope.row.storageId" style="width: 100%;" @blur="event => inputBlur(event, scope.$index)"></wf-input>
                          </wf-form-item>
                      </template>
                  </wf-table-column>
                  <wf-table-column prop="handlerName" :label="t('between.handlerName')"></wf-table-column>
                  <wf-table-column :label="t('common.operation')" :width="isMobile ? undefined : 65" align="center" v-if="!disabled">
                      <template #default="scope">
                          <wf-button link size="small" type="danger" v-if="!disabled" @click="delPermission(scope.$index)"><svg-icon icon-class="ep:delete"/></wf-button>
                      </template>
                  </wf-table-column>
              </wf-table>
              <div class="action-buttons">
                <wf-button v-if="!disabled" class="add-row-btn" @click="addPermission">{{ t('between.addRowHandler') }}</wf-button>
                <wf-button v-if="!disabled" class="add-row-btn add-row-btn-secondary" @click="initUser">{{ t('between.selectHandler') }}</wf-button>
              </div>
          </div>
        </div>
      </div>

      <!-- 监听器 -->
      <div v-show="tabsValue === '3'" class="tabPane tabPane-full">
        <div class="section-card section-purple">
          <div class="section-card-body">
            <wf-table :data="form.listenerRows" style="width: 100%">
              <wf-table-column prop="listenerType" :label="t('common.type')" :width="isMobile ? 60 : 160">
                <template #default="scope">
                  <wf-form-item :prop="'listenerRows.' + scope.$index + '.listenerType'" :rules="rules.listenerType">
                    <wf-select v-model="scope.row.listenerType" :placeholder="t('common.pleaseSelect')">
                      <wf-option :label="t('start.listenerStart')" value="start"></wf-option>
                      <wf-option :label="t('start.listenerAssignment')" value="assignment"></wf-option>
                      <wf-option :label="t('start.listenerFinish')" value="finish"></wf-option>
                      <wf-option :label="t('start.listenerCreate')" value="create"></wf-option>
                    </wf-select>
                  </wf-form-item>
                </template>
              </wf-table-column>
              <wf-table-column prop="listenerPath" :label="t('baseInfo.listenerPathLabel')">
                <template #default="scope">
                  <wf-form-item :prop="'listenerRows.' + scope.$index + '.listenerPath'" :rules="rules.listenerPath">
                      <wf-select
                          v-model="scope.row.listenerPath"
                          :placeholder="t('baseInfo.listenerPathPlaceholder')"
                          allow-create
                          filterable
                          clearable
                          style="width: 100%"
                          @change="(value) => handleListenerPathChange(value, scope.row)">
                          <wf-option
                              v-for="item in ListenerVo"
                              :key="item.path"
                              :label="item.description"
                              :value="item.path"/>
                      </wf-select>
                  </wf-form-item>
                </template>
              </wf-table-column>
              <wf-table-column :label="t('common.operation')" width="65" align="center" v-if="!disabled">
                <template #default="scope">
                  <wf-button link size="small" type="danger" @click="handleDeleteRow(scope.$index)"><svg-icon icon-class="ep:delete"/></wf-button>
                </template>
              </wf-table-column>
            </wf-table>
            <div class="action-buttons">
              <wf-button v-if="!disabled" class="add-row-btn" @click="handleAddRow">{{ t('common.addRow') }}</wf-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 动态页签（按钮权限等）- 都是节点扩展属性。tab 已表明当前分组，无需重复标题与卡片背景，直接渲染扩展属性表单 -->
      <div v-show="tabsValue !== '1' && tabsValue !== '2' && tabsValue !== '3'" class="tabPane tabPane-full">
        <div v-if="buttonList[tabsValue] && buttonList[tabsValue].length > 0" class="ext-tab-content">
          <nodeExtList :ref="`nodeExtList_${tabsValue}`" v-model="form.ext" :formList="buttonList[tabsValue]" :disabled="disabled"></nodeExtList>
        </div>
      </div>
    </wf-form>

    <!-- 权限标识：会签票签选择用户 -->
    <wf-dialog :title="t('between.userSelectTitle')" v-if="userVisible" v-model="userVisible" :width="isMobile ? '96%' : '80%'" append-to-body
      class="person-select-dialog" :class="{ 'mobile-user-dialog': isMobile }"
    >
      <selectUser v-model:selectUser="form.permissionFlag" v-model:userVisible="userVisible" :permissionRows="permissionRows" @handleUserSelect="handleUserSelect"></selectUser>
    </wf-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, reactive, ref, watch } from 'vue';
import selectUser from "./selectUser";
import {publishedList, handlerDict, nodeExt, handlerFeedback, listenerList} from "@/api/flow/definition";
import nodeExtList from "./nodeExtList";
import {getPreviousNodes} from "@/components/design/common/js/tool";
import {getFramework} from "@/utils/auth";
import { useI18n } from '@/i18n';

defineOptions({ name: 'Between' });

const { proxy } = getCurrentInstance()!;
const { t } = useI18n();

interface BetweenProps {
  /** 节点表单数据（v-model） */
  modelValue?: Record<string, any>;
  /** 是否只读 */
  disabled?: boolean;
  /** 是否展示所有协作方式 */
  showWays?: boolean;
  /** 画布节点列表 */
  nodes?: any[];
  /** 画布边列表 */
  skips?: any[];
  /** 自定义表单路径树 */
  formPathList?: any[];
}
const props = withDefaults(defineProps<BetweenProps>(), {
  modelValue: () => ({}),
  disabled: false,
  showWays: true,
  nodes: () => [],
  skips: () => [],
  formPathList: () => [],
});

const tabsValue = ref("1");
// 节点扩展属性折叠状态（默认收起）
const baseExtCollapsed = ref(true);
// Tab 图标（单路径 SVG，viewBox 0 0 24 24，跟随 tab 文字色 currentColor）
const TAB_ICONS = {
  base: 'M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z',
  handler: 'M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z',
  listener: 'M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z',
  ext: 'M20.5 11H19V7c0-1.1-.9-2-2-2h-4V3.5C13 2.12 11.88 1 10.5 1S8 2.12 8 3.5V5H4c-1.1 0-1.99.9-1.99 2v3.8H3.5c1.49 0 2.7 1.21 2.7 2.7s-1.21 2.7-2.7 2.7H2V20c0 1.1.9 2 2 2h3.8v-1.5c0-1.49 1.21-2.7 2.7-2.7 1.49 0 2.7 1.21 2.7 2.7V22H17c1.1 0 2-.9 2-2v-4h1.5c1.38 0 2.5-1.12 2.5-2.5S21.88 11 20.5 11z',
};
const tabsList = ref([
  { label: t('start.tabBase'), name: "1", iconPath: TAB_ICONS.base },
  { label: t('between.tabHandler'), name: "2", iconPath: TAB_ICONS.handler },
  { label: t('start.tabListener'), name: "3", iconPath: TAB_ICONS.listener },
]);
const form = ref<Record<string, any>>(props.modelValue);
const userVisible = ref(false);
const framework = getFramework()
//基础设置扩展属性
const baseList = ref<any[]>([]);
//按钮权限
const buttonList = ref<Record<string, any>>({});
const definitionList = ref<any[]>([]); // 流程表单列表
const dictList = ref<any>(); // 办理人选项
const permissionRows = ref<any[]>([]); // 办理人表格
const ListenerVo = ref<any[]>([]); // 监听器列表
const emit = defineEmits<{ (e: 'update:modelValue', value: any): void }>();

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

function validatePassRatio(rule: any, value: any, callback: (error?: Error) => void) {
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

function validatePassCount(rule: any, value: any, callback: (error?: Error) => void) {
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

function validateDefault(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('default@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入默认表达式'));
    } else if (!/^\$\{.*\}$/.test(value)) {
        callback(new Error('默认表达式必须以${开头，以}结尾'));
    } else {
        callback();
    }
}

function validateSpel(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('spel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入spel表达式'));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error('spel表达式必须以#{开头，以}结尾'));
    } else {
        callback();
    }
}

function validateSnel(rule: any, value: any, callback: (error?: Error) => void) {
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
function delPermission(index: number) {
  form.value.permissionFlag.splice(index, 1);
  permissionRows.value.splice(index, 1);
}
// 添加办理人
function addPermission() {
  form.value.permissionFlag.push("");
  permissionRows.value.push({ storageId: "", handlerName: "" });
}
// 办理人手动输入，失焦获取权限名称
function inputBlur(event: any, index: number) {
  form.value.permissionFlag[index] = event.target.value;
}

// 添加在其他函数后面
function handleTabChange(activeTabName: string) {
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
      let permissionFlag = form.value.permissionFlag;
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
function handleListenerPathChange(path: string, row: any) {
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
function handleUserSelect(checkedItemList: any[]) {
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
function handleDeleteRow(index: number) {
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

async function tabsValidate(resolve: (v: boolean) => void, reject: (v: boolean) => void) {
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

  /* 办理人表格：隐藏滚动条，操作列固定宽度 */
  .handler-table-mobile {
    :deep(.el-table) {
      table-layout: fixed !important;
      width: 100% !important;
      overflow: hidden;
    }
    :deep(.el-table__header-wrapper),
    :deep(.el-table__body-wrapper) {
      overflow: hidden !important;
    }

    /* ====== 手机端：三列百分比布局，操作列固定52px ====== */
    :deep(.el-table__header-wrapper) {
      th:nth-child(1) { width: calc(50% - 26px) !important; min-width: 0 !important; & .cell { width: 100%; } }
      th:nth-child(2) { width: calc(50% - 26px) !important; min-width: 0 !important; & .cell { width: 100%; } }
      th:nth-child(3) { width: 52px !important; min-width: 0 !important; max-width: none !important; & .cell { width: 100%; text-align: center; justify-content: center; } }
    }
    :deep(.el-table__body-wrapper) {
      td:nth-child(1) { width: calc(50% - 26px) !important; min-width: 0 !important; & .cell { width: 100%; } }
      td:nth-child(2) { width: calc(50% - 26px) !important; min-width: 0 !important; & .cell { width: 100%; } }
      td:nth-child(3) { width: 52px !important; min-width: 0 !important; max-width: none !important; & .cell { width: 100%; display: flex; justify-content: center; align-items: center; padding-left: 4px; padding-right: 4px; box-sizing: border-box; } }
    }

    /* ====== PC端：前两列自适应，操作列锁定42px ====== */
    &:not(.is-mobile) {
      :deep(.el-table) {
        table-layout: auto !important;
      }
      :deep(.el-table__header-wrapper),
      :deep(.el-table__body-wrapper) {
        overflow-x: hidden !important;

        /* PC端操作列强制42px */
        th:nth-child(3), td:nth-child(3) {
          width: 42px !important;
          min-width: 42px !important;
          max-width: 42px !important;
        }
        th:nth-child(3) .cell {
          text-align: center;
          justify-content: center;
        }
        td:nth-child(3) .cell {
          display: flex;
          justify-content: center;
          align-items: center;
          padding-left: 4px;
          padding-right: 4px;
          box-sizing: border-box;
        }
      }
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
  border-radius: 10px;
  transition: all 0.3s ease;
  height: 40px;
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

/* ========== 人员选择弹窗：iOS 风格外壳 ==========
   弹窗为 append-to-body，不在组件作用域内，必须用扁平的 :global(完整选择器)。
   不能写成 :global(.person-select-dialog) { .el-dialog__header {} } 这种「:global 内再嵌套子选择器」，
   否则 Vue 会给内层注入 scoped 属性而失效，且头部声明会漏到弹窗根节点、把 .el-dialog 默认白底覆盖成透明渐变。 */
:global(.person-select-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 16px 56px rgba(0, 0, 0, 0.18);
  background-color: var(--el-dialog-bg-color, #fff);
}
:global(.person-select-dialog .el-dialog__header) {
  position: relative;
  margin: 0;
  padding: 18px 24px;
  background-color: var(--el-dialog-bg-color, #fff);
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
}
:global(.person-select-dialog .el-dialog__title) {
  font-weight: 600;
  font-size: 16px;
  color: var(--wf-text-primary, #303133);
}
/* 关闭按钮：相对头部垂直居中，避免偏下错位 */
:global(.person-select-dialog .el-dialog__headerbtn) {
  top: 50%;
  right: 16px;
  width: 32px;
  height: 32px;
  margin: 0;
  transform: translateY(-50%);
}
:global(.person-select-dialog .el-dialog__body) {
  padding: 20px 24px;
  background-color: var(--el-dialog-bg-color, #fff);
}
/* 表头：覆盖全局 ruoyi 的灰底(#f8f8f9 !important)，统一白底；弹窗 append-to-body 需 :global */
:global(.person-select-dialog .el-table th.el-table__cell),
:global(.person-select-dialog .el-table__header-wrapper th) {
  background-color: var(--el-dialog-bg-color, #fff) !important;
}
:global(html.dark .person-select-dialog),
:global(html.dark .person-select-dialog .el-dialog__header),
:global(html.dark .person-select-dialog .el-dialog__body) {
  background: var(--wf-bg-color, #1f1f1f);
}
:global(html.dark .person-select-dialog .el-dialog__header) {
  border-bottom-color: var(--wf-border-color, #333333);
}
:global(html.dark .person-select-dialog .el-dialog__title) {
  color: var(--wf-text-primary, #e0e0e0);
}
:global(html.dark .person-select-dialog .el-table th.el-table__cell),
:global(html.dark .person-select-dialog .el-table__header-wrapper th) {
  background-color: var(--wf-bg-color, #1f1f1f) !important;
  color: #c0c4cc !important;
}

/* 蓝色主题 - 办理人（扁平：保留蓝色标题/图标做语义，去卡片底/边框/渐变头） */
.section-blue {
  .section-card-header {
    background: transparent;
    border-bottom-color: var(--wf-border-lighter, #ebeef5);
    html.dark & { background: transparent; border-bottom-color: var(--wf-border-color, #333333); }
  }
  .section-card-icon { color: var(--wf-primary, #409eff); }
  .section-card-title { color: var(--wf-primary, #409eff); }
}

/* 紫色主题 - 监听器（扁平：保留紫色标题/图标做语义，去卡片底/边框/渐变头） */
.section-purple {
  .section-purple-header {
    background: transparent;
    border-bottom-color: var(--wf-border-lighter, #ebeef5);
    html.dark & { background: transparent; border-bottom-color: var(--wf-border-color, #333333); }
  }
  .section-purple-icon { color: #8960dc; }
  .section-purple-title { color: #8960dc; }
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
    background: var(--wf-primary-light, #ecf5ff);
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

/* ========== 扩展页签内容（无卡片背景与重复标题，tab 已表明当前分组） ========== */
.ext-tab-content {
  padding: 4px 0;
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

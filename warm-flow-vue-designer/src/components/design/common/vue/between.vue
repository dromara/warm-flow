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
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validatePassRatio, trigger: ["change", "blur"]}
            ];
        } else if (['passCount', 'rejectCount'].includes(type)) {
            return [
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validatePassCount, trigger: ["change", "blur"]}
            ];
        } else if (type === 'default') {
            return [
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validateDefault, trigger: ["change", "blur"]}
            ];
        }  else if (type === 'spel') {
            return [
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validateSpel, trigger: ["change", "blur"]}
            ];
        }  else if (type === 'snel') {
            return [
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validateSnel, trigger: ["change", "blur"]}
            ];
        }
        // 其他类型不作限制
        return [];
    }),
    listenerType: [{required: true, message: t('between.listenerTypeRequired'), trigger: 'change'}],
    listenerPath: [{required: true, message: t('between.listenerPathRequired'), trigger: 'blur'}]
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
        callback(new Error(t('between.ratioPassRatioRequired')));
    } else {
        const numValue = Number(value);
        if (isNaN(numValue)) {
            callback(new Error(t('between.ratioInvalidNumber')));
        } else if (numValue < 0.001 || numValue > 100) {
            callback(new Error(t('between.ratioRange')));
        } else if (!/^\d+(\.\d{1,3})?$/.test(value)) {
            callback(new Error(t('between.ratioDecimal')));
        }  else {
            callback();
        }
    }
}

function validatePassCount(rule: any, value: any, callback: (error?: Error) => void) {
    if (value === '' || value === undefined || value === null) {
        callback(new Error(t('between.ratioCountRequired')));
    } else {
        value = value.replace('passCount', '').replace('rejectCount', '').replace('=', '').trim();
        const reg = /^[1-9]\d*$/;
        if (!reg.test(value)) {
            callback(new Error(t('between.ratioPositiveInt')));
        } else {
            callback();
        }
    }
}

function validateDefault(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('default@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error(t('skip.defaultRequired')));
    } else if (!/^\$\{.*\}$/.test(value)) {
        callback(new Error(t('skip.defaultFormat')));
    } else {
        callback();
    }
}

function validateSpel(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('spel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error(t('skip.spelRequired')));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error(t('skip.spelFormat')));
    } else {
        callback();
    }
}

function validateSnel(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('snel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error(t('skip.snelRequired')));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error(t('skip.snelFormat')));
    } else {
        callback();
    }
}

function getNodeRatioDescription() {
    const type = form.value.nodeRatioType;
    switch (type) {
        case 'passRatio':
            return t('between.descPassRatio');
        case 'passCount':
            return t('between.descPassCount');
        case 'rejectCount':
            return t('between.descRejectCount');
        case 'default':
            return t('between.descDefault');
        case 'spel':
            return t('skip.descSpel');
        case 'snel':
            return t('skip.descSnel');
        default:
            return t('common.pleaseInput');
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
@import './between.scoped.scss';
</style>

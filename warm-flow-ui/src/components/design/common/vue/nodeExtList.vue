<template>
    <el-form ref="nodeExtRef" class="nodeExtForm" :model="form" label-width="150px" size="small" :disabled="disabled"
             label-position="left">
        <el-form-item :label="`${item.label}：`" :prop="item.code" v-for="(item, index) in formList" 10key="index"
                      :rules="[{ required: item.must, message: `${item.label}不能为空`, trigger: ['blur', 'change'] }]">
            <template #label>
                <span>{{ item.label }}</span>
                <el-tooltip v-if="[4, 5].includes(item.type)" effect="dark" :content="item.desc">
                    <el-icon :size="14" style="margin-left: 2px;margin-top: 4px;">
                        <WarningFilled/>
                    </el-icon>
                </el-tooltip>
                <span>：</span>
            </template>
            <el-input v-if="item.type === 1" v-model="form[item.code]" :placeholder="item.desc"></el-input>
            <el-input v-else-if="item.type === 2" v-model="form[item.code]" :rows="2" type="textarea"
                      :placeholder="item.desc"/>
            <el-select v-else-if="item.type === 3" v-model="form[item.code]" clearable
                       :multiple="item.multiple || false"
                       :placeholder="item.desc" style="width: 400px">
                <el-option v-for="dItem in item.dict" :key="dItem.value" :label="dItem.label"
                           :value="dItem.value"></el-option>
            </el-select>
            <div v-else-if="item.type === 4">
                <el-radio-group v-if="!item.multiple" v-model="form[item.code]" placeholder="请输入">
                    <el-row :gutter="20">
                        <el-col :span="item.dict.length < 3 ? null :8" v-for="(dItem, dIndex) in item.dict"
                                :key="dIndex">
                            <el-radio :label="String(dItem.value)">{{ dItem.label }}</el-radio>
                        </el-col>
                    </el-row>
                </el-radio-group>
                <el-checkbox-group v-else v-model="form[item.code]" :placeholder="item.desc">
                    <el-row :gutter="20">
                        <el-col :span="item.dict.length < 3 ? null :8" v-for="(dItem, dIndex) in item.dict"
                                :key="dIndex">
                            <el-checkbox :label="String(dItem.value)">{{ dItem.label }}</el-checkbox>
                        </el-col>
                    </el-row>
                </el-checkbox-group>
            </div>
            <div v-else-if="item.type === 5">
                <span
                    v-for="(row, rowIndex) in permissionRows[item.code]"
                    :key="rowIndex"
                    class="el-tag is-closable el-tag--light"
                    style="margin-right: 10px; margin-bottom: 5px;">
                    <span class="el-tag__content">{{ row.handlerName }}</span>
                    <i class="el-icon el-tag__close" @click="delPermission(item.code, row)" style="cursor: pointer;">
                        <svg-icon :icon-class="'close'"/>
                    </i>
                </span>
                <el-button type="primary" @click="openSelectDialog(item.code)">选择</el-button>
            </div>
            <el-input-number
                v-else-if="item.type === 6"
                v-model="form[item.code]"
                :placeholder="item.desc"
                :precision="item.precision ? item.precision : 0"
                :step="item.step ? Number(item.step) : 1"
                :min="item.min ? Number(item.min) : 0"
                style="width: 400px; margin-right: 10px; margin-bottom: 5px;"/>
            <div v-else-if="item.type === 7">
                <el-time-picker
                    v-if="['timepicker', 'timepickerrange'].includes(item.dateType)"
                    :is-range="item.dateType === 'timepickerrange'"
                    v-model="form[item.code]"
                    :value-format="item.dateFormat"
                    :placeholder="item.desc"/>
                <el-date-picker
                    v-else
                    clearable
                    v-model="form[item.code]"
                    :type="item.dateType ? item.dateType : 'datetime'"
                    :value-format="item.dateFormat"
                    :placeholder="item.desc"/>

            </div>
        </el-form-item>
    </el-form>
    <!-- 权限标识：会签票签选择用户 -->
    <el-dialog title="人员选择" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
        <selectUser v-model:selectUser="form[itemCode]" v-model:userVisible="userVisible"
                    :permissionRows="permissionRows[itemCode]"
                    @handleUserSelect="(checkedItemList) => handleUserSelect(checkedItemList, itemCode)"></selectUser>
    </el-dialog>
</template>

<script setup name="NodeExtList">
import SelectUser from "@/components/design/common/vue/selectUser.vue";
import {handlerFeedback} from "@/api/flow/definition.js";

const {proxy} = getCurrentInstance();

const props = defineProps({
    modelValue: {
        type: Object,
        default() {
            return {}
        }
    },
    formList: { // 表单项列表
        type: Array,
        default() {
            return []
        }
    },
    disabled: { // 是否禁止
        type: Boolean,
        default: false
    },
});

const form = ref(props.modelValue);
const userVisible = ref(false);
const permissionRows = ref({}); // 办理人表格
const itemCode = ref(''); // 办理人表格

// 表单必填校验
async function validate() {
    let isValid = null;
    await proxy.$refs.nodeExtRef.validate((valid) => {
        isValid = valid;
    });
    return isValid;
}

// 打开选择弹窗
function openSelectDialog(code) {
    itemCode.value = code
    userVisible.value = true;
}


// 获取选中用户数据
function handleUserSelect(checkedItemList, code) {
    form.value[code] = checkedItemList.map(e => {
        return e.storageId;
    }).filter(n => n);

    // 办理人表格展示
    permissionRows.value[code] = checkedItemList;
}

// 删除用户
function delPermission(code, row) {
    if (permissionRows.value[code] && permissionRows.value[code].length > 0) {
        permissionRows.value[code] = permissionRows.value[code].filter(e => e.storageId !== row.storageId);

        form.value[code] = permissionRows.value[code].map(e => {
            return e.storageId;
        }).filter(n => n);
    }
}

/** 用户名称回显 */
function getHandlerFeedback() {
    // 遍历form,判断如果type为5，则获取权限名称，并且回显
    // formList转成map，code为key
    const formMap = new Map();
    props.formList.forEach(item => {
        formMap.set(item.code, item);
    });

    for (let key in form.value) {
        if (form.value[key] && form.value[key].length > 0 && formMap.get(key) && formMap.get(key).type === 5) {
            handlerFeedback({storageIds: form.value[key]}).then(response => {
                if (response.code === 200 && response.data) {
                    permissionRows.value[key] = response.data;
                }
            });
            permissionRows.value[key] = form.value[key];
        }
    }
}

// 初始化表单数据，确保数字类型字段为正确的类型
function initFormData() {
    props.formList.forEach(item => {
        const value = form.value[item.code];
        // 处理 null、undefined、"null"、"undefined"、空字符串等情况
        if (value !== undefined && value !== null && value !== '' && value !== 'null' && value !== 'undefined') {
            if (item.type === 6) {
                const numValue = Number(value);
                if (!isNaN(numValue)) {
                    form.value[item.code] = numValue;
                }
            }
        }
    });
}

initFormData();
getHandlerFeedback()

defineExpose({
    validate
})
</script>

<style scoped lang="scss">
/* ========== 1. 表单整体 ========== */
.nodeExtForm {
  padding: 8px 16px;
  html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); }
}

/* ========== 2. 表单项通用样式 ========== */
.nodeExtForm :deep(.el-form-item) {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
  transition: var(--wf-transition, all 0.3s ease);

  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
}

.nodeExtForm :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  letter-spacing: 0.3px;
  padding-right: 8px;
}

/* ========== 3. 输入框 / 文本域 (type 1, 2) ========== */
.nodeExtForm :deep(.el-input__wrapper),
.nodeExtForm :deep(.el-textarea__inner) {
  border-radius: var(--wf-radius, 8px);
  transition: all 0.3s ease;
}

.nodeExtForm :deep(.el-input__wrapper:hover),
.nodeExtForm :deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px var(--wf-primary, #409eff) inset;
}

.nodeExtForm :deep(.el-input.is-focus .el-input__wrapper),
.nodeExtForm :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px var(--wf-primary, #409eff) inset, 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.nodeExtForm :deep(.el-textarea__inner) {
  min-height: 80px !important;
}

/* ========== 4. 下拉选择框 (type 3) ========== */
.nodeExtForm :deep(.el-select) {
  width: 100% !important;
}

.nodeExtForm :deep(.el-select .el-select__tags .el-tag) {
  border-radius: var(--wf-radius-round, 20px);
  background: var(--wf-primary-light, #ecf5ff);
  border-color: transparent;
  color: var(--wf-primary, #409eff);
}

/* ========== 5. Radio/Checkbox 卡片化 (type 4) ========== */
.nodeExtForm :deep(.el-radio-group),
.nodeExtForm :deep(.el-checkbox-group) {
  width: 100%;
}

.nodeExtForm :deep(.el-radio-group .el-row),
.nodeExtForm :deep(.el-checkbox-group .el-row) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-left: 0 !important;
  margin-right: 0 !important;
}

.nodeExtForm :deep(.el-radio-group .el-col),
.nodeExtForm :deep(.el-checkbox-group .el-col) {
  flex: 0 0 auto;
  min-width: 120px;
  max-width: 100%;
  padding-left: 0 !important;
  padding-right: 0 !important;
}

/* Radio 卡片 */
.nodeExtForm :deep(.el-radio) {
  display: flex;
  align-items: center;
  border: 1.5px solid var(--wf-border-color, #dcdfe6);
  border-radius: var(--wf-radius, 8px);
  padding: 8px 16px;
  margin-right: 0;
  background: var(--wf-bg-white, #ffffff);
  transition: all 0.3s ease;
  height: auto;
  width: 100%;
  box-sizing: border-box;

  &:hover {
    border-color: var(--wf-primary, #409eff);
    box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  }

  &.is-checked {
    border-color: var(--wf-primary, #409eff);
    background: var(--wf-primary-light, #ecf5ff);
    box-shadow: var(--wf-shadow-primary, 0 2px 8px rgba(64, 158, 255, 0.3));

    .el-radio__label {
      color: var(--wf-primary, #409eff);
    }
  }
}

/* Checkbox 卡片 */
.nodeExtForm :deep(.el-checkbox) {
  display: flex;
  align-items: center;
  border: 1.5px solid var(--wf-border-color, #dcdfe6);
  border-radius: var(--wf-radius, 8px);
  padding: 8px 16px;
  margin-right: 0;
  background: var(--wf-bg-white, #ffffff);
  transition: all 0.3s ease;
  height: auto;
  width: 100%;
  box-sizing: border-box;

  &:hover {
    border-color: var(--wf-primary, #409eff);
    box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  }

  &.is-checked {
    border-color: var(--wf-primary, #409eff);
    background: var(--wf-primary-light, #ecf5ff);
    box-shadow: var(--wf-shadow-primary, 0 2px 8px rgba(64, 158, 255, 0.3));

    .el-checkbox__label {
      color: var(--wf-primary, #409eff);
    }
  }
}

/* ========== 6. 人员选择器 (type 5) ========== */
.nodeExtForm :deep(.el-tag) {
  border-radius: var(--wf-radius-round, 20px);
  background: var(--wf-primary-light, #ecf5ff);
  color: var(--wf-primary, #409eff);
  border: none;
  padding: 4px 12px;
  margin-right: 8px !important;
  margin-bottom: 8px !important;
  box-shadow: 0 1px 3px rgba(64, 158, 255, 0.15);
  transition: all 0.3s ease;
  font-weight: 500;

  .el-tag__content {
    color: var(--wf-primary, #409eff);
  }

  .el-tag__close,
  .el-icon.el-tag__close {
    border-radius: 50%;
    background: rgba(64, 158, 255, 0.15);
    color: var(--wf-primary, #409eff);
    transition: all 0.25s ease;

    &:hover {
      background: #f56c6c;
      color: #fff;
    }
  }
}

/* 选择按钮 */
.nodeExtForm :deep(.el-button--primary) {
  border-radius: var(--wf-radius, 8px);
  background: linear-gradient(135deg, var(--wf-primary, #409eff), var(--wf-primary-dark, #2b7de9));
  border: none;
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  transition: all 0.3s ease;
  font-weight: 500;

  &:hover {
    transform: translateY(-1px);
    box-shadow: var(--wf-shadow-primary, 0 2px 8px rgba(64, 158, 255, 0.3));
  }

  &:active {
    transform: translateY(0);
  }
}

/* ========== 7. 数字输入框 (type 6) ========== */
.nodeExtForm :deep(.el-input-number) {
  width: 100% !important;
  margin-right: 0 !important;
  margin-bottom: 0 !important;
}

.nodeExtForm :deep(.el-input-number .el-input-number__decrease),
.nodeExtForm :deep(.el-input-number .el-input-number__increase) {
  border-radius: var(--wf-radius-sm, 4px);
}

/* ========== 8. 日期/时间选择器 (type 7) ========== */
.nodeExtForm :deep(.el-date-editor),
.nodeExtForm :deep(.el-time-picker) {
  width: 100%;
}

.nodeExtForm :deep(.el-date-editor .el-input__wrapper) {
  border-radius: var(--wf-radius, 8px);
}

.nodeExtForm :deep(.el-date-editor .el-input__prefix .el-icon),
.nodeExtForm :deep(.el-time-picker .el-input__prefix .el-icon) {
  color: var(--wf-primary, #409eff);
}

/* ========== 9. Tooltip 图标 ========== */
.nodeExtForm :deep(.el-form-item__label .el-icon) {
  color: var(--wf-text-secondary, #909399);
  cursor: help;
  transition: color 0.25s ease;
  vertical-align: middle;

  &:hover {
    color: var(--wf-primary, #409eff);
  }
}

/* ========== 10. 暗黑模式（统一使用 ::global 兜底） ========== */
/* 注意：冗余的 ::deep(html.dark) 嵌套已清理，下方 ::global(html.dark) 规则可覆盖所有场景 */
:global(html.dark) .nodeExtForm :deep(.el-input__wrapper),
:global(html.dark) .nodeExtForm :deep(.el-textarea__inner) {
  background: var(--wf-bg-white);
}

:global(html.dark) .nodeExtForm :deep(.el-radio),
:global(html.dark) .nodeExtForm :deep(.el-checkbox) {
  background: var(--wf-bg-white);
  border-color: var(--wf-border-color);

  &.is-checked {
    background: var(--wf-primary-light);
    border-color: var(--wf-primary);
    box-shadow: 0 0 8px rgba(64, 158, 255, 0.25);
  }
}

:global(html.dark) .nodeExtForm :deep(.el-tag) {
  background: var(--wf-primary-light);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

:global(html.dark) .nodeExtForm :deep(.el-select .el-select__tags .el-tag) {
  background: var(--wf-primary-light);
}

/* ========== 11. 用户选择对话框 ========== */
:deep(.el-dialog__header) {
  background: linear-gradient(135deg, var(--wf-primary-light, #ecf5ff), var(--wf-primary-lighter, #f0f7ff));
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
  padding: 16px 20px;
  border-radius: var(--wf-radius-lg, 12px) var(--wf-radius-lg, 12px) 0 0;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: var(--wf-primary, #409eff);
  letter-spacing: 0.5px;
}

:deep(.el-dialog__body) {
  padding: 16px 20px;
}

:global(html.dark) :deep(.el-dialog__header) {
  background: linear-gradient(135deg, var(--wf-primary-light), #1a2744);
}
</style>

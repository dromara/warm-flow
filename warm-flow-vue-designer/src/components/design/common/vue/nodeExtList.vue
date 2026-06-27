<template>
    <wf-form ref="nodeExtRef" class="nodeExtForm" :model="form" label-width="140px" size="small" :disabled="disabled"
             label-position="left">
        <wf-form-item :label="`${item.label}：`" :prop="item.code" v-for="(item, index) in formList" :key="index"
                      :rules="[{ required: item.must, message: t('nodeExtList.required', { label: item.label }), trigger: ['blur', 'change'] }]">
            <template #label>
                <span>{{ item.label }}</span>
                <wf-tooltip v-if="[4, 5].includes(item.type)" effect="dark" :content="item.desc">
                    <wf-icon :size="14" style="margin-left: 2px;margin-top: 4px;">
                        <svg-icon icon-class="ep:warning-filled" />
                    </wf-icon>
                </wf-tooltip>
                <span>：</span>
            </template>
            <wf-input v-if="item.type === 1" v-model="form[item.code]" :placeholder="item.desc"></wf-input>
            <wf-input v-else-if="item.type === 2" v-model="form[item.code]" :rows="2" type="textarea"
                      :placeholder="item.desc"/>
            <wf-select v-else-if="item.type === 3" v-model="form[item.code]" clearable
                       :multiple="item.multiple || false"
                       :placeholder="item.desc" style="width: 400px">
                <wf-option v-for="dItem in item.dict" :key="dItem.value" :label="dItem.label"
                           :value="dItem.value"></wf-option>
            </wf-select>
            <div v-else-if="item.type === 4">
                <wf-radio-group v-if="!item.multiple" v-model="form[item.code]" :placeholder="t('common.pleaseInput')">
                    <wf-row :gutter="20">
                        <wf-col :span="item.dict.length < 3 ? null :8" v-for="(dItem, dIndex) in item.dict"
                                :key="dIndex">
                            <wf-radio :label="String(dItem.value)">{{ dItem.label }}</wf-radio>
                        </wf-col>
                    </wf-row>
                </wf-radio-group>
                <wf-checkbox-group v-else v-model="form[item.code]" :placeholder="item.desc">
                    <wf-row :gutter="20">
                        <wf-col :span="item.dict.length < 3 ? null :8" v-for="(dItem, dIndex) in item.dict"
                                :key="dIndex">
                            <wf-checkbox :label="String(dItem.value)">{{ dItem.label }}</wf-checkbox>
                        </wf-col>
                    </wf-row>
                </wf-checkbox-group>
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
                <wf-button type="primary" @click="openSelectDialog(item.code)">{{ t('between.selectHandler') }}</wf-button>
            </div>
            <wf-input-number
                v-else-if="item.type === 6"
                v-model="form[item.code]"
                :placeholder="item.desc"
                :precision="item.precision ? item.precision : 0"
                :step="item.step ? Number(item.step) : 1"
                :min="item.min ? Number(item.min) : 0"
                style="width: 400px; margin-right: 10px; margin-bottom: 5px;"/>
            <div v-else-if="item.type === 7">
                <wf-time-picker
                    v-if="['timepicker', 'timepickerrange'].includes(item.dateType)"
                    :is-range="item.dateType === 'timepickerrange'"
                    v-model="form[item.code]"
                    :value-format="item.dateFormat"
                    :placeholder="item.desc"/>
                <wf-date-picker
                    v-else
                    clearable
                    v-model="form[item.code]"
                    :type="item.dateType ? item.dateType : 'datetime'"
                    :value-format="item.dateFormat"
                    :placeholder="item.desc"/>

            </div>
        </wf-form-item>
    </wf-form>
    <!-- 权限标识：会签票签选择用户 -->
    <wf-dialog :title="t('between.userSelectTitle')" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
        <selectUser v-model:selectUser="form[itemCode]" v-model:userVisible="userVisible"
                    :permissionRows="permissionRows[itemCode]"
                    @handleUserSelect="(checkedItemList) => handleUserSelect(checkedItemList, itemCode)"></selectUser>
    </wf-dialog>
</template>

<script setup lang="ts">
import { getCurrentInstance, ref } from 'vue';
import SelectUser from "@/components/design/common/vue/selectUser.vue";
import {handlerFeedback} from "@/api/flow/definition";
import { useI18n } from '@/i18n';

defineOptions({ name: 'NodeExtList' });

const {proxy} = getCurrentInstance()!;
const { t } = useI18n();

interface NodeExtListProps {
  /** 扩展属性表单数据（v-model） */
  modelValue?: Record<string, any>;
  /** 表单项列表（节点扩展属性定义） */
  formList?: any[];
  /** 是否只读 */
  disabled?: boolean;
}
const props = withDefaults(defineProps<NodeExtListProps>(), {
  modelValue: () => ({}),
  formList: () => [],
  disabled: false,
});

const form = ref<Record<string, any>>(props.modelValue);
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
function openSelectDialog(code: string) {
    itemCode.value = code
    userVisible.value = true;
}


// 获取选中用户数据
function handleUserSelect(checkedItemList: any[], code: string) {
    form.value[code] = checkedItemList.map(e => {
        return e.storageId;
    }).filter(n => n);

    // 办理人表格展示
    permissionRows.value[code] = checkedItemList;
}

// 删除用户
function delPermission(code: string, row: any) {
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
@import './nodeExtList.scoped.scss';
</style>

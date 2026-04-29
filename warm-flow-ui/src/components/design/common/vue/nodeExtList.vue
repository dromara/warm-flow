<template>
    <el-form ref="nodeExtRef" class="nodeExtForm" :model="form" label-width="150px" size="small" :disabled="disabled"
             label-position="left">
        <el-form-item :label="`${item.label}：`" :prop="item.code" v-for="(item, index) in formList" :key="index"
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

<style scoped lang="scss"></style>

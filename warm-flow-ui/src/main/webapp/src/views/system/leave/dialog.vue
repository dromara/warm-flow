<template>
  <div class="app-container">
    <!-- 添加或修改OA 请假申请对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" v-if="open" append-to-body>
      <el-form ref="leaveRef" :model="form" :rules="rules" label-width="110px" :disabled="disabled">
        <el-row>
          <el-col :span="12">
            <el-form-item label="请假类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择请假类型" filterable :disabled="disabled">
                <el-option
                  v-for="dict in leave_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="请假原因" prop="reason">
              <el-input v-model="form.reason" type="textarea" placeholder="请输入内容" :disabled="disabled"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                clearable size="small"
                v-model="form.startTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择开始时间"
                @change="calculateLeaveDays"
                :disabled="disabled">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                clearable size="small"
                v-model="form.endTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择结束时间"
                @change="calculateLeaveDays"
                :disabled="disabled">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="请假天数" prop="day">
                <el-input-number
                  v-model="form.day"
                  placeholder="请输入请假天数"
                  controls-position="right"
                  :min="0"
                  :disabled="disabled"
                />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="自定义流程状态" prop="flowStatus">
            <el-input v-model="flowStatus" />
          </el-form-item>
          <el-tooltip class="item" effect="dark" content="warm-flow工作流默认内置了一套流程状态，如果此字段填写可以自定义流程状态">
            <el-icon :size="14" class="ml5 mt10"><WarningFilled /></el-icon>
          </el-tooltip>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="抄送人" prop="additionalHandler">
              <el-select v-model="form.additionalHandler" multiple collapse-tags :disabled="disabled" :clearable="!disabled" filterable placeholder="请选择抄送人员">
                <el-option-group
                  v-for="groupOption in groupOptions"
                  :key="groupOption.label"
                  :label="groupOption.label"
                  :disabled="disabled">
                  <el-option
                    v-for="item in groupOption.options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false" v-if="!disabled">取消</el-button>
          <el-button type="primary" v-if="!disabled" @click="submitForm">确定</el-button>
          <el-button type="primary" v-if="disabled" @click="handleSubmit">提交审批</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Dialog">
import { getLeave, addLeave, updateLeave, submit } from "@/api/system/leave";
import {listUser} from "@/api/system/user";

const { proxy } = getCurrentInstance();
const { flow_status, leave_type } = proxy.useDict('flow_status', 'leave_type');
const open = ref(false);
const disabled = ref(false);
const title = ref("");
const groupOptions = ref([]);
// 自定义流程状态扩展
const flowStatus = ref("");

const data = reactive({
  form: {
    type: null,
    reason: null,
    startTime: null,
    endTime: null,
    day: null,
    flowStatus: null,
    additionalHandler: []
  },
  rules: {
    type: [
      { required: true, message: "请假类型不能为空", trigger: "change" }
    ],
    reason: [
      { required: true, message: "请假原因不能为空", trigger: "blur" }
    ],
    startTime: [
      { required: true, message: "开始时间不能为空", trigger: "blur" },
      { validator: validateDateRange, trigger: 'change' }
    ],
    endTime: [
      { required: true, message: "结束时间不能为空", trigger: "blur" },
      { validator: validateDateRange, trigger: 'change' }
    ],
    day: [
      { required: true, message: "请假天数不能为空", trigger: "blur" },
      { validator: validateDay, trigger: 'blur' }
    ]
  }
});

const props = defineProps({
  propsGetList: {
    type: Function,
    required: true,
  },
});

const emit = defineEmits(["refresh"]);
const { form, rules } = toRefs(data);

/** 提交审批按钮操作 */
function handleSubmit() {
  submit(form.value.id, flowStatus.value).then(() => {
    props.propsGetList();
    proxy.$modal.msgSuccess("提交审批成功");
    open.value = false;
  });
}

function getUser(){
  listUser().then(response => {
    let groupOption = {
      label: '用户',
      options: response.rows.map(item =>{
          return {
            value: item.userId,
            label: item.nickName
          }
        }
      )
    }
    groupOptions.value.push(groupOption);
  });
}

/** 打开OA 请假申请弹框 */
async function show(id, _disabled, _title) {
  reset();
  disabled.value = _disabled
  if (id) {
    await getLeave(id).then(response => {
      form.value = response.data;
      flowStatus.value = response.data.flowStatus;
    });
  }
  open.value = true
  if (disabled.value) {
    title.value = "详情"
  } else if (id) {
    title.value = _title || "修改"
  } else {
    title.value = "新增"
  }
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

function validateDateRange(rule, value, callback) {
  const { startTime, endTime } = form.value;
  if (startTime && endTime) {
    if (new Date(startTime) > new Date(endTime)) {
      callback(new Error('开始时间不能晚于结束时间'));
    } else {
      callback();
    }
  } else {
    callback();
  }
}
function validateDay(rule, value, callback) {
  const { startTime, endTime, day } = form.value;
  if (startTime && endTime) {
    const timeDiff = new Date(endTime) - new Date(startTime);
    const calculatedDays = Math.ceil(timeDiff / (1000 * 60 * 60 * 24))+1;
    if (day != calculatedDays) {
      callback(new Error('请假天数应等于结束时间和开始时间之间的天数差'));
    } else {
      callback();
    }
  } else {
    callback();
  }
}
// 表单重置
function reset() {
  form.value = {
    id: null,
    tenantId: null,
    type: null,
    reason: null,
    startTime: null,
    endTime: null,
    day: null,
    instanceId: null,
    nodeCode: null,
    nodeName: null,
    flowStatus: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  };
  disabled.value = false;
  flowStatus.value = "";
  proxy.resetForm("leaveRef");
}

function calculateLeaveDays() {
  if (form.value.startTime && form.value.endTime) {
    const start = new Date(form.value.startTime);
    const end = new Date(form.value.endTime);
    const diffTime = Math.abs(end - start);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // 包含开始和结束的当天
    form.value.day = diffDays;
  } else {
    form.value.day = '';
  }
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["leaveRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateLeave(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          emit('refresh');
        });
      } else {
        addLeave(form.value, flowStatus.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          emit('refresh');
        });
      }
    }
  });
}

getUser();

defineExpose({
  show,
});
</script>

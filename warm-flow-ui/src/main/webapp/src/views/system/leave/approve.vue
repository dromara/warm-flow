<template>
  <div class="app-container">
    <!-- 添加或修改OA 请假申请对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" v-if="open" append-to-body>
      <el-form ref="leaveApproveRef" :model="form" :rules="rules" label-width="110px" :disabled="disabled">
        <el-row>
          <el-col :span="12">
            <el-form-item label="请假类型" prop="type">
              <div v-if="testLeaveVo && Object.keys(testLeaveVo).length">{{ getLeaveTypeLabel(form.type) }}</div>
              <el-select v-else v-model="form.type" placeholder="请选择请假类型">
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
              <div v-if="testLeaveVo && Object.keys(testLeaveVo).length">{{ form.reason }}</div>
              <el-input v-else v-model="form.reason" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <div v-if="testLeaveVo && Object.keys(testLeaveVo).length">{{ form.startTime }}</div>
              <el-date-picker
                v-else
                clearable size="small"
                v-model="form.startTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <div v-if="testLeaveVo && Object.keys(testLeaveVo).length">{{ form.endTime }}</div>
              <el-date-picker
                v-else
                clearable size="small"
                v-model="form.endTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="请假天数" prop="day">
              <div v-if="testLeaveVo && Object.keys(testLeaveVo).length">{{ form.day }}</div>
              <el-input-number
                v-else
                v-model="form.day"
                placeholder="请输入请假天数"
                controls-position="right"
                :min="0"/>
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
            <el-form-item label="跳转节点" prop="showAnyNode" v-if="showAnyNode">
              <el-select v-model="nodeCode" placeholder="请选择跳转节点">
                <el-option
                    v-for="dict in anyNode"
                    :key="dict.nodeCode"
                    :label="dict.nodeName"
                    :value="dict.nodeCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider></el-divider>
        <el-form-item label="审批意见" prop="message" v-if="showApprovalFields">
          <el-input v-model="form.message" type="textarea" placeholder="请输入审批意见" :autosize="{ minRows: 4, maxRows: 4 } "/>
        </el-form-item>
      </el-form>
      <template #footer v-if="showApprovalFields">
        <div class="dialog-footer">
          <el-button type="primary" @click="handleBtn('PASS')">审批通过</el-button>
          <el-button @click="handleBtn('REJECT')">退回</el-button>
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Approve">
import { getLeave, handle } from "@/api/system/leave";
import { anyNodeList } from "@/api/flow/execute";

const { proxy } = getCurrentInstance();
const { flow_status, leave_type } = proxy.useDict('flow_status', 'leave_type');

const props = defineProps({
  /* 业务id */
  modelValue: {
    type: String,
    default: "",
  },
  /* 实例id */
  taskId: {
    type: String,
    default: "",
  },
  /* 是否可以标编辑 */
  disabled: {
    type: Boolean,
    default: false,
  },
  /* 传递的testLeaveVo数据 */
  testLeaveVo: {
    type: Object,
    default: () => ({}),
  }
})
// 是否显示弹出层
const open = ref(false);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const showAnyNode = ref(false);
// 是否显示弹出层
const anyNode = ref([]);
// 指定跳转节点
const nodeCode = ref("");
// 自定义流程状态扩展
const flowStatus = ref("");

const data = reactive({
  form: {},
  rules: {
    type: [
      { required: true, message: "请假类型不能为空", trigger: "change" }
    ],
    reason: [
      { required: true, message: "请假原因不能为空", trigger: "blur" }
    ],
    startTime: [
      { required: true, message: "开始时间不能为空", trigger: "blur" }
    ],
    endTime: [
      { required: true, message: "结束时间不能为空", trigger: "blur" }
    ],
    day: [
      { required: true, message: "请假天数不能为空", trigger: "blur" }
    ],
  }
});
// 控制审批意见和按钮是否显示
const showApprovalFields = ref(true);

const { form, rules } = toRefs(data);
const emit = defineEmits(["refresh"]);

function getInfo() {
  reset();
  if (props.testLeaveVo && Object.keys(props.testLeaveVo).length) {
    form.value = { ...form.value, ...props.testLeaveVo };
    showApprovalFields.value = false; // 隐藏审批意见和按钮
  } else {
    getLeave(props.modelValue).then(response => {
      if (!response.data) {
        proxy.$modal.alertWarning("待办任务不存在");
      }
      form.value = response.data;
      anyNodeList(form.value.instanceId, form.value.nodeCode).then(response => {
        if (response.code === 200 && response.data !== null && response.data !== undefined) {
          props.showAnyNode = true;
          props.anyNode = response.data;
        }
      });
    });
  }
  title.value = "办理"
  open.value = true
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    message: null,
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
  flowStatus.value = "";
  proxy.resetForm("leaveApproveRef");
}

/** 审核通过按钮 */
function handleBtn(skipType) {
  proxy.$refs["leaveApproveRef"].validate(valid => {
    if (valid) {
      handle(form.value, props.taskId, skipType, form.value.message, nodeCode.value, flowStatus.value).then(() => {
        proxy.$modal.msgSuccess("办理成功");
        open.value = false;
        emit('refresh');
      });
    }
  });
}

// 获取请假类型标签
function getLeaveTypeLabel(value) {
  const type = leave_type.value.find(dict => dict.value === value);
  return type ? type.label : '';
}

getInfo();
</script>

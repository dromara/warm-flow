<template>
  <div class="app-container">
    <!-- 添加或修改OA 请假申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" v-if="open" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" :disabled="disabled">
        <el-form-item label="请假类型" prop="type">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ getLeaveTypeLabel(form.type) }}</div>
          <el-select v-else v-model="form.type" placeholder="请选择请假类型">
            <el-option
              v-for="dict in dict.type.leave_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.reason }}</div>
          <el-input v-else v-model="form.reason" type="textarea" placeholder="请输入内容" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.startTime }}</div>
          <el-date-picker v-else clearable size="small" v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.endTime }}</div>
          <el-date-picker v-else clearable size="small" v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="请假天数" prop="day">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.day }}</div>
          <el-input v-else v-model="form.day" placeholder="请输入请假天数" />
        </el-form-item>
        <el-form-item label="自定义流程状态" prop="flowStatus">
          <el-input v-model="flowStatus"/>
          <el-tooltip class="item" effect="dark" content="warm-flow工作流默认内置了一套流程状态，如果此字段填写可以自定义流程状态">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </el-form-item>
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
        <el-divider></el-divider>
        <el-form-item label="审批意见" prop="message" v-if="showApprovalFields">
          <el-input v-model="form.message" type="textarea" placeholder="请输入审批意见" :autosize="{ minRows: 4, maxRows: 4 }" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" v-if="showApprovalFields">
        <el-button type="primary" @click="handleBtn('PASS')">审批通过</el-button>
        <el-button @click="handleBtn('REJECT')">退回</el-button>
        <el-button @click="rejectLast" v-if="!showAnyNode">驳回到上一个任务</el-button>
        <el-button @click="taskBack" v-if="!showAnyNode">拿回</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getLeave, handle, rejectLast, taskBack} from "@/api/system/leave";
import { anyNodeList } from "@/api/flow/execute";

export default {
  name: "Approve",
  dicts: ['flow_status', 'leave_type'],
  props: {
    /* 业务id */
    value: {
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

    /* 是否显示任意任意跳转选择框 */
    showAnyNode: {
      type: Boolean,
      default: false,
    },

    /* 传递的infoVo数据 */
    infoVo: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {},
      // 是否显示弹出层
      anyNode: [],
      // 指定跳转节点
      nodeCode: "",
      // 自定义流程状态扩展
      flowStatus: "",
      // 表单校验
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
      },
      showApprovalFields: true // 控制审批意见和按钮是否显示
    };
  },
  created() {
    this.reset();
    if (this.infoVo && Object.keys(this.infoVo).length) {
      this.form = { ...this.form, ...this.infoVo };
      this.showApprovalFields = false; // 隐藏审批意见和按钮
    } else {
      getLeave(this.value).then(response => {
        if (!response.data) {
          this.$modal.alertWarning("待办任务不存在");
        }
        this.form = response.data;
        if (this.showAnyNode) {
          anyNodeList(this.form.instanceId).then(response => {
            if (response.code === 200 && response.data !== null && response.data !== undefined) {
              this.anyNode = response.data;
            }
          });
        }
      });
    }
    this.title = "办理"
    this.open = true
  },
  methods: {
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        message: null,
        id: null,
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
      this.flowStatus = "";
      this.resetForm("form");
    },
    /** 审核通过按钮 */
    handleBtn(skipType) {
      this.$refs["form"].validate(valid => {
        if (valid) {
          handle(this.form, this.taskId, skipType, this.form.message, this.nodeCode, this.flowStatus).then(response => {
            this.$modal.msgSuccess("办理成功");
            this.open = false;
            this.$emit('refresh');
          });
        }
      });
    },

    /** 驳回到上一个任务 */
    rejectLast() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          rejectLast(this.form, this.taskId, this.form.message, this.flowStatus).then(response => {
            this.$modal.msgSuccess("驳回到上一个任务成功");
            this.open = false;
            this.$emit('refresh');
          });
        }
      });
    },

    /** 拿回 */
    taskBack() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          taskBack(this.form, this.taskId, this.form.message, this.flowStatus).then(response => {
            this.$modal.msgSuccess("拿回到最近办理的任务");
            this.open = false;
            this.$emit('refresh');
          });
        }
      });
    },

    // 获取请假类型标签
    getLeaveTypeLabel(value) {
      const type = this.dict.type.leave_type.find(dict => dict.value === value);
      return type ? type.label : '';
    }
  }
};
</script>

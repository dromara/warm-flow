<template>
  <div class="app-container">
    <!-- 添加或修改OA 请假申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" v-if="open" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" :disabled="disabled">
          <el-form-item label="合同名称" prop="contractName">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.contractName }}</div>
            <el-input v-else :disabled="true" v-model="form.contractName" placeholder="请输入合同名称" />
          </el-form-item>
          <el-form-item label="结构和性质" v-if="form.nodeCode >= 1 || form.nodeType === 2 || form.nodeCode === 'negotiate'" prop="structureAndNature">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.structureAndNature }}</div>
            <el-input v-else :disabled="form.nodeCode > 1 || form.nodeCode === 'negotiate'" v-model="form.structureAndNature" placeholder="请输入结构和性质" />
          </el-form-item>
          <el-form-item label="拟定条件" v-if="form.nodeCode >= 2 || form.nodeType === 2 || form.nodeCode === 'negotiate'" prop="proposedConditions">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.proposedConditions }}</div>
            <el-input v-else :disabled="form.nodeCode > 2 || form.nodeCode === 'negotiate'" v-model="form.proposedConditions" type="textarea" placeholder="请输入内容" />
          </el-form-item>
          <el-form-item label="谈判内容" v-if="form.nodeCode >= 3 || form.nodeType === 2 || form.nodeCode === 'negotiate'">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.negotiationContent }}</div>
            <editor v-else :disabled="form.nodeCode > 3 || form.nodeCode === 'negotiate'" v-model="form.negotiationContent" :min-height="192"/>
          </el-form-item>
          <el-form-item label="谈判附件"  v-if="form.nodeCode >= 3 || form.nodeType === 2" prop="fileId">
            <file-upload :disabled="form.nodeCode > 3 || form.nodeCode === 'negotiate'" v-model="form.fileId"/>
          </el-form-item>
          <el-form-item label="谈判结果" v-if="form.nodeCode >= 3 || form.nodeType === 2 || form.nodeCode === 'negotiate'" prop="negotiationResult">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.negotiationResult }}</div>
            <el-input v-else :disabled="form.nodeCode > 3 || form.nodeCode === 'negotiate'" v-model="form.negotiationResult" placeholder="请输入谈判结果" />
          </el-form-item>
          <el-form-item label="协商方案" v-if="form.nodeCode === 'negotiate' || form.adjustmentScheme || form.nodeType === 2" prop="adjustmentScheme">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.adjustmentScheme }}</div>
            <el-input v-else :disabled="form.nodeCode !== 'negotiate'" v-model="form.adjustmentScheme" type="textarea" placeholder="请输入内容" />
          </el-form-item>
          <el-form-item label="签订日期" v-if="form.nodeCode >= 4 || form.nodeType === 2" prop="signDate">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.signDate }}</div>
            <el-date-picker  v-else :disabled="form.nodeCode > 4" clearable
                            v-model="form.signDate"
                            type="date"
                            value-format="yyyy-MM-dd"
                            placeholder="请选择签订日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="签订人" v-if="form.nodeCode >= 4 || form.nodeType === 2" prop="signer">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.signer }}</div>
            <el-input v-else :disabled="form.nodeCode > 4" v-model="form.signer" placeholder="请输入签订人" />
          </el-form-item>
          <el-form-item label="备案日期" v-if="form.nodeCode >= 5 || form.nodeType === 2" prop="filingDate">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.filingDate }}</div>
            <el-date-picker v-else :disabled="form.nodeCode > 5" clearable
                            v-model="form.filingDate"
                            type="date"
                            value-format="yyyy-MM-dd"
                            placeholder="请选择备案日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="备案部门" v-if="form.nodeCode >= 5 || form.nodeType === 2" prop="filingDepartment">
            <div v-if="infoVo && Object.keys(infoVo).length">{{ form.filingDepartment }}</div>
            <el-input v-else :disabled="form.nodeCode > 5" v-model="form.filingDepartment" placeholder="请输入备案部门" />
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
import {getProcess, handle, rejectLast, taskBack} from "@/api/system/process";
import { anyNodeList } from "@/api/flow/execute";

export default {
  name: "Approve",
  dicts: ['flow_status'],
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
        contractName: [
          { required: true, message: "合同名称不能为空", trigger: "blur" }
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
      getProcess(this.value).then(response => {
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
  }
};
</script>

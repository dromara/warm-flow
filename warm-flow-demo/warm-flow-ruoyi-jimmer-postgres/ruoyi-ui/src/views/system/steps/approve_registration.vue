<template>
  <div class="app-container">
    <!-- 添加或修改OA 请假申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" v-if="open" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" :disabled="disabled">
        <el-form-item label="采购名称" prop="purchaseName">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.purchaseName }}</div>
          <el-input v-else :disabled="true" v-model="form.purchaseName" placeholder="请输入采购名称" />
        </el-form-item>
        <el-form-item label="采购计划" prop="purchasePlan">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.purchasePlan }}</div>
          <el-input v-else :disabled="true" v-model="form.purchasePlan" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="是否加急" prop="urgent">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.urgent === 0 ? '是': '否' }}</div>
          <el-select v-else :disabled="true" v-model="form.urgent" clearable>
            <el-option label="否" value="1"></el-option>
            <el-option label="是" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="加急采购计划" prop="urgentPurchasePlan" v-if="form.urgent === 0">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.urgentPurchasePlan }}</div>
          <el-input v-else :disabled="true" v-model="form.urgentPurchasePlan" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="按需采购" prop="onDemandProcurement">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.onDemandProcurement }}</div>
          <el-input v-else :disabled="true" v-model="form.onDemandProcurement" placeholder="请输入按需采购" />
        </el-form-item>
        <el-form-item label="提供物品" prop="provideItems">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.provideItems }}</div>
          <el-input v-else :disabled="true" v-model="form.provideItems" placeholder="请输入提供物品" />
        </el-form-item>
        <el-form-item label="产品验收" prop="productInspection">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.productInspection === 0 ? '合格': '不合格' }}</div>
          <el-select v-else :disabled="true" v-model="form.productInspection" clearable>
            <el-option label="合格" value="0"></el-option>
            <el-option label="不合格" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="登记记录" prop="recordEntry">
          <div v-if="infoVo && Object.keys(infoVo).length">{{ form.recordEntry }}</div>
          <el-input v-else v-model="form.recordEntry" type="textarea" placeholder="请输入内容" />
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
import { anyNodeList } from "@/api/flow/execute";
import {getSteps, handle, rejectLast, taskBack} from "@/api/system/steps";

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
        purchaseName: [
          { required: true, message: "采购名称不能为空", trigger: "blur" }
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
      getSteps(this.value).then(response => {
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
        id: null,
        purchaseName: null,
        purchasePlan: null,
        urgentPurchasePlan: null,
        onDemandProcurement: null,
        provideItems: null,
        productInspection: null,
        issueInvoice: null,
        recordEntry: null,
        returnItems: null,
        warehousing: null,
        receiveItems: null,
        maintainRecords: null,
        instanceId: null,
        nodeCode: null,
        nodeName: null,
        nodeType: null,
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

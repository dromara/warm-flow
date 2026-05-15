<template>
  <div class="app-container">
    <!-- 添加或修改OA 自定义流程表单对话框 -->
    <el-dialog :title="formName" :visible.sync="showFormDialog" width="500px" v-if="showFormDialog" append-to-body @close="cancel">
      <iframe ref="FormCreate" :src="url" :style="`width: 100%; max-height: 60vh; height: ${offsetHeight}px; border: none;`"/>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";
export default {
  name: "formDialog",
  props: {
    // 来源：0待办-办理 1已办-流程历史记录 2已发布的表单设计
    type: {
      type: String,
      default: "0"
    },
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
    // 是否显示弹出层
    visible: {
      type: Boolean,
      default: true
    },
    // 表单名称
    formName: {
      type: String,
      default: "办理"
    },
    // 表单id，查找表单设计内容
    formId: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      url: "",
      offsetHeight: null, // iframe高度
      showFormDialog: true
    };
  },
  mounted() {
    this.url = `${process.env.VUE_APP_FLOW_API}/warm-flow-ui/index.html?type=formCreate&Authorization=Bearer ` + getToken();
    window.addEventListener("message", this.handleMessage);
  },
  beforeDestroy() {
    window.removeEventListener("message", this.handleMessage);
  },
  methods: {
    handleMessage(event) {
      switch (event.data.method) {
        case "formInit":
          let data = {
            type: this.type,
            formId: this.formId,
            taskId: this.taskId,
            disabled: this.disabled
          };
          this.$refs.FormCreate.contentWindow.postMessage({ method: "formInit", data }, '*');
          break;
        case "getOffsetHeight":
          // 获取子页面内容高度
          this.offsetHeight = event.data.offsetHeight;
          break;
        case "submitSuccess":
          this.$modal.msgSuccess("办理成功");
          this.cancel();
          break;
      }
    },
    // 取消按钮
    cancel() {
      this.showFormDialog = false;
      this.$emit("update:visible", false);
      this.$refs.FormCreate.contentWindow.postMessage({ method: "reset" }, '*');
      this.$emit('refresh');
    }
  }
};
</script>

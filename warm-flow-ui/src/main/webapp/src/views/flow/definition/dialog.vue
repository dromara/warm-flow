<template>
  <div class="app-container">
    <!-- 添加或修改流程定义对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" v-if="open" label-width="150px" append-to-body>
      <el-form ref="definitionRef" :model="form" :rules="rules"  :disabled="disabled">
        <el-form-item label="流程编码" prop="flowCode">
          <el-input v-model="form.flowCode" placeholder="请输入流程编码" maxlength="40" show-word-limit/>
        </el-form-item>
        <el-form-item label="流程名称" prop="flowName">
          <el-input v-model="form.flowName" placeholder="请输入流程名称" maxlength="100" show-word-limit/>
        </el-form-item>
        <el-form-item label="流程版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入流程版本" maxlength="20" show-word-limit/>
        </el-form-item>
        <el-form-item label="扩展字段" prop="ext" description="比如流程分类">
          <el-input v-model="form.ext" placeholder="请输入流程版本" maxlength="20" show-word-limit/>
        </el-form-item>
        <el-form-item label="是否发布" prop="isPublish" v-if="disabled">
          <el-select v-model="form.isPublish" placeholder="请选择是否开启流程">
            <el-option
              v-for="dict in is_publish"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="审批表单是否自定义" prop="formCustom">
          <el-radio-group v-model="form.formCustom">
            <el-radio label="N" >否</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="审批表单路径" prop="formPath">
          <el-input v-model="form.formPath" placeholder="请输入审批表单路径" maxlength="100" show-word-limit/>
        </el-form-item>
        <el-form-item label="监听器类型" prop="formPath">
          <el-select v-model="form.listenerType" multiple>
            <el-option label="任务创建" value="create"></el-option>
            <el-option label="任务开始办理" value="start"></el-option>
            <el-option label="分派监听器" value="assignment"></el-option>
            <el-option label="权限认证" value="permission"></el-option>
            <el-option label="任务完成" value="finish"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="监听器路径" prop="formPath">
          <el-input type="textarea" v-model="form.listenerPath" rows="8"></el-input>
          <el-tooltip class="item" effect="dark" content="输入监听器的路径，以@@分隔，顺序与监听器类型一致">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" v-if="!disabled" @click="submitForm">确 定</el-button>
          <el-button @click="cancel" v-if="!disabled">取 消</el-button>
          <el-button @click="cancel" v-if="disabled">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Dialog">
import { getDefinition, addDefinition, updateDefinition } from "@/api/flow/definition";

const { proxy } = getCurrentInstance();
const { isPublish } = proxy.useDict('isPublish');

const open = ref(false);
const disabled = ref(false);
const title = ref("");
const single = ref(true);

const data = reactive({
  // 表单参数
  form: {},
  // 表单校验
  rules: {
    flowCode: [
      { required: true, message: "流程编码不能为空", trigger: "blur" }
    ],
    flowName: [
      { required: true, message: "流程名称不能为空", trigger: "blur" }
    ],
    isPublish: [
      { required: true, message: "是否开启流程不能为空", trigger: "change" }
    ],
    formCustom: [
      { required: true, message: "请选择审批表单是否自定义", trigger: "change" }
    ],
  }
});

const emit = defineEmits(["refresh"]);
const { form, rules } = toRefs(data);

/** 打开流程定义弹框 */
async function show(id, _disabled) {
  reset();
  disabled.value = _disabled
  if (id) {
    await getDefinition(id).then(response => {
      form.value = response.data;
      if (form.value.listenerType) {
        form.value.listenerType = form.value.listenerType.split(",")
      }
    });
  }
  open.value = true
  if (disabled.value) {
    title.value = "详情"
  } else if (id) {
    title.value = "修改"
  } else {
    title.value = "新增"
  }
}
// 取消按钮
function cancel() {
  open.value = false;
  reset();
}
// 表单重置
function reset() {
  form.value = {
    id: null,
    flowCode: null,
    flowName: null,
    version: null,
    isPublish: null,
    formCustom: null,
    formPath: null,
    createTime: null,
    updateTime: null,
    delFlag: null
  };
  proxy.resetForm("definitionRef");
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["definitionRef"].validate(valid => {
    if (valid) {
      form.value.listenerType = form.value.listenerType.join(",")
      if (form.value.id != null) {
        updateDefinition(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          emit('refresh');
        });
      } else {
        addDefinition(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          emit('refresh');
        });
      }
    }
  });
}


defineExpose({
  show,
});
</script>

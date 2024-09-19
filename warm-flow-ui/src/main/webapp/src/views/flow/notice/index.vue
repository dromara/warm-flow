<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="任务名称" prop="nodeName">
        <el-input
          v-model="queryParams.nodeName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程状态" prop="flowStatus">
        <el-select v-model="queryParams.nodeType" placeholder="请选择流程状态" clearable>
          <el-option
            v-for="dict in flow_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="抄送人" prop="flowName">
        <el-input
          v-model="queryParams.flowName"
          placeholder="请输入任抄送人"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="instanceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" fixed />
      <el-table-column label="序号" width="55" align="center" key="id" prop="id">
        <template  #default="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="流程实例id" width="100" align="center" prop="id" :show-overflow-tooltip="true"/>
      <el-table-column label="流程名称" align="center" prop="flowName" :show-overflow-tooltip="true"/>
      <el-table-column label="任务名称" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="抄送人" align="center" prop="approver" :show-overflow-tooltip="true"/>
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template  #default="scope">
          <dict-tag :options="flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
        <template  #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" fixed="right" class-name="small-padding fixed-width">
        <template  #default="scope">
          <el-button
            size="small"
            type="text"
            @click="toFlowImage(scope.row.id)"
          >流程图</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <component v-bind:is="approve" v-model="businessId" :taskId="taskId" @refresh="getList"></component>
    <el-dialog title="流程图" v-model="flowChart" width="80%">
      <img :src="imgUrl" width="1000" style="margin:0 auto"/>
    </el-dialog>

    <!-- 权限标识：选择用户 -->
    <el-dialog :title="`${dialogTitle}用户选择`" v-if="userVisible" :visible.sync="userVisible" width="80%" append-to-body>
      <selectUser :postParams="postParams" :type="dialogTitle" :selectUser.sync="form.assigneePermission" :userVisible.sync="userVisible" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script setup name="Notice">
import * as api from "@/api/flow/execute";
import { flowImage } from "@/api/flow/definition";
import selectUser from "@/views/components/selectUser";

const { proxy } = getCurrentInstance();
const { flow_status } = proxy.useDict('flow_status');
const loading = ref(true);
const imgUrl = ref("");
const flowChart = ref(false);
const ids = ref([]);
const single = ref(true); // 非单个禁用
const multiple = ref(true); // 非多个禁用
const showSearch = ref(true); // 显示搜索条件
const total = ref(0);
const instanceList = ref([]); // 流程实例表格数据
// 业务审批页面
const approve = ref(null);
const taskId = ref("");
const businessId = ref("");
const dialogTitle = ref("");
const userVisible = ref(false);

const data = reactive({
  // 查询参数
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    nodeName: null,
    flowStatus: null,
    approver: null,
    flowName: null,
    createTime: null,
  },
  // 表单参数
  form: {},
  // 表单校验
  rules: {
    assigneePermission: [
      { required: true, message: "请选择", trigger: "change" }
    ],
  },
  postParams: {}
});
const { queryParams, form, rules, postParams } = toRefs(data);

/** 查询流程实例列表 */
function getList() {
  loading.value = true;
  api.copyPage(queryParams.value).then(response => {
    instanceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryForm");
  handleQuery();
}
// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length!==1
  multiple.value = !selection.length
}
/** 办理按钮操作 */
function handle(row) {
  taskId.value = row.id
  businessId.value = row.businessId
  if (row.formCustom === 'N' && row.formPath) {
    // 实际情况是，不同条件对应不同的页面，所以用动态导入组件
    import(/* @vite-ignore */`../../../../views/${row.formPath}`).then((module) => {
      approve.value = module.default;
    });
  }
}
function toFlowImage(instanceId) {
  flowImage(instanceId).then(response => {
    flowChart.value = true
    console.log(response.data)
    imgUrl.value = "data:image/gif;base64," + response.data;
  });
}
/** 转办|加签|委派|减签弹框显示按钮操作 */
function transferShow(row, title) {
  form.value.assigneePermission = null;
  taskId.value = row.id;
  dialogTitle.value = title;
  api.getTaskById(taskId.value).then(res => {
    form.value.assigneePermission = res.data.assigneePermission ? res.data.assigneePermission.split(",") : [];
  });
  userVisible.value = true;
  let operatorTypeObj = {
    "转办": "1",
    "加签": "6",
    "委派": "3",
    "减签": "7"
  };
  postParams.value = {
    url: "interactiveTypeSysUser",
    taskId: row.id,
    operatorType: operatorTypeObj[title]
  };
}
// 获取选中用户数据
function handleUserSelect(checkedItemList) {
  form.value.assigneePermission = checkedItemList.map(e => {
    return e.userId;
  });
  let assigneePermission = JSON.parse(JSON.stringify(form.value.assigneePermission));
  let operatorTypeObj = {
    "转办": "2",
    "加签": "6",
    "委派": "3",
    "减签": "7"
  };
  api.interactiveType(taskId.value, Array.isArray(assigneePermission) ? assigneePermission.join(',') : assigneePermission, operatorTypeObj[dialogTitle.value]).then(() => {
    proxy.$modal.msgSuccess(`${dialogTitle.value}成功`);
    getList();
  });
}

getList();
</script>

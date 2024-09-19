<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="任务名称" prop="nodeName">
        <el-input
          v-model="queryParams.nodeName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程状态" prop="flowStatus">
        <el-select v-model="queryParams.flowStatus" placeholder="请选择流程状态" clearable>
          <el-option
            v-for="dict in flow_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          clearable size="small"
          v-model="queryParams.createTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="instanceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" fixed />
      <el-table-column label="序号" width="55" align="center">
        <template #default="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="id" width="100" align="center" prop="id" :show-overflow-tooltip="true"/>
      <el-table-column label="流程名称" align="center" prop="flowName" :show-overflow-tooltip="true"/>
      <el-table-column label="任务名称" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="审批人" align="center" prop="approver" :show-overflow-tooltip="true"/>
      <el-table-column label="转办人" align="center" prop="transferredBy" :show-overflow-tooltip="true"/>
      <el-table-column label="委派人" align="center" prop="delegate" :show-overflow-tooltip="true"/>
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="激活状态" align="center" prop="activityStatus" width="140" sortable="custom" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="activity_status" :value="scope.row.activityStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            @click="handle(scope.row)"
            v-hasPermi="['flow:execute:handle']"
          >办理</el-button>
          <el-button
            size="small"
            type="text"
            @click="transferShow(scope.row, '转办')"
            v-hasPermi="['flow:execute:handle']"
          >转办</el-button>
          <el-button
            size="small"
            type="text"
            @click="transferShow(scope.row, '加签')"
            v-hasPermi="['flow:execute:handle']"
          >加签</el-button>
          <el-button
            size="small"
            type="text"
            @click="transferShow(scope.row, '委派')"
            v-hasPermi="['flow:execute:handle']"
          >委派</el-button>
          <el-button
            size="small"
            type="text"
            @click="transferShow(scope.row, '减签')"
            v-hasPermi="['flow:execute:handle']"
          >减签</el-button>
          <el-button
            size="small"
            type="text"
            v-if="scope.row.activityStatus === 0"
            @click="toActive(scope.row.instanceId)"
          >激活</el-button>
          <el-button
            size="small"
            type="text"
            v-if="scope.row.activityStatus === 1"
            @click="toUnActive(scope.row.instanceId)"
          >挂起</el-button>
          <el-button
            size="small"
            type="text"
            @click="toFlowImage(scope.row.instanceId)"
          >流程图</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <component :is="approve" v-model="businessId" :taskId="taskId" :disabled="false" @refresh="getList"></component>
    <el-dialog title="流程图" v-model="flowChart" width="1100">
      <img :src="imgUrl" width="1000" height="500" style="margin:0 auto"/>
    </el-dialog>

    <!-- 权限标识：选择用户 -->
    <el-dialog :title="`${dialogTitle}用户选择`" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
      <selectUser :postParams="postParams" :type="dialogTitle" v-model:selectUser="form.assigneePermission" v-model:userVisible="userVisible" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script setup name="Todo">
import * as api from "@/api/flow/execute";
import { flowImage } from "@/api/flow/definition";
import { active, unActive } from "@/api/flow/execute";
import selectUser from "@/views/components/selectUser";

const { proxy } = getCurrentInstance();
const { flow_status, activity_status } = proxy.useDict('flow_status', 'activity_status');
const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const instanceList = ref([]);
const showSearch = ref(true);
const imgUrl = ref("");
const flowChart = ref(false);
const approve = ref(null);
const taskId = ref("");
const businessId = ref("");
const dialogTitle = ref("");
const userVisible = ref(false);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    nodeName: null,
    flowStatus: null,
    approver: null,
    createTime: null,
  },
  // 表单校验
  rules: {
    assigneePermission: [
      { required: true, message: "请选择权限标识", trigger: "change" }
    ],
  },
  postParams: {}
});

const { queryParams, form, rules, postParams } = toRefs(data);

function getList() {
  loading.value = true;
  api.toDoPage(queryParams.value).then(response => {
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
  if (!row.formPath) {
    row.formPath = 'system/leave/approve.vue';
  }
  if (row.formCustom == 'N') {
    // 实际情况是，不同条件对应不同的页面，所以用动态导入组件
    import(/* @vite-ignore */`../../../../views/${row.formPath}`).then((module) => {
      approve.value = module.default;
    });
  }
}
function toFlowImage(instanceId) {
  flowImage(instanceId).then(response => {
    flowChart.value = true
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
    open.value = false;
    getList();
  });
}

function toActive(instanceId) {
  proxy.$modal.confirm('是否确认激活流程编号为"' + instanceId + '"的数据项？').then(function() {
    return active(instanceId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("激活成功");
  }).catch(() => {});
};

function toUnActive(instanceId) {
  proxy.$modal.confirm('是否确认挂起流程编号为"' + instanceId + '"的数据项？').then(function() {
    return unActive(instanceId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("挂起成功");
  }).catch(() => {});
};

getList();
</script>

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
      <el-form-item label="审批时间" prop="createTime">
        <el-date-picker
          clearable
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
    <el-table :data="instanceList">
      <el-table-column type="selection" width="55" align="center" fixed />
      <el-table-column label="序号" width="55" align="center">
        <template #default="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="流程实例id" align="center" prop="instanceId" :show-overflow-tooltip="true"/>
      <el-table-column label="流程名称" align="center" prop="flowName" :show-overflow-tooltip="true"/>
      <el-table-column label="任务名称" align="center" prop="targetNodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="审批人" align="center" prop="approver" :show-overflow-tooltip="true"/>
      <el-table-column label="协作类型" align="center" prop="cooperateType" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="cooperate_type" :value="scope.row.cooperateType"/>
        </template>
      </el-table-column>
      <el-table-column label="协作人" align="center" prop="collaborator" :show-overflow-tooltip="true"/>
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="审批时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="110" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            @click="showDoneList(scope.row.instanceId, scope.row.formPath, scope.row.testLeaveVo)"
            v-hasPermi="['flow:execute:doneList']"
          >审批记录</el-button>
          <el-button
            size="small"
            type="text"
            @click="toFlowImage(scope.row.instanceId)"
          >流程图</el-button>
          <el-button
            size="small"
            type="text"
            v-if="scope.row.ext"
            @click="handle(scope.row)"
          >查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <el-dialog title="流程图" v-model="flowChart" width="80%">
      <img :src="imgUrl" width="1000" style="margin:0 auto"/>
    </el-dialog>
    <component v-if="approve" v-bind:is="approve" :businessId="businessId" :taskId="taskId" :testLeaveVo="testLeaveVo" @refresh="getList"></component>
    <!-- 动态加载的组件 -->
    <el-dialog title="审批详情" :visible.sync="approveDialogVisible" width="80%">
      <component :is="dynamicComponent" v-if="dynamicComponent" :testLeaveVo="testLeaveVo" :taskId="taskId" :disabled="false"/>
    </el-dialog>
  </div>
</template>

<script setup name="Done">
import { donePage } from "@/api/flow/execute";
import { flowImage } from "@/api/flow/definition";
import router from "@/router";

const { proxy } = getCurrentInstance();
const { flow_status, cooperate_type } = proxy.useDict('flow_status', 'cooperate_type');
const ids = ref([]);
const showSearch = ref(true);
const imgUrl = ref("");
const flowChart = ref(false);
const total = ref(0);
const instanceList = ref([]);
const todoDetail = ref(null);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    nodeName: null,
    flowStatus: null,
    createBy: null,
    createTime: null,
  }
});
// 动态加载的组件相关
const approve = ref(null);
const dynamicComponent = ref(null);
const approveDialogVisible = ref(false);
const testLeaveVo = ref(null);
const taskId = ref(null);
const businessId = ref(null);

const { queryParams } = toRefs(data);

/** 查询流程实例列表 */
function getList() {
  donePage(queryParams.value).then(response => {
    instanceList.value = response.rows;
    total.value = response.total;
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
/** 审批记录按钮操作 */
function showDoneList(instanceId, formPath, testLeaveVo) {
  const params = { disabled: false, pageNum: queryParams.value.pageNum, formPath, testLeaveVo };
  router.push({ path: `/done/doneList/index/${instanceId}`, query: params });
}
function toFlowImage(instanceId) {
  flowImage(instanceId).then(response => {
    flowChart.value = true;
    imgUrl.value = "data:image/gif;base64," + response.data;
  });
}
function handle(row) {
  // 设置任务ID和业务ID
  taskId.value = row.id;
  businessId.value = row.businessId;

  // 解析ext字段的JSON数据
  if (row.ext) {
    try {
      const parsedExt = JSON.parse(row.ext);
      testLeaveVo.value = { ...parsedExt };
    } catch (e) {
      console.error('Invalid ext JSON format:', row.ext);
    }
  }

  // 获取或设置formPath路径
  const formPath = row.formPath;

  // 如果存在formPath，则动态加载组件
  if (formPath) {
    // 实际情况是，不同条件对应不同的页面，所以用动态导入组件
    import(`../../../../views/${formPath}`).then((module) => {
      approve.value = module.default;
    });
  } else {
    approve.value = null;
  }
}

getList();
</script>

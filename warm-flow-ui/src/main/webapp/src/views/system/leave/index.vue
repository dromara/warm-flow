<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="请假类型" prop="type">
        <el-select v-model="queryParams.type" filterable placeholder="请选择请假类型" clearable>
          <el-option
            v-for="dict in leave_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
      <el-form-item label="创建者" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入创建者"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" style="width: 308px">
        <el-date-picker
                v-model="daterangeCreateTime"
                size="small"
                value-format="YYYY-MM-DD HH:mm:ss"
                type="datetimerange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['system:leave:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:leave:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:leave:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:leave:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="leaveList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center">
        <template #default="scope">
          <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="请假类型" align="center" prop="type" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="leave_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
        <el-table-column label="开始时间" align="center" prop="startTime" width="180">
            <template #default="scope">
                <span>{{ parseTime(scope.row.startTime) }}</span>
            </template>
        </el-table-column>
        <el-table-column label="结束时间" align="center" prop="endTime" width="180">
            <template #default="scope">
                <span>{{ parseTime(scope.row.endTime) }}</span>
            </template>
        </el-table-column>
      <el-table-column label="请假天数" align="center" prop="day" :show-overflow-tooltip="true"/>
      <el-table-column label="流程节点名称" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" :show-overflow-tooltip="true"/>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
        </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <el-tooltip content="详情" placement="top">
            <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['system:leave:detail']"></el-button>
          </el-tooltip>
          <el-tooltip content="编辑" placement="top">
            <el-button
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['system:leave:edit']"
            ></el-button>
          </el-tooltip>
	       <el-tooltip content="提交审批" placement="top">
            <el-button
                link
                type="primary"
                icon="Position"
                v-if="scope.row.nodeCode === '2'"
                @click="showSubmitForm(scope.row)"
                v-hasPermi="['system:leave:submit']"
            ></el-button>
          </el-tooltip>
          <el-tooltip content="终止" placement="top">
            <el-button
                link
                type="primary"
                icon="Scissor"
                v-if="scope.row.nodeType !== 2"
                @click="toTermination(scope.row.instanceId)"
                v-hasPermi="['system:leave:submit']"
            ></el-button>
          </el-tooltip>
          <el-tooltip content="流程图" placement="top">
            <el-button link type="primary" icon="Picture" @click="toFlowImage(scope.row.instanceId)"></el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button
                link
                type="primary"
                v-if="scope.row.nodeCode === '2'"
                icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:leave:remove']"
            ></el-button>
          </el-tooltip>
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

    <Dialog ref="dialog" @refresh="getList"></Dialog>
    <el-dialog title="流程图" v-model="flowChart" width="1100">
      <img :src="imgUrl" width="1000" height="500" style="margin:0 auto"/>
    </el-dialog>	
  </div>
</template>

<script setup name="Leave">
import { listLeave, delLeave, submit, termination } from "@/api/system/leave";
import Dialog from "@/views/system/leave/dialog";
import {flowImage} from "@/api/flow/definition";

const { proxy } = getCurrentInstance();
const { flow_status, leave_type } = proxy.useDict('flow_status', 'leave_type');

const leaveList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const flowChart = ref(false);
const imgUrl = ref('');
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const daterangeCreateTime = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    type: null,
    day: null,
    flowStatus: null,
    createBy: null,
    createTime: null,
  },
});

const { queryParams } = toRefs(data);

/** 查询OA 请假申请列表 */
function getList() {
  loading.value = true;
  queryParams.value.paramsStr = "";
  if (null != daterangeCreateTime && '' != daterangeCreateTime) {
    queryParams.value.paramsStr =
        JSON.stringify({
          "beginCreateTime": daterangeCreateTime.value[0],
          "endCreateTime": daterangeCreateTime.value[1]
        });
  }
  listLeave(queryParams.value).then(response => {
    leaveList.value = response.rows;
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
  daterangeCreateTime.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  proxy.$refs["dialog"].show();
}

/** 详情按钮操作 */
function handleDetail(row) {
  proxy.$refs["dialog"].show(row.id, true);
}

/** 修改按钮操作 */
function handleUpdate(row) {
  const _id = row.id || ids.value
  proxy.$refs["dialog"].show(_id);
}


/** 终止按钮操作 */
function toTermination(instanceId) {
  termination(instanceId).then(() => {
    proxy.$modal.msgSuccess("终止成功");
    getList();
  })
}

/** 修改按钮操作 */
function showSubmitForm(row) {
  const _id = row.id || ids.value
  proxy.$refs["dialog"].show(_id, true, "OA请假申请审批");
};

/** 提交审批按钮操作 */
function handleSubmit(){
  submit(form.value.id, flowStatus.value).then(() => {
    getList();
    proxy.$modal.msgSuccess("提交审批成功");
  })
}

function toFlowImage(instanceId) {
  flowImage(instanceId).then(response => {
    flowChart.value = true
    imgUrl.value = "data:image/gif;base64," + response.data;
  });
}	
	
/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除OA 请假申请编号为"' + _ids + '"的数据项？').then(function() {
    loading.value = true;
    return delLeave(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
    loading.value = false;
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('test/leave/export', {
    ...queryParams.value
  }, `OA 请假申请_${new Date().getTime()}.xlsx`)
}

getList();
</script>

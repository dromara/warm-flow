<template>
  <div class="app-container">
    <el-table :data="taskList">
      <el-table-column label="序号" width="50" align="center" key="id" prop="id">
        <template #default="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="开始节点名称" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="结束节点名称" align="center" prop="targetNodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="审批人" align="center" prop="approver" :show-overflow-tooltip="true"/>
      <el-table-column label="协作类型" align="center" prop="cooperateType" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="cooperate_type" :value="scope.row.cooperateType"/>
        </template>
      </el-table-column>
      <el-table-column label="协作人" align="center" prop="collaborator" :show-overflow-tooltip="true"/>
      <!-- <el-table-column label="流转类型" align="center" prop="skipType" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.skipType==='PASS'">通过</el-tag>
          <el-tag v-if="scope.row.skipType==='REJECT'" type="danger">退回</el-tag>
          <el-tag v-if="scope.row.skipType==='NONE'" type="info">无动作</el-tag>
        </template>
      </el-table-column> -->
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="审批意见" align="center" prop="message" :show-overflow-tooltip="true"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="业务详情" align="center" width="100">
        <template #default="scope">
          <el-button size="mini" v-if="scope.row.ext" @click="handle(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <component v-if="approve" v-bind:is="approve" :businessId="businessId" :taskId="taskId" :testLeaveVo="testLeaveVo" @refresh="fetchTaskList"></component>
  </div>
</template>

<script setup name="DoneList">

import { doneList } from '@/api/flow/execute'

const { proxy } = getCurrentInstance();
const { flow_status, cooperate_type } = proxy.useDict('flow_status', 'cooperate_type');
const instanceId = ref('');
const formPath = ref('');
const taskList = ref([]);
const taskId = ref('');
const businessId = ref('');
const approve = ref(null);
const testLeaveVo = ref(null);

function getList() {
  instanceId.value = proxy.$route.params.instanceId;
  formPath.value = proxy.$route.query.formPath;
  testLeaveVo.value = proxy.$route.query.testLeaveVo; // 接收传递过来的testLeaveVo
  fetchTaskList();
}

function fetchTaskList() {
  doneList(instanceId.value).then(response => {
    taskList.value = response.data;
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
  const formPath = row.formPath || formPath.value;

  // 如果存在formPath，则动态加载组件
  if (formPath) {
    // 实际情况是，不同条件对应不同的页面，所以用动态导入组件
    import(/* @vite-ignore */`../../../../views/${formPath}`).then((module) => {
      approve.value = module.default;
    });
  } else {
    approve.value = null;
  }
}

getList();
</script>

<template>
    <div class="app-container">
      <el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="100px">
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
              v-for="dict in dict.type.flow_status"
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
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="instanceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" fixed />
        <el-table-column label="序号" width="55" align="center" key="id" prop="id">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="流程名称" align="center" prop="flowName" :show-overflow-tooltip="true"/>
        <el-table-column label="任务名称" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
        <el-table-column label="抄送人" align="center" prop="approver" :show-overflow-tooltip="true"/>
        <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.flow_status" :value="scope.row.flowStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="240" fixed="right" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
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

      <el-dialog title="流程图" width="70%" :visible.sync="flowChart" append-to-body>
        <WarmChart :insId="insId"></WarmChart>
      </el-dialog>

    </div>
  </template>

  <script>
  import * as api from "@/api/flow/execute";
  import WarmChart from "@/views/flow/definition/warm_chart.vue";

  export default {
    name: "Todo",
    components: {WarmChart},
    dicts: ['flow_status'],
    data() {
      return {
        // 遮罩层
        loading: true,
        insId: null,
        flowChart: false,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 流程实例表格数据
        instanceList: [],
        // 业务审批页面
        approve: null,
        taskId: "",
        businessId: "",
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
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询流程实例列表 */
      getList() {
        this.loading = true;
        api.copyPage(this.queryParams).then(response => {
          this.instanceList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      /** 办理按钮操作 */
      handle(row) {
        this.taskId = row.id
        this.businessId = row.businessId
        if (row.formCustom == 'N' && row.formPath) {
          // 实际情况是，不同条件对应不同的页面，所以用动态组件
          this.approve = (resolve) => require([`@/views/${row.formPath}`], resolve)
        }
      },
      toFlowImage(instanceId) {
        this.insId = instanceId;
        this.flowChart = true
      },
    }
  };
  </script>

<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="100px">
      <!-- 任务名称输入框 -->
      <el-form-item label="任务名称" prop="nodeName">
        <el-input
          v-model="queryParams.nodeName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- 流程状态选择框 -->
      <el-form-item label="流程状态" prop="flowStatus">
        <el-select v-model="queryParams.flowStatus" placeholder="请选择流程状态" clearable>
          <el-option
            v-for="dict in dict.type.flow_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <!-- 审批时间选择器 -->
      <el-form-item label="审批时间" prop="createTime">
        <el-date-picker
          clearable size="small"
          v-model="queryParams.createTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择创建时间">
        </el-date-picker>
      </el-form-item>
      <!-- 搜索和重置按钮 -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 流程实例表格 -->
    <el-table :data="instanceList">
      <!-- 表格列定义 -->
      <el-table-column type="selection" width="55" align="center" fixed />
      <el-table-column label="序号" width="55" align="center" key="id" prop="id">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="流程名称" align="center" prop="flowName" :show-overflow-tooltip="true"/>
      <el-table-column label="任务名称" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="审批人" align="center" prop="approver" :show-overflow-tooltip="true"/>
      <el-table-column label="协作类型" align="center" prop="cooperateType" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cooperate_type" :value="scope.row.cooperateType"/>
        </template>
      </el-table-column>
      <el-table-column label="协作人" align="center" prop="collaborator" :show-overflow-tooltip="true"/>
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="审批时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="110" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="showDoneList(scope.row.instanceId, scope.row.formPath, scope.row.infoVo)"
            v-hasPermi="['flow:execute:doneList']"
          >审批记录</el-button>
          <el-button
            size="mini"
            type="text"
            @click="toFlowImage(scope.row.instanceId)"
          >流程图</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.ext"
            @click="handle(scope.row)"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleComponent"
          >多组件加载</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="流程图" width="70%" :visible.sync="flowChart" append-to-body>
      <WarmChart :insId="insId"></WarmChart>
    </el-dialog>

    <component v-if="approve" v-bind:is="approve" :businessId="businessId" :taskId="taskId" :infoVo="infoVo" @refresh="getList"></component>
    <!-- 动态加载的组件 -->
    <el-dialog title="审批详情" :visible.sync="approveDialogVisible" width="80%">
      <component :is="dynamicComponent" v-if="dynamicComponent" :infoVo="infoVo" :taskId="taskId" :disabled="false"/>
    </el-dialog>
    <!-- 多组件加载 -->
    <el-dialog title="多组件加载" :visible.sync="componentVisible" width="80%">
      <component v-for="(item, index) in componentList" :key="index" v-bind:is="item.approve"></component>
    </el-dialog>
  </div>
</template>

<script>
import { donePage } from "@/api/flow/execute";
import WarmChart from "@/views/flow/definition/warm_chart.vue";

export default {
  name: "Done",
  components: {WarmChart},
  dicts: ['flow_status', 'cooperate_type'],
  data() {
    return {
      // 选中数组
      ids: [],
      // 显示搜索条件
      showSearch: true,
      flowChart: false,
      insId: null,
      // 总条数
      total: 0,
      // 流程实例表格数据
      instanceList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nodeName: null,
        flowStatus: null,
        createBy: null,
        createTime: null,
      },
      // 动态加载的组件相关
      approve: null,
      dynamicComponent: null,
      approveDialogVisible: false,
      infoVo: null,
      taskId: null,
      componentVisible: false,
      componentList: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询流程实例列表 */
    getList() {
      donePage(this.queryParams).then(response => {
        this.instanceList = response.rows;
        this.total = response.total;
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
    /** 办理按钮操作 */
    showDoneList(instanceId, formPath, infoVo) {
      const params = { disabled: false, pageNum: this.queryParams.pageNum, formPath, infoVo };
      this.$router.push({ path: `/done/doneList/index/${instanceId}`, query: params });
    },
    toFlowImage(instanceId) {
      this.insId = instanceId;
      this.flowChart = true
    },
    handle(row) {
      // 设置任务ID和业务ID
      this.taskId = row.id;
      this.businessId = row.businessId;

      // 解析ext字段的JSON数据
      if (row.ext) {
        try {
          const parsedExt = JSON.parse(row.ext);
          this.infoVo = { ...parsedExt };
        } catch (e) {
          console.error('Invalid ext JSON format:', row.ext);
        }
      }
      // 获取或设置formPath路径
      const formPath = row.formPath || this.formPath;

      // 如果存在formPath，则动态加载组件
      if (formPath) {
        // 通过require动态加载组件
        this.approve = (resolve) => require([`@/views/${formPath}`], resolve);
      } else {
        this.approve = null;
      }
    },
    handleComponent() {
      this.componentList.push({ approve: (resolve) => require([`@/views/system/role`], resolve) });
      this.componentList.push({ approve: (resolve) => require([`@/views/system/user`], resolve) });
      this.componentVisible = true;
    }
  }
};
</script>

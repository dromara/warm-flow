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
        <el-select v-model="queryParams.flowStatus" placeholder="请选择流程状态" clearable>
          <el-option
            v-for="dict in dict.type.flow_status"
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
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择创建时间">
        </el-date-picker>
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
      <el-table-column label="任务名称" width="200" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="审批人" align="center" prop="approver" :show-overflow-tooltip="true"/>
      <el-table-column label="转办人" align="center" prop="transferredBy" :show-overflow-tooltip="true"/>
      <el-table-column label="委派人" align="center" prop="delegate" :show-overflow-tooltip="true"/>
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="激活状态" align="center" prop="activityStatus" width="140" sortable="custom" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.activity_status" :value="scope.row.activityStatus"/>
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
            @click="handle(scope.row, false)"
            v-hasPermi="['flow:execute:handle']"
          >办理</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handle(scope.row, true)"
            v-hasPermi="['flow:execute:handle']"
          >任意跳转</el-button>
          <el-button
            size="mini"
            type="text"
            @click="transferShow(scope.row, '转办')"
            v-hasPermi="['flow:execute:handle']"
          >转办</el-button>
          <el-button
            size="mini"
            type="text"
            @click="transferShow(scope.row, '加签')"
            v-hasPermi="['flow:execute:handle']"
          >加签</el-button>
          <el-button
            size="mini"
            type="text"
            @click="transferShow(scope.row, '委派')"
            v-hasPermi="['flow:execute:handle']"
          >委派</el-button>
          <el-button
            size="mini"
            type="text"
            @click="transferShow(scope.row, '减签')"
            v-hasPermi="['flow:execute:handle']"
          >减签</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.activityStatus === 0"
            @click="toActive(scope.row.instanceId)"
          >激活</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.activityStatus === 1"
            @click="toUnActive(scope.row.instanceId)"
          >挂起</el-button>
          <el-button
            size="mini"
            type="text"
            @click="toFlowImage(scope.row.instanceId)"
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

    <component v-bind:is="approve" v-model="businessId" :type="'0'" :taskId="taskId" :showAnyNode="showAnyNode" :formId="formPath" @refresh="getList"></component>

    <el-dialog title="流程图" width="70%" :visible.sync="flowChart" append-to-body>
      <WarmChart :insId="insId"></WarmChart>
    </el-dialog>

    <!-- 权限标识：选择用户 -->
    <el-dialog :title="`${dialogTitle}用户选择`" v-if="userVisible" :visible.sync="userVisible" width="80%" append-to-body>
      <selectUser :postParams="postParams" :type="dialogTitle" :selectUser.sync="form.assigneePermission" :userVisible.sync="userVisible" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>
  </div>
</template>

<script>
import * as api from "@/api/flow/execute";
import {active, unActive} from "@/api/flow/execute";
import selectUser from "@/views/components/selectUser";
import WarmChart from "@/views/flow/definition/warm_chart.vue";

export default {
  name: "Todo",
  dicts: ['flow_status', 'activity_status'],
  components: {
    WarmChart,
    selectUser
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      flowChart: false,
      insId: null,
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
      formPath: "",
      showAnyNode: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nodeName: null,
        flowStatus: null,
        approver: null,
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
      dialogTitle: "",
      postParams: {},
      userVisible: false
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询流程实例列表 */
    getList() {
      this.loading = true;
      api.toDoPage(this.queryParams).then(response => {
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
    handle(row, showAnyNode) {
      this.taskId = row.id
      this.businessId = row.businessId
      this.showAnyNode = showAnyNode
      if(!row.formPath){
        row.formPath='system/leave/approve.vue'
      }

      if (row.formCustom == 'N') {
        // 实际情况是，不同条件对应不同的页面，所以用动态组件
        this.approve = (resolve) => require([`@/views/${row.formPath}`], resolve)
      } else if (row.formCustom === "Y") {
        this.formPath = row.formPath;
        this.approve = (resolve) => require([`@/views/form/definition/formDialog.vue`], resolve)
      }
    },

    toFlowImage(instanceId) {
      this.insId = instanceId;
      this.flowChart = true;
    },
    /** 转办|加签|委派|减签弹框显示按钮操作 */
    transferShow(row, title) {
      this.form.assigneePermission = null;
      this.taskId = row.id;
      this.dialogTitle = title;
      api.getTaskById(this.taskId).then(res => {
        this.form.assigneePermission = res.data.assigneePermission ? res.data.assigneePermission.split(",") : [];
      });
      this.userVisible = true;
      let operatorTypeObj = {
        "转办": "1",
        "加签": "6",
        "委派": "3",
        "减签": "7"
      };
      this.postParams = {
        taskId: row.id,
        operatorType: operatorTypeObj[title]
      };
    },
    // 获取选中用户数据
    handleUserSelect(checkedItemList) {
      this.form.assigneePermission = checkedItemList.map(e => {
        return e.userId;
      });
      let assigneePermission = JSON.parse(JSON.stringify(this.form.assigneePermission));
      let operatorTypeObj = {
        "转办": "2",
        "加签": "6",
        "委派": "3",
        "减签": "7"
      };
      api.interactiveType(this.taskId, Array.isArray(assigneePermission) ? assigneePermission.join(',') : assigneePermission,  operatorTypeObj[this.dialogTitle])
        .then(() => {
          this.$modal.msgSuccess(`${this.dialogTitle}成功`);
          this.getList();
        });
    },

    toActive(instanceId) {
      this.$modal.confirm('是否确认激活流程编号为"' + instanceId + '"的数据项？').then(function() {
        return active(instanceId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("激活成功");
      }).catch(() => {});
    },

    toUnActive(instanceId) {
      this.$modal.confirm('是否确认挂起流程编号为"' + instanceId + '"的数据项？').then(function() {
        return unActive(instanceId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("挂起成功");
      }).catch(() => {});
    },
  }
};
</script>

<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="采购名称" prop="purchaseName">
        <el-input
          v-model="queryParams.purchaseName"
          placeholder="请输入采购名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="更新时间" prop="updateTime">
        <el-date-picker clearable
          v-model="queryParams.updateTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择更新时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:steps:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:steps:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:steps:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stepsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="采购名称" align="center" prop="purchaseName" />
      <el-table-column label="采购计划" align="center" prop="purchasePlan" />
      <el-table-column label="是否加急" align="center" prop="urgent" />
      <el-table-column label="加急采购计划" align="center" prop="urgentPurchasePlan" />
      <el-table-column label="按需采购" align="center" prop="onDemandProcurement" />
      <el-table-column label="提供物品" align="center" prop="provideItems" />
      <el-table-column label="产品验收" align="center" prop="productInspection" />
      <el-table-column label="出具发票" align="center" prop="issueInvoice" />
      <el-table-column label="登记记录" align="center" prop="recordEntry" />
      <el-table-column label="退货" align="center" prop="returnItems" />
      <el-table-column label="入库" align="center" prop="warehousing" />
      <el-table-column label="接收物品" align="center" prop="receiveItems" />
      <el-table-column label="做好记录" align="center" prop="maintainRecords" />
      <el-table-column label="流程节点名称" align="center" prop="nodeName" />
      <el-table-column label="流程状态" align="center" prop="flowStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleDetail(scope.row)"
            v-hasPermi="['system:steps:edit']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="revoke(scope.row.id)"
            v-hasPermi="['system:steps:edit']"
          >撤销</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="taskBackByInsId(scope.row.id)"
            v-hasPermi="['system:steps:edit']"
          >拿回</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="termination(scope.row)"
            v-hasPermi="['system:steps:edit']"
          >终止</el-button>
          <el-button
            size="mini"
            type="text"
            @click="toFlowImage(scope.row.instanceId)"
          >流程图</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:steps:remove']"
          >删除</el-button>
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

    <!-- 添加或修改企业采购流程表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px"   :disabled="title === '查看企业采购流程表'">
        <el-form-item label="采购名称" prop="purchaseName">
          <el-input v-model="form.purchaseName" placeholder="请输入采购名称" />
        </el-form-item>
        <el-form-item label="采购计划" prop="purchasePlan" v-if="title === '查看企业采购流程表'">
          <el-input v-model="form.purchasePlan" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="是否加急" prop="urgent" v-if="title === '查看企业采购流程表'">
          <el-select v-model="form.urgent" clearable>
            <el-option label="否" value="1" selected></el-option>
            <el-option label="是" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="加急采购计划" prop="urgentPurchasePlan"  v-if="form.urgent === '0' && title === '查看企业采购流程表'">
          <el-input v-model="form.urgentPurchasePlan" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="按需采购" prop="onDemandProcurement" v-if="title === '查看企业采购流程表'">
          <el-input v-model="form.onDemandProcurement" placeholder="请输入按需采购" />
        </el-form-item>
        <el-form-item label="提供物品" prop="provideItems" v-if="title === '查看企业采购流程表'">
          <el-input v-model="form.provideItems" placeholder="请输入提供物品" />
        </el-form-item>
        <el-form-item label="产品验收" prop="productInspection" v-if="title === '查看企业采购流程表'">
          <el-select v-model="form.productInspection" clearable>
            <el-option label="合格" value="0"></el-option>
            <el-option label="不合格" value="1" selected></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="登记记录" prop="recordEntry" v-if="title === '查看企业采购流程表' && form.productInspection === 1">
          <el-input v-model="form.recordEntry" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="退货" prop="returnItems" v-if="title === '查看企业采购流程表' && form.productInspection === 1">
          <el-input v-model="form.returnItems" placeholder="请输入退货" />
        </el-form-item>
        <el-form-item label="出具发票" prop="issueInvoice" v-if="title === '查看企业采购流程表'">
          <el-input v-model="form.issueInvoice" placeholder="请输入出具发票" />
        </el-form-item>
        <el-form-item label="入库" prop="warehousing" v-if="title === '查看企业采购流程表'">
          <el-input v-model="form.warehousing" placeholder="请输入入库" />
        </el-form-item>
        <el-form-item label="做好记录" prop="maintainRecords" v-if="title === '查看企业采购流程表'">
          <el-input v-model="form.maintainRecords" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="接收物品" prop="receiveItems" v-if="title === '查看企业采购流程表'">
          <el-input v-model="form.receiveItems" placeholder="请输入接收物品" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm"  v-if="title === '添加企业采购流程表'">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="流程图" width="70%" :visible.sync="flowChart" append-to-body>
      <WarmChart :insId="insId"></WarmChart>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSteps,
  getSteps,
  delSteps,
  addSteps,
  updateSteps,
  termination,
  revoke,
  taskBackByInsId
} from "@/api/system/steps";
import WarmChart from "@/views/flow/definition/warm_chart.vue";

export default {
  name: "Steps",
  components: {WarmChart},
  dicts: ['flow_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      flowChart: false,
      insId: null,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 企业采购流程表表格数据
      stepsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        purchaseName: null,
        urgent: null,
        updateTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        purchaseName: [
          { required: true, message: "采购名称不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询企业采购流程表列表 */
    getList() {
      this.loading = true;
      listSteps(this.queryParams).then(response => {
        this.stepsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        purchaseName: null,
        purchasePlan: null,
        urgentPurchasePlan: null,
        onDemandProcurement: null,
        provideItems: null,
        productInspection: null,
        issueInvoice: null,
        recordEntry: null,
        returnItems: null,
        warehousing: null,
        receiveItems: null,
        maintainRecords: null,
        instanceId: null,
        nodeCode: null,
        nodeName: null,
        nodeType: null,
        flowStatus: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        delFlag: null
      };
      this.resetForm("form");
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加企业采购流程表";
      this.form.urgent = '1'
    },
    /** 查看按钮操作 */
    handleDetail(row) {
      this.reset();
      const id = row.id || this.ids
      getSteps(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "查看企业采购流程表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSteps(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSteps(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    /** 终止按钮操作 */
    termination(row) {
      termination(row).then(() => {
        this.$modal.msgSuccess("终止成功");
        this.getList();
      })
    },

    /** 撤销按钮操作 */
    revoke(id) {
      revoke(id).then(response => {
        this.$modal.msgSuccess("撤销成功");
        this.open = false;
        this.getList();
      });
    },

    /** 拿回到最近办理的任务按钮操作 */
    taskBackByInsId(id) {
      taskBackByInsId(id).then(response => {
        this.$modal.msgSuccess("拿回成功");
        this.open = false;
        this.getList();
      });
    },

    toFlowImage(instanceId) {
      this.insId = instanceId;
      this.flowChart = true
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除企业采购流程表编号为"' + ids + '"的数据项？').then(function() {
        return delSteps(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/steps/export', {
        ...this.queryParams
      }, `steps_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

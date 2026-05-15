<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="合同名称" prop="contractName">
        <el-input
          v-model="queryParams.contractName"
          placeholder="请输入合同名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:process:add']"
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
          v-hasPermi="['system:process:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:process:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="processList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="合同流程ID" align="center" prop="id" />
      <el-table-column label="合同名称" align="center" prop="contractName" />
      <el-table-column label="合同类型" align="center" prop="contractType" />
      <el-table-column label="结构和性质" align="center" prop="structureAndNature" />
      <el-table-column label="拟定条件" align="center" prop="proposedConditions" />
      <el-table-column label="谈判内容" align="center" prop="negotiationContent" />
      <el-table-column label="谈判结果" align="center" prop="negotiationResult" />
      <el-table-column label="协商方案" align="center" prop="adjustmentScheme" />
      <el-table-column label="签订日期" align="center" prop="signDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.signDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签订人" align="center" prop="signer" />
      <el-table-column label="备案日期" align="center" prop="filingDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.filingDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备案部门" align="center" prop="filingDepartment" />
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
            v-hasPermi="['system:process:edit']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="revoke(scope.row.id)"
            v-hasPermi="['system:process:submit']"
          >撤销</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="taskBackByInsId(scope.row.id)"
            v-hasPermi="['system:process:submit']"
          >拿回</el-button>
          <el-button
          size="mini"
          type="text"
          v-if="scope.row.nodeType !== 2"
          @click="termination(scope.row)"
          v-hasPermi="['system:process:submit']"
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
            v-hasPermi="['system:process:remove']"
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

    <!-- 添加或修改合同流程对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px"  :disabled="title === '查看合同流程'">
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="form.contractName" placeholder="请输入合同名称" />
        </el-form-item>
        <el-form-item label="结构和性质" v-if="title === '查看合同流程'" prop="structureAndNature">
          <el-input  v-model="form.structureAndNature" placeholder="请输入结构和性质" />
        </el-form-item>
        <el-form-item label="拟定条件" v-if="title === '查看合同流程'" prop="proposedConditions">
          <el-input v-model="form.proposedConditions" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="谈判内容" v-if="title === '查看合同流程'">
          <editor v-model="form.negotiationContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="谈判附件"  v-if="title === '查看合同流程'" prop="fileId">
          <file-upload v-model="form.fileId"/>
        </el-form-item>
        <el-form-item label="谈判结果" v-if="title === '查看合同流程'" prop="negotiationResult">
          <el-input v-model="form.negotiationResult" placeholder="请输入谈判结果" />
        </el-form-item>
        <el-form-item label="协商方案" v-if="form.adjustmentScheme && title === '查看合同流程'" prop="adjustmentScheme">
          <el-input v-model="form.adjustmentScheme" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="签订日期" v-if="title === '查看合同流程'" prop="signDate">
          <el-date-picker clearable
            v-model="form.signDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择签订日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="签订人" v-if="title === '查看合同流程'" prop="signer">
          <el-input v-model="form.signer" placeholder="请输入签订人" />
        </el-form-item>
        <el-form-item label="备案日期" v-if="title === '查看合同流程'" prop="filingDate">
          <el-date-picker clearable
            v-model="form.filingDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择备案日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备案部门" v-if="title === '查看合同流程'" prop="filingDepartment">
          <el-input v-model="form.filingDepartment" placeholder="请输入备案部门" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="title === '添加合同流程'">确 定</el-button>
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
  listProcess,
  getProcess,
  delProcess,
  addProcess,
  updateProcess,
  termination,
  revoke, taskBackByInsId
} from "@/api/system/process";
import WarmChart from "@/views/flow/definition/warm_chart.vue";

export default {
  name: "Process",
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
      // 合同流程表格数据
      processList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fileId: null,
        contractName: null,
        contractType: null,
        structureAndNature: null,
        proposedConditions: null,
        negotiationContent: null,
        negotiationResult: null,
        adjustmentScheme: null,
        signDate: null,
        signer: null,
        filingDate: null,
        filingDepartment: null,
        nodeName: null,
        flowStatus: null,
        updateTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        contractName: [
          { required: true, message: "合同名称不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询合同流程列表 */
    getList() {
      this.loading = true;
      listProcess(this.queryParams).then(response => {
        this.processList = response.rows;
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
        contractName: null,
        contractType: null,
        structureAndNature: null,
        proposedConditions: null,
        negotiationContent: null,
        negotiationResult: null,
        adjustmentScheme: null,
        signDate: null,
        signer: null,
        filingDate: null,
        filingDepartment: null,
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
      this.title = "添加合同流程";
    },
    /** 修改按钮操作 */
    handleDetail(row) {
      this.reset();
      const id = row.id || this.ids
      getProcess(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "查看合同流程";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProcess(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProcess(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除合同流程编号为"' + ids + '"的数据项？').then(function() {
        return delProcess(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/process/export', {
        ...this.queryParams
      }, `process_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

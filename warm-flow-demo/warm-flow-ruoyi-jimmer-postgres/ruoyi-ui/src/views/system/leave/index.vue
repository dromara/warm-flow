<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="请假类型" prop="type">
        <el-select v-model="queryParams.type" filterable placeholder="请选择请假类型" clearable>
          <el-option
            v-for="dict in dict.type.leave_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="请假天数" prop="day">
        <el-input
          v-model="queryParams.day"
          placeholder="请输入请假天数"
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
      <el-form-item label="创建者" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入创建者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="daterangeCreateTime"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
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
          v-hasPermi="['system:leave:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:leave:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:leave:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:leave:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="leaveList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="请假类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.leave_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="请假天数" align="center" prop="day" />
      <el-table-column label="流程节点名称" align="center" prop="nodeName" />
      <el-table-column label="流程状态" align="center" prop="flowStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-if="scope.row.nodeCode === '2'"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:leave:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeCode === '2'"
            @click="showSubmitForm(scope.row)"
            v-hasPermi="['system:leave:edit']"
          >提交审批</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="revoke(scope.row.id)"
            v-hasPermi="['system:leave:edit']"
          >撤销</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="taskBackByInsId(scope.row.id)"
            v-hasPermi="['system:leave:edit']"
          >拿回</el-button><el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType !== 2"
            @click="termination(scope.row)"
            v-hasPermi="['system:leave:edit']"
          >终止</el-button>
          <el-button
            size="mini"
            type="text"
            @click="toFlowImage(scope.row.instanceId)"
          >流程图</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeCode === '2'"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:leave:remove']"
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

    <!-- 添加或修改OA 请假申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="请假类型" prop="type">
          <el-select v-model="form.type" filterable placeholder="请选择请假类型" :disabled="disabled">
            <el-option
              v-for="dict in dict.type.leave_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入内容" :disabled="disabled"/>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable
                          v-model="form.startTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择开始时间"
                          @change="calculateLeaveDays"
                          :disabled="disabled"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择结束时间"
                          @change="calculateLeaveDays"
                          :disabled="disabled"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="请假天数" prop="day">
          <el-input v-model="form.day" placeholder="请输入请假天数" :disabled="disabled"/>
        </el-form-item>

        <el-form-item label="自定义流程状态" prop="flowStatus">
          <el-input v-model="flowStatus"/>
          <el-tooltip class="item" effect="dark" content="warm-flow工作流默认内置了一套流程状态，如果此字段填写可以自定义流程状态">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </el-form-item>

        <el-form-item label="抄送人" prop="additionalHandler">
            <el-select v-model="form.additionalHandler" multiple collapse-tags :disabled="disabled" :clearable="!disabled" filterable placeholder="请选择抄送人员">
            <el-option-group
              v-for="groupOption in groupOptions"
              :key="groupOption.label"
              :label="groupOption.label"
              :disabled="disabled">
              <el-option
                v-for="item in groupOption.options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-option-group>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" v-if="!isApprove" @click="submitForm">确定</el-button>
        <el-button type="primary" v-if="isApprove" @click="handlePending">暂存</el-button>
        <el-button type="primary" v-if="isApprove" @click="handleSubmit">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog title="流程图" width="70%" :visible.sync="flowChart" append-to-body>
      <WarmChart :insId="insId"></WarmChart>
    </el-dialog>

  </div>
</template>

<script>
import {
  listLeave,
  getLeave,
  delLeave,
  addLeave,
  updateLeave,
  submit,
  termination,
  revoke,
  taskBackByInsId, pending
} from "@/api/system/leave";
import {listUser} from "@/api/system/user";
import WarmChart from "@/views/flow/definition/warm_chart.vue";

export default {
  name: "Leave",
  components: {WarmChart},
  dicts: ['leave_type', 'flow_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      flowChart: false,
      insId: null,
      // 自定义流程状态扩展
      flowStatus: "",
      disabled: false,
      isApprove: false,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // OA 请假申请表格数据
      leaveList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 请假天数时间范围
      daterangeCreateTime: [],
      groupOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: null,
        day: null,
        flowStatus: null,
        createBy: null,
        createTime: null,
      },
      // 表单参数
      form: {
        type: null,
        reason: null,
        startTime: null,
        endTime: null,
        day: null,
        flowStatus: null,
        additionalHandler: []
      },
      // 表单校验
      rules: {
        type: [
          { required: true, message: "请假类型不能为空", trigger: "change" }
        ],
        reason: [
          { required: true, message: "请假原因不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
          { validator: this.validateDateRange, trigger: 'change' }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
          { validator: this.validateDateRange, trigger: 'change' }
        ],
        day: [
          { required: true, message: "请假天数不能为空", trigger: "blur" },
          { validator: this.validateDay, trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getUser();
  },
  methods: {
    /** 查询OA 请假申请列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params["beginCreateTime"] = this.daterangeCreateTime[0];
        this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
      }
      listLeave(this.queryParams).then(response => {
        this.leaveList = response.rows;
        this.total = response.total;
        this.loading = false;
      });

    },
    getUser(){
      listUser().then(response => {
        let groupOption = {
          label: '用户',
          options: response.rows.map(item =>{
              return {
                value: item.userId,
                label: item.nickName
              }
            }
          )
        }
        this.groupOptions.push(groupOption);
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
        type: null,
        reason: null,
        startTime: null,
        endTime: null,
        day: null,
        instanceId: null,
        nodeCode: null,
        nodeName: null,
        flowStatus: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        delFlag: null
      };
      this.disabled = false;
      this.isApprove = false;
      this.flowStatus = "";
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangeCreateTime = [];
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
      this.title = "添加OA 请假申请";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getLeave(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改OA 请假申请";
      });
    },

    /** 终止按钮操作 */
    termination(row) {
      termination(row).then(() => {
        this.$modal.msgSuccess("终止成功");
        this.getList();
      })
    },
    validateDateRange(rule, value, callback) {
      const { startTime, endTime } = this.form;
      if (startTime && endTime) {
        if (new Date(startTime) > new Date(endTime)) {
          callback(new Error('开始时间不能晚于结束时间'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    },
    validateDay(rule, value, callback) {
      const { startTime, endTime, day } = this.form;
      if (startTime && endTime) {
        const timeDiff = new Date(endTime) - new Date(startTime);
        const calculatedDays = Math.ceil(timeDiff / (1000 * 60 * 60 * 24))+1;
        if (day != calculatedDays) {
          callback(new Error('请假天数应等于结束时间和开始时间之间的天数差'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    },
    calculateLeaveDays() {
      if (this.form.startTime && this.form.endTime) {
        const start = new Date(this.form.startTime);
        const end = new Date(this.form.endTime);
        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // 包含开始和结束的当天
        this.form.day = diffDays;
      } else {
        this.form.day = '';
      }
    },

    /** 修改按钮操作 */
    showSubmitForm(row) {
      this.reset();
      const id = row.id || this.ids
      getLeave(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.isApprove = true;
        this.title = "OA请假申请审批";
      });
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

    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLeave(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addLeave(this.form, this.flowStatus).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    /** 暂存审批按钮操作 */
    handlePending(){
      pending(this.form.id, this.flowStatus).then(response => {
        this.$modal.msgSuccess("暂存审批成功");
        this.open = false;
        this.getList();
      })
    },

    /** 提交审批按钮操作 */
    handleSubmit(){
      submit(this.form.id, this.flowStatus).then(response => {
        this.$modal.msgSuccess("提交审批成功");
        this.open = false;
        this.getList();
      })
    },
    toFlowImage(instanceId) {
      this.insId = instanceId;
      this.flowChart = true
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除OA 请假申请编号为"' + ids + '"的数据项？').then(function() {
        return delLeave(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/leave/export', {
        ...this.queryParams
      }, `leave_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="表单编码" prop="formCode">
        <el-input
          v-model="queryParams.formCode"
          placeholder="请输入表单编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表单名称" prop="formName">
        <el-input
          v-model="queryParams.formName"
          placeholder="请输入表单名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表单版本" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入表单版本"
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
          v-hasPermi="['form:definition:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="definitionList" @selection-change="handleSelectionChange"  @sort-change="handleSortChange">
      <el-table-column type="selection" width="55" align="center" fixed />
      <el-table-column label="序号" width="50" align="center" key="id" prop="id">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="表单编码" align="center" prop="formCode" sortable="custom"  :show-overflow-tooltip="true"/>
      <el-table-column label="表单名称" align="center" prop="formName" sortable="custom" :show-overflow-tooltip="true"/>
      <el-table-column label="表单版本" align="center" prop="version" sortable="custom" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-tag>{{scope.row.version}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否发布" align="center" prop="isPublish" sortable="custom" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_publish" :value="scope.row.isPublish"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" sortable="custom" :sort-orders="['descending', 'ascending']" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.formType === 0"
            size="mini"
            type="text"
            @click="handleDesign(scope.row)"
            v-hasPermi="['form:design:queryDesign']"
          >表单设计</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 0"
            @click="handlePublish(scope.row.id)"
            v-hasPermi="['form:definition:publish']"
          >发布</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 1"
            @click="handleUpPublish(scope.row.id)"
            v-hasPermi="['form:definition:upPublish']"
          >取消发布</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleCopyDef(scope.row.id)"
            v-hasPermi="['form:definition:upPublish']"
          >复制表单</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 0"
            @click="handleUpdate(scope.row.id)"
            v-hasPermi="['form:definition:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 0"
            @click="handleDelete(scope.row)"
            v-hasPermi="['form:definition:remove']"
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
    <Dialog ref="dialog" @refresh="getList"></Dialog>
    <formDialog v-if="showFormDialog" :visible.sync="showFormDialog" :type="'2'" :formName="formName" :formId="formId"></formDialog>
  </div>
</template>

<script>
import {
  listDefinition,
  delDefinition,
  publish,
  unPublish,
  copyDef
} from "@/api/form/definition";
import Dialog from "./dialog";
import formDialog from "./formDialog";

export default {
  name: "formDefinition",
  dicts: ['is_publish', 'activity_status'],
  components: {
    Dialog,
    formDialog
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      imgUrl: "",
      flowChart: false,
      // 唯一标识符
      uniqueId: "",
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
      // 表单定义表格数据
      definitionList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        formCode: null,
        formName: null,
        version: null,
      },
      formName: "",
      formId: "",
      showFormDialog: false
    };
  },
  created() {
    this.getList();
  },
  activated() {
    const time = this.$route.query.t;
    if (time != null && time != this.uniqueId) {
      this.uniqueId = time;
      this.queryParams.pageNum = Number(this.$route.query.pageNum);
      this.getList();
    }
  },
  methods: {
    /** 查询表单定义列表 */
    getList() {
      this.loading = true;
      listDefinition(this.queryParams).then(response => {
        this.definitionList = response.rows;
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
    /** 新增按钮操作 */
    handleAdd() {
      this.$refs.dialog.show();
    },
    /** 表单设计按钮操作 */
    handleDesign(row) {
      if (row.isPublish === 1) {
        this.formName = row.formName;
        this.formId = row.id;
        this.showFormDialog = true;
      } else {
        const params = { pageNum: this.queryParams.pageNum };
        this.$tab.openPage("表单设计", '/form/form-design/index/' + row.id, params);
      }
    },
    /** 发布按钮操作 */
    handlePublish(id) {
      this.$modal.confirm('是否确认发布表单定义编号为"' + id + '"的数据项？').then(function() {
        return publish(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("发布成功");
      }).catch(() => {});
    },
    /** 取消发布按钮操作 */
    handleUpPublish(id) {
      this.$modal.confirm('是否确认取消发布表单定义编号为"' + id + '"的数据项？').then(function() {
        return unPublish(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("取消成功");
      }).catch(() => {});
    },
    /** 修改按钮操作 */
    handleUpdate(id) {
      this.$refs.dialog.show(id);
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除表单定义编号为"' + ids + '"的数据项？').then(function() {
        return delDefinition(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 复制表单按钮操作 */
    handleCopyDef(id) {
      this.$modal.confirm('是否确认复制表单定义编号为"' + id + '"的数据项？').then(function() {
        return copyDef(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("复制成功");
      }).catch(() => {});
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    }
  }
};
</script>

<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="流程编码" prop="flowCode">
        <el-input
          v-model="queryParams.flowCode"
          placeholder="请输入流程编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程名称" prop="flowName">
        <el-input
          v-model="queryParams.flowName"
          placeholder="请输入流程名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程类别" prop="category">
        <el-input
          v-model="queryParams.category"
          placeholder="请输入流程类别"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程版本" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入流程版本"
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
          @click="handleAdd1"
          v-hasPermi="['flow:definition:add']"
        >新增①</el-button>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd2"
          v-hasPermi="['flow:definition:add']"
        >新增②</el-button>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleImport"
          v-hasPermi="['flow:definition:importDefinition']"
        >导入流程定义</el-button>
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
      <el-table-column label="流程编码" align="center" prop="flowCode" width="150" sortable="custom"  :show-overflow-tooltip="true"/>
      <el-table-column label="流程名称" align="center" prop="flowName" sortable="custom" :show-overflow-tooltip="true"/>
      <el-table-column label="流程版本" align="center" prop="version" width="100" sortable="custom" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-tag>{{scope.row.version}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="流程类别" align="center" prop="modelValue" sortable="custom" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.modelValue === 'CLASSICS'">经典模型</el-tag>
          <el-tag v-else-if="scope.row.modelValue === 'MIMIC'">仿钉钉模型</el-tag>
          <span v-else>{{ scope.row.modelValue }}</span>
        </template>
      </el-table-column>
      <el-table-column label="流程类别" align="center" prop="category" sortable="custom" :show-overflow-tooltip="true"/>
      <el-table-column label="是否发布" align="center" prop="isPublish" width="140" sortable="custom" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_publish" :value="scope.row.isPublish"/>
        </template>
      </el-table-column>
      <el-table-column label="激活状态" align="center" prop="activityStatus" width="140" sortable="custom" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.activity_status" :value="scope.row.activityStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" sortable="custom" :sort-orders="['descending', 'ascending']" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 0"
            @click="handleUpdate(scope.row.id)"
            v-hasPermi="['flow:definition:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleDesign(scope.row.id)"
            v-hasPermi="['flow:definition:queryDesign']"
          >流程设计①</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleDesign(scope.row.id, true)"
            v-hasPermi="['flow:definition:queryDesign']"
          >流程设计②</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleDesign(scope.row.id, true, true)"
            v-hasPermi="['flow:definition:queryDesign']"
          >流程图</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 0"
            @click="handlePublish(scope.row.id)"
            v-hasPermi="['flow:definition:publish']"
          >发布</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 1"
            @click="handleUpPublish(scope.row.id)"
            v-hasPermi="['flow:definition:upPublish']"
          >取消发布</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.activityStatus === 0"
            @click="toActive(scope.row.id)"
          >激活</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.activityStatus === 1"
            @click="toUnActive(scope.row.id)"
          >挂起</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleCopyDef(scope.row.id)"
            v-hasPermi="['flow:definition:upPublish']"
          >复制流程</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleExport(scope.row)"
            v-hasPermi="['flow:definition:exportDefinition']"
          >导出流程</el-button>
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.isPublish === 0"
            @click="handleDelete(scope.row)"
            v-hasPermi="['flow:definition:remove']"
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

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        multiple
        :limit="20"
        accept=".json"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <span>仅允许导入json格式文件。</span>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="流程图" :visible.sync="flowChart" width="80%">
      <img :src="imgUrl" width="100%" style="margin:0 auto"/>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDefinition,
  delDefinition,
  publish,
  unPublish,
  copyDef,
  active, unActive
} from '@/api/flow/definition'
import Dialog from "@/views/flow/definition/dialog";
import { getToken } from '@/utils/auth'

export default {
  name: "Definition",
  components: {
    Dialog
  },
  dicts: ['is_publish', 'activity_status'],
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
      // 流程定义表格数据
      definitionList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        flowCode: null,
        flowName: null,
        version: null,
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/flow/definition/importDefinition"
      },
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
    active,
    /** 查询流程定义列表 */
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
    handleAdd1() {
      const params = { pageNum: this.queryParams.pageNum };
      this.$tab.openPage("流程设计", '/flow/flow-design/index', params);
    },

    /** 新增按钮操作 */
    handleAdd2() {
      this.$refs.dialog.show();
    },

    /** 修改按钮操作 */
    handleUpdate(id) {
      this.$refs.dialog.show(id);
    },

    /**
     * 设计按钮
     * @param id 流程定义id
     * @param onlyDesignShow 是否只显示设计器, true:只显示设计器 false:显示基础信息和设计器
     * @param disabled 是否可编辑 , true:不可标记 false:可标记 (本身warm-flow工作流内部会通过发布状态自行判断是否可以编辑，但是如果是需要查看的场景可以单独可控制)
     */
    handleDesign(id, onlyDesignShow, disabled) {
      const params = { pageNum: this.queryParams.pageNum , onlyDesignShow: onlyDesignShow, disabled: disabled};
      this.$tab.openPage("流程设计", '/flow/flow-design/index/' + id, params);
    },

    /** 发布按钮操作 */
    handlePublish(id) {
      this.$modal.confirm('是否确认发布流程定义编号为"' + id + '"的数据项？').then(function() {
        return publish(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("发布成功");
      }).catch(() => {});
    },

    /** 取消发布按钮操作 */
    handleUpPublish(id) {
      this.$modal.confirm('是否确认取消发布流程定义编号为"' + id + '"的数据项？').then(function() {
        return unPublish(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("取消成功");
      }).catch(() => {});
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除流程定义编号为"' + ids + '"的数据项？').then(function() {
        return delDefinition(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 复制流程按钮操作 */
    handleCopyDef(id) {
      this.$modal.confirm('是否确认复制流程定义编号为"' + id + '"的数据项？').then(function() {
        return copyDef(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("复制成功");
      }).catch(() => {});
    },

    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "用户导入";
      this.upload.open = true;
    },

    handleExport(row) {
      this.download('/flow/definition/exportDefinition/' + row.id, {
        ...this.queryParams
      }, row.flowCode + '_' + row.version + '.json')
    },

    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    },

    toActive(id) {
      this.$modal.confirm('是否确认激活流程定义编号为"' + id + '"的数据项？').then(function() {
        return active(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("激活成功");
      }).catch(() => {});
    },

    toUnActive(id) {
      this.$modal.confirm('是否确认挂起流程定义编号为"' + id + '"的数据项？').then(function() {
        return unActive(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("挂起成功");
      }).catch(() => {});
    },
  }
};
</script>

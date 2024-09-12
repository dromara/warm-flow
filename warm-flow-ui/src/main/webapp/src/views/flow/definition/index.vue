<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="流程编码" prop="flowCode">
        <el-input
          v-model="queryParams.flowCode"
          placeholder="请输入流程编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程名称" prop="flowName">
        <el-input
          v-model="queryParams.flowName"
          placeholder="请输入流程名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程版本" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入流程版本"
          clearable
          @keyup.enter="handleQuery"
        />
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
          v-hasPermi="['flow:definition:add']"
        >新增</el-button>
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleImport"
          v-hasPermi="['flow:definition:importDefinition']"
        >导入流程定义</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="definitionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" fixed />
      <el-table-column label="序号" width="50" align="center">
        <template #default="scope">
          <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="流程编码" align="center" prop="flowCode" width="150" :show-overflow-tooltip="true"/>
      <el-table-column label="流程名称" align="center" prop="flowName" :show-overflow-tooltip="true"/>
      <el-table-column label="流程版本" align="center" prop="version" width="100" :show-overflow-tooltip="true">
        <template #default="scope">
          <el-tag>{{scope.row.version}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="扩展字段" align="center" prop="ext" sortable="custom" :show-overflow-tooltip="true"/>
      <el-table-column label="是否发布" align="center" prop="isPublish" width="140" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="is_publish" :value="scope.row.isPublish"/>
        </template>
      </el-table-column>
      <el-table-column label="激活状态" align="center" prop="activityStatus" width="140" sortable="custom" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="activity_status" :value="scope.row.activityStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            type="text"
            size="small"
            @click="handleDesign(scope.row.id, scope.row.isPublish)"
            v-hasPermi="['flow:definition:queryDesign']"
          >流程设计</el-button>
          <el-button
            size="small"
            type="text"
            @click="toFlowImage(scope.row.id)"
          >流程图</el-button>
          <el-button
            type="text"
            size="small"
            v-if="scope.row.isPublish === 0"
            @click="handlePublish(scope.row.id)"
            v-hasPermi="['flow:definition:publish']"
          >发布</el-button>
          <el-button
            type="text"
            size="small"
            v-if="scope.row.isPublish === 1"
            @click="handleUpPublish(scope.row.id)"
            v-hasPermi="['flow:definition:upPublish']"
          >取消发布</el-button>
          <el-button
            size="small"
            type="text"
            v-if="scope.row.activityStatus === 0"
            @click="toActive(scope.row.id)"
          >激活</el-button>
          <el-button
            size="small"
            type="text"
            v-if="scope.row.activityStatus === 1"
            @click="toUnActive(scope.row.id)"
          >挂起</el-button>
          <el-button
            type="text"
            size="small"
            @click="handleCopyDef(scope.row.id)"
            v-hasPermi="['flow:definition:upPublish']"
          >复制流程</el-button>
          <el-button
            type="text"
            size="small"
            v-if="scope.row.isPublish === 0"
            @click="handleUpdate(scope.row.id)"
            v-hasPermi="['flow:definition:edit']"
          >修改</el-button>
          <el-button
            type="text"
            size="small"
            @click="handleExport(scope.row)"
            v-hasPermi="['flow:definition:exportDefinition']"
          >导出流程</el-button>
          <el-button
            type="text"
            size="small"
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
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <Dialog ref="dialog" @refresh="getList"></Dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xml"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许导入xml格式文件。</span>
          </div>
        </template>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="流程图" v-model="flowChart" width="80%" append-to-body>
      <img :src="imgUrl" width="100%" style="margin:0 auto"/>
    </el-dialog>
  </div>
</template>

<script setup name="Definition">
import {
  listDefinition,
  delDefinition,
  publish,
  unPublish,
  copyDef,
  flowChartNoColor, active, unActive
} from '@/api/flow/definition'
import Dialog from "@/views/flow/definition/dialog";
import { getToken } from '@/utils/auth'
import router from "@/router";

const { proxy } = getCurrentInstance();
const { is_publish, activity_status } = proxy.useDict('is_publish', 'activity_status');

const definitionList = ref([]);
const open = ref(false);
const loading = ref(true);
const imgUrl = ref("");
const flowChart = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    flowCode: null,
    flowName: null,
    version: null,
  },
});

const { queryParams } = toRefs(data);

/*** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: 'Bearer ' + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/flow/definition/importDefinition"
});

/** 查询流程定义列表 */
function getList() {
  loading.value = true;
  listDefinition(queryParams.value).then(response => {
    definitionList.value = response.rows;
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
  proxy.resetForm("queryForm");
  handleQuery();
}
// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length!==1
  multiple.value = !selection.length
}
/** 新增按钮操作 */
function handleAdd() {
  proxy.$refs["dialog"].show();
}

/** 流程设计按钮操作 */
function handleDesign(id, isPublish) {
  const params = { disabled: isPublish === 1, pageNum: queryParams.value.pageNum };
  router.push({ path: "/flow/flow-design/index/" + id, query: params });
}

/** 发布按钮操作 */
function handlePublish(id) {
  proxy.$modal.confirm('是否确认发布流程定义编号为"' + id + '"的数据项？').then(function() {
    return publish(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("发布成功");
  }).catch(() => {});
}

/** 取消发布按钮操作 */
function handleUpPublish(id) {
  proxy.$modal.confirm('是否确认取消发布流程定义编号为"' + id + '"的数据项？').then(function() {
    return unPublish(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("取消成功");
  }).catch(() => {});
}

/** 修改按钮操作 */
function handleUpdate(id) {
  proxy.$refs["dialog"].show(id);
}

/** 删除按钮操作 */
function handleDelete(row) {
  const ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除流程定义编号为"' + ids + '"的数据项？').then(function() {
    return delDefinition(ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 复制流程按钮操作 */
function handleCopyDef(id) {
  proxy.$modal.confirm('是否确认复制流程定义编号为"' + id + '"的数据项？').then(function() {
    return copyDef(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("复制成功");
  }).catch(() => {});
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "用户导入";
  upload.open = true;
}

function handleExport(row) {
  proxy.download('/flow/definition/exportDefinition/' + row.id, {
    ...queryParams.value
  }, row.flowCode + '_' + row.version + '.xml')
}

// 文件上传中处理
function handleFileUploadProgress(event, file, fileList) {
  upload.isUploading = true;
}
// 文件上传成功处理
function handleFileSuccess(response, file, fileList) {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].clearFiles();
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
  getList();
}
// 提交上传文件
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

function toFlowImage(id) {
  flowChartNoColor(id).then(response => {
    flowChart.value = true
    imgUrl.value = "data:image/gif;base64," + response.data;
  });
};

function toActive(id) {
  proxy.$modal.confirm('是否确认激活流程定义编号为"' + id + '"的数据项？').then(function() {
    return active(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("激活成功");
  }).catch(() => {});
};

function toUnActive(id) {
  proxy.$modal.confirm('是否确认挂起流程定义编号为"' + id + '"的数据项？').then(function() {
    return unActive(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("挂起成功");
  }).catch(() => {});
};

getList();
</script>

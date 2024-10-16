<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :span="4" :xs="24">
          <div class="head-container">
            <el-input
                v-model="groupName"
                placeholder="请输入部门名称"
                clearable
                prefix-icon="Search"
                style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
                :data="groupOptions"
                :props="{ label: 'label', children: 'children' }"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                ref="groupTreeRef"
                node-key="id"
                highlight-current
                default-expand-all
                @node-click="handleNodeClick"
            />
          </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="20" :xs="24">
          <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
            <el-form-item label="权限编码" prop="handlerCode">
              <el-input
                  v-model="queryParams.handlerCode"
                  placeholder="请输入权限编码"
                  clearable
                  style="width: 240px"
                  @keyup.enter="handleQuery"
              />
            </el-form-item>
            <el-form-item label="权限名称" prop="handlerName">
                <el-input
                  v-model="queryParams.handlerName"
                  placeholder="请输入权限名称"
                  clearable
                  style="width: 240px"
                  @keyup.enter="handleQuery"
                />
            </el-form-item>
            <el-form-item label="创建时间" style="width: 308px;">
                <el-date-picker
                  v-model="dateRange"
                  value-format="YYYY-MM-DD"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                ></el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                <el-button type="primary" :disabled="checkedItemList.length === 0" @click="submitForm">确 定</el-button>
            </el-form-item>
          </el-form>

          <el-row :gutter="10" class="mb8">
            <el-tag type="primary" style="margin-right: 10px" v-for="tag in checkedItemList" :key="tag.storageId" closable @close="handleClose(tag.storageId)">{{tag.storageId}}</el-tag>
          </el-row>

          <el-table v-loading="loading" :data="userList" @row-click="handleCheck">
            <el-table-column width="50" align="center">
              <template #default="scope">
                <el-checkbox v-model="scope.row.isChecked" @change.native="handleCheck(scope.row)"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column label="入库主键" align="center" key="storageId" prop="storageId" v-if="columns[0].visible" />
            <el-table-column label="权限编码" align="center" key="handlerCode" prop="handlerCode" v-if="columns[1].visible" :show-overflow-tooltip="true" />
            <el-table-column label="权限名称" align="center" key="handlerName" prop="handlerName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
            <el-table-column label="权限分组" align="center" key="groupName" prop="groupName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
            <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[4].visible" width="160">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="total > 0"
            style="margin-bottom: 35px;"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
          />
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="User">
import { handlerResult } from "@/api/flow/definition.js";

const { proxy } = getCurrentInstance();

const props = defineProps({
  userVisible: {
    type: Boolean
  },
  selectUser: {
    type: Array
  },
});
const userList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const groupName = ref("");
const groupOptions = ref(undefined);
// 列显隐信息
const columns = ref([
 { key: 0, label: `入库主键`, visible: true },
 { key: 1, label: `权限编码`, visible: true },
 { key: 2, label: `权限名称`, visible: true },
 { key: 3, label: `权限分组`, visible: true },
 { key: 4, label: `创建时间`, visible: true }
]);
const checkedItemList = ref([]); // 已选的itemList
const data = reactive({
 form: {},
 queryParams: {
   pageNum: 1,
   pageSize: 10,
   userName: undefined,
   status: "0",
   groupId: undefined
 },
  checkAllInfo: {
    isIndeterminate: false,
    isChecked: false,
  }
});

const { queryParams, form, checkAllInfo } = toRefs(data);
const emit = defineEmits(["update:userVisible", "handleUserSelect"]);

/** 通过条件过滤节点  */
const filterNode = (value, data) => {
 if (!value) return true;
 return data.label.indexOf(value) !== -1;
};
/** 根据名称筛选部门树 */
watch(groupName, val => {
 proxy.$refs["groupTreeRef"].filter(val);
});
watch(() => props.selectUser, (val, oldVal) => {
  if (oldVal) {
    proxy.$nextTick(() => {
      checkedItemList.value = checkedItemList.value.filter(e => {
        let index = val ? val.findIndex(v => v === e.storageId) : -1;
        userList.value.forEach(u => {
          if (u.storageId === e.storageId) u.isChecked = index !== -1;
        });
        return index !== -1;
      });
    });
  } else checkedItemList.value = val ? val.map(e => { return { storageId: e } }).filter(n => n.storageId) : [];
},{ deep: true, immediate: true });

/** 查询用户列表 */
function getList() {
  loading.value = true;
  let params = proxy.addDateRange(queryParams.value, dateRange.value);
  handlerResult(params).then(res => {
    loading.value = false;
    let handlerAuths = res.data[0].handlerAuths;
    total.value = handlerAuths.total;
    handlerAuths.rows.forEach(item => {
      item.isChecked = checkedItemList.value.findIndex(e => e.storageId === item.storageId) !== -1;
    })
    groupOptions.value = res.data[0].treeSelections;
    userList.value = handlerAuths.rows;
    isCheckedAll();
  });

};
/** 节点单击事件 */
function handleNodeClick(data) {
 queryParams.value.groupId = data.id;
 handleQuery();
}

/** 搜索按钮操作 */
function handleQuery() {
 queryParams.value.pageNum = 1;
 getList();
}

/** 重置按钮操作 */
function resetQuery() {
 dateRange.value = [];
 proxy.resetForm("queryRef");
 queryParams.value.groupId = undefined;
 proxy.$refs.groupTreeRef.setCurrentKey(null);
 handleQuery();
}

// 是否全选中
function isCheckedAll() {
  const len = userList.value.length;
  let count = 0;
  userList.value.map(item => {
    if (item.isChecked) count += 1;
  });
  checkAllInfo.value.isChecked = len === count && len > 0;
  checkAllInfo.value.isIndeterminate = count > 0 && count < len;
}

// 全选
function handleCheckAll() {
  const checkedArr = checkedItemList.value;
  checkAllInfo.value.isIndeterminate = false;
  if (checkAllInfo.value.isChecked) {
    userList.value = userList.value.map(item => {
      item.isChecked = true;
      if (checkedItemList.value.findIndex(e => e.storageId === item.storageId) === -1) {
        checkedArr.push({ storageId: item.storageId });
      }
      return item;
    });
  } else {
    userList.value = userList.value.map(item => {
      item.isChecked = false;
      let index = checkedArr.findIndex(e => e.storageId === item.storageId);
      if (index !== -1) checkedArr.splice(index, 1);
      return item;
    });
  }
  checkedItemList.value = checkedArr;
}

function handleCheck(row) {
  userList.value.forEach(e => {
    if (e.storageId === row.storageId) e.isChecked = !e.isChecked;
  });
  const checkedArr = [...checkedItemList.value];
  if (row.isChecked) {
    checkedArr.push({ storageId: row.storageId });
  } else {
    const index = checkedArr.findIndex(n => n.storageId === row.storageId);
    if (index !== -1) checkedArr.splice(index, 1);
  }
  checkedItemList.value = checkedArr;
  isCheckedAll();
};
// 删除标签
function handleClose(storageId) {
  userList.value.forEach(e => {
    if (e.storageId === storageId) e.isChecked = !e.isChecked;
  });
  const checkedArr = checkedItemList.value
  const index = checkedArr.findIndex(n => n.storageId === storageId);
  if (index !== -1) checkedArr.splice(index, 1);
  checkedItemList.value = checkedArr;
  isCheckedAll();
}

// 取消按钮
function cancel() {
  emit("update:userVisible", false);
}

// 提交按钮
function submitForm() {
  emit("handleUserSelect", checkedItemList.value);
  cancel();
}

getList();
</script>

<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :span="4" :xs="24">
          <div class="head-container">
            <el-input
                v-model="deptName"
                placeholder="请输入部门名称"
                clearable
                prefix-icon="Search"
                style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
                :data="deptOptions"
                :props="{ label: 'label', children: 'children' }"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                ref="deptTreeRef"
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
            <el-form-item label="用户名称" prop="userName">
                <el-input
                  v-model="queryParams.userName"
                  placeholder="请输入用户名称"
                  clearable
                  style="width: 240px"
                  @keyup.enter="handleQuery"
                />
            </el-form-item>
            <el-form-item label="手机号码" prop="phonenumber">
                <el-input
                  v-model="queryParams.phonenumber"
                  placeholder="请输入手机号码"
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
            <el-tag type="primary" style="margin-right: 10px" v-for="tag in checkedItemList" :key="tag.userId" closable @close="handleClose(tag.userId)">{{tag.userId}}</el-tag>
          </el-row>

          <el-table v-loading="loading" :data="userList" @row-click="handleCheck">
            <el-table-column width="50" align="center">
              <template #header v-if="!['转办', '委派'].includes(type)">
                  <el-checkbox
                      :indeterminate="checkAllInfo.isIndeterminate"
                      v-model="checkAllInfo.isChecked"
                      @change="handleCheckAll"
                  ></el-checkbox>
              </template>
              <template #default="scope">
                <el-checkbox v-model="scope.row.isChecked" @change.capture="handleCheck(row)"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="columns[0].visible" />
            <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
            <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
            <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
            <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" v-if="columns[4].visible" width="120" />
            <el-table-column label="状态" align="center" key="status" v-if="columns[5].visible">
              <template #default="scope">
                <el-tag :type="scope.row.status === '0' ? '' : 'warning'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[6].visible" width="160">
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
import * as api from "@/api/system/user";

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable, sys_user_sex } = proxy.useDict("sys_normal_disable", "sys_user_sex");

const props = defineProps({
  userVisible: {
    type: Boolean
  },
  selectUser: {
    type: Array
  },
  postParams: {
    type: Object
  },
  type: {
    type: String
  }
});
const userList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const deptName = ref("");
const deptOptions = ref(undefined);
const initPassword = ref(undefined);
const postOptions = ref([]);
const roleOptions = ref([]);
// 列显隐信息
const columns = ref([
 { key: 0, label: `用户编号`, visible: true },
 { key: 1, label: `用户名称`, visible: true },
 { key: 2, label: `用户昵称`, visible: true },
 { key: 3, label: `部门`, visible: true },
 { key: 4, label: `手机号码`, visible: true },
 { key: 5, label: `状态`, visible: true },
 { key: 6, label: `创建时间`, visible: true }
]);
const checkedItemList = ref([]); // 已选的itemList
const data = reactive({
 form: {},
 queryParams: {
   pageNum: 1,
   pageSize: 10,
   userName: undefined,
   phonenumber: undefined,
   status: "0",
   deptId: undefined
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
watch(deptName, val => {
 proxy.$refs["deptTreeRef"].filter(val);
});
watch(() => props.selectUser, (val, oldVal) => {
  if (oldVal) {
    proxy.$nextTick(() => {
      checkedItemList.value = checkedItemList.value.filter(e => {
        let index = val ? val.findIndex(v => v === e.userId) : -1;
        userList.value.forEach(u => {
          if (u.userId === e.userId) u.isChecked = index !== -1;
        });
        return index !== -1;
      });
    });
  } else checkedItemList.value = [];
},{ deep: true, immediate: true });
/** 查询部门下拉树结构 */
function getDeptTree() {
 api.deptTreeSelect().then(response => {
   deptOptions.value = response.data;
 });
};
/** 查询用户列表 */
function getList() {
  loading.value = true;
  let params = proxy.addDateRange(queryParams.value, dateRange.value);
  let url = "listUser";
  if (props.type) {
    let postParams = JSON.parse(JSON.stringify(props.postParams));
    url = postParams.url;
    delete postParams.url;
    params = { ...postParams, deptId: queryParams.value.deptId };
  }
  api[url](params).then(res => {
    loading.value = false;
    total.value = res.total;
    res.rows.forEach(item => {
      item.isChecked = checkedItemList.value.findIndex(e => e.userId === item.userId) !== -1;
    })
    userList.value = res.rows;
    isCheckedAll();
  });
};
/** 节点单击事件 */
function handleNodeClick(data) {
 queryParams.value.deptId = data.id;
 handleQuery();
};
/** 搜索按钮操作 */
function handleQuery() {
 queryParams.value.pageNum = 1;
 getList();
};
/** 重置按钮操作 */
function resetQuery() {
 dateRange.value = [];
 proxy.resetForm("queryRef");
 queryParams.value.deptId = undefined;
 proxy.$refs.deptTreeRef.setCurrentKey(null);
 handleQuery();
};
// 是否全选中
function isCheckedAll() {
  const len = userList.value.length;
  let count = 0;
  userList.value.map(item => {
    if (item.isChecked) count += 1;
  });
  checkAllInfo.value.isChecked = len === count && len > 0;
  checkAllInfo.value.isIndeterminate = count > 0 && count < len;
};
// 全选
function handleCheckAll() {
  const checkedArr = checkedItemList.value;
  checkAllInfo.value.isIndeterminate = false;
  if (checkAllInfo.value.isChecked) {
    userList.value = userList.value.map(item => {
      item.isChecked = true;
      if (checkedItemList.value.findIndex(e => e.userId === item.userId) === -1) {
        checkedArr.push({ userId: item.userId });
      }
      return item;
    });
  } else {
    userList.value = userList.value.map(item => {
      item.isChecked = false;
      let index = checkedArr.findIndex(e => e.userId === item.userId);
      if (index !== -1) checkedArr.splice(index, 1);
      return item;
    });
  }
  checkedItemList.value = checkedArr;
};
// 单选
function handleCheck(row) {
  // 转办|委派仅支持单选
  if (['转办', '委派'].includes(props.type)) {
    userList.value.forEach(e => {
      if (e.userId === row.userId) e.isChecked = true;
      else e.isChecked = false;
    });
    checkedItemList.value = [{ userId: row.userId }];
  } else {
    userList.value.forEach(e => {
      if (e.userId === row.userId) e.isChecked = !e.isChecked;
    });
    const checkedArr = [...checkedItemList.value];
    if (row.isChecked) {
      checkedArr.push({ userId: row.userId });
    } else {
      const index = checkedArr.findIndex(n => n.userId === row.userId);
      if (index !== -1) checkedArr.splice(index, 1);
    }
    checkedItemList.value = checkedArr;
    isCheckedAll();
  }
};
// 删除标签
function handleClose(userId) {
  userList.value.forEach(e => {
    if (e.userId === userId) e.isChecked = !e.isChecked;
  });
  const checkedArr = checkedItemList.value
  const index = checkedArr.findIndex(n => n.userId === userId);
  if (index !== -1) checkedArr.splice(index, 1);
  checkedItemList.value = checkedArr;
  isCheckedAll();
};
// 取消按钮
function cancel() {
  emit("update:userVisible", false);
};
// 提交按钮
function submitForm() {
  emit("handleUserSelect", checkedItemList.value);
  cancel();
};

getDeptTree();
getList();
</script>

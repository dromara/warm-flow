<template>
  <!-- 选择用户 -->
  <div class="selectUser">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :span="4" :xs="24">
        <div class="head-container">
          <el-input
            v-model="deptName"
            placeholder="请输入部门名称"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            style="margin-bottom: 20px"
          />
        </div>
        <div class="head-container">
          <el-tree
            :data="deptOptions"
            :props="defaultProps"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            ref="tree"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="用户名称" prop="userName">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入用户名称"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input
              v-model="queryParams.phonenumber"
              placeholder="请输入手机号码"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="dateRange"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <div style="text-align: right">
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            <el-button type="primary" size="mini" :disabled="checkedItemList.length === 0" @click="submitForm">确 定</el-button>
          </div>
        </el-form>

        <el-row class="mb8">
          <el-tag style="margin-right: 10px" v-for="tag in checkedItemList" :key="tag.userId" closable @close="handleClose(tag.userId)">{{tag.userId}}</el-tag>
        </el-row>

        <el-table ref="table" v-loading="loading" :data="userList" @row-click="handleCheck">
          <el-table-column width="50" align="center">
            <template #header v-if="!['转办', '委派'].includes(type)">
                <el-checkbox
                    :indeterminate="checkAllInfo.isIndeterminate"
                    v-model="checkAllInfo.isChecked"
                    @change="handleCheckAll"
                ></el-checkbox>
            </template>
            <template slot-scope="{ row }">
              <el-checkbox v-model="row.isChecked" @change.capture="handleCheck(row)"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="columns[0].visible" />
          <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
          <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" v-if="columns[4].visible" width="120" />
          <el-table-column label="状态" align="center" key="status" v-if="columns[5].visible">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === '0' ? '' : 'warning'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[6].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          style="margin-bottom: 35px;"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {interactiveTypeSysUser} from "@/api/flow/execute";
import {deptTreeSelect} from "@/api/system/user";

export default {
  name: "User",
  dicts: ['sys_normal_disable', 'sys_user_sex'],
  components: { Treeselect },
  props: ["userVisible", "selectUser", "postParams", "type"],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: null,
      // 弹出层标题
      title: "",
      // 部门树选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: "0",
        deptId: undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `用户编号`, visible: true },
        { key: 1, label: `用户名称`, visible: true },
        { key: 2, label: `用户昵称`, visible: true },
        { key: 3, label: `部门`, visible: true },
        { key: 4, label: `手机号码`, visible: true },
        { key: 5, label: `状态`, visible: true },
        { key: 6, label: `创建时间`, visible: true }
      ],
      checkedItemList: [], // 已选的itemList
      checkAllInfo: {
        isIndeterminate: false,
        isChecked: false,
      }
    };
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
    selectUser: {
      handler(val, oldVal) {
        if (oldVal) {
          this.$nextTick(() => {
            this.checkedItemList = this.checkedItemList.filter(e => {
              let index = val ? val.findIndex(v => v === e.userId) : -1;
              this.userList.forEach(u => {
                if (u.userId === e.userId) u.isChecked = index !== -1;
              });
              return index !== -1;
            });
          });
        } else {
          this.checkedItemList = val.map(e => { return { userId: e } });
        }
      },
      deep: true,
      immediate: true
    }
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      let params = this.addDateRange(this.queryParams, this.dateRange);
      if (this.type) {
        let postParams = JSON.parse(JSON.stringify(this.postParams));
        delete postParams.url;
        params = { ...postParams, deptId: this.queryParams.deptId };
      }
      interactiveTypeSysUser(params).then(response => {
          this.total = response.total;
          this.loading = false;
          response.rows.forEach(item => {
            item.isChecked = this.checkedItemList.findIndex(e => e.userId === item.userId) !== -1;
          })
          this.userList = response.rows;
          this.isCheckedAll();
        }
      );
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.handleQuery();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.queryParams.deptId = undefined;
      this.$refs.tree.setCurrentKey(null);
      this.handleQuery();
    },
    // 是否全选中
    isCheckedAll() {
      const len = this.userList.length;
      let count = 0;
      this.userList.map(item => {
        if (item.isChecked) count += 1;
      });
      this.checkAllInfo.isChecked = len === count && len > 0;
      this.checkAllInfo.isIndeterminate = count > 0 && count < len;
    },
    // 全选
    handleCheckAll() {
      const checkedItemList = this.checkedItemList;
      this.checkAllInfo.isIndeterminate = false;
      if (this.checkAllInfo.isChecked) {
        this.userList = this.userList.map(item => {
          item.isChecked = true;
          if (this.checkedItemList.findIndex(e => e.userId === item.userId) === -1) {
            checkedItemList.push({ userId: item.userId });
          }
          return item;
        });
      } else {
        this.userList = this.userList.map(item => {
          item.isChecked = false;
          let index = checkedItemList.findIndex(e => e.userId === item.userId);
          if (index !== -1) checkedItemList.splice(index, 1);
          return item;
        });
      }
      this.checkedItemList = checkedItemList
    },
    // 单选
    handleCheck(row) {
      // 转办|委派仅支持单选
      if (['转办', '委派'].includes(this.type)) {
        this.userList.forEach(e => {
          if (e.userId === row.userId) e.isChecked = true;
          else e.isChecked = false;
        });
        this.checkedItemList = [{ userId: row.userId }];
      } else {
        this.userList.forEach(e => {
          if (e.userId === row.userId) e.isChecked = !e.isChecked;
        });

        const checkedItemList = this.checkedItemList;
        if (row.isChecked) {
          checkedItemList.push({ userId: row.userId });
        } else {
          const index = checkedItemList.findIndex(n => n.userId === row.userId);
          if (index !== -1) checkedItemList.splice(index, 1);
        }
        this.checkedItemList = checkedItemList;
        this.isCheckedAll();
      }
    },
    // 删除标签
    handleClose(userId) {
      this.userList.forEach(e => {
        if (e.userId === userId) e.isChecked = !e.isChecked;
      });
      const checkedItemList = this.checkedItemList
      const index = checkedItemList.findIndex(n => n.userId === userId);
      if (index !== -1) checkedItemList.splice(index, 1);
      this.checkedItemList = checkedItemList;
      this.isCheckedAll();
    },
    // 取消按钮
    cancel() {
      this.$emit("update:userVisible", false);
    },
    // 提交按钮
    submitForm() {
      this.$emit("handleUserSelect", this.checkedItemList);
      this.cancel();
    }
  }
};
</script>

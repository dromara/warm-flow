<template>
  <div class="app-container">
    <el-table :data="taskList">
      <el-table-column label="序号" width="50" align="center" key="id" prop="id">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="开始节点名称" align="center" prop="nodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="结束节点名称" align="center" prop="targetNodeName" :show-overflow-tooltip="true"/>
      <el-table-column label="审批人" align="center" prop="approver" :show-overflow-tooltip="true"/>
      <el-table-column label="协作类型" align="center" prop="cooperateType" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cooperate_type" :value="scope.row.cooperateType"/>
        </template>
      </el-table-column>
      <el-table-column label="协作人" align="center" prop="collaborator" :show-overflow-tooltip="true"/>
      <!--       <el-table-column label="流转类型" align="center" prop="skipType" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.skipType==='PASS'">通过</el-tag>
                <el-tag v-if="scope.row.skipType==='REJECT'" type="danger">退回</el-tag>
                <el-tag v-if="scope.row.skipType==='NONE'" type="info">无动作</el-tag>
              </template>
            </el-table-column> -->
      <el-table-column label="流程状态" align="center" prop="flowStatus" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.flow_status" :value="scope.row.flowStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="审批意见" align="center" prop="message" :show-overflow-tooltip="true"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="业务详情" align="center" width="100">
        <template slot-scope="scope">
          <el-button size="mini" v-if="scope.row.ext" @click="handle(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="110" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.nodeType === 1 && scope.row.formCustom === 'Y'"
            @click="handleForm(scope.row.id)"
          >查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <component v-if="approve" v-bind:is="approve" :businessId="businessId" :taskId="taskId" :infoVo="infoVo" @refresh="fetchTaskList"></component>
    <formDialog v-if="showFormDialog" :disabled="true" :formName="'查看'" :visible.sync="showFormDialog" :showApproval="false" :taskId="taskId" :type="'1'"></formDialog>
  </div>
</template>

<script>
import {doneList} from '@/api/flow/execute';
import formDialog from "../../../form/definition/formDialog";

export default {
  name: "DoneList",
  dicts: ['flow_status', 'cooperate_type'],
  components: {
    formDialog
  },
  data() {
    return {
      instanceId: "",
      formPath: "",
      // 历史任务记录表格数据
      taskList: [],
      taskId: "",
      businessId: "",
      approve: null,
      infoVo: null,
      showFormDialog: false
    };
  },
  activated() {
    this.instanceId = this.$route.params.instanceId;
    this.formPath = this.$route.query.formPath;
    this.infoVo = this.$route.query.infoVo; // 接收传递过来的infoVo
    this.fetchTaskList();
  },
  methods: {
    fetchTaskList() {
      doneList(this.instanceId).then(response => {
        this.taskList = response.data;
      });
    },
    handle(row) {
      // 设置任务ID和业务ID
      this.taskId = row.id;
      this.businessId = row.businessId;

      // 获取或设置infoVo数据
      // 解析ext字段的JSON数据
      if (row.ext) {
        try {
          const parsedExt = JSON.parse(row.ext);
          this.infoVo = {...parsedExt};
        } catch (e) {
          console.error('Invalid ext JSON format:', row.ext);
        }
      }
      // 获取或设置formPath路径
      const formPath = row.formPath || this.formPath;

      // 如果存在formPath，则动态加载组件
      if (formPath) {
        // 通过require动态加载组件
        this.approve = (resolve) => require([`@/views/${formPath}`], resolve);
      } else {
        this.approve = null;
      }
    },
    // 查看表单详情
    handleForm(id) {
      this.taskId = id;
      this.showFormDialog = true;
    }
  }
};
</script>

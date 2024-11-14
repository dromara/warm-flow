<template>
     <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick" type="border-card" stretch>
        <el-form
            :model="nodeConfig"
            label-position="top"
            label-width="auto"
        >
        <el-tab-pane label="审批设置" name="first">
        
            <el-form-item label="审批人组合" >
                <el-button circle icon="Plus" @click="initUser"></el-button>
            </el-form-item>
            <el-form-item label="已选择" >
              
                <div class="flow-user-selected" v-show="nodeConfig.permissionFlag.length > 0">
                <!-- <span> 审批：</span>   下面为选中的办理人， 根据 使用tag 方式展示 -->
                <el-tag  closable  @close="selectClose( item.id)" v-for="item in nodeConfig.permissionFlag" :key="item.id">
                            {{ item.name }}
                    </el-tag>
                </div>
                <div v-show="nodeConfig.permissionFlag.length == 0">
                    无
                </div>
            </el-form-item>

            <el-form-item label="协作方式" prop="nodeRatioType">
                <el-radio-group v-model="nodeConfig.nodeRatioType">
                    <el-radio  label="1">或签</el-radio>
                    <el-radio  label="2">票签</el-radio>
                    <el-radio  label="3">会签</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="票签占比（%）" prop="nodeRatio"   v-show="nodeConfig.nodeRatioType == '2'">
               
                <el-tooltip class="box-item" effect="dark" content="票签比例范围：(0-100)的值">
                    <el-input-number
                        v-model="nodeConfig.nodeRatio"
                        class="mx-4"
                        :min="0"
                        :max="100"
                        controls-position="right"
                    />
                    <el-icon :size="14" class="mt5">
                    <WarningFilled />
                    </el-icon>
                </el-tooltip>
            </el-form-item>

            <el-form-item label="回退时的节点" prop="backType">
                <el-radio-group v-model="nodeConfig.backType">
                    <el-radio  label="1" @click="backToStartNode()">开始节点</el-radio>
                    <el-radio  label="2">选择节点</el-radio>
                    <!-- <el-radio  value="3" disabled>动态节点</el-radio> -->
                </el-radio-group>
            </el-form-item>
            <el-form-item label="请选择节点" prop="backTypeNode" v-if="nodeConfig.backType == '2'">
                <el-select v-model="nodeConfig.backTypeNode"   >
                    <el-option   v-for="item,index in backNodeList" :key="index"  :value="item.nodeId" :label="item.nodeName" />
                </el-select>
            </el-form-item>

            <!-- <el-form-item label="审批人为空时"  prop="emptyApprove.type">
                <el-radio-group v-model="nodeConfig.emptyApprove.type">
                    <el-radio  value="AUTO">自动通过</el-radio>
                    <el-radio  value="REJECT">自动拒绝</el-radio>
                    <el-radio  value="USER">
                        <span class="flow-user-select-checkbox">
                            指定人员 
                            <span style="width: 20px; " @click.prevent>
                                <el-icon style="float: right" :size="14" @click="addDefaultUser()" >
                                    <CirclePlus />
                                </el-icon>
                            </span>
                        </span>
                    </el-radio>
                    <el-radio  value="MANAGER">流程管理员</el-radio>
                </el-radio-group>
            </el-form-item> -->
            <el-form-item :label="nodeConfig.emptyApprove.value.length == 0 ? '请选择用户': '已选择用户' " v-if="nodeConfig.emptyApprove.type == 'user'" >
                <div class="flow-user-selected" >
                <el-tag  closable   v-for="item in nodeConfig.emptyApprove.value" :key="item.id" @close="emptyUserRemove(item.id)" >
                        {{ item.name }}
                    </el-tag>
                </div>
            </el-form-item>
        
            
        </el-tab-pane>
        <el-tab-pane label="监听器" name="third">
            <el-form-item label="审批人为空时"  prop="listenerTypes">
                <el-select
                    v-model="nodeConfig.listenerTypes"
                    multiple
                    placeholder="请选择"
                    >
                            <el-option label="任务创建" value="create"></el-option>
                            <el-option label="任务开始办理" value="start"></el-option>
                            <el-option label="分派监听器" value="assignment"></el-option>
                            <el-option label="权限认证" value="permission"></el-option>
                            <el-option label="任务完成" value="finish"></el-option>
                        </el-select>
            </el-form-item>
            <el-form-item label="监听器路径" description="输入监听器的路径，以@@分隔,顺序与监听器类型一致" prop="listenerPath">
                <el-input type="textarea" v-model="nodeConfig.listenerPath" rows="8"></el-input>
                <el-tooltip class="box-item" effect="dark" content="输入监听器的路径，以@@分隔，顺序与监听器类型一致">
                    <el-icon :size="14" class="mt5">
                    <WarningFilled />
                    </el-icon>
                </el-tooltip>
            </el-form-item>

        </el-tab-pane>
    </el-form>
  </el-tabs>
  <el-dialog title="办理人选择" v-if="userVisible" v-model="userVisible" width="80%" append-to-body>
      <selectUser v-model:selectUser="nodeConfig.permissionFlag" v-model:userVisible="userVisible" @handleUserSelect="handleUserSelect"></selectUser>
    </el-dialog>

</template>

<script setup>
    import {defineProps, toRefs,watch, ref, onMounted, reactive} from 'vue';
    import selectUser from "@/views/components/selectUser";
    const roleSelectRef = ref(null);
    const userSelectRef = ref(null);
    const emptySelectRef = ref(null)
    const props = defineProps({
        data:{},
        backNodeList:[],  // 回退节点选择列表
    })
    
    const userVisible = ref(false);

    const nodeConfig = reactive({
        nodeRatioType: "1",
        backType: '1',
        nodeRatio: 0,
        permissionFlag: [],
        combination:{
            role: [],
            user:[]
        },
        emptyApprove:{
            type: 'AUTO',
            value: [],
        },
        value:'',
        buttons:[
            {
                type: 'aggren',
                checked: true,
                text:"同意",
            },
            {
                type: 'reject',
                checked: true,
                text:"拒绝",
            },
            {
                type: 'back',
                checked: false,
                text:"回退",
            },
            {
                type: 'transfer',
                checked: false,
                text:"转办",
            },
            {
                type: 'depute',
                checked: false,
                text:"委派",
            },
           
            {
                type: 'signAdd',
                checked: false,
                text:"加签",
            },
            {
                type: 'signRedu',
                checked: false,
                text:"减签",
            }
            
            
        ]
    })

    // 协作方式值
    watch(()=> nodeConfig.nodeRatioType, (newV,oldv) => {
        if(newV == '1'){
            nodeConfig.nodeRatio = 0;
        }else if(newV == '3'){
            nodeConfig.nodeRatio = 100;
        }else{
            nodeConfig.nodeRatio = 70;
        }
    })
    
    const initUser = () => {
        userVisible.value = true;
    }
    const handleUserSelect =(checkedItemList) => {
        nodeConfig.value.permissionFlag = checkedItemList.map(e => {
            return e.storageId;
        }).filter(n => n);
    }

    // 流程图节点中显示的内容
    const showValue = () => {
        let value = '';
        if(nodeConfig.permissionFlag){
            const permissionFlags  = nodeConfig.permissionFlag
            for(const i in permissionFlags){
                value  += permissionFlags[i] + ",";
            }
        }
        nodeConfig.value = value;
      
    }

  
 
    // 表格中选择的角色，转为流程中的角色
   
    const emptySelectData = (userList) => {
        const users =  nodeConfig.emptyApprove.value;
        for(const i in userList){
                const user = userList[i];
            if( !users.find(r => r.id == user.id )){
                users.push({
                    id: user.id,
                    name: user.nick
                })
            }
        }
    }


    // 关闭tag时， 移除审批人
    const selectClose = ( id) =>{
        const permissionFlags =  nodeConfig.permissionFlag;
        let index =  roles.findIndex(item => item.id === id);
        if(index >= 0){
            roles.splice(index, 1);
        }
       
    }
    // 审批人为空  指定人员移除
    const emptyUserRemove = (id) => {
        const users =  nodeConfig.emptyApprove.value;
        let index =  users.findIndex(item => item.id === id);
            if(index >= 0){
                users.splice(index, 1);
            }
    }

    // 回退到开始节点， 默认第一个为开始节点，且勿要错乱顺序
    const backToStartNode = () =>{
        nodeConfig.backTypeNode = backNodeList.value[0].nodeId;
    }

    const userList = ref([])
  

    const activeName = ref('first');
    
    const {data, backNodeList} = toRefs(props);

    // 页面打开时， 参数初始化
    Object.assign(nodeConfig, data.value.properties);


    // 关闭页面时的验证和处理。 
    const formConfig = async () => {
        return new Promise((resolve, reject) => {
             showValue();
            if(nodeConfig.backTypeNode == null){
                nodeConfig.backTypeNode = backNodeList.value[0].nodeId;
            }
            resolve(nodeConfig);
            // reject();
        })
    }

    defineExpose({
        formConfig
    })

   
</script>

<style  scoped>
    .flow-user-select-checkbox{
        display: flex;
        flex-direction: row;
        flex-wrap: nowrap;
        align-items: center;
        gap: 5px;
    }
    .flow-user-selected{
        display: flex;
        align-content: center;
        align-items: center;
        flex-wrap: wrap;
        gap: 10px;
        width: 100%;
    
    }
    .flow-operate-setting{
        display: flex;
        flex-direction: column;
        gap: 10px;
    }
</style>
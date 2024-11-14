<template>
    <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick" type="border-card" >
        <el-form
                :model="nodeConfig"
                label-position="top"
                label-width="auto"
                :rules="rules"
                >
            <el-tab-pane label="监听器" name="first" >
                <el-form-item label="监听器类型"  prop="listenerTypes">
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
</template>

<script setup>
   import {defineProps, defineExpose, toRefs,watch, ref, reactive} from 'vue';
   const props = defineProps({
       data:{},
   })
   const activeName = ref('first');
  
   const nodeConfig = reactive({
        listenerPath: [],
        listenerTypes: [],
   })

   const rules = ref({})

   const {data} = toRefs(props);
   watch(()=>data,(newv,oldv)=>{
       alert(newv);
   })

   Object.assign(nodeConfig, data.value.properties);
    const formConfig = () => {
        return new Promise((resolve, reject) => {
            nodeConfig.value = "所有人";
            resolve(nodeConfig);
        });
    }

    defineExpose({
        formConfig
    })

  

</script>

<style  scoped>
    .flow-operate-setting{
        display: flex;
        flex-wrap: nowrap;
        flex-direction: column;
        gap: 10px;
    }
</style>
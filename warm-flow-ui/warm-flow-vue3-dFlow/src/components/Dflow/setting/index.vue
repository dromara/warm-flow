<template>
   <el-drawer :key="nodeData?.nodeId"
            v-model="drawer"
            @open="openDrawer"
            @close="closeHandler"
            :direction="direction"
            destroy-on-close	
            :force-render="false"
        >   
        <template #header>
            <el-text v-if="!drawerEdit" @click="changeTitle" tag="ins">{{ nodeData?.nodeName }}</el-text>
            <el-input v-else  ref="drawerEditInput" v-model="nodeData.nodeName" autofocus  @blur="drawerEditBlur" />
        </template>
        <template #default>
       
            <component  ref="settingRef" :key="nodeData.nodeId" :data="nodeData" :is="currComponent" :backNodeList="backNodeList"></component>
         
        </template>
          
    </el-drawer>
</template>

<script setup>
    
    import {ref,  defineExpose, nextTick} from 'vue';
    import UserConfig from './userConfig.vue'
    import StartConfig from './startConfig.vue'
    import SerialConfig from './serialConfig.vue'
    const drawer = ref(false);
    // const props = defineProps({
    //     type: String,
    //     nodeData:{},
    // })
    const direction = ref('rtl')
    const nodeData = ref();
    const backNodeList = ref([]);
    const currComponent = ref();
    const settingRef =ref();
    
    // 初始化 动态组件
    const initComponent = () => {
        switch(nodeData.value?.nodeType){
            case 'start': currComponent.value = StartConfig; break;
            case 'bewteen': currComponent.value = UserConfig; break;
            case 'serial-node': currComponent.value = SerialConfig; break;
            default : currComponent.value = UserConfig; break;
        }
        
    }

    // 打开抽屉事件
    const openDrawer = (e, e1, e2) =>{
        console.log(e)
    }
    // 打开抽屉, 传入值
    const openHandler = (currNode, backNodes) => {
        nodeData.value = currNode
        backNodeList.value = backNodes
        // 加载对应组件
        initComponent();
        drawer.value = true;
    }
    const closeHandler = () => {
        // 配置验证和保存
       settingRef.value.formConfig().then(formValue => {
            nodeData.value.properties = formValue;
            nodeData.value.value = formValue.value;
       }).catch(error => {
           console.log(error)
       });
   
     
        drawer.value = false;
    }

    
    const drawerEdit = ref(false);
    const drawerEditInput = ref(null);
    // 修改title
    const changeTitle = () => { 
        drawerEdit.value = true;
        nextTick(() => {
            drawerEditInput.value.focus();
        })
    }
    const drawerEditBlur = () => {
        drawerEdit.value = false;
        drawerEditInput.value.blur();   
    }

    defineExpose({
        openHandler, closeHandler
    })
    
</script>

<style scoped>
    .flow-setting-container-aa{
        display: inline-block;
        box-sizing: border-box;
        /* border: 1px solid tomato; */
        padding: 10px;
    }
</style>
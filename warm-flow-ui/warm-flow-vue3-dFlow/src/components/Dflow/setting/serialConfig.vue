<template>
     <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick" type="border-card" stretch>
   <el-tab-pane label="条件设置" name="first">
        <el-form
                :model="formData"
                label-position="top"
                label-width="auto"
                >
            <div class="flow-operate-setting">
                <el-row  >
                    <el-col :span="24" style="display: flex; align-items: center;  gap: 10px;">
                        <h3 >
                            <el-checkbox v-model="formData.conditions.simple" disabled></el-checkbox> 简单模式 </h3>
                            <el-switch  
                                v-model="formData.conditions.group"
                                 active-text="且"
                                inactive-text="或"
                                 active-value="and"
                                inactive-value="or"
                            ></el-switch>
                    </el-col>
                            
                </el-row>
                <el-row v-if="formData.conditions.simpleData.length == 0">
                    <el-col :span="24">  <el-button @click="addCondition(0)"  :size="12" icon="Plus" circle /> </el-col>
                </el-row>
                <el-row  :gutter="10" v-for="(item, index) in formData.conditions.simpleData" :key="index">
                    
                    <el-col :span="8">
                        <el-input v-model="item.key" placeholder="条件" />
                    </el-col>
                    <el-col :span="8">
                        <el-select v-model="item.cond" placeholder="条件类型" >
                            <el-option  label="大于"  value="gt"  /> 
                            <el-option  label="大于等于"  value="ge"  /> 
                            <el-option  label="等于"  value="eq"  /> 
                            <el-option  label="不等于"  value="ne"  /> 
                            <el-option  label="小于"  value="lt"  /> 
                            <el-option  label="小于等于"  value="le"  /> 
                            <el-option  label="包含"  value="like"  /> 
                            <el-option  label="不包含"  value="notNike"  /> 
                        </el-select>
                    </el-col>
                    <el-col :span="8">
                        <el-input v-model="item.value" placeholder="值" />
                    </el-col>
                    <el-col :span="6" style="margin: 10px 0;">
                        <el-select v-model="formData.conditions.group" disabled placeholder="组合条件" >
                            <el-option  label="且"  value="and"  /> 
                            <el-option  label="或"  value="or"  /> 
                        </el-select>
                    </el-col>
                    <el-col :span="18" style="margin: 10px 0; display: flex; align-items: center; gap: 5px;">
                        <el-button @click="addCondition(index)"  :size="24" icon="Plus" circle />
                        <el-button @click="removeCondition(index)" type="danger" :size="24" icon="Minus" circle/>
                    </el-col>
                </el-row>
                
            </div>
        </el-form>
   </el-tab-pane>
   <el-tab-pane label="表单权限" name="second">表单权限</el-tab-pane>
 </el-tabs>
</template>

<script setup>
    import {defineProps, toRefs, defineExpose, reactive, ref } from 'vue';
    const props = defineProps({
        data:{},
    })
    const {data} = toRefs(props);   
   
    const formData = reactive({
        conditions:{
            simple: true,
            group:'and',
            simpleData:[
                {
                    key:'',
                    cond:'',
                    value:'',
                    next: 'and',
                }
            ]
        },
   })
    const showValue = () => {
        let showInfo  = "";
        const simpleData = formData.conditions.simpleData;
        const group =  formData.conditions.group  == 'and' ? "且" : "或"; 
        for(const i in simpleData){
           const key = simpleData[i].key;
           const cond =  condTypeToName(simpleData[i].cond);
           const value = simpleData[i].value;
           if(key.trim() == "" || cond.trim() == "" ){
               console.log("缺少必填项");
               return false;
           }
           showInfo  +=  key + " " +  cond  + " " + value ;
            if(i < simpleData.length - 1){
                showInfo += " " + group + " ";
            }
        }
        formData.value = showInfo;
        return true;
    }

    const condTypeToName = (type) => {
        switch (type) {
            case "gt":
                return "大于";
            case "ge":
                return "大于等于";
            case "eq":
                return "等于";
            case "ne":
                return "不等于";
            case "lt":
                return "小于";
            case "le":
                return "小于等于";
            case "like":
                return "包含";
            case "notLike":
                return "不包含";
            default:
                return "";
        }
    }

    const addCondition = (index) => {
        formData.conditions.simpleData.splice(index + 1, 0, {
            key:'',
            cond:'',
            value:'',
            next: 'and',
        })
    }
    const removeCondition = (index) => {
        formData.conditions.simpleData.splice(index, 1);
    }

    Object.assign(formData, data.value.config);
    const formConfig = async () => {
        return new Promise((resolve, reject) => {
            const result = showValue();
            if(result){
                resolve(formData);
            }else {
                reject('error');
            }
        })

        
    }

    const activeName = ref('first');


    defineExpose({
        formConfig
    })

</script>

<style lang="scss" scoped>

</style>
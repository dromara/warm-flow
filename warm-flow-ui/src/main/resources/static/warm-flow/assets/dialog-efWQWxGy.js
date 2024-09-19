import{e as j,h as I,i as L}from"./definition-CAAY_8pD.js";import{r as h,C as S,J as E,d as i,o as d,c as P,h as l,l as c,f as n,m as k,p as V,n as _,e as t,K as J,L as K,D as O,B as z,v as A}from"./index-CHQH9-TC.js";const G={class:"app-container"},H=k("i",{class:"el-icon-question"},null,-1),M={class:"dialog-footer"},Q=z({name:"Dialog"}),Z=Object.assign(Q,{emits:["refresh"],setup(W,{expose:T,emit:N}){const{proxy:v}=A();v.useDict("isPublish");const s=h(!1),m=h(!1),b=h("");h(!0);const U=S({form:{},rules:{flowCode:[{required:!0,message:"流程编码不能为空",trigger:"blur"}],flowName:[{required:!0,message:"流程名称不能为空",trigger:"blur"}],isPublish:[{required:!0,message:"是否开启流程不能为空",trigger:"change"}],formCustom:[{required:!0,message:"请选择审批表单是否自定义",trigger:"change"}]}}),w=N,{form:e,rules:D}=E(U);async function q(f,o){x(),m.value=o,f&&await j(f).then(u=>{e.value=u.data,e.value.listenerType&&(e.value.listenerType=e.value.listenerType.split(","))}),s.value=!0,m.value?b.value="详情":f?b.value="修改":b.value="新增"}function y(){s.value=!1,x()}function x(){e.value={id:null,flowCode:null,flowName:null,version:null,isPublish:null,formCustom:null,formPath:null,createTime:null,updateTime:null,delFlag:null},v.resetForm("definitionRef")}function B(){v.$refs.definitionRef.validate(f=>{f&&(e.value.listenerType=e.value.listenerType.join(","),e.value.id!=null?I(e.value).then(o=>{v.$modal.msgSuccess("修改成功"),s.value=!1,w("refresh")}):L(e.value).then(o=>{v.$modal.msgSuccess("新增成功"),s.value=!1,w("refresh")}))})}return T({show:q}),(f,o)=>{const u=i("el-input"),r=i("el-form-item"),p=i("el-option"),C=i("el-select"),R=i("el-tooltip"),F=i("el-form"),g=i("el-button"),$=i("el-dialog");return d(),P("div",G,[l(s)?(d(),c($,{key:0,title:l(b),modelValue:l(s),"onUpdate:modelValue":o[8]||(o[8]=a=>O(s)?s.value=a:null),width:"500px","label-width":"150px","append-to-body":""},{footer:n(()=>[k("div",M,[l(m)?_("",!0):(d(),c(g,{key:0,type:"primary",onClick:B},{default:n(()=>[V("确 定")]),_:1})),l(m)?_("",!0):(d(),c(g,{key:1,onClick:y},{default:n(()=>[V("取 消")]),_:1})),l(m)?(d(),c(g,{key:2,onClick:y},{default:n(()=>[V("关 闭")]),_:1})):_("",!0)])]),default:n(()=>[t(F,{ref:"definitionRef",model:l(e),rules:l(D),disabled:l(m)},{default:n(()=>[t(r,{label:"流程编码",prop:"flowCode"},{default:n(()=>[t(u,{modelValue:l(e).flowCode,"onUpdate:modelValue":o[0]||(o[0]=a=>l(e).flowCode=a),placeholder:"请输入流程编码",maxlength:"40","show-word-limit":""},null,8,["modelValue"])]),_:1}),t(r,{label:"流程名称",prop:"flowName"},{default:n(()=>[t(u,{modelValue:l(e).flowName,"onUpdate:modelValue":o[1]||(o[1]=a=>l(e).flowName=a),placeholder:"请输入流程名称",maxlength:"100","show-word-limit":""},null,8,["modelValue"])]),_:1}),t(r,{label:"流程版本",prop:"version"},{default:n(()=>[t(u,{modelValue:l(e).version,"onUpdate:modelValue":o[2]||(o[2]=a=>l(e).version=a),placeholder:"请输入流程版本",maxlength:"20","show-word-limit":""},null,8,["modelValue"])]),_:1}),t(r,{label:"扩展字段",prop:"ext",description:"比如流程分类"},{default:n(()=>[t(u,{modelValue:l(e).ext,"onUpdate:modelValue":o[3]||(o[3]=a=>l(e).ext=a),placeholder:"请输入流程版本",maxlength:"20","show-word-limit":""},null,8,["modelValue"])]),_:1}),l(m)?(d(),c(r,{key:0,label:"是否发布",prop:"isPublish"},{default:n(()=>[t(C,{modelValue:l(e).isPublish,"onUpdate:modelValue":o[4]||(o[4]=a=>l(e).isPublish=a),placeholder:"请选择是否开启流程"},{default:n(()=>[(d(!0),P(J,null,K(f.is_publish,a=>(d(),c(p,{key:a.value,label:a.label,value:parseInt(a.value)},null,8,["label","value"]))),128))]),_:1},8,["modelValue"])]),_:1})):_("",!0),t(r,{label:"审批表单路径",prop:"formPath"},{default:n(()=>[t(u,{modelValue:l(e).formPath,"onUpdate:modelValue":o[5]||(o[5]=a=>l(e).formPath=a),placeholder:"请输入审批表单路径",maxlength:"100","show-word-limit":""},null,8,["modelValue"])]),_:1}),t(r,{label:"监听器类型",prop:"formPath"},{default:n(()=>[t(C,{modelValue:l(e).listenerType,"onUpdate:modelValue":o[6]||(o[6]=a=>l(e).listenerType=a),multiple:""},{default:n(()=>[t(p,{label:"任务创建",value:"create"}),t(p,{label:"任务开始办理",value:"start"}),t(p,{label:"分派监听器",value:"assignment"}),t(p,{label:"权限认证",value:"permission"}),t(p,{label:"任务完成",value:"finish"})]),_:1},8,["modelValue"])]),_:1}),t(r,{label:"监听器路径",prop:"formPath"},{default:n(()=>[t(u,{type:"textarea",modelValue:l(e).listenerPath,"onUpdate:modelValue":o[7]||(o[7]=a=>l(e).listenerPath=a),rows:"8"},null,8,["modelValue"]),t(R,{class:"item",effect:"dark",content:"输入监听器的路径，以@@分隔，顺序与监听器类型一致"},{default:n(()=>[H]),_:1})]),_:1})]),_:1},8,["model","rules","disabled"])]),_:1},8,["title","modelValue"])):_("",!0)])}}});export{Z as default};
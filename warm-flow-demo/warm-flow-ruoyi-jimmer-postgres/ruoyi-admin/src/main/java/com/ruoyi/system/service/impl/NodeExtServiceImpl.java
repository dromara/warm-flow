package com.ruoyi.system.service.impl;

import org.dromara.warm.flow.ui.service.NodeExtService;
import org.dromara.warm.flow.ui.vo.NodeExt;
import org.dromara.warm.flow.ui.vo.NodeExt.ChildNode;
import org.dromara.warm.flow.ui.vo.NodeExt.DictItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程设计器-节点扩展属性
 *
 * @author warm
 */
@Component
public class NodeExtServiceImpl implements NodeExtService {

    @Override
    public List<NodeExt> getNodeExt() {
        List<NodeExt> nodeExts = new ArrayList<>();

        // 第一个 NodeExt 对象
        NodeExt nodeExt1 = new NodeExt();
        nodeExt1.setCode("base");
        nodeExt1.setDesc("基础设置扩展属性");
        nodeExt1.setType(1);

        List<ChildNode> childs1 = new ArrayList<>();

        // 第一个 ChildNode 对象
        ChildNode childNode1 = new ChildNode();
        childNode1.setCode("base1");
        childNode1.setLabel("输入框");
        childNode1.setDesc("基础设置扩展属性1");
        childNode1.setType(1);
        childNode1.setMust(false);
        childs1.add(childNode1);

        // 第二个 ChildNode 对象
        ChildNode childNode2 = new ChildNode();
        childNode2.setCode("base2");
        childNode2.setLabel("文本域");
        childNode2.setDesc("基础设置扩展属性2");
        childNode2.setType(2);
        childNode2.setMust(false);
        childs1.add(childNode2);

        // 第三个 ChildNode 对象
        ChildNode childNode3 = new ChildNode();
        childNode3.setCode("base3");
        childNode3.setLabel("下一步角色");
        childNode3.setDesc("基础设置扩展属性3");
        childNode3.setType(3);
        childNode3.setMust(false);

        List<DictItem> dictItems3 = new ArrayList<>();
        dictItems3.add(new DictItem("普通角色", "common", true));
        dictItems3.add(new DictItem("领导", "leader"));
        dictItems3.add(new DictItem("员工", "yuangong"));
        childNode3.setDict(dictItems3);
        childs1.add(childNode3);

        // 第四个 ChildNode 对象
        ChildNode childNode4 = new ChildNode();
        childNode4.setCode("base4");
        childNode4.setLabel("单选框");
        childNode4.setDesc("基础设置扩展属性4");
        childNode4.setType(4);
        childNode4.setMust(false);

        List<DictItem> dictItems4 = new ArrayList<>();
        dictItems4.add(new DictItem("是否弹窗选人", "1", true));
        dictItems4.add(new DictItem("是否能委托", "2", true));
        dictItems4.add(new DictItem("是否能转办", "3"));
        dictItems4.add(new DictItem("是否能抄送", "4"));
        dictItems4.add(new DictItem("是否显示退回", "5"));
        dictItems4.add(new DictItem("是否能加签", "6"));
        dictItems4.add(new DictItem("是否能减签", "7"));
        childNode4.setDict(dictItems4);
        childs1.add(childNode4);

        nodeExt1.setChilds(childs1);
        nodeExts.add(nodeExt1);

        // 第二个 NodeExt 对象
        NodeExt nodeExt2 = new NodeExt();
        nodeExt2.setCode("new_tabs");
        nodeExt2.setName("按钮权限");
        nodeExt2.setDesc("按钮权限设置");
        nodeExt2.setType(2);

        List<ChildNode> childs2 = new ArrayList<>();

        // 第一个 ChildNode 对象
        ChildNode childNode5 = new ChildNode();
        childNode5.setCode("new_tabs1");
        childNode5.setLabel("复选框");
        childNode5.setDesc("按钮权限1");
        childNode5.setType(4);
        childNode5.setMust(false);
        childNode5.setMultiple(true);

        List<DictItem> dictItems5 = new ArrayList<>();
        dictItems5.add(new DictItem("是否弹窗选人", "1"));
        dictItems5.add(new DictItem("是否能委托", "2"));
        dictItems5.add(new DictItem("是否能转办", "3", true));
        dictItems5.add(new DictItem("是否能抄送", "4", true));
        dictItems5.add(new DictItem("是否显示退回", "5"));
        dictItems5.add(new DictItem("是否能加签", "6"));
        dictItems5.add(new DictItem("是否能减签", "7"));
        childNode5.setDict(dictItems5);
        childs2.add(childNode5);

        ChildNode childNode6 = new ChildNode();
        childNode6.setCode("new_tabs2");
        childNode6.setLabel("下拉选-多选");
        childNode6.setDesc("基础设置扩展属性3");
        childNode6.setType(3);
        childNode6.setMust(false);
        childNode6.setMultiple(true);

        List<DictItem> dictItems6 = new ArrayList<>();
        dictItems6.add(new DictItem("选项A", "1", true));
        dictItems6.add(new DictItem("选项B", "2"));
        dictItems6.add(new DictItem("选项C", "3"));
        dictItems6.add(new DictItem("选项D", "4"));
        dictItems6.add(new DictItem("选项E", "5"));
        childNode6.setDict(dictItems6);
        childs2.add(childNode6);

        nodeExt2.setChilds(childs2);
        nodeExts.add(nodeExt2);

        // 第三个 NodeExt 对象
        NodeExt nodeExt3 = new NodeExt();
        nodeExt3.setCode("users");
        nodeExt3.setName("服务");
        nodeExt3.setDesc("服务");
        nodeExt3.setType(2);

        List<ChildNode> childs3 = new ArrayList<>();

        ChildNode childNode7 = new ChildNode();
        childNode7.setCode("users1");
        childNode7.setLabel("抄送人");
        childNode7.setDesc("抄送人");
        childNode7.setType(5);
        childNode7.setMust(false);
        childs3.add(childNode7);

        ChildNode childNode8 = new ChildNode();
        childNode8.setCode("users2");
        childNode8.setLabel("下个节点办理人");
        childNode8.setDesc("下个节点办理人");
        childNode8.setType(5);
        childNode8.setMust(false);
        childs3.add(childNode8);

        ChildNode childNode9 = new ChildNode();
        childNode9.setCode("autoApproval");
        childNode9.setLabel("超时自动审批");
        childNode9.setDesc("请输入超时时间，单位为小时");
        childNode9.setType(6);
        childNode9.setMust(false);
        childNode9.setPrecision(2);
        childNode9.setStep("0.01");
        childNode9.setMin("0");
        childs3.add(childNode9);

        ChildNode childNode12 = new ChildNode();
        childNode12.setCode("autoApproval_skipType");
        childNode12.setLabel("超时自动审批后动作");
        childNode12.setDesc("通过还是退回，退回需要画退回线或者设置驳回到指定节点");
        childNode12.setType(4);
        childNode12.setMust(false);

        List<DictItem> dictItems7 = new ArrayList<>();
        dictItems7.add(new DictItem("审批通过", "PASS"));
        dictItems7.add(new DictItem("审批退回", "REJECT"));
        childNode12.setDict(dictItems7);
        childs3.add(childNode12);

        ChildNode childNode10 = new ChildNode();
        childNode10.setCode("time1");
        childNode10.setLabel("年月日");
        childNode10.setDesc("请选择年月日");
        childNode10.setType(7);
        childNode10.setMust(false);
        childNode10.setDateType("date");
        childNode10.setDateFormat("YYYY-MM-DD");
        childs3.add(childNode10);

        ChildNode childNode11 = new ChildNode();
        childNode11.setCode("time2");
        childNode11.setLabel("年月日时分秒");
        childNode11.setDesc("请选择年月日时分秒");
        childNode11.setType(7);
        childNode11.setMust(false);
        childNode11.setDateType("datetime");
        childNode11.setDateFormat("YYYY-MM-DD HH:mm:ss");
        childs3.add(childNode11);

        ChildNode childNode13 = new ChildNode();
        childNode13.setCode("time3");
        childNode13.setLabel("年月日时分秒-时间范围");
        childNode13.setDesc("请选择年月日时分秒-时间范围");
        childNode13.setType(7);
        childNode13.setMust(false);
        childNode13.setDateType("datetimerange");
        childNode13.setDateFormat("YYYY-MM-DD HH:mm:ss");
        childs3.add(childNode13);

        ChildNode childNode14 = new ChildNode();
        childNode14.setCode("time4");
        childNode14.setLabel("时分秒");
        childNode14.setDesc("请选择时分秒");
        childNode14.setType(7);
        childNode14.setMust(false);
        childNode14.setDateType("timepicker");
        childNode14.setDateFormat("HH:mm:ss");
        childs3.add(childNode14);

        ChildNode childNode15 = new ChildNode();
        childNode15.setCode("time5");
        childNode15.setLabel("时分秒-时间范围");
        childNode15.setDesc("请选择时分秒-时间范围");
        childNode15.setType(7);
        childNode15.setMust(false);
        childNode15.setDateType("timepickerrange");
        childNode15.setDateFormat("HH:mm:ss");
        childs3.add(childNode15);

        ChildNode childNode16 = new ChildNode();
        childNode16.setCode("httpUrl");
        childNode16.setLabel("远程访问");
        childNode16.setDesc("请输入远程访问地址");
        childNode16.setType(1);
        childNode16.setMust(false);
        childs3.add(childNode16);

        ChildNode childNode17 = new ChildNode();
        childNode17.setCode("script");
        childNode17.setLabel("脚本");
        childNode17.setDesc("请输入脚本信息");
        childNode17.setType(2);
        childNode17.setMust(false);
        childs3.add(childNode17);

        nodeExt3.setChilds(childs3);
        nodeExts.add(nodeExt3);

        return nodeExts;
    }
}

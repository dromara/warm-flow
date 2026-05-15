package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业采购流程表对象 procurement_steps
 *
 * @author ruoyi
 * @date 2025-05-08
 */
public class ProcurementSteps extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 采购名称 */
    @Excel(name = "采购名称")
    private String purchaseName;

    /** 采购计划 */
    @Excel(name = "采购计划")
    private String purchasePlan;

    /** 是否加急（0加急 1不加急） */
    private String urgent;

    /** 加急采购计划 */
    @Excel(name = "加急采购计划")
    private String urgentPurchasePlan;

    /** 按需采购 */
    @Excel(name = "按需采购")
    private String onDemandProcurement;

    /** 提供物品 */
    @Excel(name = "提供物品")
    private String provideItems;

    /** 产品验收是否合格（0合格 1不合格） */
    @Excel(name = "产品验收是否合格（0合格 1不合格）")
    private String productInspection;

    /** 出具发票 */
    @Excel(name = "出具发票")
    private String issueInvoice;

    /** 登记记录 */
    @Excel(name = "登记记录")
    private String recordEntry;

    /** 退货 */
    @Excel(name = "退货")
    private String returnItems;

    /** 入库 */
    @Excel(name = "入库")
    private String warehousing;

    /** 接收物品 */
    @Excel(name = "接收物品")
    private String receiveItems;

    /** 做好记录 */
    @Excel(name = "做好记录")
    private String maintainRecords;

    /** 流程实例的id */
    private Long instanceId;

    /** 节点编码 */
    private String nodeCode;

    /** 流程节点名称 */
    @Excel(name = "流程节点名称")
    private String nodeName;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    private Integer nodeType;

    /** 流程状态（0待提交 1审批中 2 审批通过 3自动通过 4终止 5作废 6撤销 7取回  8已完成 9已退回 10失效） */
    @Excel(name = "流程状态", readConverterExp = "0=待提交,1=审批中,2=,审=批通过,3=自动通过,4=终止,5=作废,6=撤销,7=取回,8=已完成,9=已退回,1=0失效")
    private String flowStatus;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setPurchaseName(String purchaseName)
    {
        this.purchaseName = purchaseName;
    }

    public String getPurchaseName()
    {
        return purchaseName;
    }
    public void setPurchasePlan(String purchasePlan)
    {
        this.purchasePlan = purchasePlan;
    }

    public String getPurchasePlan()
    {
        return purchasePlan;
    }
    public void setUrgentPurchasePlan(String urgentPurchasePlan)
    {
        this.urgentPurchasePlan = urgentPurchasePlan;
    }

    public String getUrgent() {
        return urgent;
    }

    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }

    public String getUrgentPurchasePlan()
    {
        return urgentPurchasePlan;
    }
    public void setOnDemandProcurement(String onDemandProcurement)
    {
        this.onDemandProcurement = onDemandProcurement;
    }

    public String getOnDemandProcurement()
    {
        return onDemandProcurement;
    }
    public void setProvideItems(String provideItems)
    {
        this.provideItems = provideItems;
    }

    public String getProvideItems()
    {
        return provideItems;
    }
    public void setProductInspection(String productInspection)
    {
        this.productInspection = productInspection;
    }

    public String getProductInspection()
    {
        return productInspection;
    }
    public void setIssueInvoice(String issueInvoice)
    {
        this.issueInvoice = issueInvoice;
    }

    public String getIssueInvoice()
    {
        return issueInvoice;
    }
    public void setRecordEntry(String recordEntry)
    {
        this.recordEntry = recordEntry;
    }

    public String getRecordEntry()
    {
        return recordEntry;
    }
    public void setReturnItems(String returnItems)
    {
        this.returnItems = returnItems;
    }

    public String getReturnItems()
    {
        return returnItems;
    }
    public void setWarehousing(String warehousing)
    {
        this.warehousing = warehousing;
    }

    public String getWarehousing()
    {
        return warehousing;
    }
    public void setReceiveItems(String receiveItems)
    {
        this.receiveItems = receiveItems;
    }

    public String getReceiveItems()
    {
        return receiveItems;
    }
    public void setMaintainRecords(String maintainRecords)
    {
        this.maintainRecords = maintainRecords;
    }

    public String getMaintainRecords()
    {
        return maintainRecords;
    }
    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }
    public void setNodeCode(String nodeCode)
    {
        this.nodeCode = nodeCode;
    }

    public String getNodeCode()
    {
        return nodeCode;
    }
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public String getNodeName()
    {
        return nodeName;
    }
    public void setNodeType(Integer nodeType)
    {
        this.nodeType = nodeType;
    }

    public Integer getNodeType()
    {
        return nodeType;
    }
    public void setFlowStatus(String flowStatus)
    {
        this.flowStatus = flowStatus;
    }

    public String getFlowStatus()
    {
        return flowStatus;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("purchaseName", getPurchaseName())
            .append("purchasePlan", getPurchasePlan())
            .append("urgent", getUrgent())
            .append("urgentPurchasePlan", getUrgentPurchasePlan())
            .append("onDemandProcurement", getOnDemandProcurement())
            .append("provideItems", getProvideItems())
            .append("productInspection", getProductInspection())
            .append("issueInvoice", getIssueInvoice())
            .append("recordEntry", getRecordEntry())
            .append("returnItems", getReturnItems())
            .append("warehousing", getWarehousing())
            .append("receiveItems", getReceiveItems())
            .append("maintainRecords", getMaintainRecords())
            .append("instanceId", getInstanceId())
            .append("nodeCode", getNodeCode())
            .append("nodeName", getNodeName())
            .append("nodeType", getNodeType())
            .append("flowStatus", getFlowStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}

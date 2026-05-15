package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 合同流程对象 contract_process
 *
 * @author ruoyi
 * @date 2025-04-30
 */
public class ContractProcess extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 合同流程ID（主键） */
    private Long id;

    /** 合同名称 */
    @Excel(name = "合同名称")
    private String contractName;

    /** 合同类型 */
    @Excel(name = "合同类型")
    private String contractType;

    /** 结构和性质 */
    @Excel(name = "结构和性质")
    private String structureAndNature;

    /** 拟定条件 */
    @Excel(name = "拟定条件")
    private String proposedConditions;

    /** 谈判内容 */
    @Excel(name = "谈判内容")
    private String negotiationContent;

    /** 谈判附件 */
    @Excel(name = "谈判附件")
    private String fileId;

    /** 谈判结果 */
    @Excel(name = "谈判结果")
    private String negotiationResult;

    /** 协商方案 */
    @Excel(name = "协商方案")
    private String adjustmentScheme;

    /** 签订日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签订日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date signDate;

    /** 签订人 */
    @Excel(name = "签订人")
    private String signer;

    /** 备案日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "备案日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date filingDate;

    /** 备案部门 */
    @Excel(name = "备案部门")
    private String filingDepartment;

    /** 流程实例的id */
    private Long instanceId;

    /** 节点编码 */
    private String nodeCode;

    /** 流程节点名称 */
    @Excel(name = "流程节点名称")
    private String nodeName;

    /** 节点类型 */
    private Integer nodeType;

    /** 流程状态 */
    @Excel(name = "流程状态")
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
    public void setContractName(String contractName)
    {
        this.contractName = contractName;
    }

    public String getContractName()
    {
        return contractName;
    }
    public void setContractType(String contractType)
    {
        this.contractType = contractType;
    }

    public String getContractType()
    {
        return contractType;
    }
    public void setStructureAndNature(String structureAndNature)
    {
        this.structureAndNature = structureAndNature;
    }

    public String getStructureAndNature()
    {
        return structureAndNature;
    }
    public void setProposedConditions(String proposedConditions)
    {
        this.proposedConditions = proposedConditions;
    }

    public String getProposedConditions()
    {
        return proposedConditions;
    }
    public void setNegotiationContent(String negotiationContent)
    {
        this.negotiationContent = negotiationContent;
    }

    public String getNegotiationContent()
    {
        return negotiationContent;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setNegotiationResult(String negotiationResult)
    {
        this.negotiationResult = negotiationResult;
    }

    public String getNegotiationResult()
    {
        return negotiationResult;
    }
    public void setAdjustmentScheme(String adjustmentScheme)
    {
        this.adjustmentScheme = adjustmentScheme;
    }

    public String getAdjustmentScheme()
    {
        return adjustmentScheme;
    }
    public void setSignDate(Date signDate)
    {
        this.signDate = signDate;
    }

    public Date getSignDate()
    {
        return signDate;
    }
    public void setSigner(String signer)
    {
        this.signer = signer;
    }

    public String getSigner()
    {
        return signer;
    }
    public void setFilingDate(Date filingDate)
    {
        this.filingDate = filingDate;
    }

    public Date getFilingDate()
    {
        return filingDate;
    }
    public void setFilingDepartment(String filingDepartment)
    {
        this.filingDepartment = filingDepartment;
    }

    public String getFilingDepartment()
    {
        return filingDepartment;
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
            .append("contractName", getContractName())
            .append("contractType", getContractType())
            .append("structureAndNature", getStructureAndNature())
            .append("proposedConditions", getProposedConditions())
            .append("negotiationContent", getNegotiationContent())
            .append("negotiationResult", getNegotiationResult())
            .append("adjustmentScheme", getAdjustmentScheme())
            .append("signDate", getSignDate())
            .append("signer", getSigner())
            .append("filingDate", getFilingDate())
            .append("filingDepartment", getFilingDepartment())
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

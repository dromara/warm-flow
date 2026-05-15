package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * OA 请假申请对象 test_leave
 *
 * @author ruoyi
 * @date 2024-03-07
 */
public class TestLeave extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 请假类型 */
    @Excel(name = "请假类型")
    private Long type;

    /** 请假原因 */
    private String reason;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 请假天数 */
    @Excel(name = "请假天数")
    private Long day;

    /** 流程实例的id */
    private Long instanceId;

    /** 节点编码 */
    private String nodeCode;

    /** 流程节点名称 */
    @Excel(name = "流程节点名称")
    private String nodeName;

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    private Integer nodeType;

    /** 流程状态（0待提交 1审批中 2 审批通过 3自动通过 4终止 5作废 6撤销 7取回  8已完成 9已退回 10失效） */
    @Excel(name = "流程状态", readConverterExp = "0=待提交,1=审批中,2=,审=批通过,8=已完成,9=已退回,1=0失效")
    private String flowStatus;

    /**抄送人*/
    private List<String> additionalHandler;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
    public void setType(Long type)
    {
        this.type = type;
    }

    public Long getType()
    {
        return type;
    }
    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
    public void setDay(Long day)
    {
        this.day = day;
    }

    public Long getDay()
    {
        return day;
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

    public Integer getNodeType() {
        return nodeType;
    }

    public TestLeave setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
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

    public List<String> getAdditionalHandler()
    {
        return additionalHandler;
    }
    public void setAdditionalHandler(List<String> additionalHandler)
    {
        this.additionalHandler=additionalHandler;
    }
    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("type", getType())
            .append("reason", getReason())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("day", getDay())
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

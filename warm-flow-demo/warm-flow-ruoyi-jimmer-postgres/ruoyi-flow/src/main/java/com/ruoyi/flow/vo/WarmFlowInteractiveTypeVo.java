package com.ruoyi.flow.vo;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

public class WarmFlowInteractiveTypeVo extends BaseEntity {
    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 增加办理人
     */
    private List<String> addHandlers;

    /**
     * 操作类型[2:转办,6:加签,3:委派,7:减签]
     */
    private Integer operatorType;

    /**
     * 部门ID
     */
    private Long deptId;

    private List<String> userIds;

    public Long getTaskId() {
        return taskId;
    }

    public WarmFlowInteractiveTypeVo setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public List<String> getAddHandlers() {
        return addHandlers;
    }

    public WarmFlowInteractiveTypeVo setAddHandlers(List<String> addHandlers) {
        this.addHandlers = addHandlers;
        return this;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public WarmFlowInteractiveTypeVo setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
        return this;
    }

    public Long getDeptId() {
        return deptId;
    }

    public WarmFlowInteractiveTypeVo setDeptId(Long deptId) {
        this.deptId = deptId;
        return this;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public WarmFlowInteractiveTypeVo setUserIds(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }
}

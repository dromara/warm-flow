package com.warm.flow.core.dto;

import java.util.List;

/**
 * 修改办理人参数
 *
 * @author warm
 */
public class ModifyHandler {

    /**
     * 修改的任务id
     */
    private Long taskId;

    /**
     * 当前办理人
     */
    private String curUser;

    /**
     * 用户权限标识
     */
    private List<String> permissionFlag;

    /**
     * 增加办理人：加签，转办，委托
     */
    private List<String> addHandlers;

    /**
     * 减少办理人：减签，委托
     */
    private List<String> reductionHandlers;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 转办忽略权限校验（true - 忽略，false - 不忽略）
     */
    private boolean ignore;

    /**
     * 历史任务动作类型(0审批 1转办 2会签 3票签 4委派 5加签 6减签)
     */
    private Integer actionType;

    public static ModifyHandler build() {
        return new ModifyHandler();
    }

    public Long getTaskId() {
        return taskId;
    }

    public ModifyHandler setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getCurUser() {
        return curUser;
    }

    public ModifyHandler setCurUser(String curUser) {
        this.curUser = curUser;
        return this;
    }

    public List<String> getPermissionFlag() {
        return permissionFlag;
    }

    public ModifyHandler setPermissionFlag(List<String> permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    public List<String> getAddHandlers() {
        return addHandlers;
    }

    public ModifyHandler setAddHandlers(List<String> addHandlers) {
        this.addHandlers = addHandlers;
        return this;
    }

    public List<String> getReductionHandlers() {
        return reductionHandlers;
    }

    public ModifyHandler setReductionHandlers(List<String> reductionHandlers) {
        this.reductionHandlers = reductionHandlers;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ModifyHandler setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public ModifyHandler setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public Integer getActionType() {
        return actionType;
    }

    public ModifyHandler setActionType(Integer actionType) {
        this.actionType = actionType;
        return this;
    }
}

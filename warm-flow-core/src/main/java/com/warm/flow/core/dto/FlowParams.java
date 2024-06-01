package com.warm.flow.core.dto;

import java.util.List;
import java.util.Map;

/**
 * @author warm
 * @description: 工作流内置参数
 * @date: 2023/3/31 17:18
 */
public class FlowParams {
    private static final long serialVersionUID = 1L;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 用户账号，唯一标识就行
     */
    private String createBy;

    /**
     * 节点编码（如果要指定跳转节点，传入）
     */
    private String nodeCode;

    /**
     * 权限标识 例如：role:admin,user:2
     */
    private List<String> permissionFlag;
    /**
     * 额外办理人：加减签，转办，委托，抄送人权限标识，例如：[role:admin,user:2]
     */
    private List<String> additionalHandler;
    /**
     * 跳转类型（PASS审批通过 REJECT退回）
     */
    private String skipType;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 流程变量
     */
    private Map<String, Object> variable;

    /**
     * 扩展字段
     */
    private String ext;

    /**
     * 租户id
     */
    private String tenantId;

    public static FlowParams build() {
        return new FlowParams();
    }

    public FlowParams flowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    public FlowParams createBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public FlowParams nodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    public FlowParams variable(Map<String, Object> variable) {
        this.variable = variable;
        return this;
    }

    public FlowParams skipType(String skipType) {
        this.skipType = skipType;
        return this;
    }

    public FlowParams tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public FlowParams message(String message) {
        this.message = message;
        return this;
    }

    public FlowParams ext(String ext) {
        this.ext = ext;
        return this;
    }

    public Map<String, Object> getVariable() {
        return variable;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public FlowParams permissionFlag(List<String> permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    public List<String> getAdditionalHandler() {
        return additionalHandler;
    }

    public FlowParams additionalHandler(List<String> permissionList) {
        this.additionalHandler = permissionList;
        return this;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public String getCreateBy() {
        return createBy;
    }

    public List<String> getPermissionFlag() {
        return permissionFlag;
    }

    public String getSkipType() {
        return skipType;
    }

    public String getMessage() {
        return message;
    }

    public String getExt() {
        return ext;
    }

    public String getTenantId() {
        return tenantId;
    }

}

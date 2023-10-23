package com.warm.flow.core.domain.dto;

import java.util.List;

/**
 * @author minliuhua
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
     * 用户昵称
     */
    private String nickName;

    /**
     * 节点编码（如果要指定跳转节点，传入）
     */
    private String nodeCode;

    /**
     * 权限标识 例如：role:admin,user:2
     */
    private List<String> permissionFlag;

    /**
     * 跳转类型（PASS审批通过 REJECT驳回）
     */
    private String skipType;


    /**
     * 跳转条件
     */
    private String skipCondition;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 扩展字段
     */
    private String ext;

    /**
     * 租户id
     */
    private Long tenantId;

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

    public FlowParams nickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public FlowParams setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    public FlowParams permissionFlag(List<String> permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    public FlowParams skipType(String skipType) {
        this.skipType = skipType;
        return this;
    }

    public FlowParams skipCondition(String skipCondition) {
        this.skipCondition = skipCondition;
        return this;
    }


    public String getSkipCondition() {
        return skipCondition;
    }

    public void setSkipCondition(String skipCondition) {
        this.skipCondition = skipCondition;
    }

    public FlowParams message(String message) {
        this.message = message;
        return this;
    }

    public FlowParams ext(String ext) {
        this.ext = ext;
        return this;
    }

    public FlowParams tenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public List<String> getPermissionFlag() {
        return permissionFlag;
    }

    public void setPermissionFlag(List<String> permissionFlag) {
        this.permissionFlag = permissionFlag;
    }


    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}

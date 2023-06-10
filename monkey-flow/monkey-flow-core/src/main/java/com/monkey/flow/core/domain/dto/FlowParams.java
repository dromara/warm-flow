package com.monkey.flow.core.domain.dto;

import java.util.List;

/**
 * @description:  工作流内置参数
 * @author minliuhua
 * @date: 2023/3/31 17:18
 */
public class FlowParams
{
    private static final long serialVersionUID = 1L;

    /** 流程编码 */
    private String flowCode;

    /** 用户账号，唯一标识就行 */
    private String createBy;

    /** 用户昵称 */
    private String nickName;

    /** 权限标识 例如：role:admin,user:2 */
    private List<String> permissionFlag;

    /** 扩展字段 */
    private String ext;

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

    public FlowParams permissionFlag(List<String> permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    public FlowParams ext(String ext) {
        this.ext = ext;
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

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}

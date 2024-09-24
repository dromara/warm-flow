/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.warm.flow.core.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author warm
 * @description: 工作流内置参数
 * @date: 2023/3/31 17:18
 */
public class FlowParams implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 办理人唯一标识
     */
    private String handler;

    /**
     * 节点编码（如果要指定跳转节点，传入）
     */
    private String nodeCode;

    /**
     * 用户权限标识
     */
    private List<String> permissionFlag;

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
     * 流程实例状态
     */
    private String flowStatus;

    /**
     * 历史任务表状态
     */
    private String hisStatus;

    /**
     * 流程激活状态（0挂起 1激活）
     */
    private Integer activityStatus;

    /**
     * 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
     */
    private Integer cooperateType;

    /**
     * 扩展字段，预留给业务系统使用
     */
    private String ext;

    /**
     * 扩展字段，预留给业务系统使用
     */
    private String hisTaskExt;

    public static FlowParams build() {
        return new FlowParams();
    }

    public FlowParams flowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    public FlowParams handler(String handler) {
        this.handler = handler;
        return this;
    }

    public FlowParams nodeCode(String nodeCode) {
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

    public FlowParams message(String message) {
        this.message = message;
        return this;
    }

    public FlowParams variable(Map<String, Object> variable) {
        this.variable = variable;
        return this;
    }

    public FlowParams flowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    public FlowParams hisStatus(String hisStatus) {
        this.hisStatus = hisStatus;
        return this;
    }

    public FlowParams activityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
        return this;
    }

    public FlowParams cooperateType(Integer cooperateType) {
        this.cooperateType = cooperateType;
        return this;
    }

    public FlowParams ext(String ext) {
        this.ext = ext;
        return this;
    }

    public FlowParams hisTaskExt(String hisTaskExt) {
        this.hisTaskExt = hisTaskExt;
        return this;
    }

    public Map<String, Object> getVariable() {
        return variable;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public String getHandler() {
        return handler;
    }

    public List<String> getPermissionFlag() {
        return permissionFlag;
    }

    public String getSkipType() {
        return skipType;
    }

    public FlowParams setSkipType(String skipType) {
        this.skipType = skipType;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public String getExt() {
        return ext;
    }

    public String getHisTaskExt() {
        return hisTaskExt;
    }

    public String getFlowStatus() {
        return flowStatus;
    }

    public FlowParams setFlowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    public String getHisStatus() {
        return hisStatus;
    }

    public FlowParams setHisStatus(String hisStatus) {
        this.hisStatus = hisStatus;
        return this;
    }

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public FlowParams setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
        return this;
    }

    public Integer getCooperateType() {
        return cooperateType;
    }

    public FlowParams setCooperateType(Integer cooperateType) {
        this.cooperateType = cooperateType;
        return this;
    }

    public FlowParams setFlowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    public FlowParams setHandler(String handler) {
        this.handler = handler;
        return this;
    }

    public FlowParams setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    public FlowParams setPermissionFlag(List<String> permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    public FlowParams setMessage(String message) {
        this.message = message;
        return this;
    }

    public FlowParams setVariable(Map<String, Object> variable) {
        this.variable = variable;
        return this;
    }

    public FlowParams setExt(String ext) {
        this.ext = ext;
        return this;
    }

    public FlowParams setHisTaskExt(String hisTaskExt) {
        this.hisTaskExt = hisTaskExt;
        return this;
    }

}

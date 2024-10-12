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
package com.warm.flow.ui.dto;

/**
 * 流程设计器-办理人权限设置列表查询参数
 * 办理人权限列表选择框，可能存在多个，比如：部门、角色、用户的情况
 *
 * @author warm
 */
public class HandlerQuery {

    /** 权限主键，用户/角色/部门等 */
    private String id;

    /** 权限编码，如：zhang、roleAdmin、deptAdmin等 */
    private String handlerCode;

    /** 权限名称，如：管理员、角色管理员、部门管理员等 */
    private String handlerName;

    /** 办理权限类型，比如user，role，dept */
    private String handlerType;

    /** 权限分组，如：角色、部门等主键 */
    private String groupId;

    /** 创建时间-开始时间 */
    private String StartTime;

    /** 创建时间-结束时间 */
    private String EndTime;

    /** 当前页码 */
    private Integer pageNum;

    /** 每页显示条数 */
    private Integer pageSize;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHandlerCode() {
        return handlerCode;
    }

    public void setHandlerCode(String handlerCode) {
        this.handlerCode = handlerCode;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}

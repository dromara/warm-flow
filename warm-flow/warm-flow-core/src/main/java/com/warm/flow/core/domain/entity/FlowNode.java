package com.warm.flow.core.domain.entity;

import com.warm.mybatis.core.entity.FlowEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程结点对象 flow_node
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowNode implements FlowEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /**
     * 结点类型,0开始结点,1中间结点,2结束结点
     */
    private Integer nodeType;
    /**
     * 流程id
     */
    private Long definitionId;
    /**
     * 流程结点名称
     */
    private String nodeName;
    /**
     * 流程结点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制
     */
    private String nodeCode;

    /**
     * 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)
     */
    private String permissionFlag;

    /**
     * 版本
     */
    private String version;

    /**
     * 跳转规则描述
     */
    private String skipDescribe;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /**
     * 跳转条件
     */
    List<FlowSkip> skipList = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public Long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getPermissionFlag() {
        return permissionFlag;
    }

    public void setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSkipDescribe() {
        return skipDescribe;
    }

    public void setSkipDescribe(String skipDescribe) {
        this.skipDescribe = skipDescribe;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<FlowSkip> getSkipList() {
        return skipList;
    }

    public void setSkipList(List<FlowSkip> skipList) {
        this.skipList = skipList;
    }
}

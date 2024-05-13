package com.warm.flow.core.listener;

import java.util.List;

/**
 * 权限监听器，动态设置后续节点的权限
 *
 * @author warm
 */
public class NodePermission {

    /**
     * 节点编码
     */
    private String nodeCode;

    /**
     * 该节点的办理权限，多个权限用逗号分隔
     */
    private String permissionFlag;

    /**
     * 该节点的办理权限集合
     */
    private List<String> permissionFlagList;


    public NodePermission() {
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

    public List<String> getPermissionFlagList() {
        return permissionFlagList;
    }

    public void setPermissionFlagList(List<String> permissionFlagList) {
        this.permissionFlagList = permissionFlagList;
    }

    @Override
    public String toString() {
        return "NodePermission{" +
                "nodeCode='" + nodeCode + '\'' +
                ", permissionFlag='" + permissionFlag + '\'' +
                ", permissionFlagList=" + permissionFlagList +
                '}';
    }
}

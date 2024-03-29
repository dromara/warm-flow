package com.warm.flow.core.listener;

public class NodePermission {
    private String nodeCode;
    private String permissionFlag;


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

    @Override
    public String toString() {
        return "ValueHolder{" +
                "path='" + nodeCode + '\'' +
                ", parms='" + permissionFlag + '\'' +
                '}';
    }
}

package com.warm.flow.core.listener;

public class ValueHolder {
    private String path;
    private String parms;


    public ValueHolder() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParms() {
        return parms;
    }

    public void setParms(String parms) {
        this.parms = parms;
    }

    @Override
    public String toString() {
        return "ValueHolder{" +
                "path='" + path + '\'' +
                ", parms='" + parms + '\'' +
                '}';
    }
}

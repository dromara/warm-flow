package com.warm.flow.core.listener;

public class ValueHolder {
    private String path;
    private String params;


    public ValueHolder() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ValueHolder{" +
                "path='" + path + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}

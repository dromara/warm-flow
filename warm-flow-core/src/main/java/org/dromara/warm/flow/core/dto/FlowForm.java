package org.dromara.warm.flow.core.dto;


import org.dromara.warm.flow.core.entity.Form;

import java.io.Serializable;
import java.util.Map;

/**
 * @author vanlin
 * @className FlowForm
 * @description
 * @since 2024-9-24 11:11
 */
public class FlowForm implements Serializable {
    private Form form;

    private Object data;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

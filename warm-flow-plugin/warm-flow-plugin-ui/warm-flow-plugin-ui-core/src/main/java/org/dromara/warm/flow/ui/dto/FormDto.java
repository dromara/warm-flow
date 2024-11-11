package org.dromara.warm.flow.ui.dto;

/**
 * @author vanlin
 * @className FormDto
 * @description
 * @since 2024-11-11 15:38
 */
public class FormDto {
    private Long id;

    private String formName;

    private String formCode;

    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

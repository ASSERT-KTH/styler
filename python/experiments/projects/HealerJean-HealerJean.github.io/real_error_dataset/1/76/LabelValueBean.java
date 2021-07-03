package com.duodian.admore.data;

import java.io.Serializable;

/**
 * 下拉列表用
 */
public class LabelValueBean implements Serializable{
    private static final long serialVersionUID = -1211726511402154326L;

    private String label;
    private String value;

    private Boolean checked = false;

    public LabelValueBean() {
    }

    public LabelValueBean(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}

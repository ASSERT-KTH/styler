package com.hlj.data.res.check;

import java.io.Serializable;

/**
 * 类名称：
 * 类描述：
 * 创建人：liqingxu
 * 修改人：
 * 修改时间：2017/7/21 上午11:40
 * 修改备注：
 *
 * @version 1.0.0
 */
public class JobDetailData implements Serializable{


    private static final long serialVersionUID = -1L;

    private String schedName;

    private String jobName;

    private String jobGroup;

    private String description;

    private String jobClassName;

    public String getSchedName() {
        return schedName;
    }

    public void setSchedName(String schedName) {
        this.schedName = schedName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }
}

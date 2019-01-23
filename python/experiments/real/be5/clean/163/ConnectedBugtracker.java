package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.annot.PropertyName;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConnectedBugtracker
{

    private final Project project;
    private String name = "";
    private String projectId = "";
    private Map<String, String> bugtrackers = new LinkedHashMap<>();

    public ConnectedBugtracker()
    {
        this.project = null;
    }

    public ConnectedBugtracker(Project project, String name, String projectId, Map<String, String> bugtrackers)
    {
        this.project = project;
        this.name = name;
        this.projectId = projectId;
        this.bugtrackers = bugtrackers;
    }

    @PropertyName("Bugtracker name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (this.bugtrackers.containsKey(this.name))
            this.bugtrackers.remove(this.name);

        this.name = name;
        this.bugtrackers.put(this.name, this.projectId);
        fireChanged();
    }

    @PropertyName("Project identifier")
    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
        this.bugtrackers.put(this.name, this.projectId);
        fireChanged();
    }

    private void fireChanged()
    {
        if (project != null)
            project.fireCodeChanged();
    }

}

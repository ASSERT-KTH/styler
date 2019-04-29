package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.Objects;

public class Daemon extends BeModelElementSupport
{
    private String className;
    private String configSection;
    private String daemonType;
    private String description;
    private int slaveNo;

    public Daemon(String name, BeModelCollection<Daemon> origin)
    {
        super(name, origin);
    }

    @PropertyName("Daemon class")
    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
        fireChanged();
    }

    @PropertyName("Configuration section")
    public String getConfigSection()
    {
        return configSection;
    }

    public void setConfigSection(String configSection)
    {
        this.configSection = configSection;
        fireChanged();
    }

    @PropertyName("Daemon type")
    public String getDaemonType()
    {
        return daemonType;
    }

    public void setDaemonType(String daemonType)
    {
        this.daemonType = daemonType;
        fireChanged();
    }

    @PropertyName("Description")
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
        fireChanged();
    }

    public int getSlaveNo()
    {
        return slaveNo;
    }

    public void setSlaveNo(int slaveNo)
    {
        this.slaveNo = slaveNo;
        fireChanged();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Daemon other = (Daemon) obj;
        return Objects.equals(className, other.className) &&
                Objects.equals(configSection, other.configSection) &&
                Objects.equals(daemonType, other.daemonType) &&
                Objects.equals(description, other.description) &&
                slaveNo == other.slaveNo;
    }

    public static String[] getTypes()
    {
        return new String[]{"periodic", "cron", "service", "manual"};
    }

    public static String getDefaultType()
    {
        return "periodic";
    }

    @Override
    protected void fireChanged()
    {
        if (getOrigin().get(getName()) == this)
            getOrigin().fireCodeChanged();
    }
}

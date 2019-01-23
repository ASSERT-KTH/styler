package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;
import com.developmentontheedge.beans.annot.PropertyName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

@PropertyName("Icon")
public class Icon
{
    public static final String SOURCE_PROJECT = "Project";
    public static final String SOURCE_NONE = "No icon";
    public static final String SOURCE_BE = "BeanExplorer";

    // private static final String[] SOURCES = {SOURCE_NONE, SOURCE_BE, SOURCE_PROJECT};

    private final BeModelElement owner;
    private String source = SOURCE_NONE;
    private String name;
    private String originModule = "";
    private byte[] data;
    private boolean notificationEnabled = true;

    public Icon(BeModelElement owner)
    {
        this.owner = owner;
    }

    public BeModelElement getOwner()
    {
        return owner;
    }

    @PropertyName("Owner ID")
    public String getOwnerID()
    {
        if (owner instanceof Entity)
        {
            return "entities." + owner.getName();
        }
        if (owner instanceof Query)
        {
            return "queries." + ((Query) owner).getEntity().getName() + "." + owner.getName();
        }
        if (owner instanceof Operation)
        {
            return "operations." + ((Operation) owner).getEntity().getName() + "." + owner.getName();
        }
        return "unknown";
    }

    @PropertyName("MIME type")
    public String getMimeType()
    {
        if (name == null)
            return "image/gif";
        if (name.toLowerCase().endsWith(".png"))
            return "image/png";
        return "image/gif";
    }

    @PropertyName("Source")
    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        String newSource = SOURCE_BE.equals(source) || SOURCE_PROJECT.equals(source) ? source : SOURCE_NONE;
        if (!newSource.equals(this.source))
        {
            this.source = newSource;
            if (notificationEnabled)
            {
                if (owner instanceof BeModelCollection)
                {
                    ((BeModelCollection<?>) owner).customizeProperty("icon");
                }
                fireChanged();
            }
        }
    }

    private void fireChanged()
    {
        if (!notificationEnabled)
            return;
        if (owner instanceof Entity)
            ((Entity) owner).fireCodeChanged();
        else if (owner instanceof EntityItem)
            ((EntityItem) owner).fireChanged();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (!Objects.equals(name, this.name))
        {
            this.name = name;
            if (notificationEnabled)
            {
                if (owner instanceof BeModelCollection)
                {
                    ((BeModelCollection<?>) owner).customizeProperty("icon");
                }
                fireChanged();
            }
        }
    }

    public String getMetaPath()
    {
        switch (source)
        {
            case SOURCE_BE:
                return "be:" + name;
            case SOURCE_PROJECT:
                return "project:" + name;
            default:
                return "none";
        }
    }

    public void setMetaPath(String path)
    {
        if (path == null)
        {
            setSource(SOURCE_NONE);
            return;
        }
        path = path.trim();
        if (path.startsWith("be:"))
        {
            setSource(SOURCE_BE);
            setName(path.substring("be:".length()).trim());
        }
        else if (path.startsWith("project:"))
        {
            setSource(SOURCE_PROJECT);
            setName(path.substring("project:".length()).trim());
        }
        else
        {
            setSource(SOURCE_NONE);
        }
    }

    protected byte[] getBeIcon() throws IOException
    {
        // TODO be5 has not special path for icons
//        Path beanExplorerIconsPath;
//        try
//        {
//            beanExplorerIconsPath = ModuleUtils.getBeanExplorerIconsPath();
//        }
//        catch ( UnsupportedOperationException e )
//        {
//            throw new IOException( e );
//        }
//        return Files.readAllBytes( beanExplorerIconsPath.resolve( name ) );

        return null;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
        if (data == null)
        {
            this.source = SOURCE_NONE;
            return;
        }
        try
        {
            byte[] data2 = getBeIcon();
            if (Arrays.equals(data, data2))
            {
                this.source = SOURCE_BE;
                return;
            }
        }
        catch (IOException e)
        {
        }
        this.source = SOURCE_PROJECT;
    }

    public void copyFrom(Icon icon)
    {
        setNotificationEnabled(false);
        setSource(icon.getSource());
        setName(icon.getName());
        setData(icon.getData());
        setOriginModuleName(icon.getOriginModuleName());
        setNotificationEnabled(true);
    }

    private ProjectFileSystem getProjectFileSystem()
    {
        Project project = getOwner().getProject();
        return new ProjectFileSystem(project);
    }

    public void save() throws IOException
    {
        if (SOURCE_PROJECT.equals(this.source))
        {
            Path iconsFileName = getProjectFileSystem().getIconsFile(name);
            if (data != null)
            {
                Files.createDirectories(iconsFileName.getParent());
                Files.write(iconsFileName, data);
            }
        }
    }

    public void load() throws ReadException
    {
        Path iconsFile = null;
        try
        {
            switch (source)
            {
                case SOURCE_PROJECT:
                    iconsFile = getProjectFileSystem().getIconsFile(name);
                    data = Files.readAllBytes(iconsFile);
                    break;
                case SOURCE_BE:
                    data = getBeIcon();
                    break;
                default:
                    data = null;
            }
        }
        catch (IOException e)
        {
            throw new ReadException(e, getOwner(), iconsFile, "Unable to load icon");
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Icon other = (Icon) obj;
        return Objects.equals(name, other.name) && Arrays.equals(data, other.data);
    }

    public String getOriginModuleName()
    {
        return originModule;
    }

    public void setOriginModuleName(String originModule)
    {
        this.originModule = originModule == null ? "" : originModule;
    }

    public boolean isNotificationEnabled()
    {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled)
    {
        this.notificationEnabled = notificationEnabled;
    }

}

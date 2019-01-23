package com.developmentontheedge.be5.metadata.model;

public enum BeConnectionProfileType
{

    LOCAL("Local"),
    REMOTE("Remote");

    private final String name;

    private BeConnectionProfileType(String readableName)
    {
        this.name = readableName;
    }

    /**
     * @return a human readable name: "Local" or "Remote"
     */
    public String getName()
    {
        return name;
    }

}

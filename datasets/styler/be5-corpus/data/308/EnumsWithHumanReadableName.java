package com.developmentontheedge.be5.metadata.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumsWithHumanReadableName
{

    public static <T extends EnumWithHumanReadableName> List<String> names(final T[] values)
    {
        return Arrays.stream(values).map(EnumWithHumanReadableName::getHumanReadableName).collect(Collectors.toList());
    }

    public static <T extends EnumWithHumanReadableName> String[] namesArray(final T[] values)
    {
        return Arrays.stream(values).map(EnumWithHumanReadableName::getHumanReadableName).toArray(String[]::new);
    }

    public static <T extends EnumWithHumanReadableName> T byName(final Class<T> klass, final String name)
    {
        return Arrays.stream(klass.getEnumConstants()).filter(t -> t.getHumanReadableName().equals(name)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

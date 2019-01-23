package com.developmentontheedge.be5.metadata.util;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JarUtils
{

    public static Path resolveJarForClass(Class<?> clazz)
    {
        URL url = clazz.getResource(clazz.getSimpleName() + ".class");
        if (url != null && url.getProtocol().equals("jar"))
        {
            String urlPath = url.getPath();
            if (urlPath.startsWith("file:/"))
            {
                int endPos = urlPath.lastIndexOf('!');
                if (endPos > 0)
                {
                    return Paths.get(urlPath.substring("file:/".length(), endPos));
                }
            }
        }
        return null;
    }

}

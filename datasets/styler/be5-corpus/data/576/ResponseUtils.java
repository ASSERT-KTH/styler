package com.developmentontheedge.be5.server.util;

import com.developmentontheedge.be5.web.Response;
import com.google.common.io.ByteStreams;
import com.google.common.net.UrlEscapers;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;


public class ResponseUtils
{
    public static void sendFile(Response res, boolean download, String filename, String contentType, String charset, InputStream in)
    {
        HttpServletResponse response = res.getRawResponse();

        response.setContentType(contentType + "; charset=" + charset);
        //response.setCharacterEncoding(encoding);

        if (download)
        {
            response.setHeader("Content-disposition", "attachment; filename=" + UrlEscapers.urlFormParameterEscaper().escape(filename));
        }
        else
        {
            response.setHeader("Content-disposition", "filename=" + UrlEscapers.urlFormParameterEscaper().escape(filename));
        }

        try
        {
            ByteStreams.copy(in, response.getOutputStream());
            in.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}

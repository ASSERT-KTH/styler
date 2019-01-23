package com.developmentontheedge.be5.web;

import com.developmentontheedge.be5.web.impl.ResponseImpl;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ResponseImplTest
{
    private Response response;
    private HttpServletResponse rawResponse;
    private PrintWriter writer;

    @Before
    public void init() throws Exception
    {
        writer = mock(PrintWriter.class);

        rawResponse = mock(HttpServletResponse.class);
        when(rawResponse.getWriter()).thenReturn(writer);

        response = new ResponseImpl(rawResponse);
    }

    @Test
    public void sendAsJson()
    {
        Action call = new Action("call", "test/path");
        response.sendAsJson(call);

        verify(rawResponse).setContentType("application/json;charset=UTF-8");
        verify(writer).append(doubleQuotes("{'arg':'test/path','name':'call'}"));
    }

    @Test
    public void sendError()
    {
        Action call = new Action("call", "test/path");
        response.sendAsJson(call, HttpServletResponse.SC_FORBIDDEN);

        verify(rawResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(writer).append(doubleQuotes("{'arg':'test/path','name':'call'}"));
    }

    @Test
    public void getRawResponse()
    {
        assertEquals(rawResponse, response.getRawResponse());
    }
//
//    @Test
//    public void testJsonObject()
//    {
//        JsonApiModel jsonApiModel = JsonApiModel.data(new ResourceData("testType", "test", Collections.singletonMap("self", "url")),
//                Collections.singletonMap("_ts_", 1503291145939L));
//        response.sendAsJson(jsonApiModel);
//
//        verify(writer).append(doubleQuotes("{" +
//                "'data':{'attributes':'test','links':{'self':'url'},'type':'testType'}," +
//                "'meta':{'_ts_':1503291145939}" +
//                "}"));
//    }

    public class Action
    {
        public final String name;
        public final String arg;

        public Action(String name, String arg)
        {
            this.name = name;
            this.arg = arg;
        }

        public String getName()
        {
            return name;
        }

        public String getArg()
        {
            return arg;
        }

    }

    protected static String doubleQuotes(Object s)
    {
        return s.toString().replace("'", "\"");
    }
}

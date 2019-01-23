package com.developmentontheedge.be5.server.helpers;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.base.util.HtmlUtils;
import com.developmentontheedge.be5.operation.services.GroovyOperationLoader;
import com.developmentontheedge.be5.server.model.jsonapi.ErrorModel;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;


public class ErrorModelHelper
{
    public final Logger log = Logger.getLogger(ErrorModelHelper.class.getName());

    private final UserInfoProvider userInfoProvider;
    private final GroovyOperationLoader groovyOperationLoader;
    private final UserAwareMeta userAwareMeta;

    @Inject
    public ErrorModelHelper(UserInfoProvider userInfoProvider, GroovyOperationLoader groovyOperationLoader,
                            UserAwareMeta userAwareMeta)
    {
        this.userInfoProvider = userInfoProvider;
        this.groovyOperationLoader = groovyOperationLoader;
        this.userAwareMeta = userAwareMeta;
    }

    private String exceptionAsString(Throwable e)
    {
        if (userInfoProvider.isSystemDeveloper())
        {
            StringWriter sw = new StringWriter();
            if (e instanceof Be5Exception && e.getCause() != null)
            {
                e.getCause().printStackTrace(new PrintWriter(sw));
            }
            else
            {
                e.printStackTrace(new PrintWriter(sw));
            }
            return sw.toString();
        }
        else
        {
            return null;
        }
    }

    public ErrorModel getErrorModel(Be5Exception e)
    {
        return getErrorModel(e, null);
    }

    public ErrorModel getErrorModel(Be5Exception e, Map<String, String> links)
    {
        if (userInfoProvider.isSystemDeveloper())
        {
            return new ErrorModel(
                    e.getHttpStatusCode(),
                    userAwareMeta.getLocalizedBe5ErrorMessage(e),
                    Be5Exception.getMessage(e) + getErrorCodeLine(e),
                    exceptionAsString(e),
                    links
            );
        }
        else
        {
            return new ErrorModel(e.getHttpStatusCode(), userAwareMeta.getLocalizedBe5ErrorMessage(e), links);
        }
    }

    private String getErrorCodeLine(Throwable e)
    {
        Set<String> printedGroovyClasses = new HashSet<>();
        Throwable err = e;

        Stack<Throwable> throwables = new Stack<>();
        throwables.add(err);
        while (err.getCause() != null)
        {
            throwables.add(err.getCause());
            err = err.getCause();
        }

        StringBuilder sb = new StringBuilder();
        while (!throwables.empty())
        {
            err = throwables.pop();

            StackTraceElement[] stackTrace = err.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++)
            {
                if (stackTrace[i].getFileName() != null && stackTrace[i].getFileName().endsWith(".groovy")
                        && !printedGroovyClasses.contains(stackTrace[i].getFileName()))
                {
                    printedGroovyClasses.add(stackTrace[i].getFileName());
                    sb.append(getErrorCodeLinesForClass(stackTrace[i]));
                    break;
                }
            }
        }

        return sb.toString();
    }

    private String getErrorCodeLinesForClass(StackTraceElement e)
    {
        int lineID = e.getLineNumber();
        StringBuilder sb = new StringBuilder("\n" + Be5Exception.getFullStackTraceLine(e));

        String className = e.getClassName().indexOf('$') == -1
                ? e.getClassName()
                : e.getClassName().substring(0, e.getClassName().indexOf('$'));

        String code = groovyOperationLoader
                .getByFullName(className + ".groovy")
                .getCode();
        String lines[] = HtmlUtils.escapeHTML(code).split("\\r?\\n");

        sb.append("\n\n<code>");
        for (int i = Math.max(0, lineID - 4); i < Math.min(lineID + 3, lines.length); i++)
        {
            String lineNumber = String.format("%4d", i + 1) + " | ";
            if (lineID == i + 1)
            {
                sb.append("<span style=\"color: #e00000;\">").append(lineNumber).append(lines[i]).append("</span>\n");
            }
            else
            {
                sb.append(lineNumber).append(lines[i]).append("\n");
            }
        }
        sb.append("</code>");

        return sb.toString();
    }
}

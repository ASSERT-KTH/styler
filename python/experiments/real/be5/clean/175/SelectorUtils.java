package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

import java.util.ArrayList;
import java.util.List;

public class SelectorUtils
{
    private SelectorUtils()
    {
        throw new AssertionError();
    }

    public static String escapeIdentifier(String input)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            boolean alpha = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            boolean digit = c >= '0' && c <= '9';
            boolean hyphen = c == '-';
            boolean underscore = c == '_';
            boolean high = c >= 0xA0;
            if (alpha || underscore || high
                    || (digit && i > 0 && (i > 1 || result.charAt(0) != '-') || (hyphen && (i != 1 || result.charAt(0) != '-'))))
            {
                result.append(c);
            }
            else if (digit)
            {
                result.append("\\3").append(c).append(' ');
            }
            else
            {
                result.append('\\').append(c);
            }
        }
        return result.toString();
    }

    public static String escapeString(String input)
    {
        StringBuilder result = new StringBuilder();
        result.append('"');
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            if (c == '"' || c == '\\')
                result.append('\\').append(c);
            else if (c < ' ')
                result.append('\\').append(Integer.toHexString(c)).append(' ');
            else
                result.append(c);
        }
        result.append('"');
        return result.toString();
    }

    public static String unescape(String input)
    {
        if (input.startsWith("'") && input.endsWith("'"))
            return unescapeInternal(input.substring(1, input.length() - 1), false);
        if (input.startsWith("\"") && input.endsWith("\""))
            return unescapeInternal(input.substring(1, input.length() - 1), false);
        return unescapeInternal(input, true);
    }

    private static String unescapeInternal(String input, boolean identifier)
    {
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++)
        {
            char c = input.charAt(i);
            if (c != '\\')
            {
                sb.append(c);
                continue;
            }
            i++;
            if (i == len)
            {
                sb.append(c);
                break;
            }
            char c1 = input.charAt(i);
            if (c1 == '\n' || c1 == '\f' || c1 == '\r')
            {
                if (identifier)
                    sb.append(c1);
                continue;
            }
            if ((c1 < '0' || c1 > '9') && (c1 < 'a' || c1 > 'f'))
            {
                sb.append(c1);
                continue;
            }
            int numDigits = 1;
            int hexcode = c1 >= 'a' ? (c1 - 'a' + 10) : c1 - '0';
            while (true)
            {
                i++;
                if (i == len)
                    break;
                c1 = input.charAt(i);
                if ((c1 < '0' || c1 > '9') && (c1 < 'a' || c1 > 'f'))
                    break;
                if (numDigits == 6)
                    break;
                numDigits++;
                hexcode = (hexcode * 16) + (c1 >= 'a' ? (c1 - 'a' + 10) : c1 - '0');
            }
            if (c1 != ' ' && c1 != '\t' && c1 != '\r' && c1 != '\n' && c1 != '\f')
                i--;
            sb.append((char) hexcode);
        }
        return sb.toString();
    }

    /**
     * Returns true if given string can be used as identifier without escaping
     *
     * @param input
     */
    public static boolean isIdentifier(String input)
    {
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            boolean alpha = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            boolean digit = c >= '0' && c <= '9';
            boolean hyphen = c == '-';
            boolean underscore = c == '_';
            boolean high = c >= 0xA0;
            if ((!alpha && !digit && !hyphen && !underscore && !high) || (digit && i == 0 || (i == 1 && input.charAt(0) == '-'))
                    || (hyphen && i == 1 && input.charAt(0) == '-'))
                return false;
        }
        return true;
    }

    public static String escapeIdentifierOrString(String input)
    {
        return isIdentifier(input) ? input : escapeString(input);
    }

    @SuppressWarnings("unchecked")
    private static void select(List<BeModelElement> result, BeModelCollection<? extends BeModelElement> collection, SelectorRule selector)
    {
        for (BeModelElement element : collection)
        {
            if (selector.matches(element))
                result.add(element);
            if (element instanceof BeModelCollection)
                select(result, (BeModelCollection<? extends BeModelElement>) element, selector);
        }
    }

    public static List<BeModelElement> select(Project project, SelectorRule selector)
    {
        List<BeModelElement> result = new ArrayList<>();
        select(result, project.getModules(), selector);
        select(result, project.getApplication(), selector);
        return result;
    }
}

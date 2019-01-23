package com.developmentontheedge.be5.metadata;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Temporary class for imported and unclassified constants from BeanExplorer EE.
 */
public class MetadataUtils
{
    public static final Logger log = Logger.getLogger(MetadataUtils.class.getName());

    /**
     * @param values collection of the values, that needs to be in "IN" clause
     * @return surrounded values
     */
    public static String toInClause(Collection values)
    {
        return toInClause(values, false);
    }

    /**
     * @param values    collection of the values, that needs to be in "IN" clause
     * @param isNumeric specifies, that values in collection has numeric data type
     * @return surrounded values
     */
    public static String toInClause(Collection values, boolean isNumeric)
    {
        return toInClause(values, isNumeric, null);
    }

    /**
     * Surround specified collection values ( "(", ",", ")" ) for putting them into IN clause.
     * <br/>Example: SELECT * FROM some_table WHERE some_column IN (values[0], values[1], ...)
     * <br/><br/><b>Attention!!!</b> Values (except numeric values) in the collection must be already formatted for SQL syntax.
     * If collection contains numeric data, you must set isNumeric parameter to true.
     *
     * @param isNumeric specifies, that values in collection has numeric data type
     * @param values    collection of the values, that needs to be in "IN" clause
     * @param prefix    string value to be added to every element in the list
     * @return surrounded values
     */
    public static String toInClause(Collection values, boolean isNumeric, String prefix)
    {
        StringBuilder clause = new StringBuilder("(");
        Set set = new HashSet();
        boolean first = true;
        prefix = (prefix == null) ? "" : prefix;
        for (Object v : values)
        {
            if (v == null)
            {
                continue;
            }

            if (isNumeric)
            {
                try
                {
                    if (v != null && !"null".equalsIgnoreCase(v.toString()))
                    {
                        Double.parseDouble(v.toString());
                    }
                }
                catch (NumberFormatException exc)
                {
                    log.log(Level.WARNING, "toInClause: Bad numeric value '" + v + "'");
                    continue;
                }
            }

            if (!set.add(v))
            {
                continue;
            }

            if (first)
            {
                first = false;
            }
            else
            {
                clause.append(',');
            }

            if (isNumeric)
            {
                clause.append(prefix).append(v);
            }
            else
            {
                String val = v.toString();
                if (val.startsWith("'") && val.endsWith("'"))
                {
                    clause.append(val);
                }
                else
                {
                    clause.append("'").append(prefix).append(val).append("'");
                }
            }
        }
        clause.append(")");
        return clause.toString();
    }

    public static String classPathToFileName(String fileName, String fileExtension)
    {
        return fileName.substring(0, fileName.length() - fileExtension.length())
                .replace(".", "/") + fileExtension;
    }

}

package com.developmentontheedge.be5.operation.services.validation;


public class ValidationRules
{
    public static Rule range(long min, long max)
    {
        return new Rule("range", new Range(min, max));
    }

    public static Rule range(double min, double max)
    {
        return new Rule("range", new Range(min, max));
    }

    public static Rule range(long min, long max, String customMessage)
    {
        return new Rule("range", new Range(min, max), customMessage);
    }

    public static Rule range(double min, double max, String customMessage)
    {
        return new Rule("range", new Range(min, max), customMessage);
    }

    public static Rule range(String min, String max, String customMessage)
    {
        return new Rule("range", new Range(min, max), customMessage);
    }

    public static Rule step(long value)
    {
        return new Rule("step", Long.toString(value));
    }

    public static Rule step(double value)
    {
        return new Rule("step", Double.toString(value));
    }

    public static Rule step(String value)
    {
        return new Rule("step", value);
    }

    public static Rule step(long value, String customMessage)
    {
        return new Rule("step", Long.toString(value), customMessage);
    }

    public static Rule step(double value, String customMessage)
    {
        return new Rule("step", Double.toString(value), customMessage);
    }

    public static Rule pattern(String value)
    {
        return new Rule("pattern", value);
    }

    public static Rule pattern(String value, String customMessage)
    {
        return new Rule("pattern", value, customMessage);
    }

    public static Rule unique(String tableName)
    {
        return new Rule("unique", new Unique(tableName));
    }

    public static Rule unique(String tableName, String customMessage)
    {
        return new Rule("unique", new Unique(tableName), customMessage);
    }

    public static class Rule
    {
        private String type;
        private Object attr;
        private String customMessage;

        Rule(String type, Object attr)
        {
            this.type = type;
            this.attr = attr;
        }

        Rule(String type, Object attr, String customMessage)
        {
            this.type = type;
            this.attr = attr;
            this.customMessage = customMessage;
        }

        public String getType()
        {
            return type;
        }

        public Object getAttr()
        {
            return attr;
        }

        public String getCustomMessage()
        {
            return customMessage;
        }

        @Override
        public String toString()
        {
            return "Rule{" +
                    "type='" + type + '\'' +
                    ", attr=" + attr +
                    ", customMessage='" + customMessage + '\'' +
                    '}';
        }
    }

    public static class Range
    {
        String min, max;

        Range(long min, long max)
        {
            this.min = Long.toString(min);
            this.max = Long.toString(max);
        }

        Range(double min, double max)
        {
            this.min = Double.toString(min);
            this.max = Double.toString(max);
        }

        Range(String min, String max)
        {
            this.min = min;
            this.max = max;
        }

        public String getMin()
        {
            return min;
        }

        public String getMax()
        {
            return max;
        }
    }

    public static class Unique
    {
        String tableName;
        String columnName;

        Unique(String tableName)
        {
            this.tableName = tableName;
        }

        Unique(String tableName, String columnName)
        {
            this.tableName = tableName;
            this.columnName = columnName;
        }

        public String getTableName()
        {
            return tableName;
        }

        public String getColumnName()
        {
            return columnName;
        }
    }
}

package com.developmentontheedge.be5.operation.services.validation;


public interface Validation
{
    enum Status
    {
        SUCCESS, WARNING, ERROR;

        @Override
        public String toString()
        {
            return this.name().toLowerCase();
        }
    }

//    // Name of the property attribute used to define validation rules in Java code.
//    String RULES_ATTR = BeanInfoConstants.VALIDATION_RULES;
//
//    // Names of the cache entries.
//    String RULES_CACHE_ENTRY = "validationRules";
//    String METHODS_CACHE_ENTRY = "validationMethods";
//
//    // Database schema constants.
//    String RULES_TABLE_NAME = "validationRules";
//    String METHODS_TABLE_NAME = "validationMethods";
//    String ENTITY_NAME = "entity_name";
//    String PROPERTY_NAME = "property_name";
//    String RULE = "rule";
//    String MESSAGE = "message";
//    String CODE = "code";
//    String DEFAULT_MESSAGE = "defaultMessage";
//
//    // for HttpSearchOperation
//    String PATTERN = "pattern";
//    String PATTERN2 = "pattern2";
//    String REQUIRED = "required";
//    String REMOTE = "remote";
//    String UNIQUE = "unique";
//    String QUERY = "query";
//    String INTERVAL = "interval";
//    String URL = "url2";
//    String STARTS_WITH_DIGITS = "startWithDigits";
//
//    String IP_MASK = "ipMask";
//
//    String OWNER_IDS_IGNORED = "___MyOwnerIDsIgnored";

    // Default messages.

//    class UniqueStruct
//    {
//        public String entity;
//        public String column;
//        public String message;
//
//        public Map<String,String> extraParams;
//    }
//
//    class QueryStruct
//    {
//        public String entity;
//        public String query;
//        public String message;
//
//        public Map<String,String> extraParams;
//    }
//
//    class IntervalStruct
//    {
//        public Object intervalFrom;
//        public Object intervalTo;
//        public String message;
//    }
}

package com.developmentontheedge.be5.operation.model;


public enum OperationStatus
{
    CREATE("create"),
    GENERATE("generate"),
    EXECUTE("execute"),
    SCHEDULED("scheduled"),
    CANCELLED("cancelled"),
    //IN_PROGRESS ("in progress"),
    INTERRUPTING("interrupting"),
    INTERRUPTED("interrupted"),
    FINISHED("finished"),
    REDIRECTED("redirect"),
    ERROR("error");

    /* PENDING
    PAUSED      ("paused"),
    ABORTED     ("aborted"),
    STOPPED     ("stopped");

    PENDING( "pending" ),
    LOCKED( "locked" ),
    DELAYED( "delayed" ),
    TEMPLATE( "template" )*/

    private final String value;

    OperationStatus(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

//    public static OperationStatus parse(String str)
//    {
//        for( OperationStatus s : values() )
//        {
//            if( s.value.equals(str))
//                return s;
//        }
//        return null;
//    }
}

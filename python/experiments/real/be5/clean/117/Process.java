package com.developmentontheedge.be5.modules.core.model.scheduling;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public abstract class Process implements Job
{
    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            doWork(context);
            //updateLastExecutionResult( ok );
        }
        catch (Exception e)
        {
            //updateLastExecutionResult( error );
            //journal( "Exception in doWork(): " + e.getMessage(), e );
            throw new JobExecutionException(e);
        }
    }

    public abstract void doWork(JobExecutionContext context) throws Exception;
}

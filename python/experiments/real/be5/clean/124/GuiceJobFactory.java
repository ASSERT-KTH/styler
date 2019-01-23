package com.developmentontheedge.be5.modules.core.services.scheduling.impl;

import com.google.inject.Injector;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import javax.inject.Inject;
import java.util.logging.Logger;


public class GuiceJobFactory implements JobFactory
{
    private static final Logger log = Logger.getLogger(GuiceJobFactory.class.getName());

    private final Injector guice;

    @Inject
    public GuiceJobFactory(final Injector guice)
    {
        this.guice = guice;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException
    {
        JobDetail jobDetail = bundle.getJobDetail();
        Class jobClass = jobDetail.getJobClass();

        try
        {
            log.fine("Producing instance of Job '" + jobDetail.getKey() + "', class=" + jobClass.getName());

            return guice.getInstance(bundle.getJobDetail().getJobClass());
        }
        catch (Exception e)
        {
            throw new SchedulerException("Problem instantiating class '" + jobDetail.getJobClass().getName() + "'", e);
        }
    }
}
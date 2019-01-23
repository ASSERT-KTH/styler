package com.developmentontheedge.be5.modules.core.services.scheduling.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.CoreUtils;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.util.Utils;
import com.developmentontheedge.be5.metadata.model.Daemon;
import com.developmentontheedge.be5.modules.core.model.scheduling.Process;
import com.developmentontheedge.be5.modules.core.services.scheduling.DaemonStarter;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class DaemonStarterImpl implements DaemonStarter
{
    private static final Logger log = Logger.getLogger(DaemonStarterImpl.class.getName());

    public static final String DAEMONS_GROUP = "Daemons";
    public static final String DAEMON_STATUS_WORKING = "working";
    public static final String DAEMON_STATUS_NOT_WORKING = "not working";
    public static final String DAEMON_STATUS_ERROR = "error";
    public static final String DAEMON_STATUS_VETOED = "vetoed";

    //constants for names of params passed to job
//    public static final String JOB_PARAM_DATA_SOURCE = "dataSource";
//    public static final String JOB_PARAM_CONNECT_STRING = "connectString";
//    public static final String JOB_PARAM_ID = "ID";
    public static final String JOB_PARAM_NAME = "name";
    public static final String JOB_PARAM_CONFIG_SECTION = "configSectionName";

    private static final long PERIOD_FOREVER = Long.MAX_VALUE;

    private final Meta meta;
    private final CoreUtils coreUtils;
    private final Scheduler scheduler;

    @Inject
    public DaemonStarterImpl(Meta meta, CoreUtils coreUtils, GuiceJobFactory guiceJobFactory)
    {
        this.meta = meta;
        this.coreUtils = coreUtils;

        try
        {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.setJobFactory(guiceJobFactory);
            scheduler.start();

            initQuartzDaemons();
        }
        catch (SchedulerException se)
        {
            log.log(Level.SEVERE, "Error in scheduler", se);
            throw Be5Exception.internal(se);
        }
    }

    @Override
    public JobDetail getJobDetail(String jobName)
    {
        try
        {
            JobKey jobKey = new JobKey(jobName, DAEMONS_GROUP);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
//            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
//            triggersOfJob.get(0)
            return jobDetail;
        }
        catch (SchedulerException e)
        {
            throw Be5Exception.internal(e);
        }
    }

    @Override
    public boolean isJobRunning(String jobName)
    {
        List<JobExecutionContext> currentJobs;
        try
        {
            currentJobs = scheduler.getCurrentlyExecutingJobs();
        }
        catch (SchedulerException e)
        {
            throw Be5Exception.internal(e);
        }

        for (JobExecutionContext jobCtx : currentJobs)
        {
            String thisJobName = jobCtx.getJobDetail().getKey().getName();
            String thisGroupName = jobCtx.getJobDetail().getKey().getGroup();
            if (jobName.equalsIgnoreCase(thisJobName) && DAEMONS_GROUP.equalsIgnoreCase(thisGroupName))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void shutdown()
    {
        //todo stop jobs
        try
        {
            scheduler.shutdown();
        }
        catch (SchedulerException se)
        {
            log.log(Level.SEVERE, "Error in scheduler", se);
        }
    }

    private void initQuartzDaemons()
    {
        for (Daemon daemon : meta.getDaemons())
        {
            reInitQuartzDaemon(daemon, false);
        }
//        try
//        {
//            OfflineExecuteDaemon.initByDaemonStarter( connector );
//        }
//        catch( Throwable e )
//        {
//            log( "Could not call OfflineExecuteDaemon on startup", e );
//        }
//
//        try
//        {
//            getScheduler().addGlobalJobListener( new DaemonExecutionListener( connector ) );
//
//            String sql = "SELECT ID, className FROM daemons ORDER BY";
//            sql += "   CASE WHEN className = 'com.beanexplorer.enterprise.process.DaemonLoggerService' THEN 1";
//            sql += "        WHEN className = 'com.beanexplorer.enterprise.process.QueuedStatementExecuter' THEN 2";
//            sql += "        ELSE 3";
//            sql += "   END";
//            Object []daemons = Utils.readAsArray( connector, sql );
//            for( int i = 0; i < daemons.length; i++ )
//            {
//                Object []rec = ( Object [] )daemons[ i ];
//                try
//                {
//                    reInitQuartzDaemon( connector, "" + rec[ 0 ], false );
//                }
//                catch( Exception e )
//                {
//                    log( "Could not create quartz daemon with classname: " + rec[ 1 ], e );
//                }
//            }
//        }
//        catch( Throwable e )
//        {
//            log( "Could not init quartz daemons", e );
//        }
    }


    private long getPeriod(String config)
    {
        long mills = 0;
        long secs = 0;
        long mins = 0;
        long hours = 0;
        String sPeriod = coreUtils.getSystemSettingInSection(config, "PERIOD");
        String[] timeParts = sPeriod.split("\\.");
        if (timeParts.length == 0 || timeParts.length > 2)
        {
            throw new RuntimeException("incorrect period parameter '" + sPeriod + "'");
        }
        String[] time = timeParts[0].split(":");
        if (timeParts.length == 1)
        {
            if (time.length == 1)
            {
                mills = Long.parseLong(time[0]);
            }
        }
        else
        {
            mills = Long.parseLong(timeParts[1]);
            if (time.length == 1)
            {
                secs = Long.parseLong(time[0]);
            }
        }

        if (time.length == 2)
        {
            secs = Long.parseLong(time[1]);
            mins = Long.parseLong(time[0]);
        }
        else if (time.length == 3)
        {
            secs = Long.parseLong(time[2]);
            mins = Long.parseLong(time[1]);
            hours = Long.parseLong(time[0]);
        }
        if (!(timeParts.length == 1 && time.length == 1) && (mills > 999 || secs > 59 || mins > 59))
        {
            throw new IllegalArgumentException("incorrect period parameter '" + sPeriod + "'");
        }
        return mills + (secs + (mins + hours * 60) * 60) * 1000;
    }

    private int getOffset(String config)
    {
        try
        {
            String sOffset = coreUtils.getSystemSettingInSection(config, "START_OFFSET");
            return Integer.parseInt(sOffset);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private String getCronMask(String config)
    {
        return coreUtils.getSystemSettingInSection(config, "CRON_MASK");
    }

//    public static void interruptQuartzDaemon( String daemonId, UserInfo ui ) throws Exception
//    {
//        JDBCRecordAdapterAsQuery q = getDaemonInfo( connector, daemonId );
//        String name = q.getString( "name" );
//        String type = q.getString( "daemonType" );
//
//
//        if( "service".equals( type ) )
//        {
//            if( services == null )
//            {
//                services = new HashMap<String, Service>();
//            }
//            Service service = services.get( name );
//            if( service != null )
//            {
//                try
//                {
//                    context.log( "Stopping service \"" + service.getName() + "\"..." );
//                    service.stopWork();
//                    services.remove( name );
//                }
//                catch( Exception e )
//                {
//                    context.log( "Stopping of service \"" + service.getName() + "\" has failed!", e );
//                    updateQuartzDaemonStatus( connector, daemonId, DAEMON_STATUS_ERROR );
//                }
//            }
//        }
//        else
//        {
//            List jobs = Arrays.asList( getScheduler().getJobNames(DAEMONS_GROUP) );
//
//            if ( jobs.contains( getNameForOneTime( name, null ) ) )
//                getScheduler().interrupt( getNameForOneTime( name, null ), DAEMONS_GROUP );
//            else
//                getScheduler().interrupt( name, DAEMONS_GROUP );
//        }
//    }

    @Override
    public synchronized void reInitQuartzDaemon(Daemon daemon, boolean initManualDaemon)
    {
        String name = daemon.getName();

        String config = daemon.getConfigSection();
        if (Utils.isEmpty(config))
        {
            config = name;
        }
        String type = daemon.getDaemonType();

        JobDetail job = JobBuilder.newJob(getProcessClass(daemon))
                .withIdentity(name, DAEMONS_GROUP)
                .build();

        fillJobDataMap(job, name, config, Collections.emptyMap());

        try
        {
            scheduler.deleteJob(job.getKey());
            if (isEnabled(config) && (!"manual".equals(type) || initManualDaemon))
            {
                scheduler.scheduleJob(job, getTrigger(name, config, type));
            }
        }
        catch (SchedulerException se)
        {
            log.log(Level.SEVERE, "Error in delete or add job", se);
            throw Be5Exception.internal(se);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Process> getProcessClass(Daemon daemon)
    {
        Class<? extends Process> cls;
        try
        {
            cls = (Class<? extends Process>) Class.forName(daemon.getClassName());
        }
        catch (ClassNotFoundException e)
        {
            throw Be5Exception.internal(e);
        }
        return cls;
    }

    private Trigger getTrigger(String name, String config, String type)
    {
        if ("periodic".equals(type))
        {
            return newTrigger()
                    .withIdentity(name, DAEMONS_GROUP)
                    .startAt(new Date(System.currentTimeMillis() + getOffset(config)))
                    .withSchedule(simpleSchedule()
                            .withIntervalInMilliseconds(getPeriod(config))
                            .repeatForever())
                    .build();
        }
        else if ("manual".equals(type))
        {
            return newTrigger()
                    .withIdentity(name, DAEMONS_GROUP)
                    .startAt(new Date(System.currentTimeMillis() + getOffset(config)))
                    .withSchedule(simpleSchedule()
                            .withIntervalInMilliseconds(PERIOD_FOREVER)
                            .withRepeatCount(0))
                    .build();
        }
        else
        {
            return newTrigger()
                    .withIdentity(name, DAEMONS_GROUP)
                    .withSchedule(cronSchedule(getCronMask(config)))
                    .build();
        }
    }

//    public static synchronized void runQuartzDaemon(
//            String daemonId, boolean initManualDaemon ) throws Exception
//    {
//        runQuartzDaemon( connector, daemonId, null, Collections.EMPTY_MAP );
//    }
//
//    public static synchronized void runQuartzDaemon(
//            String daemonId, String uniqueId, Map extraJobParams ) throws Exception
//    {
//        JDBCRecordAdapterAsQuery q = getDaemonInfo( connector, daemonId );
//
//        String className = q.getString( "className" );
//        String name = q.getString( "name" );
//        String config = q.getString( "configSection" );
//        if( Utils.isEmpty( config ) )
//        {
//            config = name;
//        }
//        String type = q.getString( "daemonType" );
//
//        if( "service".equals( type ) )
//        {
//            startService( connector, daemonId, className, name, config );
//        }
//        else
//        {
//            Class cls = Class.forName( className );
//            if ( Arrays.asList( getScheduler().getJobNames( DAEMONS_GROUP ) ).contains( name ) ) // job initialized
//            {
//                getScheduler().triggerJob( name, DAEMONS_GROUP );
//            }
//            else
//            {
//                String oneTimeName = getNameForOneTime( name, uniqueId );
//                JobDetail job = new JobDetail( oneTimeName, DAEMONS_GROUP, cls );
//
//                SimpleTrigger simpleTrigger = new SimpleTrigger( oneTimeName, DAEMONS_GROUP );
//                simpleTrigger.setRepeatCount( 0 );
//                simpleTrigger.setRepeatInterval( PERIOD_FOREVER );
//                simpleTrigger.setStartTime( new Date( System.currentTimeMillis() + getOffset( connector, config ) ) );
//
//                fillJobDataMap( job, daemonId, name, config, extraJobParams );
//
//                getScheduler().scheduleJob( job, simpleTrigger );
//            }
//        }
//    }
//
//    private static String getNameForOneTime( String name, String postfix )
//    {
//        return "OneTime" + name + ( postfix != null ? postfix : "" );
//    }

    @Override
    public boolean isEnabled(String section)
    {
        return "enabled".equals(coreUtils.getSystemSettingInSection(section, "STATUS", "disabled"));
    }

    /**
     * Put needed values to job "context" .
     * ATTENTION! Since daemons can be used in distributed environment, only serializable objects must be stored in it.
     *
     */
    private static void fillJobDataMap(JobDetail job, String name, String config, Map<? extends String, ?> extra)
    {
        JobDataMap map = job.getJobDataMap();

        map.put(JOB_PARAM_NAME, name);
        map.put(JOB_PARAM_CONFIG_SECTION, config);

        map.putAll(extra);
    }

}

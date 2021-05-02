package com.arjun.cowin.quartz;

import com.arjun.cowin.config.AppProperties;
import com.arjun.cowin.service.VaccineLocatorService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzScheduler {

    private Scheduler scheduler;
    private Logger logger = LoggerFactory.getLogger(QuartzScheduler.class);
    private VaccineLocatorService vaccineLocatorService;

    public QuartzScheduler(VaccineLocatorService vaccineLocatorService) {
        this.vaccineLocatorService = vaccineLocatorService;
        init();
    }

    private void init() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            scheduleJobForVaccineLocator();
        } catch (Exception ex) {
            logger.error("Error while initializing quartz scheduler", ex);
        }
    }

    private void scheduleJobForVaccineLocator() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(VaccineLocatorJob.class)
                .withIdentity("vaccine-locator", "universal")
                .build();
        job.getJobDataMap().put("vaccineLocatorService", vaccineLocatorService);
        scheduleJob(job, "vaccine-locator", "universal", AppProperties.INSTANCE.getString("frequency"));
    }

    public void scheduleJob(JobDetail job, String name, String group, String cronExp) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    public void shutDown() {
        try {
            if (!scheduler.isShutdown())
                scheduler.shutdown();
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

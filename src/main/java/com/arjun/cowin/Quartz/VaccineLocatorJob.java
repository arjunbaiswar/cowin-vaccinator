package com.arjun.cowin.Quartz;


import com.arjun.cowin.config.AppProperties;
import com.arjun.cowin.service.VaccineLocatorService;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class VaccineLocatorJob implements Job {

    private Logger log = LoggerFactory.getLogger(VaccineLocatorJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final JobDetail jobDetail = jobExecutionContext.getJobDetail();
        final AppProperties appProperties = AppProperties.INSTANCE;

        log.info("Quartz Job: {} triggered at: {}", jobDetail.getKey().getName(), jobExecutionContext.getFireTime());
        VaccineLocatorService vaccineLocatorService = (VaccineLocatorService) jobDetail.getJobDataMap().get("vaccineLocatorService");
        List<String> districts = Arrays.asList(appProperties.getStringArray("districts"));
        districts.forEach(district -> {
            vaccineLocatorService.notifyIfAvailable(Arrays.asList(appProperties.getStringArray(district + ".pinCodes")),
                    Arrays.asList(appProperties.getStringArray(district + ".recipients")));
        });
    }

}


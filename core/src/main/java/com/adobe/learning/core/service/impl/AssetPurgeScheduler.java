package com.adobe.learning.core.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.adobe.learning.core.service.config.DigitalAssetPurgeSchedulerConfiguration;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shiv
 */
@Component(service = Job.class, immediate = true)
@Designate(ocd = DigitalAssetPurgeSchedulerConfiguration.class)
public class AssetPurgeScheduler implements Job {

    private final Logger logger = LoggerFactory.getLogger(AssetPurgeScheduler.class);
    private static final String ASSET_PATH = "assetPath";

    @Reference
    Scheduler scheduler;

    String schedulerName;

    @Activate
    private void activate(DigitalAssetPurgeSchedulerConfiguration configuration) {
        this.schedulerName = configuration.updateSchedulerName();
        logger.info("****Asset Update Scheduler ****");
        // This scheduler will continue to run automatically even after the server
        // reboot, otherwise the scheduled tasks will stop running after the server
        // reboot.
        addScheduler(configuration);
    }

    @Modified
    protected void modified(DigitalAssetPurgeSchedulerConfiguration configuration) {
        // Remove the scheduler registered with old configuration
        removeScheduler(configuration);
        // Add the scheduler registered with new configuration
        addScheduler(configuration);

    }

    @Deactivate
    protected void deactivated(DigitalAssetPurgeSchedulerConfiguration configuration) {
        removeScheduler(configuration);
        logger.info("****Removing Scheduler Successfully on deactivation ****");
    }

    private void removeScheduler(DigitalAssetPurgeSchedulerConfiguration configuration) {
        scheduler.unschedule(schedulerName);
        logger.info("****Removing Scheduler Successfully ****{}", schedulerName);
    }

    private void addScheduler(DigitalAssetPurgeSchedulerConfiguration configuration) {
        boolean enabled = configuration.enabled();
        if (enabled) {
            //Create a schedule options to schedule the job for English locale
            String enCronExpression = configuration.enCronExpression();
            ScheduleOptions enScheduleOptions = scheduler.EXPR(enCronExpression);
            Map<String, Serializable> enMap = new HashMap<>();
            String enAssetPath = configuration.enAssetPath();
            enMap.put(ASSET_PATH, enAssetPath);
            enScheduleOptions.config(enMap);
            enScheduleOptions.canRunConcurrently(false);
            scheduler.schedule(this, enScheduleOptions);

            //Create a schedule options to schedule the job for French locale
            String frCronExpression = configuration.frCronExpression();
            ScheduleOptions frScheduleOptions = scheduler.EXPR(frCronExpression);
            Map<String, Serializable> frMap = new HashMap<>();
            String frAssetPath = configuration.frAssetPath();
            frMap.put(ASSET_PATH, frAssetPath);
            frScheduleOptions.config(frMap);
            frScheduleOptions.canRunConcurrently(false);
            scheduler.schedule(this, frScheduleOptions);

            //Create a schedule options to schedule the job for India
            String inCronExpression = configuration.inCronExpression();
            ScheduleOptions inScheduleOptions = scheduler.EXPR(inCronExpression);
            Map<String, Serializable> inMap = new HashMap<>();
            String inAssetPath = configuration.inAssetPath();
            inMap.put(ASSET_PATH, inAssetPath);
            inScheduleOptions.config(inMap);
            inScheduleOptions.canRunConcurrently(false);
            scheduler.schedule(this, inScheduleOptions);
        }
    }

    @Override
    public void execute(JobContext jobContext) {
        logger.info("****Asset Path**** {} ", jobContext.getConfiguration().get(ASSET_PATH));
        //Purge business logic will be included here
    }
}

package com.adobe.learning.core.schedulers;

import com.adobe.learning.core.service.config.LearningSchedulerConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd= LearningSchedulerConfig.class)
@Component(service=Runnable.class, immediate = true)
public class LearningScheduler implements Runnable{

    private final Logger logger = LoggerFactory.getLogger(LearningScheduler.class);

    LearningSchedulerConfig learningSchedulerConfig;

    @Activate
    protected void activate(LearningSchedulerConfig config) {
        this.learningSchedulerConfig = config;
    }

    @Override
    public void run() {
        logger.debug("Execute the scheduled task here after the interval of : {}", learningSchedulerConfig.scheduler_expression());
    }
}

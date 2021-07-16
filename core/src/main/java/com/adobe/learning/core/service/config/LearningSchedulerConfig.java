package com.adobe.learning.core.service.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Demo Scheduler Configuration", description = "Simple demo for schedule task to operate on specific interval")
public @interface LearningSchedulerConfig {

    @AttributeDefinition(name = "schedulerName", description = "Enter a unique identifier that represents name of the scheduler",
            type = AttributeType.STRING)
    String scheduler_name() default "Demo";

    @AttributeDefinition(name = "schedulerExpression", description = "Cron expression which will decide how the scheduler will run",
            type = AttributeType.STRING)
    String scheduler_expression() default "0 0/1 * 1/1 * ? *";

    @AttributeDefinition(name = "schedulerConcurrent", description = "Whether or not to schedule this task concurrently", type = AttributeType.BOOLEAN)
    boolean scheduler_concurrent() default false;

    @AttributeDefinition(name = "serviceEnabled", description = "Check the box to enable the scheduler", type = AttributeType.BOOLEAN)
    boolean service_enabled() default true;
}
package com.adobe.learning.core.service.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Shiv
 * This is the configuration interface that takes properties for a
 * scheduler to run on country specific asset folders at different time
 */
@ObjectClassDefinition(name = "Digital Asset Purge Scheduler Configuration", description = "Digital Asset Purge Scheduler Configuration")
public @interface DigitalAssetPurgeSchedulerConfiguration {

    @AttributeDefinition(name = "Scheduler name", description = "Name of the scheduler", type = AttributeType.STRING)
    String updateSchedulerName() default "Digital Asset Purge Scheduler Configuration";

    @AttributeDefinition(name = "Enabled", description = "True, if scheduler service is enabled", type = AttributeType.BOOLEAN)
    boolean enabled() default false;

    @AttributeDefinition(name = "Cron Expression Associates With English folder Assets", description = "Cron expression used by the scheduler on English folder Assets", type = AttributeType.STRING)
    String enCronExpression() default "0 0 9 1/1 * ? *";

    @AttributeDefinition(name = "Asset Path of English Assets", description = "Asset Path of English Assets", type = AttributeType.STRING)
    String enAssetPath() default "/content/dam/we-retail/en";

    @AttributeDefinition(name = "Cron Expression Associates With French folder Assets", description = "Cron expression used by the scheduler on French folder Assets", type = AttributeType.STRING)
    String frCronExpression() default "0 2 9 1/1 * ? *";

    @AttributeDefinition(name = "Asset Path of French Assets", description = "Asset Path of French Assets", type = AttributeType.STRING)
    String frAssetPath() default "/content/dam/we-retail/fr";

    @AttributeDefinition(name = "Cron Expression Associates With India folder Assets", description = "Cron expression used by the scheduler on India folder Assets", type = AttributeType.STRING)
    String inCronExpression() default "0 5 9 1/1 * ? *";

    @AttributeDefinition(name = "Asset Path of India Assets", description = "Asset Path of India Assets", type = AttributeType.STRING)
    String inAssetPath() default "/content/dam/we-retail/in";

}
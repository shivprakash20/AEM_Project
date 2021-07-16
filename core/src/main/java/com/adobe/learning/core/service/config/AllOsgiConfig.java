package com.adobe.learning.core.service.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * This interface represents an OSGi configuration which can be found at -
 * ./system/console/configMgr
 */
@ObjectClassDefinition(name = "All OSGI Configuration", description = "This configuration for Learning Reference")
public @interface AllOsgiConfig {

    /*Checkbox*/
    @AttributeDefinition( name = "Enable config", description = "This property indicates whether the configuration values will taken into account or not",
            type = AttributeType.BOOLEAN)
    boolean enableConfig() default false;

    /*Dropdown*/
    @AttributeDefinition(name = "Protocol", description = "Choose Protocol", type = AttributeType.STRING,
            options = {@Option(label = "HTTP", value = "http"), @Option(label = "HTTPS", value = "https") })
    String getProtocol() default StringUtils.EMPTY;

    /*String , Text field*/
    @AttributeDefinition(name = "Server", description = "Enter the server name", type = AttributeType.STRING)
    String getServer() default StringUtils.EMPTY;

    /*String Array*/
    @AttributeDefinition(name = "UserEnvironments", description = "Define the all environment where this user will be available")
    String[] getEnvironments() default {};

    /*Int*/
    @AttributeDefinition(name = "UserValidity", description ="Validity of user account", required = true, type = AttributeType.INTEGER, min = "10")
    int getValidity() default 10;

    /*Password*/
    @AttributeDefinition(name = "UserPassword", description = "Password of the user account", type = AttributeType.PASSWORD)
    String getPassword() default "";

    /*Cron Expression*/
    @AttributeDefinition(name = "Scheduler_Expression", description = "Expression for Cron Job", type = AttributeType.STRING)
    String scheduler_expression() default "*/30 * * * * ?";
}
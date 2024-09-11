package com.adobe.learning.core.service.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

    @ObjectClassDefinition(name = "Curl Command Host Configuration", description = "Sender and receiver host details for curl command")
public @interface CurlCommandHostConfig {

    @AttributeDefinition(name = "senderHostURL", description = "Enter Sender Host URL (like http://localhost:4502)",
            type = AttributeType.STRING)
    String sender_Host_URL() default "http://localhost:4502";

    @AttributeDefinition(name = "senderUserName", description = "Enter User Name (like admin)",
            type = AttributeType.STRING)
    String sender_User_Name() default "admin";

    @AttributeDefinition(name = "senderUserPassword", description = "Enter Sender User Password (like admin)",
            type = AttributeType.STRING)
    String sender_User_Password() default "admin";

    @AttributeDefinition(name = "receiverHostURL", description = "Enter Receiver Host URL (like http://localhost:4503)",
            type = AttributeType.STRING)
    String receiver_Host_URL() default "http://localhost:4503";

    @AttributeDefinition(name = "receiverUserName", description = "Enter Receiver User Name (like http://localhost:4503)",
            type = AttributeType.STRING)
    String receiver_User_Name() default "admin";

    @AttributeDefinition(name = "receiverUserPassword", description = "Enter Receiver User Password (like admin)",
            type = AttributeType.STRING)
    String receiver_User_Password() default "admin";
}
package com.hcommerce.heecommerce.common;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AWSSQSProducer {

    @Value("${spring.cloud.aws.sqs.pr.input.name}")
    private String sqsQueueName;

    @Autowired
    private SqsTemplate sqsTemplate;

    public void sendMessage(String message) {
        if (message == null) {
            final String errorMessage = "empty message";
            throw new RuntimeException(errorMessage);
        }

        sqsTemplate.send(sqsQueueName, message);
    }
}

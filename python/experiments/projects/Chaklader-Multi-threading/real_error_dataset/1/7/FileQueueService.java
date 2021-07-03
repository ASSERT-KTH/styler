package com.example;

import com.amazonaws.services.sqs.model.Message;

public class FileQueueService implements QueueService {

    @Override
    public void sendMessage(String queueUrl, String messageBody) {
        // some code             
    }

    @Override
    public Message receiveMessage(String queueUrl) {
        return null;
    }

    @Override
    public void deleteMessage(String queueUrl, String receiptHandle) {
        // some code
    }
}

package com.vi.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vi.model.SqsMessage;
import com.vi.repository.SqsMessageRepository;
import com.vi.service.SqsService;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Service
@Transactional
public class SqsServiceImpl implements SqsService {
	
	@Autowired
	private SqsMessageRepository sqsMessageRepository;
	
	private static final String QUEUE_NAME = "CVI_QUEUE.fifo";
	private static final Integer MAX_NUMBER_OF_MESSAGES = 10;
	private static final SqsClient SQS_CLIENT = SqsClient.builder().region(Region.US_WEST_2).build();
	private static String queueUrl;
	
	@Override
	public List<String> consumeMessages() {
		GetQueueUrlResponse getQueueUrlResponse = SQS_CLIENT.getQueueUrl(GetQueueUrlRequest.builder().queueName(QUEUE_NAME).build());
		queueUrl = getQueueUrlResponse.queueUrl();
    
		ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
				.queueUrl(queueUrl)
				.maxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
				.build();
	    List<Message> receivedMessages = SQS_CLIENT.receiveMessage(receiveMessageRequest).messages();
	    
	    List<String> returnedMessages = new ArrayList<>();
	    for (Message receivedMessage : receivedMessages) {
	    	// save message to H2 database
	    	SqsMessage sqsMessage = new SqsMessage();
	    	sqsMessage.setMessage(receivedMessage.body());
	    	sqsMessage.setTimestamp(LocalDateTime.now());
	    	sqsMessageRepository.save(sqsMessage);
	    	
	    	// add message to list of messages to be returned
	        returnedMessages.add(receivedMessage.body());
	        
	        // delete message
	        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
	        		.queueUrl(queueUrl)
	                .receiptHandle(receivedMessage.receiptHandle())
	                .build();
	        SQS_CLIENT.deleteMessage(deleteMessageRequest);
	    }
	    return returnedMessages;
	}

}

/* 
 * Name: Christopher Vi
 * Description: Sample code for AWS SDK SQS and H2 DB using Spring Boot
*/

package com.vi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vi.service.SqsService;

@SpringBootApplication
public class CviAwsSqsDbCodeApplication implements CommandLineRunner {
	
	@Autowired
	private SqsService sqsService;
	
	public static void main(String[] args) {
		SpringApplication.run(CviAwsSqsDbCodeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<String> messages = sqsService.consumeMessages();
		for(String message : messages)
			System.out.println("Message: " + message);
	}

}

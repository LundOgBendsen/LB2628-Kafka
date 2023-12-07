package com.lb.kafkademo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;


@SpringBootApplication
public class KafkaDemoApplication implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value("${topic.name}")
	private String topicName;

	@Override
	public void run(String... args) throws Exception {
		CompletableFuture<SendResult<String, String>> send =
				kafkaTemplate.send(topicName, "hello-world");

		//optionally do something with the CompleteableFuture
		send.thenAccept(this::outputSendResult);
	}

	private void outputSendResult(SendResult<String, String> sendResult) {
		System.out.printf("Topic: %s%n", sendResult.getRecordMetadata().topic());
		System.out.printf("Partition: %s%n", sendResult.getRecordMetadata().partition());
		System.out.printf("Offset: %s%n", sendResult.getRecordMetadata().offset());
	}


}

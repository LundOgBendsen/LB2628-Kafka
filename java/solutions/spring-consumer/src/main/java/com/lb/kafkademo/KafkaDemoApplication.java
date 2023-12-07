package com.lb.kafkademo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@SpringBootApplication
public class KafkaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}


	@KafkaListener(topics = "${topic.name}", groupId = "${group.name}")
	public void consume(ConsumerRecord<String, String> payload){
		System.out.println(payload.value());
	}


//	@KafkaListener(topics = "${topic.name}", groupId = "${group.name}", batch = "true")
//	public void consume(List<ConsumerRecord<String, String>> payload){
//		System.out.println(payload.size());
//	}


}

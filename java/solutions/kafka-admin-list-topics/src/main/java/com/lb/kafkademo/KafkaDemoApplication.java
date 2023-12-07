package com.lb.kafkademo;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;


@SpringBootApplication
public class KafkaDemoApplication implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		Properties properties = new Properties();
		properties.put("bootstrap.servers", "localhost:29092");
		properties.put("client.id", "admin-client");

		AdminClient client = AdminClient.create(properties);

		//TODO List Topics

		ListTopicsResult topics = client.listTopics();
		topics.listings().get().forEach(this::printTopic);


	}

	private void printTopic(TopicListing topicListing) {
		System.out.println(topicListing.name());
	}

}

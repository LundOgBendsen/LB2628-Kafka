package com.lb.kafkademo;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
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


		String topicname = "my-demo-topic";

		NewTopic mydemotopic = new NewTopic(topicname, 1, (short) 1); //new NewTopic(topicName, numPartitions, replicationFactor)

		client.createTopics(Arrays.asList(mydemotopic));

		DescribeTopicsResult topicList = client.describeTopics(Arrays.asList(topicname));

		TopicDescription topicDescription = topicList.values().get(topicname).get();

		System.out.println("Name=" + topicDescription.name());
		System.out.println("Partitions=" + topicDescription.partitions().size());


	}

}

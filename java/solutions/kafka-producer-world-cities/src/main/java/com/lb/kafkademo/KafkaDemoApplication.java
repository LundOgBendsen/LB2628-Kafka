package com.lb.kafkademo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


@SpringBootApplication
public class KafkaDemoApplication implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}


	@Value("${kafka.bootstrap.server}")
	private String bootstrapServer;


	@Override
	public void run(String... args) throws Exception {

		Map<String, Object> props = new HashMap<>();
		props.put(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstrapServer);
		props.put(
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		props.put(
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);

		System.out.println("user.dir: " + System.getProperty("user.dir"));

		try (Scanner scanner = new Scanner(new File("../../data/world-cities.txt"));
			 KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				System.out.println(line);

				ProducerRecord<String, String> producerRecord =
						new ProducerRecord<>("world-cities", line);

				// send data - asynchronous
				producer.send(producerRecord);
			}

			producer.flush();
		}

	}

}

package com.lb.kafkademo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


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


		// create the producer
		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		{
			// create a producer record
			ProducerRecord<String, String> producerRecord =
					new ProducerRecord<>("demo-topic", "hello world");

			// send data - asynchronous
			RecordMetadata recordMetadata = producer.send(producerRecord).get(5, TimeUnit.SECONDS);
			outputRecordMetadata(recordMetadata);
		}

		{
			// create a producer record
			ProducerRecord<String, String> producerRecord =
					new ProducerRecord<>("demo-topic", 1, "key","hello world");

			RecordMetadata recordMetadata = producer.send(producerRecord).get(5, TimeUnit.SECONDS);
			outputRecordMetadata(recordMetadata);
		}

		// flush data - synchronous
		producer.flush();

		// flush and close producer
		producer.close();


	}

	private void outputRecordMetadata(RecordMetadata recordMetadata) {
		System.out.printf("partition: %s, offset: %s\n", recordMetadata.partition(), recordMetadata.offset());
	}

}

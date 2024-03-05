package com.lb.kafkademo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class KafkaDemoApplication implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}


	@Value("${kafka.bootstrap.server}")
	private String bootstrapServer;

	@Value("${kafka.groupid}")
	private String groupId;


	@Override
	public void run(String... args) throws Exception {

		Map<String, Object> props = new HashMap<>();
		props.put(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstrapServer);
		props.put(
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				StringDeserializer.class);
		props.put(
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				StringDeserializer.class);

		props.put(
				ConsumerConfig.GROUP_ID_CONFIG,
				groupId);

		props.put(
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
				"earliest");


		props.put(
				ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
				"true");


		// create consumer
		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

			consumer.subscribe(Arrays.asList("demo-topic"));

			// poll for new data
			while (true) {
				ConsumerRecords<String, String> records =
						consumer.poll(Duration.ofMillis(100));

				for (ConsumerRecord<String, String> record : records) {
					System.out.printf("Key: %s, Value: %s, Partition: %s, Offset: %s\n",
							record.key(), record.value(), record.partition(), record.offset());
					// consumer.commitSync(Collections.singletonMap(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1)));

				}
			}
		}

	}

}

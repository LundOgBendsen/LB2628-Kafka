package com.lb.kafkademo.consumer;

import com.example.protos.HelloValue;
import org.apache.kafka.clients.consumer.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Properties;


@SpringBootApplication
public class ProtobufConsumer implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(ProtobufConsumer.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		Properties props = new Properties();

		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "hello-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer");
		props.put("schema.registry.url", "http://localhost:8085");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		final Consumer<String, HelloValue.HelloWorld> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("hello"));

		try {
			while (true) {
				ConsumerRecords<String, HelloValue.HelloWorld> records = consumer.poll(100);
				for (ConsumerRecord<String, HelloValue.HelloWorld> record : records) {
					System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
				}
			}
		} finally {
			consumer.close();
		}
	}

}

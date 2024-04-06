package com.lb.kafkademo.producer;

import com.example.tutorial.protos.Hello;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;


@SpringBootApplication
public class ProtobufProducer implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(ProtobufProducer.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
		props.put("schema.registry.url", "http://127.0.0.1:8085");
		props.put("auto.register.schemas", false);



		Producer<String, Hello.HelloWorld> producer = new KafkaProducer<String, Hello.HelloWorld>(props);

		Hello.HelloWorld message = Hello.HelloWorld.newBuilder()
				.setAge(10).build();

		ProducerRecord<String, Hello.HelloWorld> record
				= new ProducerRecord<String, Hello.HelloWorld>("hello-topic", "key", message);
		producer.send(record).get();
		producer.close();

	}

}

package com.lb.kafkademo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;

import java.time.Duration;
import java.util.Properties;


public class KafkaDemoApplication {


	public static void main(final String[] args) {
		final Properties streamsConfiguration = new Properties();

		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-1");
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "streams-1-client");

		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");

		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());


		streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

		streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

		streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "c:/stream-tmp");

		Topology topology = getTopology();

		KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);

		streams.cleanUp();
		streams.start();
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

	private static org.apache.kafka.streams.Topology getTopology() {
		final StreamsBuilder builder = new StreamsBuilder();


		KStream<String, String> inputStream1 = builder.stream("inputTopic-1");
		KStream<String, String> inputStream1_rekey = inputStream1.selectKey((k,v) -> v.substring(0,1));

		KStream<String, String> inputStream2 = builder.stream("inputTopic-2");
		KStream<String, String> inputStream2_rekey = inputStream2.selectKey((k,v) -> v.substring(0,1));

		KStream<String, String> join = inputStream1_rekey.join(inputStream2_rekey, (v1, v2) -> v1 + ":" + v2,
				JoinWindows.of(Duration.ofSeconds(5)));

		join.to("outputTopic");

		return builder.build();
	}

}

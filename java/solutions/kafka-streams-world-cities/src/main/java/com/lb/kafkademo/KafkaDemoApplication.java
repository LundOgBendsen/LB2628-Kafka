package com.lb.kafkademo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.util.List;
import java.util.Properties;


public class KafkaDemoApplication {


	public static void main(final String[] args) {
		final Properties streamsConfiguration = new Properties();

		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-world-cities");
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "streams-world-cities-client");

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

		KStream<String, String> inputStream = builder.stream("world-cities");

		KeyValueMapper<String, String, KeyValue<String, String>> cityKeyValueMapper = (key, value) -> {
			// Modify the key and value here
			String newKey = List.of(value.split(",")).get(0);
			String newValue = value;
			return KeyValue.pair(newKey, newValue);
		};

		// Change the stream to use city as key (not really needed)
		KStream<String,String> keyedstream = inputStream.map(cityKeyValueMapper);

		KTable<String, Long>  ktable = keyedstream
				.selectKey((k, v) -> List.of(v.split(",")).get(1))
				.groupByKey()
				.count();

		ktable.toStream().to("world-cities-count");

		return builder.build();
	}

}

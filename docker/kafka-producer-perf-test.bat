docker run -it --rm --network host confluentinc/cp-kafka /bin/kafka-producer-perf-test --producer-props bootstrap.servers=localhost:29092 %* 

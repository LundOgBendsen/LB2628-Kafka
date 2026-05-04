podman run -it --rm --network host confluentinc/cp-kafka:7.8.0 /bin/kafka-producer-perf-test --producer-props bootstrap.servers=localhost:29092 %* 

podman run -it --rm --network host confluentinc/cp-kafka:7.8.0 /bin/kafka-console-producer --bootstrap-server localhost:29092 %*

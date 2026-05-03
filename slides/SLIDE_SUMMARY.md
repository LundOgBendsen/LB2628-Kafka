# Apache Kafka — 2-Day Introductory Course: Slide Summary

---

## 01 - Introduction

**Purpose:** Course overview and logistics.

**Key topics:**
- Instructor: Martin Højgaard Clausen
- 2-day agenda overview
- Course goals: understand Kafka architecture, produce/consume messages, use Spring Kafka, Kafka Streams, and Kafka Connect

**Exercises:**
- 1.1 — Log in to Azure VM via RDP
- 1.2 — Clone the course git repository

**Notes:** Uses Azure VMs with hardcoded RDP credentials for lab environment.

---

## 01.A - Architecture

**Purpose:** Motivates the shift from monolith to microservices.

**Key topics:**
- Monolith pros: simple deployment, easy refactoring, no network latency
- Monolith cons: scaling, team bottlenecks, technology lock-in, long release cycles
- Microservices pros: independent deployment, scalability, team autonomy, polyglot
- Microservices cons: operational complexity, distributed systems challenges
- Martin Fowler's "monolith first" recommendation — avoid premature decomposition

---

## 01.B - Communication

**Purpose:** Introduces synchronous vs asynchronous communication patterns and the Reactive Manifesto.

**Key topics:**
- **Reactive Manifesto:** Responsive, Resilient, Elastic, Message-Driven
- Synchronous communication: tight coupling, shared fate, cascading failures
- Asynchronous communication: loose coupling, decoupled failure domains
- Event backbone concept: events as the integration layer between services
- Challenges of async/event-driven systems:
  - Eventual consistency
  - Data versioning / schema evolution
  - Back pressure
  - Security
  - Observability and monitoring

---

## 01.C - EDA (Event-Driven Architecture)

**Purpose:** Deep dive into EDA patterns and building blocks.

**Key topics:**
- Events vs Commands vs Queries
- Event communication patterns:
  - **Pub/Sub** — broadcast to subscribers
  - **Point-to-point** — direct routing
  - **Event Sourcing** — event log as the source of truth
  - **Event Streaming** — continuous processing of event streams
  - **CDC (Change Data Capture)** — capture DB changes as events
  - **Process Manager** — orchestrates multi-step workflows
- Fat events vs Delta events
- Delivery failure strategies: DLQ, redrive, drop, archive/replay
- **CQRS** — Command Query Responsibility Segregation
- EDA at scale
- **DDD Bounded Contexts** — defining service boundaries around domain concepts

---

## 02 - Kafka Overview

**Purpose:** Introduces Apache Kafka — history, architecture, and use cases.

**Key topics:**
- Kafka history: created at LinkedIn, open-sourced 2011, Apache top-level project
- Core concepts: brokers, topics, partitions, offsets, producers, consumers
- Architecture diagram showing ZooKeeper + 2 brokers (note: "KRaft is the new protocol")
- Use cases: event streaming, log aggregation, metrics, CDC, decoupling services
- How Kafka works: append-only log, sequential reads, durability via replication

**Exercises:**
- 2.1 — Start a local cluster: ZooKeeper + 2 brokers via Docker Compose (`confluentinc/cp-kafka` images)
- 2.2 — Explore running containers in Docker Desktop

**Notes:** Docker Compose setup still uses ZooKeeper; KRaft is mentioned but not used in exercises.

---

## 03 - Kafka CLI

**Purpose:** Hands-on with Kafka command-line tools.

**Key topics and commands:**

| Tool | Purpose |
|---|---|
| `kafka-topics` | Create, list, describe, delete topics |
| `kafka-console-producer` | Produce messages from CLI |
| `kafka-console-consumer` | Consume messages from CLI |
| `kafka-consumer-groups` | List groups, describe offsets, reset offsets |
| `kafka-producer-perf-test` | Throughput benchmarking |
| `kafka-configs` | Alter broker/topic configs |
| `kafka-get-offsets` | Query current offsets |
| `kafka-log-dirs` | Inspect log directory sizes |

**Notes:** Slides use `.bat` wrapper scripts around the standard Kafka shell tools.

---

## 04 - Broker

**Purpose:** Internals of the Kafka broker.

> **Note:** This PDF (16.4 MB) could not be rendered for text extraction. Content is not available in this summary. Topics likely covered include: broker architecture, replication, leader election, log segments, retention, compaction, and KRaft/ZooKeeper coordination.

---

## 05 - Producer

**Purpose:** Deep dive into Kafka producer internals and configuration.

**Key topics:**
- Bootstrapping: producer connects to any broker to fetch cluster metadata
- Message send pipeline:
  `Interceptor → Serialization → Partition assignment → Record Accumulator → Transaction management → Compression → Channel selector → Network client → Broker socket`
- **Partition assignment:** `Hash(key) % num_partitions` (keyed) or round-robin (no key)
- **Record Accumulator:** batches records per partition before sending
- **Latency vs Throughput tuning:**
  - `linger.ms` — wait time before sending a batch
  - `batch.size` — max bytes per batch
- **Compression:** `snappy`, `gzip`, `lz4`
- **Acknowledgements (`acks`):**
  - `0` — fire and forget
  - `1` — leader ack only
  - `all` — all in-sync replicas
- **Delivery guarantees:** at-most-once, at-least-once, exactly-once
- **Idempotent producer:** `enable.idempotence=true` — uses producer ID + sequence number to deduplicate retries

**Key config properties:**
`compression.type`, `acks`, `enable.idempotence`, `delivery.timeout.ms`, `retry.backoff.ms`, `max.in.flight.requests.per.connection`

**Exercises:**
- 5.1 — Console producer basics
- 5.2 — Observe offsets
- 5.3 — Demo effect of `linger.ms` on latency/throughput

---

## 06 - Consumer

**Purpose:** Deep dive into Kafka consumer internals, offset management, and rebalancing.

**Key topics:**
- Bootstrapping: consumer connects to broker, finds group coordinator
- `auto.offset.reset`: `earliest` / `latest`
- **Consumer groups:** partitions distributed across group members; each partition consumed by exactly one member
- **`__consumer_offsets` topic:** Kafka's internal offset storage
- **Auto-commit:** `enable.auto.commit=true` + `auto.commit.interval.ms` — not recommended in production
- **Manual commit:**
  - `commitAsync()` — non-blocking, fire and forget
  - `commitSync()` — blocking, used on shutdown
  - Pattern: use `commitAsync` in poll loop, `commitSync` in finally block
- **Consumer internals:** Fetcher I/O threads decoupled from single-threaded poll loop
- **Parallel processing:** wrap poll loop with `ExecutorService`
- **Delivery guarantees at consumer:** at-most-once, at-least-once, exactly-once
- **Rebalancing:** triggered on join/leave; coordinated via group coordinator and group leader
  - Protocol: JoinGroup → SyncGroup → `onPartitionsAssigned`
- **Partition assignors:** `RangeAssignor`, `RoundRobinAssignor`, `StickyAssignor`, `CooperativeStickyAssignor`
- **`ConsumerRebalanceListener`:** `onPartitionsRevoked` / `onPartitionsAssigned` for external offset stores

**Key config properties:**
`fetch.min.bytes`, `group.id`, `session.timeout.ms`, `allow.auto.create.topics`, `auto.offset.reset`, `enable.auto.commit`, `fetch.max.bytes`, `max.poll.interval.ms`, `max.poll.records`, `partition.assignment.strategy`

**Exercises:**
- 6.1 — Basic consumer
- 6.2 — Consume from beginning
- 6.3 — Consumer groups
- 6.4 — Reset offsets
- 6.5 — Partition reassignment

---

## 07 - Kafka Spring

**Purpose:** Spring Kafka integration — high-level and low-level APIs, testing.

**Key topics:**

**Dependency:** `spring-kafka`

**Low-level producer:**
```java
KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.send(new ProducerRecord<>("topic", key, value));
```

**High-level producer (`KafkaTemplate`):**
```java
kafkaTemplate.send("topic", key, value);                    // async
kafkaTemplate.send(...).get();                              // sync (blocks)
kafkaTemplate.send(...).thenAccept(result -> {...});        // async callback
```

**Low-level consumer:** manual `KafkaConsumer` poll loop

**High-level consumer (`@KafkaListener`):**
```java
@KafkaListener(topics = "my-topic", groupId = "my-group")
public void listen(String message) { ... }

@KafkaListener(topics = "my-topic")
public void listen(ConsumerRecord<String, String> record) { ... }

@KafkaListener(topics = "my-topic", batch = "true")
public void listen(List<String> messages) { ... }
```

**Acknowledgement mode:**
```java
@KafkaListener(...)
public void listen(String msg, Acknowledgment ack) {
    ack.acknowledge();   // commit
    ack.nack(duration);  // negative ack / retry after delay
}
```

**`ConcurrentKafkaListenerContainerFactory`:** controls concurrency and batch mode

**AdminClient:** `createTopics`, `deleteTopics`, `listTopics`, `describeTopics`

**Testing:**
- `@EmbeddedKafka` — in-process Kafka for unit tests (with `@SpringBootTest` + `@DirtiesContext`)
- `TestContainers` — `KafkaContainer` using `confluentinc/cp-kafka:latest`
- `CountDownLatch` pattern for async consumer test synchronization

**Key Spring config properties:**
`bootstrap-servers`, `acks`, `retries`, `batch-size`, `linger-ms`, `key-serializer`, `value-serializer`, `group-id`, `auto-offset-reset`, `enable-auto-commit`

**Exercises:** 7.1 – 7.8 (low-level producer, high-level producer, low-level consumer, high-level consumer variants, AdminClient, EmbeddedKafka test, TestContainers test)

---

## 08 - Kafka Streams

**Purpose:** Introduces Kafka Streams for real-time stream processing using a declarative DSL.

**Key topics:**

**Why Kafka Streams:**
- Client-side library built on top of Consumer/Producer APIs
- **Declarative DSL** (vs imperative consumer/producer code): `filter()`, `map()`, `join()`, `aggregate()`
- API stack: Consumer/Producer → Kafka Streams → ksqlDB
- ETL framework: processors chained in unix-pipe style (Source → Stream processors → Sink)

**Architecture:**
- One **task** per source partition; each task has its own copy of the topology
- **Threads** (`num.streams.threads`) execute tasks — cannot exceed task count
- Multiple instances share the same `application.id` (used as consumer group ID)

**Topology (DAG):**
- **Source Nodes** — read from Kafka topics (`KStream`, `KTable`, `GlobalKTable`)
- **Processor Nodes** — filter, map, aggregate, join
- **Sink Nodes** — write to Kafka topics or external systems
- **State Stores** — local RocksDB-backed cache for stateful operations; backed up to a compacted changelog topic

**Source stream types:**
```java
KStream<String, String> stream = builder.stream("inputTopic");         // event stream
KTable<String, String> table = builder.table("inputTopic");            // latest value per key (per partition)
GlobalKTable<String, String> global = builder.globalTable("inputTopic"); // latest value per key (all partitions)
```

**Defining a topology:**
```java
StreamsBuilder builder = new StreamsBuilder();
builder.stream("inputTopic")
    .filter((k, v) -> v.color() != green)
    .mapValues(v -> v.label().toUpperCase())
    .to("outputTopic", Produced.with(...));
KafkaStreams streams = new KafkaStreams(builder.build(), props);
streams.start();
```

**Stateless processors:**
- `filter()` — remove records by predicate
- `map()` / `mapValues()` — transform records
- `flatMap()` — one record → zero or more records
- `branch()` / `split()` — route records to multiple streams
- `groupBy()` — group by a new key
- `merge()` — combine two streams

**Stateful processors:**
- `count()` — count records per grouped key
- `join()` — merge two streams by key within a time window (requires co-partitioning)
- `aggregate()`, `reduce()`

**State stores:**
- Local RocksDB cache, one per task
- Backed up to a compacted Kafka changelog topic
- New tasks rebuild state from the changelog on startup

**Re-partition:**
- Operations that change keys (`map()`, `selectKey()`) trigger automatic creation of a repartition topic to re-route records to correct tasks

**Joins:**
- **Inner join** — both sides must have matching key
- **Left join** — always emits left-side record; right side may be null
- **Outer join** — emits for either side; missing side is null
- Supports: KStream-KStream, KTable-KTable, KStream-KTable, KStream-GlobalKTable

**Windowing (for KStream-KStream joins):**
- **Hopping** — fixed width, overlapping (step < width)
- **Tumbling** — fixed width, non-overlapping (step = width)
- **Sliding** — event-driven step

**Co-partitioning requirements for joins:**
- Same key semantics
- Same number of partitions
- Same partitioning algorithm on producer side

**Sub-topologies:** break topology into chunks using `repartition()` to increase parallelism

**Startup sequence:** build topology → create KafkaStreams → create StreamThreads (each with own consumer/producer) → assign tasks via `StreamPartitionAssignor` → start all threads

**Topology visualization:** `Topology#describe()` — output can be pasted into `https://zz85.github.io/kafka-streams-viz/`

**Custom processors:**
```java
inputStream.process(MyProcessor::new).to("outputTopic");
// MyProcessor implements Processor<KIn, VIn, KOut, VOut>
// override init(), process(Record), close()
```

**Testing:** `TopologyTestDriver` — in-memory test driver; create `TestInputTopic` / `TestOutputTopic`, pipe records, assert output

**Exercises:**
- 8.1 — Simple pass-through stream (source → sink)
- 8.2 — Filtered stream (filter on message content)
- 8.3 — Mapped stream (replace characters with `mapValues`)
- 8.4 — GroupBy + count (re-key by vocal/consonant, count, output to topic)
- 8.5 — Joined streams (two input topics, re-key, join with 10s window)
- 8.6 — Custom processor (buffer records, flush on timer, concatenate values)

---

## 09 - Kafka Connect

**Purpose:** Introduces Kafka Connect for scalable, configuration-driven data integration.

**Key topics:**

**What is Kafka Connect:**
- Framework for streaming data between Kafka and external systems (databases, files, APIs)
- Avoids writing custom producers/consumers for common integrations
- Configured via JSON POSTed to REST API at `localhost:8083`

**Architecture:**
- **Connectors** — define the integration (source or sink)
- **Tasks** — parallelized units of work within a connector
- **Workers** — JVM processes that run connectors and tasks
- **Converters** — serialize/deserialize data (Avro, JSON, String)

**Built-in connectors (for exercises):**
- `FileStreamSourceConnector` — reads from a file, produces to a topic
- `FileStreamSinkConnector` — consumes from a topic, writes to a file

**Debezium CDC connector for PostgreSQL:**
- Uses WAL (Write-Ahead Log) via `pgoutput` replication protocol
- Captures INSERT/UPDATE/DELETE as Kafka events
- Connector class: `io.debezium.connector.postgresql.PostgresConnector`

**Debezium SMT (Single Message Transforms):**

| Transform | Purpose |
|---|---|
| Topic Routing | Route events to different topics |
| Content-Based Routing | Filter/route on message content |
| New Record State Extraction | Extract only the `after` state |
| **Outbox Event Router** | Implement transactional outbox pattern |
| Message Filtering | Drop unwanted events |

**Outbox pattern:**
- Write events to an `outbox` DB table in the same transaction as domain changes
- Debezium captures the outbox table via CDC
- Outbox Event Router SMT transforms and routes events to appropriate topics
- Guarantees exactly-once event publishing without distributed transactions

**Exercises:**
- 9.1 — File source connector
- 9.2 — File sink connector
- 9.3 — Inspect Connect REST API (`/connectors`, `/connector-plugins`, `/tasks`)
- 9.4 — Postgres Debezium CDC connector
- 9.5 — Inspect CDC events
- 9.6 — New Record State Extraction SMT
- 9.7 — Outbox Event Router SMT

---

*This summary covers all 12 slide decks in the course. Slide 04 (Broker) could not be fully summarized due to PDF rendering limitations (16.4 MB file).*

---

## Missing and Outdated Topics (as of 2025)

The slides were created around 2022. The following topics are either absent or significantly outdated and should be added or updated before the course is run again.

---

### 1. KRaft Mode — ZooKeeper is Gone

**Status in slides:** Mentioned as a footnote ("KRaft is the new protocol"). All exercises still use ZooKeeper.

**What changed:**
- KRaft became production-ready in **Kafka 3.3** (September 2022)
- ZooKeeper support was **fully removed in Kafka 4.0** (March 2025)
- All new deployments must use KRaft — there is no ZooKeeper option anymore

**What to add:**
- Dedicated slide explaining KRaft: Raft consensus protocol, controller quorum, combined vs. isolated controller nodes
- Updated Docker Compose setup using KRaft-only images (no ZooKeeper container)
- Migration path from ZooKeeper to KRaft (for students who manage existing clusters)
- `kafka-storage.sh format` for initialising a KRaft cluster

---

### 2. Schema Registry and Schema Evolution

**Status in slides:** Referenced in Connect exercises (Avro converter mentioned), but no dedicated module exists. The `12 - Schema registry.pdf` file is absent from the repository.

**What to add:**
- Confluent Schema Registry (or Apicurio): what it is and why it matters
- Supported formats: **Avro**, **Protobuf**, **JSON Schema**
- Schema compatibility modes: `BACKWARD`, `FORWARD`, `FULL`, `NONE`
- How producers register schemas; how consumers resolve them
- Spring Kafka with `KafkaAvroSerializer` / `KafkaAvroDeserializer`
- Schema evolution rules and breaking vs. non-breaking changes
- Local setup with `confluentinc/cp-schema-registry` Docker image

---

### 3. Security

**Status in slides:** Not covered at all. The course uses an open, unauthenticated local cluster throughout.

**What to add:**
- **Encryption:** SSL/TLS between clients and brokers (`ssl.keystore`, `ssl.truststore`)
- **Authentication (SASL):**
  - `SASL/PLAIN` — simple username/password (good for dev)
  - `SASL/SCRAM-SHA-256/512` — hashed credentials stored in ZooKeeper/KRaft
  - `SASL/OAUTHBEARER` — token-based, integrates with identity providers
  - `SASL/GSSAPI` (Kerberos) — enterprise/AD environments
- **Authorization:** ACLs with `kafka-acls.sh`; role-based access in Confluent Platform
- **Encryption at rest:** broker-level and storage-level options
- Spring Kafka SSL/SASL configuration properties

---

### 4. Error Handling and Failure Patterns

**Status in slides:** The `11 - Failures.pdf` file is absent. Error handling is barely mentioned (DLQ referenced in EDA module, `ack.nack()` in Spring Kafka).

**What to add:**
- **Poison pill messages** — records that always fail processing; how to detect and handle
- **Spring Kafka `DefaultErrorHandler`** (replaced `SeekToCurrentErrorHandler` in Spring Kafka 2.8 / Spring Boot 2.7):
  - Configurable retry with `FixedBackOff` / `ExponentialBackOffWithMaxRetries`
  - `DeadLetterPublishingRecoverer` — route failed records to a `.DLT` topic
- **Dead Letter Topic (DLT) pattern** in detail: headers, routing, monitoring, reprocessing
- **Non-retryable vs. retryable exceptions** — how to classify and configure
- **`@RetryableTopic`** annotation (Spring Kafka 2.7+) for non-blocking retries with multiple retry topics
- Consumer-side idempotency — handling duplicate delivery after rebalance

---

### 5. Observability and Monitoring

**Status in slides:** Listed as a challenge in 01.B (Communication) but never addressed technically.

**What to add:**
- **Key metrics to monitor:**
  - Consumer lag (`records-lag-max`) — most critical operational metric
  - Producer request latency and error rate
  - Broker under-replicated partitions
  - ISR shrink/expand rate
- **JMX metrics** — Kafka exposes metrics via JMX; how to access them
- **Prometheus + Grafana stack:**
  - `kafka-exporter` or `jmx_exporter` to scrape JMX metrics into Prometheus
  - Standard Grafana dashboards for Kafka
- **Consumer lag tools:** `kafka-consumer-groups.sh --describe`, Burrow, AKHQ lag view
- **Distributed tracing** — propagating trace context through Kafka message headers (OpenTelemetry)
- **Logging best practices** — correlating consumer processing logs with Kafka offsets

---

### 6. Exactly-Once Semantics (EOS) in Depth

**Status in slides:** Idempotent producer is covered. Transactions are mentioned in the producer pipeline diagram but not explained. EOS at the consumer side is listed but not detailed.

**What to add:**
- **Kafka Transactions API:** `initTransactions()`, `beginTransaction()`, `commitTransaction()`, `abortTransaction()`
- **`transactional.id`** config and what it enables
- **`isolation.level=read_committed`** on the consumer — only reads committed transaction data
- **End-to-end EOS** with Kafka Streams (`processing.guarantee=exactly_once_v2`)
- Performance cost of transactions vs. at-least-once
- When EOS is worth the complexity vs. idempotent application design

---

### 7. ksqlDB

**Status in slides:** Shown in the Kafka Streams API pyramid diagram but not covered.

**What to add:**
- What ksqlDB is: SQL engine built on top of Kafka Streams
- `CREATE STREAM`, `CREATE TABLE`, `SELECT`, `JOIN`, `GROUP BY`, `WINDOW` over Kafka topics
- Persistent queries vs. push queries vs. pull queries
- When to use ksqlDB vs. Kafka Streams DSL: ease of use vs. flexibility
- Local setup with `confluentinc/ksqldb-server` Docker image
- REST API and CLI (`ksql`)

---

### 8. Cloud-Managed Kafka

**Status in slides:** Not covered — course is entirely self-hosted/local.

**What to add:**
- **Confluent Cloud** — fully managed Kafka; pricing model (CKUs), connectors, Schema Registry, ksqlDB
- **Amazon MSK** — managed Kafka on AWS; IAM authentication, MSK Connect
- **Azure Event Hubs** — Kafka-compatible endpoint; useful for Azure-native architectures
- **Aiven for Kafka** — multi-cloud managed Kafka
- Trade-offs: operational burden vs. cost vs. feature parity with self-hosted
- Key differences students will encounter moving from local Docker to cloud (bootstrap servers, auth, TLS)

---

### 9. Updated Spring Kafka / Spring Boot 3

**Status in slides:** Uses Spring Boot 2.x era APIs. Several things have changed.

**What to update:**
- **Spring Boot 3.x** requires Java 17 minimum; `jakarta.*` namespace replaces `javax.*`
- **Spring Kafka 3.x** changes:
  - `DefaultErrorHandler` is now the default (not `SeekToCurrentErrorHandler`)
  - `@RetryableTopic` for non-blocking retry patterns
  - `RecordInterceptor` and `BatchInterceptor` for cross-cutting concerns
  - Improved `KafkaTemplate` API with `CompletableFuture` (replaces `ListenableFuture`)
- **Observability:** Spring Boot 3 Actuator + Micrometer auto-instruments Kafka producers and consumers
- **GraalVM native image** support — Spring Boot 3 + Kafka can compile to native binaries

---

### 10. Kafka Connect — Expanded Coverage

**Status in slides:** Good introduction, but missing operationally important topics.

**What to add:**
- **Distributed vs. standalone workers** — production uses distributed mode with multiple worker nodes
- **Connector lifecycle management:** pause, resume, restart individual tasks
- **Exactly-once in Connect** (`exactly.once.source.support` — Kafka 3.3+)
- **Common sink connectors:** JDBC sink, Elasticsearch, S3, MongoDB
- **Common source connectors:** JDBC source (polling), MongoDB CDC
- **Error handling in Connect:** `errors.tolerance`, `errors.deadletterqueue.topic.name`
- **SMT chaining** — applying multiple transforms in sequence

---

### 11. Tiered Storage (Kafka 3.6+)

**Status in slides:** Not mentioned.

**What to add:**
- What tiered storage is: offload older log segments to object storage (S3, Azure Blob, GCS)
- Enables very long retention without large broker disk requirements
- Configuration: `remote.log.storage.system.enable`, `remote.log.manager.task.interval.ms`
- Use case: compliance/audit log retention, cost reduction

---

### 12. Updated Docker and Tooling

**Status in slides:** Uses older `confluentinc/cp-kafka` images with ZooKeeper. `.bat` scripts are Windows-only.

**What to update:**
- Replace ZooKeeper-based Docker Compose with KRaft-only setup
- Use current image tags (not `latest` — pin to a specific version)
- Consider `apache/kafka` official image (introduced with Kafka 3.7) as an alternative to Confluent images
- Cross-platform scripts (bash + PowerShell) or a `Makefile`
- **AKHQ** is still a good UI choice but mention alternatives: Redpanda Console (works with Kafka), Confluent Control Center
- **Kafka UI** by Provectus as another popular open-source option

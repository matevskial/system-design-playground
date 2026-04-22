# Kafka Playground

This readme will contain information about the module and submodules under this module,
as well as basic knowledge notes about Kafka.


## Module organization

There should be modules that represent kafka producer, kafka consumer, working with kafka streams or
application that makes use of kafka.

## How to run

Before running ay application under this module:
* run the docker compose located in the folder `docker-compose-local/kafka-single-node` at the root this repository

Then compile and run the application either from the command line or using IDE such as  IntelliJ.
Note: A run configuration for Intellij is stored in the folder `.run` at the root of this repository for each application.

# Kafka general notes

Open console inside running kafka broker

```bash
docker exec --workdir /opt/kafka/bin/ -it system-design-playground-kafka-single-node-broker-controller-1 sh
```

Create topic:

```bash
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic test-topic
```

Alter topic(increase partition count for example):

```bash
./kafka-topics.sh --topic test-topic --bootstrap-server localhost:9092 --alter --partitions 4
```

Open console consumer:

```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning
```

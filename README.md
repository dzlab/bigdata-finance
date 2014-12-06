Starting Zookeeper
`$ ./bin/zookeeper-server-start.sh config/zookeeper.properties`

Starting Kafka
`$ ./bin/kafka-server-start.sh config/server.properties`

Create Kafka topic
`$ ./bin/kafka-topics.sh --create --topic forex_events --zookeeper localhost:2181 --replication-factor 1 --partitions 1`

Check the list of created topics
`$ ./bin/kafka-topics.sh --list --zookeeper localhost:2181`

Manually reading all events sent to this topic
`$ ./bin/kafka-console-consumer.sh --topic forex_events --from-beginning --zookeeper localhost:2181`

Clearing the queue
1. Stop Kafka daemon
2. rm -rf /tmp/kafka-logs/MyTopic-0
3. Delete the topic metadata: zkCli.sh then rmr /brokers/MyTopic
4. Start Kafka daemon

Building samza jobs
mvn clean package
```
mkdir -p deploy/samza
tar -xvf samza-job-package-1.0-SNAPSHOT-dist.tar.gz -C deploy/samza/
./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/forex-engine.properties
```
Stop a samza Application Master
`yarn application -kill <application_id>`

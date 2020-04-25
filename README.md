# Kafka-Streams-Twitter-Example
This project contains an example that demonstrates how to gather data from Twitter, ingest them using kafka connect in distributed mode and create a stream from them that calculates the number of tweets about kafka ecosystem per country using Kafka Streams and writes the results back to kafka.
## Getting started 
These instructions will get you a copy of the project up and running on your local machine.
### Prerequisites
#### - docker
Get the Docker engine community edition following the steps in the official documentation [here](https://docs.docker.com/install/linux/docker-ce/ubuntu/).<br/>
This Setup was tested on Ubuntu 18.04. 
#### - docker-compose 
Install docker compose which relies on docker engine following the steps [here](https://docs.docker.com/compose/install/).
#### - Maven
Install Apache Maven following the steps [here](https://maven.apache.org/install.html)
#### - Java 8 JDK
Get Java 8 JDK from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
### Setup
In this project we will use the fast-data-dev docker image from landoop; full fledged Kafka installation that includes Kafka Connect, Schema Registry, Lenses.io's Stream Reactor 25+ Connectors and intuitive UIs. 
1. Deploy services by running the following command after changing to the directory where you have copied the docker compose file:
```
docker-compose up -d 
```
2. Create the topic we're going to write the tweets to
```
docker exec -it kafka-cluster bash
kafka-topics --create --topic tweets-input --partitions 3 --replication-factor 1 --zookeeper 127.0.0.1:2181
```
3. Create a Twitter Application
  - Go [here](https://developer.twitter.com/) and apply for a twitter developper account if you don't already have one
  - Create a New App
  - Fill in an application name & description & web site and accept the developer agreement. 
  - Click on Create my access token
4. Populate the file [source-twitter.properties](source-twitter.properties) with:
  - consumer key
  - consumer secret 
  - access token
  - access token secret 
5. Setup the Twitter connector

- Setup instructions for the connector could be found at https://github.com/Eneco/kafka-connect-twitter
- You can also create the connector by accessing the connectors UI at ``http://127.0.0.1:8003`` and apply the configurations at the properties file. 

6. Start a console consumer on the topic
```
docker exec -it kafka-cluster bash
kafka-console-consumer --topic tweets-input --bootstrap-server 127.0.0.1:9092 
```
7. Launch the connector and you will see the data flowing in
8. Create the topic we're going to write the results of the streaming application to
```
docker exec -it kafka-cluster bash
kafka-topics --create --topic tweets-output --partitions 3 --replication-factor 1 --zookeeper 127.0.0.1:2181
```
9. Run our KStreams Application
 - Package the application into a fat jar which contains the code + the dependencies by running:
 ```
 mvn clean package
 ```
 - Run application using jar:
 ```
 java -jar target/name_of_jar
 ```
## Built with
* Java 
## Authors 
* [Firas Esbai](https://github.com/firasesbai) 
## Licence 
This project is licenced under the [Apache-2.0 Licence](https://www.apache.org/licenses/LICENSE-2.0)

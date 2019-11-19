import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

public class KafkaMentions {

    public static void main(String[] args) {

        Properties conf = new Properties();
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, "twitter-input");
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        conf.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        conf.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        conf.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();


        // 1- Stream from kafka
        KStream<String, String> tweets = builder.stream("twitter-input");
        // 2- Flatmap values split by ','
        KTable<String, Long> locationCounts= tweets.flatMapValues(value -> Arrays.asList(value.split(",")))
        // 3- Remove tweets of length lower than 8 to avoid exceptions
                .filter((key,value )-> value.length() > 8)
        // 4- Select location field from tweets
                .filter((key,value) -> value.substring(1,9).equals("location"))
        // 5- Select key to apply and remove quotation
                .selectKey((key,value)->Arrays.asList(value.split(":")).get(1).replaceAll("^\"|\"$", ""))
        // 6- Remove null values
                .filterNot((key,value) -> key.equalsIgnoreCase("null"))
        // 7- group by key before aggregation
                .groupByKey()
        // 8- count occurences
                .count();
        // 9- print results on the standard output
        //locationCounts.toStream().foreach((key, value) -> System.out.println(key + " => " + value));

        // 10- to in order to write the results back to kafka
        locationCounts.toStream().to("tweets-output-2", Produced.with(Serdes.String(),Serdes.Long()));
        KafkaStreams streams = new KafkaStreams(builder.build(), conf);
        streams.start();
        // print the topology
        System.out.println(streams.toString());

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}

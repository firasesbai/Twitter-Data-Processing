package com.github.firasesbai.spark

import com.github.firasesbai.spark.Utilities._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._


object PopularHashtagTweets {

  /** Our main function where the action happens */
  def main(args: Array[String]) {

    // Configure Twitter credentials using the file twitter under data folder
    setupTwitter()

    // Set up a Spark streaming context named "PopularHashtagTweets" that runs locally using
    // all CPU cores and one-second batches of data
    val ssc = new StreamingContext("local[*]", "PopularHashtagTweets", Seconds(1))

    // Get rid of log spam (should be called after the context is set up)
    setupLogging()

    // Create a DStream from Twitter using our streaming context
    val tweets = TwitterUtils.createStream(ssc, None)

    // Now extract the text of each status update into RDD's using map()
    val statuses = tweets.map(status => status.getText())

    // Extract each word
    val words = statuses.flatMap(input => input.split(" "))

    // Filter out only hashtags
    val hashtags = words.filter(word => word.startsWith("#"))

    // Map each hashtag to a key/value pair of (hashtag, 1) so we can count them up by adding up the values
    val hashtagKeyValues = hashtags.map(hashtag => (hashtag, 1))

    // count them up over a 5 minute window sliding every one second
    val hashtagCounts = hashtagKeyValues.reduceByKeyAndWindow( _ + _, _ -_, Seconds(300), Seconds(1))

    // Print out the first ten
    hashtagCounts.print

    // Create a checkpoint
    ssc.checkpoint("./checkpoint/")
    // start stream
    ssc.start()
    ssc.awaitTermination()
  }

}

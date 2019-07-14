import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}


object Consumer {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("Streaming live data").master("local[*]").getOrCreate()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(2))

    val cwd = System.getProperty("user.dir")
    println(cwd)
    val checkpoints = cwd + "/src/main/Checkpoints"
    try {
      new java.io.File(checkpoints).mkdirs
    } catch {
      case exception: Exception => println("Unable to create folder as ", exception)
    }
    val kafka_parms = Map("bootstrap.servers" -> "localhost:9092",
      "key.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer",
      "group.id"-> "group1",
      "client.id"-> "consumer-1"
    )
    val topic = List("TopicTest7").toSet

//    val lines = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String, String](topic, kafka_parms))
//    lines.foreachRDD(x =>
//        println(x.collect().toString)
//    )

    val df = spark
      .readStream
      .format("kafka")
      .options(Map("kafka.bootstrap.servers"-> "localhost:9092",
        "subscribe"-> "TopicTest2",
        "key.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer",
        "group.id"-> "group1",
        "client.id"-> "consumer-1"
        )
      ).load()

    df.writeStream
      .format("console")
      .option("truncate","false")
      .start()
      .awaitTermination()



  }
}
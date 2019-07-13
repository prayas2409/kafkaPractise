import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Consumer {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("Streaming live data").master("local[*]").getOrCreate()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(2))

//    val cwd = System.getProperty("user.dir")
//    println(cwd)
//    val checkpoints = cwd + "/src/main/Checkpoints"
//    try {
//      new java.io.File(checkpoints).mkdirs
//    } catch {
//      case exception: Exception => println("Unable to create folder as ", exception)
//    }
//    val kafka_parms = Map("meta.broker.list" -> "localhost:9092")
//    val topic = List("TopicTest1").toSet
//
//    //    val lines = KafkaUtils.
//    //    val rdd = KafkaUtils.createRDD[String, String](spark.sparkContext,"" ,kafka_parms, PreferConsistent)
//    //    val lines = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String, String](topic, kafka_parms))
//
//    val ds1 = spark.readStream.format("kafka").option("kafka.bootstrap.servers", "localhost:9092").option("subscribe","TopicTest1").load()
//    ds1.show()
//


    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9095")
      .option("subscribe", "TopicTest2")
      .load()

    df.writeStream
      .format("console")
      .option("truncate","false")
      .start()
      .awaitTermination()
  }
}
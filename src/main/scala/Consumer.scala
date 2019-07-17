import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import play.libs.Json

//import play.api.http.MediaRange.parse

object Consumer {

  def printer(df: DataFrame):DataFrame= {
    df.select("4. close").show()
    df
  }

  def main(args: Array[String]): Unit = {
//    val fields = List("4. close","5. volume","1. open","2. high","3. low")
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

    // Adding the parameters
    val kafka_parms = Map(
      "bootstrap.servers" -> "localhost:9092", // the brokers
      "key.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer", // serialize ments to convert to byte stream
      "value.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer",
      "group.id"-> "group5", // clients can take
      "client.id"-> "consumer4"
    )
    val topic = List("TopicTest30").toSet
    val lines = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String, String](topic, kafka_parms))
//    lines.foreachRDD(x => println(x.collect().toString))
    var xi = Json.parse("""{"a":"b" }""")
    val json_data = lines.map(record=>(record.value().toString))
    json_data.foreachRDD(
      x =>
       spark.sqlContext.read.json(x).rdd.collect().foreach(
         y =>
          spark.sparkContext.makeRDD(List(y.toString().replaceAll(","," ").replace("[","").replace("]","")))
           .pipe("/home/admin1/IdeaProjects/kafkaexecute/src/main/python/Predictor.py").collect().foreach(println)
       )
//         .foreach(y=>
//         spark.sparkContext.makeRDD((List(y.getLong(0)+" "+y.getLong(1)+" "+y.getLong(2)+" "+y.getLong(3)))).pipe("/home/admin1/IdeaProjects/kafkaexecute/src/main/scala/vectorizer_model.py").collect().foreach(println)
//          .foreach(
//            y=>
//          spark.sparkContext.makeRDD(Row(y(0),y(4),y(3),y(1)).toString()).pipe("/home/admin1/IdeaProjects/kafkaexecute/src/main/scala/vectorizer_model.py").collect().foreach(println)
//              )
    )


//    json_data.toString().foreach(x =>
//
//      //    Json.parse(x.toString.replace("(","").replace(")","")).toString().foreach(println)
//      println("hiw",x.toString.split(":"))
//    )
//    var values = ""
//    fields.foreach( x =>
//      values += j(x)+" "
//    )
//    val s =spark.sparkContext.makeRDD(values).pipe("/home/admin1/IdeaProjects/kafkaexecute/src/main/scala/vectorizer_model.py")
//    s.collect().foreach(println)
    //    foreachRDD(x => x.pipe("/home/admin1/IdeaProjects/kafkaexecute/src/main/scala/vectorizer_model.py"))
//    json_data.foreachRDD(x=>println(x.collect()))
//   var values =""
//    fields.foreach(x =>
//      values = values + json_data(x)+" "
//    )
//    for(i <- fields){
//      values = values + json_data(i)+" "
//    }
////    spark.sparkContext.makeRDD(values).pipe("")
//    println(values)

    ssc.start()
    ssc.awaitTermination()

////    using structured streaming
//    // reading the data from kafka
//    val df = spark
//      .readStream
//      .format("kafka")
//      .options(Map("kafka.bootstrap.servers"-> "localhost:9092",
//        "subscribe"-> "TopicTest18",
//        "key.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer",
//        "value.deserializer"->"org.apache.kafka.common.serialization.StringDeserializer",
//        "group.id"-> "group1",
//        "client.id"-> "consumer-1"
//        )
//      ).load()
//
//    // import implicits to make the $ symbol work
//    import spark.implicits._
//
//    val data_df = df.withColumn("Value", $"value".cast(StringType)).
//      withColumn("Key", $"key".cast(StringType))
//      .withColumn("Topic", $"topic".cast(StringType))
//      .withColumn("Offset", $"offset".cast(LongType))
//      .withColumn("Partition", $"partition".cast(IntegerType))
//      .withColumn("Timestamp", $"timestamp".cast(TimestampType))
//      .withColumn("Value", $"value".cast(StringType))
//      .select("Value")
//
//      val data_from_data_df = data_df.select("Value").toDF()
//
//    data_from_data_df.map(x=>
//        spark.read.json(x.toString())).foreach(y=>
//      spark.sparkContext.makeRDD(Row(y(0),y(4),y(3),y(1)).toString()).pipe("/home/admin1/IdeaProjects/kafkaexecute/src/main/scala/vectorizer_model.py").collect())
//    ssc.start()
//    ssc.awaitTermination()


    //
//      val z = xc.foreach(y=>
//        spark.sparkContext.makeRDD(Row(y(0),y(4),y(3),y(1)).toString()).pipe("/home/admin1/IdeaProjects/kafkaexecute/src/main/scala/vectorizer_model.py").collect()
//        //           )
//      )
//      z.toString.foreach(println)
//
//// The data to write stream
//      xc.writeStream.format("console")
//      .option("truncate","false")
//      .start()
//      .awaitTermination()
//    data_from_data_df.transform(x => printer(x.collect().toString))


  }
}
import java.io.DataOutputStream
import java.net.{InetAddress, Socket}
import java.util.Properties
//import play.api.http.MediaRange.parse
//import play.api.http.MediaRange.parse
import java.io.IOException

import org.json4s.jackson.JsonMethods.parse

import scala.io.Source
//import scala.swing.event.Key

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object SimpleProducer extends App{

  val time = "5min"
  val symbol = "MSFT"
  val apikey = "0D8RR9NDGU7URNQT"
  val label = s"Time Series ($time)"

  val response = Source.fromURL(s"https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=$symbol&interval=$time&outputsize=full&apikey=$apikey")
  val string = response.mkString.replaceAll("4. close","close")
    .replaceAll("1. open","open")
    .replaceAll("2. high","high")
    .replaceAll("5. volume","volume")
    .replaceAll("3. low","low")

  implicit val formats = org.json4s.DefaultFormats
  val j = play.api.libs.json.Json.parse(string)
  val s = j(label).toString()

  val ar = parse(string).extract[Map[String, Any]]
  val k1 = (parse(s).extract[Map[String, Any]].keys)

//  //    This part is to extract the keys out of the data.
//  val data = j(label)("2019-07-05 16:00:00")
//  var keys = ""
//  val fields = List("close","volume","open","high","low")
//  val newdata = data.toString().replace("{","").replace("}","").toString
//  val l = newdata.split(",")
//  val words = l.flatMap(_.split(":"))
//
//  fields.foreach( x =>
//    words.foreach(y =>
//      if (y.contains(x)){
//        keys += y+","
//      }
//    ))
//
//  keys = keys.replace("\"","")
//  val allkeys = keys.split(",")
//
//  var data1 = j(label)("2019-07-05 16:00:00")

  lazy val producer: KafkaProducer[String, String] = new KafkaProducer(getKafkaConfigProperties)
//  lazy val testEmpObjects:List[Employee] = (0 to 1000).map(x=>Employee("John"+x, x)).toList

//  testEmpObjects.foreach { emp =>
//    producer.send(new ProducerRecord[String, String]("TopicTest12", emp.id.toString, Employee.asJson(emp)))
//    Thread.sleep(1000)
//  }
  for (k <- k1) {
    producer.send(new ProducerRecord[String, String]("TopicTest30", j(label)(k).toString()))
    Thread.sleep(2000)
  }

  def from_url(): Unit={
    val time = "5min"
    val symbol = "MSFT"
    val apikey = "0D8RR9NDGU7URNQT"
    val label = s"Time Series ($time)"

    val response = Source.fromURL(s"https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=$symbol&interval=$time&outputsize=full&apikey=$apikey")
    val string = response.mkString

    implicit val formats = org.json4s.DefaultFormats
    val j = play.api.libs.json.Json.parse(string)
    val s = j(label).toString()

    val ar = parse(string).extract[Map[String, Any]]
    val k1 = (parse(s).extract[Map[String, Any]].keys)

    //    This part is to extract the keys out of the data.
    val data = j(label)("2019-07-05 16:00:00")
    var keys = ""
    val fields = List("close","volume","open","high","low")
    val newdata = data.toString().replace("{","").replace("}","").toString
    val l = newdata.split(",")
    val words = l.flatMap(_.split(":"))

    fields.foreach( x =>
    words.foreach(y =>
    if (y.contains(x)){
    keys += y+","
  }
    )
    )

    keys = keys.replace("\"","")
    val allkeys = keys.split(",")

    var data1 = j(label)("2019-07-05 16:00:00")
    val socket = new Socket(InetAddress.getByName("localhost"), 9019)
    val dOut = new DataOutputStream(socket.getOutputStream())

    for (k <- k1){
    data1 = j(label)(k)
    var values = ""
    allkeys.foreach(x =>
    values += data1(x)+" "
    )

    values = values.replace("\"","")
    try {
    println("trying to connect to socket sending values")
    println(values, values.getClass)
    dOut.writeUTF(values.replace(",","")+"\n")
    //        dOut.writeChars("\n")
    dOut.flush()
    Thread.sleep(1500)
  } catch {
    case e: IOException =>
    System.err.println(e)
  }  }}

  def getKafkaConfigProperties: Properties = {
    val config = new Properties()

    config.put("bootstrap.servers", "localhost:9092")
    config.put("group.id", "group5")
    config.put("client.id", "consumer4")
    config.put("enable.auto.commit", "true")
    config.put("session.timeout.ms", "10000")
    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    config
  }
}

case class Employee(name:String,id:Int)
object Employee {
  def asJson(e: Employee): String ={
    s"""{"name":"${e.name}", "id":"${e.id}"}"""
  }
}
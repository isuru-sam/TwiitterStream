package actor;

import akka.actor.{ Actor, ActorRef, Props }
import play.api.Logger
import play.api.libs.json.Json
class TwitterStreamer(out: ActorRef) extends Actor {
  /*def receive = { 
    case "subscribe" =>
      Logger.info("Received subscription from a client")
      println("here")
      out ! Json.obj("text" -> "Hello, world!")
         case _ => println("many")
  }*/
   def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
  
  
}
object TwitterStreamer {
  def props(out: ActorRef) = Props(new TwitterStreamer(out))
}
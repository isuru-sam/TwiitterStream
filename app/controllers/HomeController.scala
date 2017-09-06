package controllers

import javax.inject._
import play.api.mvc.Action
import play.api.mvc.Results.Ok
import play.api.mvc.Controller
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.oauth.{ ConsumerKey, RequestToken }
import play.api.Play.current
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws._
import play.api.libs.iteratee._
import play.api.Logger

import play.api.libs.json._
import play.extras.iteratees._
import play.api.mvc.WebSocket

import play.api.libs.streams.ActorFlow
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.mvc.WebSocket.MessageFlowTransformer
import actor.TwitterStreamer

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(implicit system: ActorSystem, materializer: Materializer,configuration: play.api.Configuration) extends Controller {
implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[String, JsValue]
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
Ok(views.html.index("Tweets"))
}

  /*def tweets = play.api.mvc.Action.async {   
    credentials.map {
      case (consumerKey, requestToken) =>
        WS
          .url("https://stream.twitter.com/1.1/statuses/filter.json")
          .sign(OAuthCalculator(consumerKey, requestToken))
          .withQueryString("track" -> "reactive")
          .get { response =>
            Logger.info("Status: " + response.status)
            loggingIteratee
          }.map { _ =>
            Ok("Stream closed")
          }
    }getOrElse {
  Future.successful(InternalServerError("No credentials are set"))
}
 }*/
   def tweets = WebSocket.accept[String, JsValue] { implicit request =>
     println("here")
    ActorFlow.actorRef(out => TwitterStreamer.props(out))
  }
   
  
   
   
/*  
def tweets2 = WebSocket.acceptWithActor[String, JsValue] {
request => out => TwitterStreamer.props(out)
}*/

  def replicateFeed = Action { implicit request => Ok
  //  Ok.feed(TwitterStreamer.subscribeNode)
  }
  

  

  def tweets2 = Action.async {
    credentials.map {
      case (consumerKey, requestToken) =>
        val (iteratee, enumerator) = Concurrent.joined[Array[Byte]]
        val jsonStream: Enumerator[JsObject] =
          enumerator &>
            Encoding.decode() &>
            Enumeratee.grouped(JsonIteratees.jsSimpleObject)
        val loggingIteratee = Iteratee.foreach[JsObject] { value =>
          Logger.info(value.toString)
        }
        jsonStream run loggingIteratee
        WS
          .url("https://stream.twitter.com/1.1/statuses/filter.json")
          .sign(OAuthCalculator(consumerKey, requestToken))
          .withQueryString("track" -> "reactive")
          .get { response =>
            Logger.info("Status: " + response.status)
            iteratee
          }.map { _ =>
            Ok("Stream closed")
          }
    }getOrElse {
  Future.successful(InternalServerError("No credentials are set"))
}
  }
  
  
  
     def loggingIteratee = Iteratee.foreach[Array[Byte]] { array =>Logger.info(array.map(_.toChar).mkString)}
  

  
    def credentials: Option[(ConsumerKey, RequestToken)] = for {
      apiKey <- configuration.getString("twitter.apiKey")
      apiSecret <- configuration.getString("twitter.apiSecret")
      token <- configuration.getString("twitter.token")
      tokenSecret <- configuration.getString("twitter.tokenSecret")
    } yield (
      ConsumerKey(apiKey, apiSecret),
      RequestToken(token, tokenSecret))
  

}
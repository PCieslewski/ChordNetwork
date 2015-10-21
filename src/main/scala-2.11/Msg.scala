import akka.actor.ActorRef

//This file contains all of the message definitions.
sealed trait Msg
case class FindSuccessor(key: Long, origSender: ActorRef) extends Msg

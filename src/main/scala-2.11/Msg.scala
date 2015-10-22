import akka.actor.ActorRef

//This file contains all of the message definitions.
sealed trait Msg
//case class FindSuccessor(key: Long, origSender: ActorRef) extends Msg
case class JoinOn(nodeTarget: ActorRef) extends Msg
case class Join(originalSender: ActorRef, id: Long) extends Msg
case class FindPredecessor(id: Long, originalSender: ActorRef) extends Msg
case class Predecessor(id: Long, originalSender: ActorRef) extends Msg

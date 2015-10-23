import akka.actor.ActorRef

//This file contains all of the message definitions.
sealed trait Msg
//case class FindSuccessor(key: Long, origSender: ActorRef) extends Msg
case class JoinOn(childId: Long) extends Msg
case class ConfirmJoinOn(oldNextNode: ActorRef, oldEndPosition: Long) extends Msg
case class FindPredecessor(id: Long, originalSender: ActorRef) extends Msg
case class Predecessor(id: Long, originalSender: ActorRef) extends Msg
case class InRange(id: Long) extends Msg
case class NewNode() extends Msg
case class LatchOntoParent(parentNode: ActorRef) extends Msg

case class FindParent(childId: Long) extends Msg
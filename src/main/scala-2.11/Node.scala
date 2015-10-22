import akka.actor._
import akka.pattern.{ask, pipe}
import Hasher.hash

import scala.collection.mutable
import scala.concurrent.Future

import scala.util._

class Node(name: String) extends Actor{

  import context.dispatcher

  val n: Long = hash(name)

  var nextNode: ActorRef = self

  var range: Range = new Range(0, Long.MaxValue >>> 1)

  var dataList: mutable.MutableList[Data] = new mutable.MutableList[Data]

  var fingerTable: Array[Finger] = Array.fill(62)(new Finger())


  def initFingerTable(): Unit = {

    for(k <- 0 to 61) {
      fingerTable(k).start = (((BigInt(n) + BigInt(2).pow(k)) % BigInt(2).pow(62))).toLong
    }

    for(k <- 0 to 60) {
      val interval: Range = new Range(fingerTable(k).start, fingerTable(k+1).start)
      fingerTable(k).range = interval
    }

    fingerTable(61).range = new Range(fingerTable(61).start, n)

    for(k <- 0 to 61) {
      fingerTable(k).predecessor = self
    }

//    for(k <- 0 to 61) {
//      println(fingerTable(k).range)
//    }

  }

//  def closestPrecedingFinger(id: Long): ActorRef = {
//
//  }


  def joinOn(parentNode: ActorRef) ={
    //both nodes have to update range and .next node
    //original changes range from his to new node, and makes .next new node
    //new node makes his range from him to (old)parent.next range, and makes (old)parent.next = his .next

  }

  def updateFingerTable() ={
    for(k <- 0 to 61) {
      val predecessor = findPredecessor(fingerTable(k).start);
      predecessor.onComplete {
        case Success(thePredecessor) => fingerTable(k).predecessor = thePredecessor
        case Failure(e) => println("Error :(")
      }
    }
  }

  def findPredecessor(id: Long): Future[ActorRef] = Future{
    val futurePredecessor = (self ? InRange(id))
    futurePredecessor.asInstanceOf[ActorRef]
  }

  def receive() ={

    case InRange(id: Long) => {
      if(range.contains(id)) {
        sender ! self
      }
      else {
        for(k <- 61 to 0 by -1) {
          if(fingerTable(k).range.contains(id)) {
            val futurePredecessor = fingerTable(k).predecessor ? InRange(id)
            futurePredecessor pipeTo sender
          }
        }
      }
    }

    case JoinOn(childId: Long) => {
      val oldNextNode = nextNode
      val oldEndPosition = range.getEnd()

      nextNode = sender
      range = new Range(n, childId) //n = parentId

      sender ! ConfirmJoinOn(oldNextNode, oldEndPosition)
    }

    case ConfirmJoinOn(oldNextNode: ActorRef, oldEndPosition: Long) => {
      nextNode = oldNextNode
      range =  new Range(n, oldEndPosition)
    }

    case Join(originalSender: ActorRef, id: Long) => {

    }

//    case FindPredecessor(id: Long, originalSender: ActorRef) => {
//      if(range.contains(id)) {
//        originalSender ! Predecessor(id, self)
//      }
//      else {
//        for (k <- 61 to 0 by -1) {
//          if (fingerTable(k).range.contains(id)) {
//            fingerTable(k).predecessor ! FindPredecessor(id, originalSender)
//          }
//        }
//      }
//    }



    case _ => {
      println("YO")
    }

  }

}

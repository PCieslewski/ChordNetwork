import akka.actor.{Actor, ActorRef}
import Hasher.hash

import scala.collection.mutable

class Node(name: String) extends Actor{

  val n: Long = hash(name)

  var nextNode: ActorRef = self

  var range: Range = null

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


  def join() ={

  }


  def receive() ={
    case JoinOn(nodeTarget: ActorRef) => {

    }

    case Join(originalSender: ActorRef, id: Long) => {

    }

    case FindPredecessor(id: Long, originalSender: ActorRef) => {
      if(range.contains(id)) {
        originalSender ! Predecessor(id, self)
      }
      else {
        for (k <- 61 to 0 by -1) {
          if (fingerTable(k).range.contains(id)) {
            fingerTable(k).predecessor ! FindPredecessor(id, originalSender)
          }
        }
      }
    }

    case _ => {
      println("YO")
    }

  }

}

import akka.actor.ActorRef
import Hasher.hash

import scala.collection.mutable

class Node(name: String) {

  val id: Long = hash(name)

  var nextNode: ActorRef = null

  var range: Range = null

  var dataList: mutable.MutableList[Data] = new mutable.MutableList[Data]

  var fingerTable: Array[Finger] = Array.fill(62)(new Finger())










}

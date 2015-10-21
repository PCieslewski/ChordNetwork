import akka.actor.ActorRef
import Hasher.hash

import scala.collection.mutable

class Node(name: String) {

  val id: Long = hash(name)

  var prevNode: ActorRef = null
  var nextNode: ActorRef = null

  var dataList: mutable.MutableList[Data] = new mutable.MutableList[Data]










}

class Range(start: Long, end: Long){

  def inRange(id: Long): Boolean ={

    //Case where the range is not on the boundary of 0 and MAX
    if(end > start){
      return inRange(start, end, id)
    }
    //Case where range is on the boundary
    else{
      return (inRange(start, Long.MaxValue >>> 1, id) | inRange(0, end, id))
    }

  }

  def inRange(start: Long, end: Long, id: Long): Boolean ={
    if(id > start && id < end){
      return true
    }
    else{
      return false
    }
  }

}

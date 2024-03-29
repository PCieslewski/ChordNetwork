import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._

class Node(name: String, bigDaddy: ActorRef) extends Actor{

  import context.dispatcher
  implicit val timeout = Timeout(60 seconds)

  val n: Long = Hasher.hash(name)
  println("New node joining system. Name: " + name)

  var prevNode: ActorRef = self

  var range: Range = new Range(n, n)

  var dataList: ArrayBuffer[Data] = new ArrayBuffer[Data]

  var fingerTable: Array[Finger] = Array.fill(62)(new Finger())

  var isBusy: Boolean = true

  var pointedToByFingers: ArrayBuffer[FingerPointer] = new ArrayBuffer[FingerPointer]


  initFingerTable()

  def initFingerTable() = {

    for(k <- 0 to 61) {
      fingerTable(k).start = ((BigInt(n) + BigInt(2).pow(k)) % BigInt(2).pow(62)).toLong
    }

    for(k <- 0 to 60) {
      val interval: Range = new Range(fingerTable(k).start, fingerTable(k+1).start)
      fingerTable(k).range = interval
    }
    fingerTable(61).range = new Range(fingerTable(61).start, n)

    for(k <- 0 to 61) {
      fingerTable(k).successor = self
    }

  }

  def updateOthers() = {
    for(i <- 1 to 62){

      var pid: Long = (BigInt(n) - BigInt(2).pow(i-1)).toLong
      if(pid < 0) {
        pid = pid + (Long.MaxValue >>> 1) + 1
      }

      prevNode ! UpdateOthers(pid, self, i, range)
//      for (k <- 61 to 0 by -1) {
//        if (fingerTable(k).range.contains(pid)) {
//          //fingerTable(k).successor ! UpdateOthers(pid, self, i, range)
//        }
//      }

    }
  }

  def receive() ={

    case DisplayRange() => {
      println("Name: " + name + " Range: " + range)
    }

    //This message is sent by a child node that is not in the circle to any node
    //Eventually, Parent() will get returned to the child
    case FindParent(childId: Long, originalNode: ActorRef) => {

      if(range.contains(childId)){
        if(isBusy){
          println("BUSY")
          context.system.scheduler.scheduleOnce(10 milliseconds, self, FindParent(childId, originalNode))
        }
        else {
          isBusy = true
          originalNode ! ParentResponse(prevNode, range)
        }
      }
      else{
        for (k <- 61 to 0 by -1) {
          if (fingerTable(k).range.contains(childId)) {
            fingerTable(k).successor ! FindParent(childId, originalNode)
          }
        }
      }

    }

    //Once you receive your parent, send the JoinOn along with your ID.
    case ParentResponse(parentPrev: ActorRef, parentOldRange: Range) => {

      //Update childs properties to reflect its position in circle
      prevNode = parentPrev
      range = new Range(parentOldRange.getStart(),n)

      //Initialize child finger table.
      for(k <- 0 to 61) {
        fingerTable(k).successor = sender
      }

      //Initiate Join
      sender ! JoinOn(n, range)
    }

    //This message is sent by the child and received by the parent.
    //This initiates the joining of a child node
    case JoinOn(childId: Long, childRange: Range) => {

      //Notify the previous node that there is a child incoming and that they need to update their finger table!
      prevNode ! ChildIncoming(sender, childRange)

      //Set our previous node to the child and update our range!
      prevNode = sender
      range = new Range(childId, n) //n = parentId

      for(k <- 0 to 61) {
        if(childRange.contains(fingerTable(k).start)){
          fingerTable(k).successor = sender
        }
      }

      //Set busy back to false to accept new nodes into the system.
      isBusy = false
      sender ! SetBusy(false)
      sender ! UpdateFingerTable()
      context.system.scheduler.scheduleOnce(100 milliseconds, sender, UpdateLogOthers())

    }

    case ChildIncoming(childRef: ActorRef, childRange: Range) => {
      for(k <- 0 to 61) {
        if(fingerTable(k).successor == self){
          fingerTable(k).successor = childRef
        }
        if(childRange.contains(fingerTable(k).start)){
          fingerTable(k).successor = childRef
        }
      }
    }

    case JoinSystem(node: ActorRef) => {
      node ! FindParent(n, self)
    }

    case SetBusy(flag: Boolean) => {
      isBusy = flag
    }

    //Asks a good node to help find where the new fingers should go
    case FindFingerSuccessor(id: Long, originalSender: ActorRef, fingerNumber: Int) => {
      if(range.contains(id)) {
        originalSender ! SuccessorResponse(self, fingerNumber)
      }
      else{
        for (k <- 61 to 0 by -1) {
          if (fingerTable(k).range.contains(id)) {
            fingerTable(k).successor ! FindFingerSuccessor(id, originalSender, fingerNumber)
          }
        }
      }
    }

    case SuccessorResponse(fingerSuccessor: ActorRef, fingerNumber: Int) => {
      fingerTable(fingerNumber).successor = fingerSuccessor
    }

    case UpdateFingerTable() => {
      if(isBusy){
        context.system.scheduler.scheduleOnce(10 milliseconds, self, UpdateFingerTable())
      }
      else {
        for (k <- 0 to 61) {
          if (range.contains(fingerTable(k).start)) {
          }
          else {
            self ! FindFingerSuccessor(fingerTable(k).start, self, k)
          }
        }
      }
    }

    case UpdateOthers(id: Long, originalSender: ActorRef, indexOfFinger: Int, newRange: Range) => {
      if(range.contains(id)) {
        prevNode ! UpdateSingleFinger(id, originalSender, indexOfFinger, newRange)
      }
      else{
        prevNode ! UpdateOthers(id, originalSender, indexOfFinger, newRange)
      }
      bigDaddy ! HeartBeat()
    }

    case UpdateLogOthers() => {
      updateOthers()
    }

    case UpdateSingleFinger(id: Long, originalSender: ActorRef, indexOfFinger: Int, newRange: Range) => {
      if(newRange.contains(fingerTable(indexOfFinger-1).start)) {
        fingerTable(indexOfFinger - 1).successor = originalSender
        prevNode ! UpdateSingleFinger(id, originalSender, indexOfFinger, newRange)
      }
      bigDaddy ! HeartBeat
    }


    case DisplayFingerTable() => {
       println("FINGER TABLE OF: " + name)
       for(k <- 0 to 61) {
         println("FINGER NUMBER " + k + ": " + fingerTable(k))
       }
    }

    case DisplayPreviousNode() => {
      println("Name: " + name + " Previous Node: " + prevNode)
    }

    case StoreData(newData: Data) => {
      if(range.contains(newData.id)) {
        dataList += newData
      }
      else {
        for(k <- 61 to 0 by -1) {
          if(fingerTable(k).range.contains(newData.id)) {
            fingerTable(k).successor ! StoreData(newData)
          }
        }
      }
      bigDaddy ! HeartBeat()
    }

    case QueryData(key: String, originalSender: ActorRef) => {
      val hashedKey = Hasher.hash(key)
      self ! QueryDataHelper(hashedKey, originalSender, 0)
    }

    case QueryDataHelper(id: Long, originalSender: ActorRef, numberOfBounces: Int) => {
      if(range.contains(id)) {
        for(k <- dataList.indices) {
          if(id == dataList(k).id) {
            originalSender ! QueryResponse(dataList(k), numberOfBounces)
          }
        }
      }
      else {
        val updatedBounceNumber = numberOfBounces + 1
        for(k <- 61 to 0 by -1) {
          if(fingerTable(k).range.contains(id)) {
            fingerTable(k).successor ! QueryDataHelper(id, originalSender, updatedBounceNumber)
          }
        }
      }
      bigDaddy ! HeartBeat()
    }

    case _ => {
      println("Node got a strange message.")
    }

  }

}

import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util._

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._

import scala.util._

class Node(name: String) extends Actor{

  import context.dispatcher
  implicit val timeout = Timeout(60 seconds)

  val n: Long = Hasher.hash(name)
  println(name + " " + n + " " + self)

  var prevNode: ActorRef = self

  var range: Range = new Range(n, n)

  var dataList: mutable.MutableList[Data] = new mutable.MutableList[Data]

  var fingerTable: Array[Finger] = Array.fill(62)(new Finger())

  var isBusy: Boolean = true

  var pointedToByFingers: mutable.ArrayBuffer[FingerPointer] = new mutable.ArrayBuffer[FingerPointer]


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

////TODO
//  def join(nodeInCircle: ActorRef) = {
//
//    val futureParent = findParent(nodeInCircle)
//    futureParent.onComplete {
//      case Success(parent) => {
//        joinOn(parent)
//      }
//      case Failure(e) => println(e.printStackTrace())
//    }
////    var predecessor = findPredecessor()
//
//  }
//
//  def joinOn(parentNode: ActorRef) ={
//    //both nodes have to update range and .next node
//    //original changes range from his to new node, and makes .next new node
//    //new node makes his range from him to (old)parent.next range, and makes (old)parent.next = his .next
//
//    //maybe also update finger tables when we do join
//    parentNode ! JoinOn(n)
//  }

//  def updateFingerTable() ={
//    for(k <- 0 to 61) {
//      val predecessor = findPredecessor(fingerTable(k).start);
//      predecessor.onComplete {
//        case Success(thePredecessor) => fingerTable(k).predecessor = thePredecessor
//        case Failure(e) => println("Error :(")
//      }
//    }
//  }

//  def findPredecessor(id: Long): Future[ActorRef] = Future{
//    val futurePredecessor = self ? InRange(id)
//    futurePredecessor.asInstanceOf[ActorRef]
//  }
//
//  def findParent(nodeInCircle: ActorRef): Future[ActorRef] = Future{
//    val futureParent = nodeInCircle ? FindParent(n)
//    futureParent.asInstanceOf[ActorRef]
//  }

  def receive() ={

//    case InRange(id: Long) => {
//      if(range.contains(id)) {
//        sender ! self
//      }
//      else {
//        for(k <- 61 to 0 by -1) {
//          if(fingerTable(k).range.contains(id)) {
//            val futurePredecessor = fingerTable(k).predecessor ? InRange(id)
//            futurePredecessor pipeTo sender
//          }
//        }
//      }
//    }
//
//    case FindParent(childId: Long) => {
//      val futurePredecessor = findPredecessor(childId)
//      futurePredecessor pipeTo sender
//    }
//

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
          println(name + " -- sent parent response")
//          println("Name: " + name +
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

      //Initialize child finger table. NOT THOROUGH
      for(k <- 0 to 61) {
        if(range.contains(fingerTable(k).start)){
          fingerTable(k).successor = self
        }
        else{
          fingerTable(k).successor = sender
        }
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

      //Ensure that any fingers which may point to child are updated
      //COULD BE OPTIMIZED - i think
      for(k <- 0 to 61) {
        if(childRange.contains(fingerTable(k).start)){
          fingerTable(k).successor = sender
        }
      }

      //Set busy back to false to accept new nodes into the system.
      isBusy = false
      sender ! SetBusy(false)

    }

    case ChildIncoming(childRef: ActorRef, childRange: Range) => {
      for(k <- 0 to 61) {
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

      //TODO: refresh this
    case UpdateFingerTable() => {
      if(isBusy){
        println("BUSY (inside UpdateFingerTable)")
        context.system.scheduler.scheduleOnce(10 milliseconds, self, UpdateFingerTable())
      }
      else {
        for (k <- 0 to 61) {
          if (range.contains(fingerTable(k).start)) {
            fingerTable(k).successor = self
          }
          else {
            //could be fingerTable(k).successor VVV
            self ! FindFingerSuccessor(fingerTable(k).start, self, k)
          }
        }
      }
    }

    case DisplayFingerTable() => {
       println("FINGER TABLE OF: " + name)
       for(k <- 0 to 61) {
         println("FINGER NUMBER " + k + ": " + fingerTable(k))
       }
    }



//    case ConfirmJoinOn(oldNextNode: ActorRef, oldEndPosition: Long) => {
//      nextNode = oldNextNode
//      range =  new Range(n, oldEndPosition)
//      println("Confirmed join :)")
//      println(range)
//    }

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

//    case LatchOntoParent(parentNode: ActorRef) => {
//      join(parentNode)
//    }

   case DisplayPreviousNode() => {
    println("Name: " + name + " Previous Node: " + prevNode)
  }

    case _ => {
      println("Node got a strange message.")
    }

  }

}

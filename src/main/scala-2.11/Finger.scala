import akka.actor.ActorRef

class Finger() {

  var start: Long = 0
  var range: Range = null
  var successor: ActorRef = null

  override def toString(): String ={
    val resultString : String = "Start: " + start + " Range: " + range + " Successor: " + successor
    return resultString
  }

}

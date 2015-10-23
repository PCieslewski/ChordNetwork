import akka.actor._

import scala.collection.mutable.ArrayBuffer

class BigDaddy extends Actor{

  var networkNode: ArrayBuffer[ActorRef] = new ArrayBuffer()
  val nodeSystem = ActorSystem("NodeSystem")
  var nodeCount = 1

//  networkNode += nodeSystem.actorOf(Props(new Node("Bob" + nodeCount)), name = "Bob" + nodeCount)

  var will = nodeSystem.actorOf(Props(new Node("will" + nodeCount)), name = "will" + nodeCount)
  var pawel = nodeSystem.actorOf(Props(new Node("pawel" + nodeCount)), name = "pawel" + nodeCount)

  pawel ! JoinSystem(will)

  def receive = {
    case NewNode() => {
      nodeCount = nodeCount + 1
      networkNode += nodeSystem.actorOf(Props(new Node("Bob" + nodeCount)), name = "Bob" + nodeCount)
//      LatchOntoParent()
    }

  }

}

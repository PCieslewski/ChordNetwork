import akka.actor._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext._
class BigDaddy extends Actor{

  import context.dispatcher

  var networkNode: ArrayBuffer[ActorRef] = new ArrayBuffer()
  val nodeSystem = ActorSystem("NodeSystem")
  var nodeCount = 1

//  networkNode += nodeSystem.actorOf(Props(new Node("Bob" + nodeCount)), name = "Bob" + nodeCount)

  var will = nodeSystem.actorOf(Props(new Node("will" + nodeCount)), name = "will" + nodeCount)
  var pawel = nodeSystem.actorOf(Props(new Node("pawel" + nodeCount)), name = "pawel" + nodeCount)
  var ng38 = nodeSystem.actorOf(Props(new Node("ng38" + nodeCount)), name = "ng38" + nodeCount)
  var alex = nodeSystem.actorOf(Props(new Node("alex" + nodeCount)), name = "alex" + nodeCount)
  var bigJake = nodeSystem.actorOf(Props(new Node("bigJake" + nodeCount)), name = "bigJake" + nodeCount)

  will ! SetBusy(false) //Manually tell the first actor he isnt busy

  pawel ! JoinSystem(will)
  ng38 ! JoinSystem(pawel)
  alex ! JoinSystem(ng38)
  bigJake ! JoinSystem(ng38)

//  context.system.scheduler.scheduleOnce(2 seconds, ng38, JoinSystem(pawel))
//  ng38 ! JoinSystem(will)
//  context.system.scheduler.scheduleOnce(4 seconds, alex, JoinSystem(ng38))
//  alex ! JoinSystem(will)
//  context.system.scheduler.scheduleOnce(6 seconds, bigJake, JoinSystem(alex))
//
  context.system.scheduler.scheduleOnce(7 seconds, pawel, DisplayRange())
  context.system.scheduler.scheduleOnce(7 seconds, will, DisplayRange())
  context.system.scheduler.scheduleOnce(7 seconds, ng38, DisplayRange())
  context.system.scheduler.scheduleOnce(7 seconds, alex, DisplayRange())
  context.system.scheduler.scheduleOnce(7 seconds, bigJake, DisplayRange())

  def receive = {
    case NewNode() => {
      nodeCount = nodeCount + 1
      networkNode += nodeSystem.actorOf(Props(new Node("Bob" + nodeCount)), name = "Bob" + nodeCount)
//      LatchOntoParent()
    }

  }

}

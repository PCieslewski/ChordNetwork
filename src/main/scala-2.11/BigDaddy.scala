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

  bigJake ! SetBusy(false) //Manually tell the first actor he isnt busy

  pawel ! JoinSystem(bigJake)
  will ! JoinSystem(bigJake)
  alex ! JoinSystem(bigJake)
  ng38 ! JoinSystem(bigJake)

//  context.system.scheduler.scheduleOnce(1 seconds, pawel, JoinSystem(bigJake))
//  context.system.scheduler.scheduleOnce(1 seconds, will, JoinSystem(bigJake))
//  context.system.scheduler.scheduleOnce(1 seconds, alex, JoinSystem(bigJake))
//  context.system.scheduler.scheduleOnce(1 seconds, ng38, JoinSystem(bigJake))


//  context.system.scheduler.scheduleOnce(5 seconds, pawel, UpdateFingerTable())
//  context.system.scheduler.scheduleOnce(5 seconds, will, UpdateFingerTable())
//  context.system.scheduler.scheduleOnce(5 seconds, ng38, UpdateFingerTable())
//  context.system.scheduler.scheduleOnce(5 seconds, bigJake, UpdateFingerTable())
//  context.system.scheduler.scheduleOnce(5 seconds, alex, UpdateFingerTable())



//  context.system.scheduler.scheduleOnce(2 seconds, pawel, DisplayPreviousNode())
//  context.system.scheduler.scheduleOnce(2 seconds, will, DisplayPreviousNode())
//  context.system.scheduler.scheduleOnce(2 seconds, bigJake, DisplayPreviousNode())
//  context.system.scheduler.scheduleOnce(2 seconds, alex, DisplayPreviousNode())
//  context.system.scheduler.scheduleOnce(2 seconds, ng38, DisplayPreviousNode())

//  context.system.scheduler.scheduleOnce(1 seconds, pawel, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(2 seconds, will, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(3 seconds, ng38, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(4 seconds, alex, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(5 seconds, bigJake, DisplayFingerTable())

//  will ! UpdateFingerTable()
//  context.system.scheduler.scheduleOnce(2 seconds, pawel, UpdateFingerTable())
//  pawel ! UpdateFingerTable()
//  ng38 ! UpdateFingerTable()
//  alex ! UpdateFingerTable()
//  bigJake ! UpdateFingerTable()
//  will ! UpdateFingerTable()

//  alex ! UpdateFingerTable()
//  bigJake ! UpdateFingerTable()
//  context.system.scheduler.scheduleOnce(2 seconds, ng38, UpdateFingerTable())
//  context.system.scheduler.scheduleOnce(2 seconds, ng38, JoinSystem(pawel))
//  ng38 ! JoinSystem(will)
//  context.system.scheduler.scheduleOnce(4 seconds, alex, JoinSystem(ng38))
//  alex ! JoinSystem(will)
//  context.system.scheduler.scheduleOnce(6 seconds, bigJake, JoinSystem(alex))

//  context.system.scheduler.scheduleOnce(7 seconds, pawel, DisplayRange())
//  context.system.scheduler.scheduleOnce(7 seconds, will, DisplayRange())
//  context.system.scheduler.scheduleOnce(7 seconds, ng38, DisplayRange())
//  context.system.scheduler.scheduleOnce(7 seconds, alex, DisplayRange())
//  context.system.scheduler.scheduleOnce(7 seconds, bigJake, DisplayRange())

  context.system.scheduler.scheduleOnce(6 seconds, will, DisplayFingerTable())
  context.system.scheduler.scheduleOnce(7 seconds, pawel, DisplayFingerTable())
  context.system.scheduler.scheduleOnce(8 seconds, ng38, DisplayFingerTable())
  context.system.scheduler.scheduleOnce(9 seconds, alex, DisplayFingerTable())
  context.system.scheduler.scheduleOnce(10 seconds, bigJake, DisplayFingerTable())

  def receive = {
    case NewNode() => {
      nodeCount = nodeCount + 1
      networkNode += nodeSystem.actorOf(Props(new Node("Bob" + nodeCount)), name = "Bob" + nodeCount)
//      LatchOntoParent()
    }

  }

}

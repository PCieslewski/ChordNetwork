import akka.actor._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext._
class BigDaddy extends Actor{

  import context.dispatcher

  var networkNodes: ArrayBuffer[ActorRef] = new ArrayBuffer()
  val nodeSystem = ActorSystem("NodeSystem")
  var nodeCount = 1

  var heartBeatsRecevied = 0
  var heartBeatsOld = 0

  var originalNode = nodeSystem.actorOf(Props(new Node("PapaJ" + nodeCount, self)), name = "PapaJ" + nodeCount)
  originalNode ! SetBusy(false)

//  networkNode += nodeSystem.actorOf(Props(new Node("Bob" + nodeCount)), name = "Bob" + nodeCount)

//  var will = nodeSystem.actorOf(Props(new Node("will" + nodeCount, self)), name = "will" + nodeCount)
//  var pawel = nodeSystem.actorOf(Props(new Node("pawel" + nodeCount, self)), name = "pawel" + nodeCount)
//  var ng38 = nodeSystem.actorOf(Props(new Node("ng38" + nodeCount, self)), name = "ng38" + nodeCount)
//  var alex = nodeSystem.actorOf(Props(new Node("alex" + nodeCount, self)), name = "alex" + nodeCount)
//  var bigJake = nodeSystem.actorOf(Props(new Node("bigJake" + nodeCount, self)), name = "bigJake" + nodeCount)
//
//  bigJake ! SetBusy(false) //Manually tell the first actor he isnt busy
//
//  pawel ! JoinSystem(bigJake)
//  will ! JoinSystem(bigJake)
//  alex ! JoinSystem(bigJake)
//  ng38 ! JoinSystem(bigJake)



  context.system.scheduler.scheduleOnce(1 seconds, self, IsSystemBuilt())

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

//  context.system.scheduler.scheduleOnce(6 seconds, will, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(7 seconds, pawel, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(8 seconds, ng38, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(9 seconds, alex, DisplayFingerTable())
//  context.system.scheduler.scheduleOnce(10 seconds, bigJake, DisplayFingerTable())

  for(k <- 0 to 100) {
    context.system.scheduler.scheduleOnce(k*100 milliseconds, self, NewNode())
    //      bigDaddy ! NewNode()
  }

  def receive = {
    case NewNode() => {
      println("making new node")
      nodeCount = nodeCount + 1
      val newNode = nodeSystem.actorOf(Props(new Node("Bob" + nodeCount, self)), name = "Bob" + nodeCount)
      networkNodes += newNode
      newNode ! JoinSystem(originalNode)
//      LatchOntoParent()
    }

      case IsSystemBuilt() => {
        if(heartBeatsRecevied == heartBeatsOld) {
          println("System has been built")
          //context.system.scheduler.scheduleOnce(1 seconds, networkNodes(1), DisplayFingerTable())
        }
        else {
          heartBeatsOld = heartBeatsRecevied
          context.system.scheduler.scheduleOnce(1 seconds, self, IsSystemBuilt())
        }
      }

    case HeartBeat() => {
      heartBeatsRecevied += 1
      println("Beat")
    }

  }

}

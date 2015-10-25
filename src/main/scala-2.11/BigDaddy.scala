import akka.actor._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext._
class BigDaddy(numNodesPassed: Int, numRequestsPassed: Int) extends Actor{

  import context.dispatcher

  val numNodes = numNodesPassed
  val numRequests = numRequestsPassed
  val delayBetweenNodeInsertion = 50
  val numberOfMessagesToStore = 200

  var networkNodes: ArrayBuffer[ActorRef] = new ArrayBuffer()
  val nodeSystem = ActorSystem("NodeSystem")
  var nodeCount = 1

  var heartBeatsRecevied = 0
  var heartBeatsOld = 0

  var originalNode = nodeSystem.actorOf(Props(new Node("OrigNode", self)), name = "OrigNode")
  originalNode ! SetBusy(false)

  var sumBounces: Long = 0

  context.system.scheduler.scheduleOnce(2 seconds, self, IsSystemBuilt())

  for(k <- 0 to numNodes-2) {
    context.system.scheduler.scheduleOnce((k*delayBetweenNodeInsertion)+delayBetweenNodeInsertion milliseconds, self, NewNode())
  }

  def receive = {
    case NewNode() => {
      nodeCount = nodeCount + 1
      val newNode = nodeSystem.actorOf(Props(new Node("Node" + nodeCount, self)), name = "Node" + nodeCount)
      networkNodes += newNode
      newNode ! JoinSystem(originalNode)
    }

    case IsSystemBuilt() => {
      if(heartBeatsRecevied == heartBeatsOld) {
        println("\nTopology has been successfully built.")
        for(i <- 0 to numberOfMessagesToStore) {
          originalNode ! StoreData(new Data("Message" + i, "Value" + i))
        }
        println("Populating table with random messages...")
        context.system.scheduler.scheduleOnce(2 seconds, self, AreMessagesPopulated())
      }
      else {
        heartBeatsOld = heartBeatsRecevied
        context.system.scheduler.scheduleOnce(2 seconds, self, IsSystemBuilt())
      }
    }

    case AreMessagesPopulated() => {
      if(heartBeatsRecevied == heartBeatsOld) {
        println("Messages inserted into hash table.")

        //Once populated, each node queries for a random message numRequests amount of times.
        context.system.scheduler.scheduleOnce(2 seconds, self, IsEverythingComplete())
        for(i <- 0 to numRequests-1){
          context.system.scheduler.scheduleOnce(i seconds, self, QueryAllNodes())
        }

      }
      else {
        heartBeatsOld = heartBeatsRecevied
        context.system.scheduler.scheduleOnce(2 seconds, self, AreMessagesPopulated())
      }
    }

    case QueryAllNodes() => {
      println("All peers making a random request.")
      for(i <- networkNodes.indices) {
        networkNodes(i) ! QueryData("Message" + RNG.getRandNum(numberOfMessagesToStore), self)
      }
    }

    case IsEverythingComplete() => {
      if(heartBeatsRecevied == heartBeatsOld) {
        println("\nSimulation Complete. Results:")
        println("Average Number of Hops: " + (sumBounces.toDouble/(numRequests*numNodes)))
        System.exit(0)
      }
      else {
        heartBeatsOld = heartBeatsRecevied
        context.system.scheduler.scheduleOnce(2 seconds, self, IsEverythingComplete())
      }
    }

    case HeartBeat() => {
      heartBeatsRecevied += 1
    }

    case QueryResponse(result: Data, numberOfBounces: Int) => {
      sumBounces = sumBounces + numberOfBounces
    }

  }

}

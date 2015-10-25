import akka.actor._

object Main {

  def main(args: Array[String]) {

    var numNodes = 5000
    var numRequests = 5

    if(!args.isEmpty){
      numNodes = args(0).toInt
      numRequests = args(1).toInt
    }

    println("Creating Chord Network...")
    println("Number of Nodes: " + numNodes)
    println("Number of Requests: " + numRequests)

    Thread.sleep(1500)

    val bigSystem = ActorSystem("BigSystem")
    val bigDaddy = bigSystem.actorOf(Props(new BigDaddy(numNodes, numRequests)), name="BigDaddy")


  }

}
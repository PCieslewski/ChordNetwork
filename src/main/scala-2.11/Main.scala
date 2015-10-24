import javax.swing.BoundedRangeModel
import scala.concurrent.duration._
import Hasher.hash
import akka.actor.{Props, ActorSystem}

object Main {

  def main(args: Array[String]) {

//    println(hash("Hello"))
//    println(hash("Hello"))
//    println(hash("Hello"))
//     val a: Node = new Node("aasadftrmnhg")
//    a.initFingerTable()

//    for(i <- 10 to 1 by -1) {
//      println(i)
//    }

//  var a: Range = new Range(5,1)
//    println(a.contains(1))
//    println(a.contains(5))

    val bigSystem = ActorSystem("BigSystem")
    val bigDaddy = bigSystem.actorOf(Props(new BigDaddy()), name="BigDaddy")



  }

}
package TwoPhaseCommit

import org.apache.zookeeper._

import scala.util.Random

case class Coordinator(hostPort: String, root: String, n_workers: Integer) extends Watcher {
  val zk = new ZooKeeper(hostPort, 3000, this)
  val coordinatorPath: String = root + "/coordinator"
  val mutex = new Object()

  zk.create(
    coordinatorPath,
    Array.emptyByteArray,
    ZooDefs.Ids.OPEN_ACL_UNSAFE,
    CreateMode.PERSISTENT
  )

  override def process(event: WatchedEvent): Unit = {
    mutex.synchronized {
      System.err.println(s"Coordinator event: ${event.getType}")
      System.err.println(event)
      try {
        val workers = zk.getChildren(coordinatorPath, this)
        if (workers.size == n_workers) {
          System.err.println("All workers connected")
          val decision = if (Random.nextDouble() > 0.5) "commit" else "abort"
          System.err.println("Decision: " + decision)
          for (i <- 0 until n_workers) {
            zk.setData(s"$coordinatorPath/node_$i", decision.getBytes, -1)
          }
          System.err.println("All workers notified!")
        }
      }
    }
  }
}
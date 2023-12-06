package TwoPhaseCommit

import org.apache.zookeeper.{CreateMode, WatchedEvent, Watcher, ZooDefs, ZooKeeper}

import java.util.concurrent.TimeUnit
import scala.util.Random

case class Worker(id:Integer, hostPort:String, root:String) extends Watcher {
  val zk = new ZooKeeper(hostPort, 3000, this)
  val workerPath: String = root + "/worker_" + id.toString

  override def process(event: WatchedEvent): Unit = {
  }

  def run(): Unit = {
    val value = if (Random.nextDouble() > 0.5) "commit" else "abort"
    while (zk.exists(root, false) == null) {
      TimeUnit.SECONDS.sleep(5)
    }
    println(s"Node $id vote $value")
    zk.create(workerPath, value.getBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
    TimeUnit.SECONDS.sleep(10)
    zk.getData(workerPath, this, null)
    zk.delete(workerPath, -1)
    zk.close()
  }

}
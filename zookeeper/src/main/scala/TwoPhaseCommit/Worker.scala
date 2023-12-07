package TwoPhaseCommit

import org.apache.zookeeper._

import java.util.concurrent.TimeUnit
import scala.util.Random

case class Worker(id:Integer, hostPort:String, root:String) extends Watcher {
  val zk = new ZooKeeper(hostPort, 3000, this)
  val workerPath: String = root + "/node_" + id.toString

  override def process(event: WatchedEvent): Unit = {

  }

  def run(): Unit = {
    val value = if (Random.nextDouble() > 0.5) "commit" else "abort"
//    val value = "commit"
    while (zk.exists(root, false) == null) {
      TimeUnit.SECONDS.sleep(5)
    }
    System.err.println(s"Node $id vote $value")
    zk.create(
      workerPath,
      value.getBytes(),
      ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.EPHEMERAL
    )
    zk.getData(workerPath, this, null)
    TimeUnit.SECONDS.sleep(5)
    zk.delete(workerPath, -1)
    zk.close()
  }
}
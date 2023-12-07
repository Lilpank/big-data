package TwoPhaseCommit

import org.apache.zookeeper._

import java.util.concurrent.TimeUnit

case class Coordinator(hostPort:String, root:String, n_workers:Integer) extends Watcher {
  val zk = new ZooKeeper(hostPort, 3000, this)
  val coordinatorPath: String = root + "/coordinator"

  override def process(event: WatchedEvent): Unit = {

  }

  def run(): Unit = {
    zk.create(
      coordinatorPath,
      Array.emptyByteArray,
      ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT
    )

    while(true) {
      val workers = zk.getChildren(coordinatorPath, this)
      if (workers.size == n_workers) {
        System.err.println("All workers voted")
        var commits = 0
        var aborts = 0
        for (i <- 0 until n_workers) {
          val w = workers.get(i)
          val data = new String(zk.getData(s"$coordinatorPath/node_$i", false, null))
          if (data == "commit") commits += 1
          else if (data == "abort") aborts += 1
        }
        val decision = if (commits > aborts) "commit" else "abort"
        for (i <- 0 until n_workers) {
          val w = workers.get(i)
          zk.setData(s"$coordinatorPath/node_$i", decision.getBytes, -1)
        }
        System.err.println(decision)
        while (true) {
          val workers = zk.getChildren(coordinatorPath, this)
          if (workers.size == 0) {
            zk.delete(coordinatorPath, -1)
            zk.close()
            return
          } else {
            TimeUnit.SECONDS.sleep(5)
          }
        }
      } else {
        System.err.println(s"register nodes $workers")
      }
    }
  }

}
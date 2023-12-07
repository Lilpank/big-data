package TwoPhaseCommit

import org.apache.zookeeper._

case class Worker(id: Integer, hostPort: String, root: String) extends Watcher {
  val zk = new ZooKeeper(hostPort, 3000, this)
  val workerPath: String = root + "/node_" + id.toString
  val mutex = new Object()
  var data: String = ""
  zk.create(
    workerPath,
    "".getBytes(),
    ZooDefs.Ids.OPEN_ACL_UNSAFE,
    CreateMode.EPHEMERAL
  )

  override def process(event: WatchedEvent): Unit = {
    mutex.synchronized {
      data = new String(zk.getData(s"$workerPath", this, null))
      System.err.println("Пришло изменение: " + data)
      if (data == "commit") {
        System.err.println("Выполнение транзакции.")
        zk.setData(workerPath, "committed".getBytes(), -1)
      } else if (data == "abort") {
        System.err.println("Прерывание транзакции.")
        zk.setData(workerPath, "committed".getBytes(), -1)
      }
    }
  }

  def run(): Unit = {
    while (true) {

    }
  }
}
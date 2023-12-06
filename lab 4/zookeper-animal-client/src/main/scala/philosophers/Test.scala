package philosophers

import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.RetryOneTime
import org.apache.zookeeper.CreateMode

case class Test() {
  val HOSTNAME = "127.0.0.1"
  val NAMES: List[String] = List("Chui", "Vuni", "v", "q", "a")
  val root = "/philosophers"

  val zoo: CuratorFramework = CuratorFrameworkFactory.newClient(HOSTNAME, 60 * 1000, 15 * 1000, new RetryOneTime(1000))
  zoo.start()
  // let "client" be a CuratorFramework instance// let "client" be a CuratorFramework instance

  val async: Nothing = AsyncCuratorFramework.wrap(client)
  async.checkExists.forPath(somePath).thenAccept((stat) => mySuccessOperation(stat))
  val async: Nothing = AsyncCuratorFramework.wrap(client)  for (name <- NAMES) {
    val philosopherPath = root + "/" + name



  }
}
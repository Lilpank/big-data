package philosophers

import org.apache.zookeeper._

import java.util.concurrent.{CountDownLatch, Semaphore}
import java.io.{File, PrintWriter}
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryOneTime

//case class ZKConnection(host: String, philosopherPath: String) {
//  val zoo = new ZooKeeper(host, 1000, new Watcher() {
//    override def process(event: WatchedEvent): Unit = {
//      System.err.println(philosopherPath + s" Event from keeper: ${event.getType}")
//    }
//  })
//
//  def create(): Unit ={
//    zoo.create(philosopherPath, Array.emptyByteArray, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
//  }
//  def leave(): Unit={
//    zoo.delete(philosopherPath, 0)
//    Thread.sleep(10000)
//    System.err.println(philosopherPath + " встал из стола")
//  }
//}

//object ZOO{
//  val HOSTNAME = "127.0.0.1"
//  val zoo: CuratorFramework = CuratorFrameworkFactory.newClient(HOSTNAME, 60*1000, 15*1000, new RetryOneTime(1000))
//}

case class ZKConnection(HOSTNAME: String, philosopherPath: String) {
  val zoo: CuratorFramework = CuratorFrameworkFactory.newClient(HOSTNAME, 60*1000, 15*1000, new RetryOneTime(1000))

  System.err.println("zoo: " + philosopherPath)
  zoo.start()

  def create(): Unit ={
    zoo.create
      .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
      .inBackground()
      .forPath(philosopherPath)
    Thread.sleep(10000)
  }

  def leave(): Unit={
//    zoo.delete().forPath(philosopherPath)

    Thread.sleep(10000)
    System.err.println(philosopherPath + " встал из стола")
    try{
      zoo.close()
    }
  }
}


case class Philosopher(sem: Semaphore,
                       namee: String,
                       host: String,
                       root: String,
                       partySize: Integer,
                       leftFork: Fork, rightFork: Fork) extends Runnable{
  val name: String = namee
  val philosopherPath: String = root + "/" + name
  val zkConn: ZKConnection = ZKConnection(host, philosopherPath)
  val sm: Semaphore = sem
  System.err.println(System.nanoTime() + " " + philosopherPath)
  @throws[InterruptedException]
  private def doAction(action: String): Unit = {
    System.err.println(Thread.currentThread.getName + " " + action)
    Thread.sleep((Math.random * 100).toInt)
  }

  def run(): Unit ={
    try while ( {
      true
    }) {
      doAction(System.nanoTime + ": Thinking")
      sem.acquire()
      doAction(System.nanoTime() + ": at the table")
      zkConn.create()

      leftFork synchronized doAction(System.nanoTime + name + ": Picked up left fork")
      rightFork synchronized // eating
      doAction(System.nanoTime + ": Picked up right fork - eating")
      doAction(System.nanoTime + ": Put down right fork")

      // Back to thinking
      doAction(System.nanoTime + ": Put down left fork. Back to thinking")
      zkConn.leave()
      sem.release()
    }
    catch {
      case _: InterruptedException => print(_)
    }
  }
}

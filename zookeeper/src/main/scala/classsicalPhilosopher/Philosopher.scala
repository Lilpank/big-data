package classsicalPhilosopher

import org.apache.zookeeper._

case class Philosopher(name: String,
                       host: String,
                       root: String,
                       partySize: Integer,
                       leftFork: Fork,
                       rightFork: Fork) extends Watcher() {
  val philosopherPath: String = root + "/" + name
  val zk = new ZooKeeper(host, 3000, this)
  val mutex = new Object()

  override def process(event: WatchedEvent): Unit = {
    mutex.synchronized {
      mutex.notify()
    }
  }

  @throws[InterruptedException]
  private def doAction(action: String): Unit = {
    System.err.println(Thread.currentThread.getName + " " + action)
    Thread.sleep((Math.random * 10000).toInt)
  }

  def run(): Unit = {
    try while ( {
      true
    }) {
      doAction(System.nanoTime + ": Thinking")
      mutex.synchronized {
        zk.create(
          philosopherPath,
          Array.emptyByteArray,
          ZooDefs.Ids.OPEN_ACL_UNSAFE,
          CreateMode.EPHEMERAL
        )
        System.err.println(Thread.currentThread.getName + ": at the table")

        leftFork.synchronized {
          doAction(System.nanoTime + name + ": Picked up left fork")

          rightFork.synchronized {
            doAction(System.nanoTime + ": Picked up right fork")

            System.err.println(Thread.currentThread.getName + " - eating")
            doAction(System.nanoTime + ": Put down right fork")

            zk.delete(philosopherPath, -1)
            doAction(System.nanoTime + ": Put down left fork. Back to thinking")
          }
        }
      }

    }
    catch {
      case _: InterruptedException => print(_)
    }
  }
}

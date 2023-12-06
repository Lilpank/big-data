package TwoPhaseCommit

object Main {
  def main(args: Array[String]): Unit = {
    commit()
  }

  def commit(): Unit = {
    println("------Commit------")
    val hostPort = "localhost:2181"
    val root = "/commit"
    val n_workers = 7
    val workers = new Array[Thread](n_workers)

    val coordinator = Coordinator(hostPort, root, n_workers)

    val coordinator_thread = new Thread(
      () => {
        coordinator.run()
      }
    )
    coordinator_thread.start()

    for (i <- 0 until n_workers) {
      workers(i) = new Thread(
        () => {
          val worker = Worker(i, hostPort, coordinator.coordinatorPath)
          worker.run()
        }
      )
      workers(i).start()
    }
  }

}
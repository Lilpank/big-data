package TwoPhaseCommit

object Main {
  def main(args: Array[String]): Unit = {
    val hostPort = "localhost:2181"
    val root = ""
    val n_workers = 5
    val workers = new Array[Thread](n_workers)

    val coordinator = Coordinator(hostPort, root, n_workers)

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
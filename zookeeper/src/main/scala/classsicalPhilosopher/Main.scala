package classsicalPhilosopher


object Main {
  val sleepTime = 100
  val NAMES: List[String] = List("Plato", "Thales", "Pythagoras", "Pythagoras", "Diogenes")
  val root = "/philosophers"
  val HOSTNAME = "127.0.0.1"
  var philosophers: List[Philosopher] = List()
  val sleepMsBetweenRetries = 100
  val maxRetries = 3
  var forks: List[Fork] = List()

  def main(args: Array[String]): Unit = {
    var i = 0

    try {
      for (f <- NAMES.indices) {
        forks = forks.+:(new Fork)
      }

      for (p_name <- NAMES) {
        philosophers = philosophers.+:(Philosopher(p_name, HOSTNAME, root, i, forks(i), forks((i+1) % forks.length)))

        val t = new Thread(
          () => {
            val philosopher = philosophers.head
            philosopher.run()
          }, " " + p_name)
        t.start()

        i = i + 1
      }
    } catch {
      case e: Exception => println("Animal was not permitted to the zoo. " + e)
    }
  }
}

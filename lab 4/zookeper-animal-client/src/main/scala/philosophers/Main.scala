package philosophers

import java.util.concurrent.Semaphore

//Philosopher


object Main {
  val sleepTime = 100
  val NAMES: List[String] = List("Chui", "Vuni", "v", "q", "a")
  val root = "/philosophers"
  val HOSTNAME = "127.0.0.1"
  var philosophers: List[Philosopher] = List()
  val sleepMsBetweenRetries = 100
  val maxRetries = 3
  var forks: List[Fork] = List()

  def main(args: Array[String]): Unit = {
    var i = 0
    val sem = new Semaphore(2)

    try {
      for (_ <- NAMES){
        forks = forks.+:(new Fork)
      }

      for (p_name <- NAMES) {
        val leftFork = forks.apply(i)
        val rightFork = forks.apply((i+1) % forks.length)

        if (i == philosophers.length - 1){
          philosophers = philosophers.+:(Philosopher(sem, p_name, HOSTNAME, root, i, rightFork, leftFork))
        }else {
          philosophers = philosophers.+:(Philosopher(sem, p_name, HOSTNAME, root, i, leftFork, rightFork))
        }
        val t = new Thread(philosophers.apply(i), " " + p_name)
        t.start()
        i = i + 1
      }
    } catch {
      case e: Exception => println("Animal was not permitted to the zoo. " + e)
    }
  }
}



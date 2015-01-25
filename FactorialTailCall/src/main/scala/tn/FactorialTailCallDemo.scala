package tn

import scala.math.BigInt

object FactorialTailCallDemo {
  
  def factorial(n: BigInt) = {
    
    @scala.annotation.tailrec
    def factorialTailCall(m: BigInt, n: BigInt) : BigInt =
      if(n == 1) m else factorialTailCall(m * n, n - 1)
      
    factorialTailCall(1, n)
  }
  
  def main(args: Array[String]) = {
    if (args.length == 0) {Console.err.println("no args");System.exit(0)}
    try {
      for (i <- 0 until args.length) {
        if (args(i).matches("[0-9]+")) {
          println(factorial(BigInt(args(i))))
        } else {
          Console.err.println("argument \"" + args(i) + "\" is not a positive integer...skipping")
        }
      }
    } catch {
        case x: Exception => x.printStackTrace    
    }    
  }
  
}

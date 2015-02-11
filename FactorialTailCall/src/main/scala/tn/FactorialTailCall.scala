package tn

import scala.math.BigInt

object FactorialTailCall {
  
  def factorial(i: BigInt) = {
    @scala.annotation.tailrec
    def factorialTailCall(a: BigInt, i: BigInt) : BigInt =
      if(i == 1) a else factorialTailCall(a * i, i - 1)
    factorialTailCall(1, i)
  }
  
  def main(args: Array[String]) = {
    if (args.length == 0) {Console.err.println("no args");System.exit(0)}
    try {
      for (i <- 0 until args.length) {
        if (args(i).matches("0+") || !args(i).matches("[0-9]+")) {
          Console.err.println("argument \"" + args(i) + "\" is not a positive integer...skipping")
        } else {
          println(factorial(BigInt(args(i))))
        }
      }
    } catch {
        case x: Exception => x.printStackTrace    
    }    
  }
}

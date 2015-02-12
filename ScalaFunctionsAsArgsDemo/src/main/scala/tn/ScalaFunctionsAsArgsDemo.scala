package tn

object ScalaFunctionsAsArgsDemo extends App {
 
def func(i: Int)(a: Int => Int)(b: Int => Int):Int = {
  a(i) + b(i)
}

val x = func(7)(x => x + 4)(y => y * 3)

println(x)

}

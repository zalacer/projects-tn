package tn

object ScalaFunctionsAsArgsDemo extends App {
 
def func(i: Int)(j: Int)(a: Int => Int)(b: Int => Int):Int = {
  a(i) + b(j)
}

val x = func(7)(5)(x => x + 3)(y => y * 5)

println(x)

}

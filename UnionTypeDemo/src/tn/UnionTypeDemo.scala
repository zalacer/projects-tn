package tn

object UnionTypeDemo extends App {

  type ¬[A] = A => Nothing
  type ¬¬[A] = ¬[¬[A]]
  type ∨[T, U] = ¬[¬[T] with ¬[U]] 
  implicitly[¬¬[Int] <:< (Int ∨ String)]
  implicitly[¬¬[String] <:< (Int ∨ String)]
  type |∨|[T, U] = { type λ[X] = ¬¬[X] <:< (T ∨ U) }

  def size[W: (Int |∨| String)#λ](w: W) =
    w match {
      case i: Int => i
      case s: String => s.length
    }

  println(size("hello"))
  println(size(123))

}

import scala.deriving.*
import scala.compiletime.{erasedValue, summonInline}
import scala.util.Random
import io.circe.*

/**
 *  some links:
 *  Quick high level introduction Category Theory
 *   * https://www.youtube.com/watch?v=eXBwU9ieLL0&t=36s
 *   * https://ncatlab.org/nlab/show/category+theory
 *  Type class derivation in Scala 3
 *   * https://docs.scala-lang.org/scala3/reference/contextual/derivation.html
 *  https://github.com/typelevel/shapeless-3
 */

inline def summonAsList[T <: Tuple]:  List[Eg[_]] =
  inline erasedValue[T] match
    case _: EmptyTuple => Nil
    case _: (t *: xt) => summonInline[Eg[t]] :: summonAsList[xt]

trait Eg[T]:
  def make: T

object Eg:
  def apply[T](using m: Eg[T]): Eg[T] = m

  given Eg[Int] with
    def make: Int = Random.nextInt(Int.MaxValue)

  given Eg[String] with
    def make: String = Random.alphanumeric.take(8).mkString

  given Eg[User] = Eg.derived

  inline given derived[T](using m: Mirror.Of[T]): Eg[T] = {
    lazy val instances: List[Eg[_]] = summonAsList[m.MirroredElemTypes]
    inline m match
      case _: Mirror.SumOf[T] => sumDerived(instances)
      case p: Mirror.ProductOf[T] => productDerived(p, instances)
  }

  def sumDerived[T](instances: => List[Eg[_]]): Eg[T] =
    new Eg[T]:
      def make: T =
        instances(Random.nextInt(instances.size))
          .asInstanceOf[Eg[T]]
          .make

  def productDerived[T](p: Mirror.ProductOf[T], instances: List[Eg[_]]): Eg[T] =
    new Eg[T]:
      def make: T =
        p.fromProduct(Tuple.fromArray(instances.map(_.make).toArray))

case class User(id: Int, name: String)

enum BTree[T] derives Eg:
  case Node(l: BTree[T], r: BTree[T])
  case Leaf(value: T)

@main
def main(): Unit =
  (1 to 5)
    .foreach { _ =>
      println(Eg[BTree[User]].make)
    }
  case class Foo[A](a: A) derives Encoder.AsObject
  case class Bar(i: Int) derives Encoder.AsObject
  println(Encoder[Foo[Bar]].apply(Foo(Bar(42))))

package com.mudriyjo

case class Person(name: String, surname: String, age: Int)
object GivenAndUsingPlayground extends App {
  def listOfPeople(people: List[Person])(using compare: Ordering[Person]): List[Person] =
    people.sorted
  def compareTwoPerson(person1: Person, person2: Person)(compare: Ordering[Person]): List[Person] = ???

  //import StandardPeopleOrdering.orderPersonByAge
  import StandardPeopleOrdering.given Ordering[Person]

  val people = List(Person("Ron", "Wuisly", 17), Person("Harry", "Potter", 16))
  listOfPeople(people).foreach(println)

  // derivation ???
}

object ExtendedPeopleOrdering {
  given orderPersonByAge: Ordering[Person] with {
    override def compare(x: Person, y: Person): Int =
      x.age.compareTo(y.age)
  }

  given orderPersonBySurname: Ordering[Person] with {
    override def compare(x: Person, y: Person): Int =
      x.surname.compareTo(y.surname)
  }
}
object StandardPeopleOrdering {
  given orderPersonByName: Ordering[Person] with {
    override def compare(x: Person, y: Person): Int =
      x.name.compareTo(y.name)
  }
}

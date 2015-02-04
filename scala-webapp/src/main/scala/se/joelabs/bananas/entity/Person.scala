package se.joelabs.bananas.entity

import javax.persistence._

@Entity
@Table(name = "PERSON")
case class Person() {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID")
  private var id: Long = _

  def getId = id

  @Column(name = "NAME")
  private var name: String = _

  def getName = name
}

object Person {
  def apply(name: String): Person = {
    val p  = new Person
    p.name = name
    p
  }
}
package se.joelabs.bananas.entity

import javax.persistence._

@Entity
@Table(name = "PERSON")
class ScalaPersonEntity(@Id
              @GeneratedValue(strategy = GenerationType.AUTO)
              @Column(name = "ID")
              _id: java.lang.Long = null,
              @Column(name = "NAME")
              _name: String = null,
              @Column(name = "AGE")
              _age: Integer = null) {
  def this() = this(null, null, null)

  def id: Long = _id

  def name = _name

  def age: Int = if (_age == null) 0 else _age

  def copy(name: String = this._name, age: Integer = this._age) = new ScalaPersonEntity(null, name, age)
}

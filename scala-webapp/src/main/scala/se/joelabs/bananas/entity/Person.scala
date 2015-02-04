package se.joelabs.bananas.entity

import javax.persistence._

@Entity
@Table(name = "PERSON")
case class Person(@Id
                  @GeneratedValue(strategy = GenerationType.AUTO)
                  @Column(name = "ID") id: Long,
                  @Column(name = "NAME") name: String);
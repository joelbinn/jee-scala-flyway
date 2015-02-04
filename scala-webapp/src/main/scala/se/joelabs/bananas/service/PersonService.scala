package se.joelabs.bananas.service

import javax.inject.Named
import javax.persistence.{EntityManager, PersistenceContext}
import javax.transaction.Transactional

import se.joelabs.bananas.entity.{PersonEntity, ScalaPersonEntity}
import se.joelabs.bananas.web.PersonDTO

import scala.collection.JavaConversions._

@Transactional
@Named
class PersonService {
  @PersistenceContext(name = "FlywayPU")
  protected var em: EntityManager = _

  def scalaPerson(id: Long): ScalaPersonEntity =
    em.createQuery("SELECT p FROM ScalaPersonEntity p WHERE p.id = :id", classOf[ScalaPersonEntity])
      .setParameter("id", id)
      .getSingleResult


  def addScalaPerson(person: PersonDTO): ScalaPersonEntity = {
    val p = new ScalaPersonEntity(_name = person.name, _age = person.age)
    em.persist(p)
    p
  }

  def getPersonById(id: Long): PersonEntity =
    em.createQuery("SELECT p FROM PersonEntity p WHERE p.id = :id", classOf[PersonEntity])
      .setParameter("id", id)
      .getSingleResult

  def persons: List[PersonEntity] =
    em.createQuery("SELECT p FROM PersonEntity p", classOf[PersonEntity])
      .getResultList
      .toList


  def addPerson(person: PersonDTO): PersonEntity = {
    val p = new PersonEntity()
    p.name = person.name
    em.persist(p)
    p
  }
}

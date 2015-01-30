package se.joelabs.bananas.service

import javax.inject.Named
import javax.persistence.{EntityManager, PersistenceContext}
import javax.transaction.Transactional

import se.joelabs.bananas.entity.PersonEntity
import se.joelabs.bananas.web.PersonDTO

import scala.collection.JavaConversions._

/*
 * NYPS 2020
 * 
 * User: joel
 * Date: 2015-01-28
 * Time: 22:38
 */
@Transactional
@Named
class PersonService {
  @PersistenceContext(name = "FlywayPU")
  protected var em: EntityManager = _

  def getPersonByName(id: Long): PersonEntity =
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

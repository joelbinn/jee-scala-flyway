package se.joelabs.bananas.service

import javax.inject.Named
import javax.persistence.{EntityManager, PersistenceContext}
import javax.transaction.Transactional

import se.joelabs.bananas.entity.PersonEntity

/*
 * NYPS 2020
 * 
 * User: joel
 * Date: 2015-01-28
 * Time: 22:38
 */
@Transactional
@Named
class TheService {
  @PersistenceContext(name = "FlywayPU")
  protected var em: EntityManager = _

  def persons: java.lang.String =
    em.createQuery("SELECT p FROM PersonEntity p", classOf[PersonEntity])
      .getResultList
      .toString

  def addPerson(): PersonEntity = {
    val p = new PersonEntity
    p.name = "XYZ" + System.currentTimeMillis()
    em.persist(p)
    p
  }

}

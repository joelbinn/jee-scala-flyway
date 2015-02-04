package se.joelabs.bananas.web

import javax.inject.Inject
import javax.ws.rs._
import javax.ws.rs.core.MediaType

import se.joelabs.bananas.service.PersonService

case class PersonDTO(id: Long, name: String, age: Int = 0)

@Path("/persons")
class PersonResource {
  @GET
  @Path("/")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def persons(): List[PersonDTO] =
    service.persons.map(pe => PersonDTO(id = pe.id, name = pe.name))

  @GET
  @Path("/{id}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getPersonById(@PathParam("id") id: String): PersonDTO = {
    val pe = service.getPersonById(java.lang.Long.parseLong(id))
    PersonDTO(id = pe.id, name = pe.name)
  }

  @POST
  @Path("/")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def addPerson(newPerson: PersonDTO): PersonDTO = {
    val pe = service.addPerson(newPerson)
    PersonDTO(id = pe.id, name = pe.name)
  }

  @GET
  @Path("/scala/{id}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getScalaPersonById(@PathParam("id") id: java.lang.Long): PersonDTO = {
    val pe = service.scalaPerson(id)
    PersonDTO(id = pe.id, name = pe.name, age = pe.age)
  }

  @POST
  @Path("/scala/")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def addScalaPerson(newPerson: PersonDTO): PersonDTO = {
    val pe = service.addScalaPerson(newPerson)
    PersonDTO(id = pe.id, name = pe.name, age = pe.age)
  }

  @Inject
  protected var service: PersonService = _
}

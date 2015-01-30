package se.joelabs.bananas.web

import javax.inject.Inject
import javax.ws.rs._
import javax.ws.rs.core.MediaType

import se.joelabs.bananas.service.PersonService

case class PersonDTO(id: Long, name: String, age: Int = 0)

@Path("/persons")
class WebApp {
  @GET
  @Path("/")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def persons(): List[PersonDTO] =
    service.persons.map(pe => PersonDTO(id = pe.id, name = pe.name))

  @GET
  @Path("/{id}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getPersonByName(@PathParam("id") id: String): PersonDTO = {
    val pe = service.getPersonByName(java.lang.Long.parseLong(id))
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

  @Inject
  protected var service: PersonService = _
}

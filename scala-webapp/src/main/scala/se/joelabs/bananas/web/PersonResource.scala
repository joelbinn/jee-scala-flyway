package se.joelabs.bananas.web

import javax.inject.Inject
import javax.ws.rs._
import javax.ws.rs.core.MediaType

import se.joelabs.bananas.service.PersonService

case class PersonDTO(name: String, age: Int = 0)

@Path("/persons")
class WebApp {
  @GET
  @Path("/")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def persons(): List[PersonDTO] =
    service.persons.map(pe => PersonDTO(name = pe.name))

  @GET
  @Path("/{id}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getPersonByName(@PathParam("id") id: String): PersonDTO = {
    println(s"ID=$id")
    val result = service.getPersonByName(java.lang.Long.parseLong(id))
    PersonDTO(name = result.name)
  }

  @POST
  @Path("/")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def addPerson(newPerson: PersonDTO): PersonDTO = {
    val result = service.addPerson(newPerson)
    PersonDTO(name = result.name)
  }

  @Inject
  protected var service: PersonService = _
}

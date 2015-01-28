package se.joelabs.bananas.web

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.{POST, GET, Path, Produces}

import se.joelabs.bananas.service.TheService

case class PersonDTO(name: String)

@Path("/webfly")
class WebApp {
  @GET
  @Path("/hej")
  @Produces(Array(MediaType.TEXT_PLAIN))
  def hej(): java.lang.String = service.persons

  @POST
  @Path("/person")
  @Produces(Array(MediaType.TEXT_PLAIN))
  def createPerson(): java.lang.String = service.addPerson().toString

  @Inject
  protected var service: TheService = _
}

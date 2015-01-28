/*
 * NYPS 2020
 * 
 * User: joel
 * Date: 2015-01-28
 * Time: 22:24
 */

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/wf")
public class Bulle {
    @GET
    @Path("/uu")
    @Produces(MediaType.TEXT_PLAIN)
    public String uu() {
        return "UUuuuuu";
    }
}


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;


@Path("newsletter/")
public class NewsletterResource 
{
	//
	private static final NewsletterDatabase database = new NewsletterDatabase();
	
	@GET
	@Path("subscribers")
	@Produces("application/json; charset=UTF-8")
	public Response getAllSubscribers()
	{
		
		ResponseBuilder responseBuilder = null;
		SubscriberList list;
		
		try 
		{
			responseBuilder = Response.status(Status.OK);
			list = (SubscriberList) database.getSubscribers();
			responseBuilder.entity(list);
			
			
		} catch (Exception e) 
		{
			responseBuilder = Response.status(Status.NO_CONTENT);
			e.printStackTrace();
		}
		

		Response response = responseBuilder.build();

		return response;
	}
	
	@POST
	@Path("subscribers")
	@Consumes("application/json")
	public Response createSubscriber(Subscriber sub)
	{
		
		ResponseBuilder responseBuilder = null;

		
		
		try 
		{
			database.insertUser(sub);
			responseBuilder = Response.status(Status.CREATED);
		} catch (Exception e) 
		{
			responseBuilder = Response.status(Status.NO_CONTENT);
			e.printStackTrace();
		}
		
		
		Response response = responseBuilder.build();

		return response;
	}

	@PUT
	@Path("subscribers/{oldEmail}/{newEmail}")
	public Response updateSubscriber(@PathParam("oldEmail") String oldEmail, @PathParam("newEmail") String newEmail)
	{
		

		ResponseBuilder responseBuilder = null;
		
		try 
		{
			database.updateUser(oldEmail, newEmail);
			responseBuilder = Response.status(Status.OK);
		} catch (Exception e) 
		{
			responseBuilder = Response.status(Status.NO_CONTENT);
			e.printStackTrace();
		}
		
		
		Response response = responseBuilder.build();

		return response;
	}

	@DELETE
	@Path("subscribers/{email}")
	public Response deleteSubscriber(@PathParam("email") String email)
	{
		
		ResponseBuilder responseBuilder = null;

	
		try 
		{
			database.deleteUser(email);
			responseBuilder = Response.status(Status.OK);
		} catch (Exception e) 
		{
			responseBuilder = Response.status(Status.NO_CONTENT);
			e.printStackTrace();
		}
		
		Response response = responseBuilder.build();

		return response;
	}
	
	@POST
	@Path("email")
	public Response emailSubscribers()
	{
		ResponseBuilder responseBuilder = null;
		
		
		try 
		{
			database.emailDatabase();
			responseBuilder = Response.status(Status.OK);
		} catch (Exception e) 
		{
			responseBuilder = Response.status(Status.NO_CONTENT);
			e.printStackTrace();
		}
		
		
		Response response = responseBuilder.build();

		return response;
	}
}

package com.sumerge.program.rest;

import com.sumerge.program.entities.user.User;
import com.sumerge.program.entities.user.UserManager;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("user")
public class UserResource
{
	@Context
	private SecurityContext securityContext;

	@EJB
	private UserManager userManager;

	@POST
	@Path("create")
	public Response createUser(@QueryParam("username") String username, @QueryParam("firstName") String firstName,
							   @QueryParam("lastName") String lastName, @QueryParam("email") String email,
							   @QueryParam("password") String password, @QueryParam("role") String role)
	{
		try
		{
			if(!securityContext.isUserInRole("admin"))
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

			userManager = new UserManager();
			userManager.createUser(username, firstName, lastName, email, password, role);

			return Response.ok().entity(userManager).build();
		}
		catch (Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@GET
	@Path("getAll")
	public Response getUsers()
	{
		userManager = new UserManager();
		boolean isAdmin = securityContext.isUserInRole("admin");

		return Response.ok().entity(userManager.getAllUsers(isAdmin)).build();
	}

	@GET
	@Path("find/{userId}")
	public Response getUser(@PathParam("userId") int userId)
	{
		try
		{
			userManager = new UserManager();
			User user = userManager.getUserById(userId, securityContext.isUserInRole("admin"));

			return Response.ok().entity(user.toString()).build();
		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace());
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@PUT
	@Path("update")
	public Response updateUser(@QueryParam("username")String username, @QueryParam("firstName") String firstName,
							   @QueryParam("lastName") String lastName, @QueryParam("email") String email,
							   @QueryParam("currentPassword") String currentPassword,
							   @QueryParam("newPassword") String newPassword, @QueryParam("role") String role)
	{
		try
		{
			userManager = new UserManager();

			if(!securityContext.isUserInRole("admin") && username != securityContext.getUserPrincipal().toString())
				return Response.status(Response.Status.fromStatusCode(401)).entity("You do not have permissions to do this action.").build();

			if(firstName != null)
				userManager.updateUserFirstName(username, firstName);

			if(lastName != null)
				userManager.updateUserLastName(username, lastName);

			if(email != null)
				userManager.updateUserEmail(username, email);

			if(currentPassword != null)
				userManager.updateUserPassword(username, currentPassword, newPassword);

			if(role != null && securityContext.isUserInRole("admin"))
				userManager.updateUserRole(username, role);

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@PUT
	@Path("move")
	public Response moveUser(@QueryParam("username") String username, @QueryParam("oldGroupId")int oldGroupId, @QueryParam("newGroupId")int newGroupId)
	{
		try
		{
			if(!securityContext.isUserInRole("admin"))
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

			userManager = new UserManager();
			userManager.moveUser(username, oldGroupId, newGroupId);

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@PUT
	@Path("addUser")
	public Response addUser(@QueryParam("username") String username, @QueryParam("groupId")int groupId)
	{
		try
		{
			if(!securityContext.isUserInRole("admin"))
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

			userManager = new UserManager();
			userManager.addUser(username, groupId);

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@DELETE
	@Path("removeUser")
	public Response removeUser(@QueryParam("username") String username, @QueryParam("groupId")int groupId)
	{
		try
		{
			if(!securityContext.isUserInRole("admin"))
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

			userManager = new UserManager();
			userManager.removeUser(username, groupId);

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@PUT
	@Path("restore/{userId}")
	public Response restoreUser(@PathParam("userId")int userId)
	{
		try
		{
			if(!securityContext.isUserInRole("admin"))
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

			userManager = new UserManager();
			userManager.restoreDeleteUser(userId, 0);

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@DELETE
	@Path("delete/{userId}")
	public Response deleteUser(@PathParam("userId")int userId)
	{
		try
		{
			if(!securityContext.isUserInRole("admin"))
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

			userManager = new UserManager();
			User user = userManager.getUserById(userId, false);

			if(user.getUsername() == "admin")
				return Response.status(Response.Status.fromStatusCode(401)).entity("You can not delete the default administrator.").build();

			userManager.restoreDeleteUser(userId, 1);

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}



	/*
    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());

    @Context
    private SecurityContext securityContext;

    @EJB
    private UserRepository repo;

    @GET
    @Produces(APPLICATION_JSON)
    public Response get() {
		LOGGER.info("Entering get with user " + securityContext.getUserPrincipal().toString());
		try {
			return Response.ok().
					entity(repo.getAllUsers()).
					build();
		} catch (Exception e) {
			return Response.serverError().
					entity(e).
					build();
		}
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response post(UserOLD userOLD) {
        LOGGER.info("Entering post with userOLD " + securityContext.getUserPrincipal().toString());
		try {
			repo.addUser(userOLD);
			return Response.ok().
					build();
		} catch (Exception e) {
			return Response.serverError().
					entity(e.getMessage()).
					build();
		}
    }*/
}

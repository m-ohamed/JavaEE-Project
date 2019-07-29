package com.sumerge.program.rest;

import com.sumerge.program.entities.user.User;
import com.sumerge.program.entities.user.UserManager;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("user")
@RequestScoped
public class UserResource
{
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
			userManager = new UserManager();
			userManager.createUser(username, firstName, lastName, email, password, role, false);
			return Response.ok().entity(userManager).build();
		}
		catch (Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@GET
	@Path("find/{userId}")
	public Response getUser(@PathParam("userId") int userId)
	{
		try
		{
			System.out.println("in rest endpoint");
			userManager = new UserManager();
			User user = userManager.getUserById(userId);
			System.out.println("got user: " + user.getUsername());

			return Response.ok().entity(user.toString()).build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
	}

	@PUT
	@Path("update")
	public Response updateUser(@QueryParam("userId")int userId, @QueryParam("firstName") String firstName,
							   @QueryParam("lastName") String lastName, @QueryParam("email") String email,
							   @QueryParam("currentPassword") String currentPassword,
							   @QueryParam("newPassword") String newPassword, @QueryParam("role") String role)
	{
		try
		{
			userManager = new UserManager();

			if(firstName != null)
				userManager.updateUserFirstName(userId, firstName);

			if(lastName != null)
				userManager.updateUserLastName(userId, lastName);

			if(email != null)
				userManager.updateUserEmail(userId, email);

			if(currentPassword != null && newPassword != null)
				userManager.updateUserPassword(userId, currentPassword, newPassword);

			if(role != null)
				userManager.updateUserRole(userId, role);

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
			userManager = new UserManager();
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

package com.sumerge.program.rest;

import com.sumerge.program.entities.auditlog.AuditLog;
import com.sumerge.program.entities.auditlog.AuditLogManager;
import com.sumerge.program.entities.user.User;
import com.sumerge.program.entities.user.UserManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
	private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());

	@Context
	private SecurityContext securityContext;

	@EJB
	private UserManager userManager;

	private AuditLogManager auditLogManager;

	@POST
	@Path("create")
	public Response createUser(@QueryParam("username") String username, @QueryParam("firstName") String firstName,
							   @QueryParam("lastName") String lastName, @QueryParam("email") String email,
							   @QueryParam("password") String password, @QueryParam("role") String role)
	{
		LOGGER.info("Entering create user REST method.");
		auditLogManager = new AuditLogManager();

		try
		{
			if(!securityContext.isUserInRole("admin"))
			{
				auditLogManager.createLog("Create User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
			}

			userManager = new UserManager();
			User user = userManager.createUser(username, firstName, lastName, email, password, role);

			auditLogManager.createLog("Create User", securityContext.getUserPrincipal().toString(),user.toString(),"SUCCESS");

			return Response.ok().entity(userManager).build();
		}
		catch (Exception e)
		{
			auditLogManager.createLog("Create User", securityContext.getUserPrincipal().toString(),"N/A","FAILED");
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally 
		{
			LOGGER.info("Leaving create user REST method.");
		}
	}

	@GET
	@Path("getAll")
	public Response getUsers()
	{
		LOGGER.info("Entering get all users REST method.");
		auditLogManager = new AuditLogManager();

		userManager = new UserManager();
		boolean isAdmin = securityContext.isUserInRole("admin");

		List<User> usersList = userManager.getAllUsers(isAdmin);

		auditLogManager.createLog("Get All Users", securityContext.getUserPrincipal().toString(),"N/A","SUCCESS");

		LOGGER.info("Leaving get all users REST method.");
		
		return Response.ok().entity(usersList).build();
	}

	@GET
	@Path("find/{userId}")
	public Response getUser(@PathParam("userId") int userId)
	{
		LOGGER.info("Entering get user REST method.");
		auditLogManager = new AuditLogManager();

		try
		{
			userManager = new UserManager();
			User user = userManager.getUserById(userId, securityContext.isUserInRole("admin"));

			auditLogManager.createLog("Find User", securityContext.getUserPrincipal().toString(),user.toString(),"SUCCESS");

			return Response.ok().entity(user.toString()).build();
		}
		catch(Exception e)
		{
			auditLogManager.createLog("Find User", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
			System.out.println(e.getStackTrace());
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally
		{
			LOGGER.info("Leaving get user REST method.");
		}
	}

	@PUT
	@Path("update")
	public Response updateUser(@QueryParam("username")String username, @QueryParam("firstName") String firstName,
							   @QueryParam("lastName") String lastName, @QueryParam("email") String email,
							   @QueryParam("currentPassword") String currentPassword,
							   @QueryParam("newPassword") String newPassword, @QueryParam("role") String role)
	{
		LOGGER.info("Entering update user REST method.");
		
		auditLogManager = new AuditLogManager();

		try
		{
			User user = new User();

			userManager = new UserManager();

			if(!securityContext.isUserInRole("admin") && !username.equalsIgnoreCase(securityContext.getUserPrincipal().toString()))
			{
				auditLogManager.createLog("Update User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("You do not have permissions to do this action.").build();
			}

			if(username == "admin")
			{
				auditLogManager.createLog("Update User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("You can not edit the default administrator.").build();
			}

			if(firstName != null)
				user = userManager.updateUserFirstName(username, firstName);

			if(lastName != null)
				user = userManager.updateUserLastName(username, lastName);

			if(email != null)
				user = userManager.updateUserEmail(username, email);

			if(currentPassword != null)
				user = userManager.updateUserPassword(username, currentPassword, newPassword);

			if(role != null && securityContext.isUserInRole("admin"))
				user = userManager.updateUserRole(username, role);

			auditLogManager.createLog("Update User", securityContext.getUserPrincipal().toString(),user.toString(),"SUCCESS");

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			auditLogManager.createLog("Update User", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally
		{
			LOGGER.info("Leaving update user REST method.");
		}
	}

	@PUT
	@Path("move")
	public Response moveUser(@QueryParam("username") String username, @QueryParam("oldGroupId")int oldGroupId, @QueryParam("newGroupId")int newGroupId)
	{
		LOGGER.info("Entering move user REST method.");
		auditLogManager = new AuditLogManager();

		try
		{
			if(username == "admin" && oldGroupId == 1)
			{
				auditLogManager.createLog("Move User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("You can not move the default administrator from the default group.").build();
			}

			if(!securityContext.isUserInRole("admin"))
			{
				auditLogManager.createLog("Move User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
			}

			userManager = new UserManager();
			userManager.removeUser(username, oldGroupId);
			userManager.addUser(username,newGroupId);

			auditLogManager.createLog("Move User", securityContext.getUserPrincipal().toString(),"New group ID: " + newGroupId + ", old group ID: " + oldGroupId,"SUCCESS");

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			auditLogManager.createLog("Move User", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally
		{
			LOGGER.info("Leaving move user REST method.");
		}
	}

	@PUT
	@Path("addUser")
	public Response addUser(@QueryParam("username") String username, @QueryParam("groupId")int groupId)
	{
		LOGGER.info("Entering add user to group REST method.");
		auditLogManager = new AuditLogManager();

		try
		{
			if(!securityContext.isUserInRole("admin"))
			{
				auditLogManager.createLog("Add User To Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
			}

			userManager = new UserManager();
			userManager.addUser(username, groupId);

			auditLogManager.createLog("Add User To Group", securityContext.getUserPrincipal().toString(),"Group ID: " + groupId,"SUCCESS");

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			auditLogManager.createLog("Add User To Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally 
		{
			LOGGER.info("Leaving add user to group REST method.");
		}
	}

	@DELETE
	@Path("removeUser")
	public Response removeUser(@QueryParam("username") String username, @QueryParam("groupId")int groupId)
	{
		LOGGER.info("Entering remove user from group REST method.");
		
		auditLogManager = new AuditLogManager();

		try
		{
			if(!securityContext.isUserInRole("admin"))
			{
				auditLogManager.createLog("Remove User From Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
			}

			if(username == "admin")
			{
				auditLogManager.createLog("Remove User From Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("You can not remove the default administrator.").build();
			}

			userManager = new UserManager();
			userManager.removeUser(username, groupId);

			auditLogManager.createLog("Remove User From Group", securityContext.getUserPrincipal().toString(),"Group ID: " + groupId,"SUCCESS");

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			auditLogManager.createLog("Remove User From Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally
		{
			LOGGER.info("Leaving remove user from group REST method.");
		}
	}

	@PUT
	@Path("restore/{userId}")
	public Response restoreUser(@PathParam("userId")int userId)
	{
		LOGGER.info("Entering restore user REST method.");
		
		auditLogManager = new AuditLogManager();

		try
		{
			if(!securityContext.isUserInRole("admin"))
			{
				auditLogManager.createLog("Restore User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
			}

			userManager = new UserManager();
			User user = userManager.restoreDeleteUser(userId, 0);

			auditLogManager.createLog("Restore User", securityContext.getUserPrincipal().toString(),user.toString(),"SUCCESS");

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			auditLogManager.createLog("Restore User", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally
		{
			LOGGER.info("Leaving restore user REST method.");
		}
	}

	@DELETE
	@Path("delete/{userId}")
	public Response deleteUser(@PathParam("userId")int userId)
	{
		LOGGER.info("Entering delete user REST method.");
		
		try
		{
			if(!securityContext.isUserInRole("admin"))
			{
				auditLogManager.createLog("Delete User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
			}

			if(userId == 1)
			{
				auditLogManager.createLog("Delete User", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
				return Response.status(Response.Status.fromStatusCode(401)).entity("You can not delete the default administrator.").build();
			}

			userManager = new UserManager();

			User user = userManager.restoreDeleteUser(userId, 1);

			auditLogManager.createLog("Delete User", securityContext.getUserPrincipal().toString(),user.toString(),"SUCCESS");

			return Response.ok().entity(userManager).build();
		}
		catch(Exception e)
		{
			auditLogManager.createLog("Delete User", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
			return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
		}
		finally
		{
			LOGGER.info("Leaving delete user REST method.");
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

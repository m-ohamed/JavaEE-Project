package com.sumerge.program.rest;

import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.sumerge.program.entities.group.GroupViewRegistration;
import com.sumerge.program.exceptions.MissingParameterException;
import org.apache.log4j.Logger;

import java.sql.SQLIntegrityConstraintViolationException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("group")
public class GroupResource
{
    private static final Logger LOGGER = Logger.getLogger(GroupResource.class.getName());
    
    @Context
    private SecurityContext securityContext;

    @EJB
    private GroupManager groupManager;

    @POST
    @Path("create")
    @Consumes(APPLICATION_JSON)
    public Response createGroup(GroupViewRegistration newGroup)
    {
        try
        {
            if(!securityContext.isUserInRole("admin"))
                return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

            groupManager = new GroupManager();
            Group group = groupManager.createGroup(newGroup.getOwnerUsername(), newGroup.getGroupName(), securityContext.getUserPrincipal().toString());

            return Response.ok().entity(group).build();
        }
        catch (MissingParameterException e)
        {
            LOGGER.debug("Missing Parameter Exception.");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            LOGGER.debug("SQL Constraint Violation Exception.");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.debug("Leaving create group REST method.");
        }
    }

    @GET
    @Path("find/{groupId}")
    public Response getGroup(@PathParam("groupId") int groupId)
    {
        try
        {
            groupManager = new GroupManager();
            Group group = groupManager.getGroupById(groupId);

            return Response.ok().entity(group).build();
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            LOGGER.debug("SQL Constraint Violation Exception.");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.debug("Leaving find group REST method.");
        }
    }

    @PUT
    @Path("update")
    public Response updateGroup(@QueryParam("groupId")int groupId, @QueryParam("groupName")String groupName, @QueryParam("groupOwner")Integer groupOwner)
    {
        try
        {
            if(!securityContext.isUserInRole("admin"))
                return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

            if(groupId == 1)
                return Response.status(Response.Status.fromStatusCode(401)).entity("Can not edit default group.").build();

            groupManager = new GroupManager();
            Group group = new Group();

            if(groupName != null)
                group = groupManager.updateGroupName(groupId, groupName, securityContext.getUserPrincipal().toString());

            if(groupOwner != null)
                group = groupManager.updateGroupOwner(groupId, groupOwner, securityContext.getUserPrincipal().toString());

            return Response.ok().entity(group).build();
        }
        catch (MissingParameterException e)
        {
            LOGGER.debug("Missing Parameter Exception.");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            LOGGER.debug("SQL Constraint Violation Exception.");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.debug("Leaving update group REST method.");
        }
    }

    @DELETE
    @Path("delete/{groupId}")
    public Response deleteGroup(@PathParam("groupId")int groupId)
    {
        try
        {
            if(!securityContext.isUserInRole("admin"))
                return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();


            groupManager = new GroupManager();
            Group group = groupManager.getGroupById(groupId);

            if(group.getGroupName().equalsIgnoreCase("default_group"))
                return Response.status(Response.Status.fromStatusCode(401)).entity("You can not delete the default group.").build();


            groupManager.deleteGroup(groupId, securityContext.getUserPrincipal().toString());

            return Response.ok().entity(groupManager).build();
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            LOGGER.debug("SQL Constraint Violation Exception.");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.debug("Leaving delete group REST method.");
        }
    }

}

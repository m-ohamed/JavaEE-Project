package com.sumerge.program.rest;

import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("group")
public class GroupResource
{
    @Context
    private SecurityContext securityContext;

    @EJB
    private GroupManager groupManager;

    @POST
    @Path("create")
    public Response createGroup(@QueryParam("ownerUid")int ownerUid, @QueryParam("groupName")String groupName)
    {
        try
        {
            if(!securityContext.isUserInRole("admin"))
                return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();

            groupManager = new GroupManager();
            groupManager.createGroup(ownerUid, groupName);
            return Response.ok().entity(groupManager).build();
        }
        catch (Exception e)
        {
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
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

            return Response.ok().entity(group.toString()).build();
        }
        catch(Exception e)
        {
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
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

            groupManager = new GroupManager();

            if(groupName != null)
                groupManager.updateGroupName(groupId, groupName);

            if(groupOwner != null)
                groupManager.updateGroupOwner(groupId, groupOwner);

            return Response.ok().entity(groupManager).build();
        }
        catch (Exception e)
        {
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
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

            if(group.getGroupName() == "default_group")
                return Response.status(Response.Status.fromStatusCode(401)).entity("You can not delete the default group.").build();

            groupManager.deleteGroup(groupId);

            return Response.ok().entity(groupManager).build();
        }
        catch (Exception e)
        {
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
    }

}

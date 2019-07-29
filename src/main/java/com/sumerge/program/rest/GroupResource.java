package com.sumerge.program.rest;

import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("group")
public class GroupResource
{
    @EJB
    private GroupManager groupManager;

    @POST
    @Path("create")
    public Response createGroup(@QueryParam("ownerUid")int ownerUid, @QueryParam("groupName")String groupName)
    {
        try
        {
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
            System.out.println("in rest endpoint");
            groupManager = new GroupManager();
            Group group = groupManager.getGroupById(groupId);
            System.out.println("got group: " + group.getGroupName());

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
            groupManager = new GroupManager();
            groupManager.deleteGroup(groupId);

            return Response.ok().entity(groupManager).build();
        }
        catch (Exception e)
        {
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
    }

}

package com.sumerge.program.rest;

import com.sumerge.program.entities.auditlog.AuditLogManager;
import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.logging.Logger;

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

    private AuditLogManager auditLogManager;

    @POST
    @Path("create")
    public Response createGroup(@QueryParam("ownerUid")int ownerUid, @QueryParam("groupName")String groupName)
    {
        LOGGER.info("Entering create group REST method.");
        auditLogManager = new AuditLogManager();

        try
        {
            if(!securityContext.isUserInRole("admin"))
            {
                auditLogManager.createLog("Create Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
                return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
            }

            groupManager = new GroupManager();
            Group group = groupManager.createGroup(ownerUid, groupName);

            auditLogManager.createLog("Create Group", securityContext.getUserPrincipal().toString(),group.toString(),"SUCCESS");

            return Response.ok().entity(groupManager).build();
        }
        catch (Exception e)
        {
            auditLogManager.createLog("Create Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.info("Leaving create group REST method.");
        }
    }

    @GET
    @Path("find/{groupId}")
    public Response getGroup(@PathParam("groupId") int groupId)
    {
        LOGGER.info("Entering find group REST method.");
        auditLogManager = new AuditLogManager();

        try
        {
            groupManager = new GroupManager();
            Group group = groupManager.getGroupById(groupId);

            auditLogManager.createLog("Find Group", securityContext.getUserPrincipal().toString(),"Group ID: " + groupId,"SUCCESS");

            return Response.ok().entity(group.toString()).build();
        }
        catch(Exception e)
        {
            auditLogManager.createLog("Find Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.info("Leaving find group REST method.");
        }
    }

    @PUT
    @Path("update")
    public Response updateGroup(@QueryParam("groupId")int groupId, @QueryParam("groupName")String groupName, @QueryParam("groupOwner")Integer groupOwner)
    {
        LOGGER.info("Entering update group REST method.");
        
        try
        {
            if(!securityContext.isUserInRole("admin"))
            {
                auditLogManager.createLog("Update Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
                return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
            }

            groupManager = new GroupManager();
            Group group = new Group();

            if(groupName != null)
                group = groupManager.updateGroupName(groupId, groupName);

            if(groupOwner != null)
                group = groupManager.updateGroupOwner(groupId, groupOwner);

            auditLogManager.createLog("Update Group", securityContext.getUserPrincipal().toString(), group.toString(),"SUCCESS");

            return Response.ok().entity(groupManager).build();
        }
        catch (Exception e)
        {
            auditLogManager.createLog("Update Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.info("Leaving update group REST method.");
        }
    }

    @DELETE
    @Path("delete/{groupId}")
    public Response deleteGroup(@PathParam("groupId")int groupId)
    {
        LOGGER.info("Entering delete group REST method.");
        try
        {
            if(!securityContext.isUserInRole("admin"))
            {
                auditLogManager.createLog("Delete Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
                return Response.status(Response.Status.fromStatusCode(401)).entity("Only available for administrators.").build();
            }

            groupManager = new GroupManager();
            Group group = groupManager.getGroupById(groupId);

            if(group.getGroupName() == "default_group")
            {
                auditLogManager.createLog("Delete Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL: Permissions");
                return Response.status(Response.Status.fromStatusCode(401)).entity("You can not delete the default group.").build();
            }

            groupManager.deleteGroup(groupId);

            auditLogManager.createLog("Delete Group", securityContext.getUserPrincipal().toString(),"Group ID: " + groupId,"SUCCESS");

            return Response.ok().entity(groupManager).build();
        }
        catch (Exception e)
        {
            auditLogManager.createLog("Delete Group", securityContext.getUserPrincipal().toString(),"N/A","FAIL");
            return Response.serverError().entity(e.getClass() + ": " + e.getMessage()).build();
        }
        finally
        {
            LOGGER.info("Leaving delete group REST method.");
        }
    }

}

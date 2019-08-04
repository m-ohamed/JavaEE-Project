package com.sumerge.program.tests;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;
import com.sumerge.program.entities.group.GroupViewRegistration;
import com.sumerge.program.entities.user.User;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import static org.junit.Assert.*;

public class GroupTests
{

    private static GroupViewRegistration newGroup;

    @BeforeClass
    public static void init()
    {
        newGroup = new GroupViewRegistration("admin","testingGroup");
    }

    @Test
    public void testCase001CreateGroup()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", "user");
        Response response = client.target("http://localhost:8880/group").path("create")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newGroup,MediaType.APPLICATION_JSON_TYPE));
        assertEquals(401, response.getStatus());


        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/group").path("create")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newGroup,MediaType.APPLICATION_JSON_TYPE));
        Group group = response.readEntity(Group.class);
        assertEquals("testingGroup", group.getGroupName());
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCase002FindGroup()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");
        Response response = client.target("http://localhost:8880/group").path("find").path("1").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Group group = response.readEntity(Group.class);
        assertEquals("default_group",group.getGroupName());
        assertEquals(200, response.getStatus());

        response = client.target("http://localhost:8880/group").path("find").path("100").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(500,response.getStatus());
    }

    @Test
    public void testCase003UpdateGroup()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", "user");
        Response response = client.target("http://localhost:8880/group").path("update")
                .queryParam("groupId",18).queryParam("groupName","newGroupName")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(401, response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/group").path("update")
                .queryParam("groupId",18).queryParam("groupName","newGroupName")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        Group group = response.readEntity(Group.class);
        assertEquals("newGroupName",group.getGroupName());
        assertEquals(200, response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/group").path("update")
                .queryParam("groupId",18).queryParam("groupOwner",1)
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        group = response.readEntity(Group.class);
        assertEquals("admin",group.getOwnerUid().getUsername());
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCase04DeleteGroup()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", "user");
        Response response = client.target("http://localhost:8880/group").path("delete").path("19")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(401,response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/group").path("delete").path("180")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(500,response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/group").path("delete").path("19")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(200,response.getStatus());
    }

}

package com.sumerge.program.tests;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;
import com.sumerge.program.entities.user.User;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import static org.junit.Assert.*;

public class GroupTests
{

//    static Group testGroup;
//
//    @BeforeClass
//    public static void init()
//    {
//        testGroup = new Group("Test",);
//    }

    @Test
    public void testCase001CreateGroup()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", "user");
        Response response = client.target("http://localhost:8880/group").path("create")
                .queryParam("ownerUsername","testingUser").queryParam("groupName","testingGroup")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
        //String group = response.readEntity(String.class);
        //System.out.println(group);
        //assertEquals("admin",group.getGroupName());
        assertEquals(401, response.getStatus());


        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/group").path("create")
                .queryParam("ownerUsername","testingUser").queryParam("groupName","testingGroup")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(null);
        System.out.println(response);
        Group group = response.readEntity(Group.class);
        System.out.println(group);
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

        response = client.target("http://localhost:8880/user").path("find").path("100").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(500,response.getStatus());
    }

    @Test
    public void testCase003UpdateGroup()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");
        Response response = client.target("http://localhost:8880/group").path("update").path("1").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Group group = response.readEntity(Group.class);
        assertEquals("default_group",group.getGroupName());
        assertEquals(200, response.getStatus());
    }

}

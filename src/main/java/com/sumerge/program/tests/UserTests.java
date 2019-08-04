package com.sumerge.program.tests;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.sound.midi.SysexMessage;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;
import com.sumerge.program.entities.user.User;
import com.sumerge.program.entities.user.UserViewRegistration;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import static org.junit.Assert.*;

public class UserTests
{
    static UserViewRegistration newUser;

    @BeforeClass
    public static void init()
    {
        newUser = new UserViewRegistration("testUser1232",
                "FirstTest","LastTest","first@last.com","user","user");
    }

    @Test
    public void testCase01GetUser()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");
        Response response = client.target("http://localhost:8880/user").path("find").path("1").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        User user = response.readEntity(User.class);
        assertEquals("admin",user.getUsername());
        assertEquals(200, response.getStatus());

        response = client.target("http://localhost:8880/user").path("find").path("100").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testCase02CreateUser()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", "user");

        Response response = client.target("http://localhost:8880/user").path("create")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newUser,MediaType.APPLICATION_JSON_TYPE));

        assertEquals(401,response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");

        newUser.setEmail(null);
        response = client.target("http://localhost:8880/user").path("create")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newUser,MediaType.APPLICATION_JSON_TYPE));
        assertEquals(500, response.getStatus());

        newUser.setEmail("New Email");
        response = client.target("http://localhost:8880/user/create/")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newUser,MediaType.APPLICATION_JSON_TYPE));
        assertEquals(200, response.getStatus());

    }

    @Test
    public void testCase03GetAllUsers()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", "user");

        Response response = client.target("http://localhost:8880/user").path("getAll").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertTrue(response.readEntity(List.class).size() > 0);

        feature = HttpAuthenticationFeature.basic("admin", "admin");

        response = client.target("http://localhost:8880/user").path("getAll").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertTrue(response.readEntity(List.class).size() > 0);
    }

    @Test
    public void testCase04UpdateUser()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("testingUser", "user");
        Response response = client.target("http://localhost:8880/user").path("update").queryParam("username","ahmed")
                .queryParam("firstName","Ahmed").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(401, response.getStatus());


        feature = HttpAuthenticationFeature.basic("testingUser", "user");
        response = client.target("http://localhost:8880/user").path("update").queryParam("username","testingUser")
                .queryParam("firstName","TestingUser").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(200, response.getStatus());
        User user = response.readEntity(User.class);
        assertEquals("TestingUser",user.getFirstName());


        feature = HttpAuthenticationFeature.basic("testingUser", "user");
        response = client.target("http://localhost:8880/user").path("update").queryParam("username","testingUser")
                .queryParam("currentPassword","admin").queryParam("newPassword","useruser")
                .register(feature).register(JacksonJsonProvider.class).request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(500, response.getStatus());


        feature = HttpAuthenticationFeature.basic("testingUser", "user");
        response = client.target("http://localhost:8880/user").path("update").queryParam("username","testingUser")
                .queryParam("currentPassword","user").queryParam("newPassword","useruser")
                .register(feature).register(JacksonJsonProvider.class).request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(200, response.getStatus());


        feature = HttpAuthenticationFeature.basic("testingUser", "user");
        response = client.target("http://localhost:8880/user").path("update").queryParam("username","testingUser")
                .queryParam("currentPassword","useruser").queryParam("newPassword","user")
                .register(feature).register(JacksonJsonProvider.class).request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(401, response.getStatus());


        feature = HttpAuthenticationFeature.basic("testingUser", "useruser");
        response = client.target("http://localhost:8880/user").path("update").queryParam("username","testingUser")
                .queryParam("currentPassword","useruser").queryParam("newPassword","user")
                .register(feature).register(JacksonJsonProvider.class).request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCase05AddUser() throws SQLIntegrityConstraintViolationException {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("testingUser", "user");
        Response response = client.target("http://localhost:8880/user").path("addUser").queryParam("username","testingUser")
                .queryParam("groupId","1").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(401, response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/user").path("addUser").queryParam("username","testingUser")
                .queryParam("groupId","1").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(200, response.getStatus());
        User user = response.readEntity(User.class);
    }

    @Test
    public void testCase06MoveUser()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("testingUser", "user");
        Response response = client.target("http://localhost:8880/user").path("move").queryParam("oldGroupId","1")
                .queryParam("newGroupId","7").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(401, response.getStatus());


        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/user").path("move").queryParam("username","testingUser")
                .queryParam("oldGroupId","1").queryParam("newGroupId","7")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(200, response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/user").path("move").queryParam("username","testingUser")
                .queryParam("oldGroupId","7").queryParam("newGroupId","1")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCase07RemoveUser()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("testingUser", "user");
        Response response = client.target("http://localhost:8880/user").path("removeUser").queryParam("username","testingUser")
                .queryParam("groupId","1").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(401, response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/user").path("removeUser").queryParam("username","testingUser")
                .queryParam("groupId","1").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(200, response.getStatus());
        User user = response.readEntity(User.class);
    }

    @Test
    public void testCase08DeleteUser()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("testingUser", "user");
        Response response = client.target("http://localhost:8880/user").path("delete").path("testingUser")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(401, response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/user").path("delete").path("testingUser")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(200, response.getStatus());
        User user = response.readEntity(User.class);
        assertTrue(user.isDeleted());
    }

    @Test
    public void testCase09RestoreUser()
    {
        Client client = ClientBuilder.newBuilder().register(String.class).build();
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("testingUser", "user");
        Response response = client.target("http://localhost:8880/user").path("restore").path("testingUser")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(401, response.getStatus());

        feature = HttpAuthenticationFeature.basic("admin", "admin");
        response = client.target("http://localhost:8880/user").path("restore").path("testingUser")
                .register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        assertEquals(200, response.getStatus());
        User user = response.readEntity(User.class);
        assertFalse(user.isDeleted());
    }

    public static String getDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        return dateFormat.format(date);
    }
}

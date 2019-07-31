package com.sumerge.program.entities.user;

import com.sumerge.program.entities.auditlog.AuditLog;
import com.sumerge.program.entities.auditlog.AuditLogManager;
import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;
import org.apache.log4j.Logger;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Stateless
public class UserManager
{
    //@PersistenceContext(unitName = "MyPU", type = PersistenceContextType.TRANSACTION)
    //EntityManager em;

    @PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());

    private AuditLogManager auditLogManager;

    public User createUser(String username, String firstName, String lastName, String email, String password, String role, String actionAuthor)
    {
        //LOGGER.debug("Entering create user method.");
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        String hashedPassword = null;
        try {
            hashedPassword = sha256(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(hashedPassword != null)
            user.setPassword(hashedPassword);
        else
            user.setPassword("default_password");

        user.setRole(role);
        user.setDeleted(false);

        auditLogManager.createLog("Create User",actionAuthor,user,"SUCCESS");

        em.persist(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving create user method.");

        return user;
    }

    public List<User> getAllUsers(boolean isAdmin)
    {
        //LOGGER.debug("Entering get all users method.");

        EntityManager em = emf.createEntityManager();

        List<User> usersList;

        if(isAdmin)
            usersList = em.createNamedQuery("User.getAll", User.class).getResultList();
        else
            usersList = em.createNamedQuery("User.findAll", User.class).getResultList();

        LOGGER.debug("Leaving get all users method.");

        return usersList;
    }

    public User getUserById(int userId, boolean isAdmin)
    {
        //LOGGER.debug("Entering get user by ID method.");

        EntityManager em = emf.createEntityManager();

        User user;

        if(isAdmin)
            user = em.createNamedQuery("User.get", User.class).setParameter("userId",userId).getSingleResult();
        else
            user = em.createNamedQuery("User.find", User.class).setParameter("userId",userId).getSingleResult();

        LOGGER.debug("Leaving get user by ID method.");

        return user;
    }

    public User getUserByUsername(String username)
    {
        //LOGGER.debug("Entering get user by username method.");

        EntityManager em = emf.createEntityManager();

        User user = em.createNamedQuery("User.UsernameGet", User.class).setParameter("username",username).getSingleResult();

        LOGGER.debug("Leaving get user by username method.");

        return user;
    }

    public User updateUserFirstName(String username, String firstName, String actionAuthor)
    {
        //LOGGER.debug("Entering update user first name method.");
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setFirstName(firstName);

        auditLogManager.createLog("Update User", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();


        LOGGER.debug("Leaving update user first name method.");

        return user;
    }

    public User updateUserLastName(String username, String lastName, String actionAuthor)
    {
        //LOGGER.debug("Entering update user last name method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setLastName(lastName);

        auditLogManager.createLog("Update User", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user last name method.");

        return user;
    }

    public User updateUserEmail(String username, String email, String actionAuthor)
    {
        //LOGGER.debug("Entering update user email method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setEmail(email);

        auditLogManager.createLog("Update User", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user email method.");

        return user;
    }

    public User updateUserPassword(String username, String currentPassword, String newPassword, String actionAuthor)
    {
        //LOGGER.debug("Entering update user password method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);

        String currentHashedPassword = null;
        String newHashedPassword = null;

        try {
            currentHashedPassword = sha256(currentPassword);
            newHashedPassword = sha256(newPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(user.getPassword() == currentHashedPassword)
            user.setPassword(newHashedPassword);

        auditLogManager.createLog("Update User", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user password method.");

        return user;
    }

    public User updateUserRole(String username, String role, String actionAuthor)
    {
        //LOGGER.debug("Entering update user role method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setRole(role);

        auditLogManager.createLog("Update User", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user role method.");

        return user;
    }

    public void addUser(String username, int groupId, String actionAuthor)
    {
        //LOGGER.debug("Entering add user to group method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        GroupManager groupManager = new GroupManager();
        User user = getUserByUsername(username);
        Group group = groupManager.getGroupById(groupId);

        user.getGroups().add(group);

        auditLogManager.createLog("Add User To Group", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving add user to group method.");
    }

    public User removeUser(String username, int groupId, String actionAuthor)
    {
        //LOGGER.debug("Entering remove user from group method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = getUserByUsername(username);

        int i;
        for(i = 0; i < user.getGroups().size(); i++)
        {
            if(user.getGroups().get(i).getGroupId() == groupId)
                break;
        }

        user.getGroups().remove(i);

        auditLogManager.createLog("Remove User From Group", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving remove user from group method.");

        return user;
    }

    public User restoreDeleteUser(int userId, int flag, String actionAuthor)
    {
        //LOGGER.debug("Entering restore/delete user method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserById(userId,true);
        if(flag == 1)
            user.setDeleted(true);
        else
            user.setDeleted(false);


        auditLogManager.createLog("Restore/Delete User", actionAuthor, user,"SUCCESS");

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving restore/delete user method.");
        return user;
    }


    public static String sha256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //LOGGER.debug("Entering hashing method.");

        MessageDigest md5 = MessageDigest.getInstance("SHA-256");
        byte[] digest = md5.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; ++i) {
            sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
        }

        LOGGER.debug("Leaving hashing method.");
        return sb.toString();
    }
}

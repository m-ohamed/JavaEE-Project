package com.sumerge.program.entities.user;

import com.sumerge.program.entities.auditlog.AuditLog;
import com.sumerge.program.entities.auditlog.AuditLogManager;
import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;
import com.sumerge.program.exceptions.MissingParameterException;
import com.sumerge.program.exceptions.UsernameAlreadyExistsException;
import com.sumerge.program.exceptions.WrongPasswordException;
import org.apache.log4j.Logger;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.transaction.Transactional;

@Stateless
public class UserManager
{
    @PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());

    private AuditLogManager auditLogManager;

    public User createUser(String username, String firstName, String lastName, String email, String password, String role, String actionAuthor) throws MissingParameterException, UsernameAlreadyExistsException, NoResultException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = new User();

        if(username == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Username can not be null!");
        }
        else
        {

            //Username check not working
//            if(getUserByUsername(username) != null)
//            {
//                em.getTransaction().rollback();
//                throw new UsernameAlreadyExistsException("Username already exists!");
            //}
            //else
                user.setUsername(username);

        }

        if(firstName == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("First name can not be null!");
        }
        else
            user.setFirstName(firstName);

        if(lastName == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Last name can not be null!");
        }
        else
            user.setLastName(lastName);

        if(email == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Email can not be null!");
        }
        else
            user.setEmail(email);

        String hashedPassword = null;
        try {
            hashedPassword = sha256(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(hashedPassword == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Password can not be null!");
        }
        else
            user.setPassword(hashedPassword);

        if(role == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Role can not be null!");
        }
        else
            user.setRole(role);

        user.setDeleted(false);

        auditLogManager.createLog("Create User",actionAuthor,user,user.getUsername());

        em.persist(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving create user method.");

        return user;
    }

    public List<User> getAllUsers(boolean isAdmin)
    {
        EntityManager em = emf.createEntityManager();

        List<User> usersList;

        if(isAdmin)
            usersList = em.createNamedQuery("User.getAll", User.class).getResultList();
        else
            usersList = em.createNamedQuery("User.findAll", User.class).getResultList();

        LOGGER.debug("Leaving get all users method.");

        return usersList;
    }

    public User getUserById(int userId, boolean isAdmin) throws NoResultException
    {
        EntityManager em = emf.createEntityManager();

        User user;

        if(isAdmin)
            user = em.createNamedQuery("User.get", User.class).setParameter("userId",userId).getSingleResult();
        else
            user = em.createNamedQuery("User.find", User.class).setParameter("userId",userId).getSingleResult();

//        if(user == null)
//        {
//            //em.getTransaction().rollback();
//            throw new NoResultException("User not found!");
//        }

        LOGGER.debug("Leaving get user by ID method.");

        return user;
    }

    public User getUserByUsername(String username) throws NoResultException
    {
        EntityManager em = emf.createEntityManager();

        User user = em.createNamedQuery("User.UsernameGet", User.class).setParameter("username",username).getSingleResult();

//        if(user == null)
//        {
//            //em.getTransaction().rollback();
//            throw new SQLIntegrityConstraintViolationException("User not found!");
//        }

        LOGGER.debug("Leaving get user by username method.");

        return user;
    }

    public User updateUserFirstName(String username, String firstName, String actionAuthor) throws MissingParameterException, SQLIntegrityConstraintViolationException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);

        if(firstName == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("First name can not be null!");
        }
        else
            user.setFirstName(firstName);

        auditLogManager.createLog("Update User", actionAuthor, user,user.getUsername());

        em.merge(user);
        em.getTransaction().commit();


        LOGGER.debug("Leaving update user first name method.");

        return user;
    }

    public User updateUserLastName(String username, String lastName, String actionAuthor) throws MissingParameterException
    {

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);

        if(lastName == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Last name can not be null!");
        }
        else
            user.setLastName(lastName);

        auditLogManager.createLog("Update User", actionAuthor, user,user.getUsername());

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user last name method.");

        return user;
    }

    public User updateUserEmail(String username, String email, String actionAuthor) throws MissingParameterException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);

        if(email == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Email can not be null!");
        }
        else
            user.setEmail(email);

        auditLogManager.createLog("Update User", actionAuthor, user,user.getUsername());

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user email method.");

        return user;
    }

    public User updateUserPassword(String username, String currentPassword, String newPassword, String actionAuthor) throws MissingParameterException, WrongPasswordException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);

        if(currentPassword == null || newPassword == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Password can not be null!");
        }

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

        if(user.getPassword().equals(currentHashedPassword))
            user.setPassword(newHashedPassword);
        else
        {
            em.getTransaction().rollback();
            throw new WrongPasswordException("Password does not match the saved password!");
        }

        auditLogManager.createLog("Update User", actionAuthor, user,user.getUsername());

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user password method.");

        return user;
    }

    public User updateUserRole(String username, String role, String actionAuthor) throws MissingParameterException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);

        if(role == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Role can not be null!");
        }
        else
            user.setRole(role);

        auditLogManager.createLog("Update User", actionAuthor, user,user.getUsername());

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update user role method.");

        return user;
    }

    public void addUser(String username, int groupId, String actionAuthor) throws SQLIntegrityConstraintViolationException, MissingParameterException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if(groupId == 0)
            throw new MissingParameterException("Group ID can not be empty or 0.");

        GroupManager groupManager = new GroupManager();
        User user = getUserByUsername(username);
        Group group = groupManager.getGroupById(groupId);

        user.getGroups().add(group);

        auditLogManager.createLog("Add User To Group", actionAuthor, user,user.getUsername());

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving add user to group method.");
    }

    public User removeUser(String username, int groupId, String actionAuthor) throws MissingParameterException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if(groupId == 0)
            throw new MissingParameterException("Group ID can not be empty or 0.");

        User user = getUserByUsername(username);

        int i;
        for(i = 0; i < user.getGroups().size(); i++)
        {
            if(user.getGroups().get(i).getGroupId() == groupId)
                break;
        }

        user.getGroups().remove(i);

        auditLogManager.createLog("Remove User From Group", actionAuthor, user,user.getUsername());

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving remove user from group method.");

        return user;
    }

    public User restoreDeleteUser(String username, int flag, String actionAuthor)
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);

        if(flag == 1)
        {
            user.setDeleted(true);
            auditLogManager.createLog("Delete User", actionAuthor, user,user.getUsername());
        }
        else
        {
            user.setDeleted(false);
            auditLogManager.createLog("Restore User", actionAuthor, user,user.getUsername());
        }

        em.merge(user);
        em.getTransaction().commit();

        LOGGER.debug("Leaving restore/delete user method.");
        return user;
    }

    public static String sha256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
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

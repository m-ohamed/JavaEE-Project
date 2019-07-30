package com.sumerge.program.entities.user;

import com.sumerge.program.entities.group.Group;
import com.sumerge.program.entities.group.GroupManager;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Stateless
public class UserManager
{
    //@PersistenceContext(unitName = "MyPU", type = PersistenceContextType.TRANSACTION)
    //EntityManager em;

    @PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    //private static final int ITERATIONS = 65536;
    //private static final int KEY_LENGTH = 512;
    //private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    public void createUser(String username, String firstName, String lastName, String email, String password, String role)
    {
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

        em.persist(user);
        em.getTransaction().commit();
    }

    public List<User> getAllUsers(boolean isAdmin)
    {
        EntityManager em = emf.createEntityManager();

        if(isAdmin)
            return em.createNamedQuery("User.getAll", User.class).getResultList();
        else
            return em.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public User getUserById(int userId, boolean isAdmin)
    {
        EntityManager em = emf.createEntityManager();

        if(isAdmin)
            return em.createNamedQuery("User.get", User.class).setParameter("userId",userId).getSingleResult();
        else
            return em.createNamedQuery("User.find", User.class).setParameter("userId",userId).getSingleResult();

    }

    public User getUserByUsername(String username)
    {
        EntityManager em = emf.createEntityManager();

        return em.createNamedQuery("User.UsernameGet", User.class).setParameter("username",username).getSingleResult();
    }

    public void updateUserFirstName(String username, String firstName)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setFirstName(firstName);
        em.merge(user);
        em.getTransaction().commit();
    }

    public void updateUserLastName(String username, String lastName)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setLastName(lastName);
        em.merge(user);
        em.getTransaction().commit();
    }

    public void updateUserEmail(String username, String email)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setEmail(email);
        em.merge(user);
        em.getTransaction().commit();
    }

    public void updateUserPassword(String username, String currentPassword, String newPassword)
    {
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

        em.merge(user);
        em.getTransaction().commit();
    }

    public void updateUserRole(String username, String role)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserByUsername(username);
        user.setRole(role);

        em.merge(user);
        em.getTransaction().commit();
    }

    public void addUser(String username, int groupId)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        GroupManager groupManager = new GroupManager();
        User user = getUserByUsername(username);
        Group group = groupManager.getGroupById(groupId);

        user.getGroups().add(group);
        em.merge(user);

        em.getTransaction().commit();
    }

    public void removeUser(String username, int groupId)
    {
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
        em.merge(user);

        em.getTransaction().commit();
    }

    public void restoreDeleteUser(int userId, int flag)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = getUserById(userId,true);
        if(flag == 1)
            user.setDeleted(true);
        else
            user.setDeleted(false);


        em.merge(user);
        em.getTransaction().commit();
    }


    public static String sha256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("SHA-256");
        byte[] digest = md5.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; ++i) {
            sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}

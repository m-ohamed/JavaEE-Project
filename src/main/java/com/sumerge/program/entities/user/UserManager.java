package com.sumerge.program.entities.user;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Stateless
public class UserManager
{
    @PersistenceContext
    EntityManager em;
    //@PersistenceUnit
    //EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    public void createUser(String username, String firstName, String lastName, String email, String password, String role, boolean isDeleted)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();

        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        Optional<String> hashedPassword = hashPassword(password);
        //user.setPassword(hashedPassword.get());
        user.setPassword(password);

        user.setRole(role);
        user.setDeleted(false);

        em.persist(user);
        //em.getTransaction().commit();
    }

    public User getUserById(int userId)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        User user = em.find(User.class, userId);
        //em.getTransaction().commit();
        return user;
    }

    public void updateUserFirstName(int userId, String firstName)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        User user = getUserById(userId);
        user.setFirstName(firstName);
        em.persist(user);
        //em.getTransaction().commit();
    }

    public void updateUserLastName(int userId, String lastName)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        User user = getUserById(userId);
        user.setLastName(lastName);
        em.persist(user);
        //em.getTransaction().commit();
    }

    public void updateUserEmail(int userId, String email)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        User user = getUserById(userId);
        user.setEmail(email);
        em.persist(user);
        //em.getTransaction().commit();
    }

    public void updateUserPassword(int userId, String currentPassword, String newPassword)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        User user = getUserById(userId);

        Optional<String> currentHashedPassword = hashPassword(currentPassword);
        Optional<String> newHashedPassword = hashPassword(newPassword);
        //user.setPassword(hashedPassword.get());

        if(user.getPassword() == currentHashedPassword.get())
            user.setPassword(newHashedPassword.get());

        em.persist(user);

        //em.getTransaction().commit();
    }

    public void updateUserRole(int userId, String role)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        User user = getUserById(userId);
        user.setRole(role);

        em.persist(user);
        //em.getTransaction().commit();
    }

    public void restoreDeleteUser(int userId, int flag)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        User user = getUserById(userId);
        if(flag == 1)
            user.setDeleted(true);
        else
            user.setDeleted(false);


        em.persist(user);
        //em.getTransaction().commit();
    }


    public static Optional<String> hashPassword (String password) {

        String salt = "m2&&aVh@+e3G5QNE";

        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return Optional.of(Base64.getEncoder().encodeToString(securePassword));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Exception encountered in hashPassword()");
            return Optional.empty();

        } finally {
            spec.clearPassword();
        }
    }
}

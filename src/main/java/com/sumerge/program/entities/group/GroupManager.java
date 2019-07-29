package com.sumerge.program.entities.group;

import com.sumerge.program.entities.user.User;
import com.sumerge.program.rest.UserResource;

import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
public class GroupManager
{
    @PersistenceContext(unitName = "MyPU")
    EntityManager em;

    //@PersistenceUnit
    //EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    public void createGroup(int ownerUid, String groupName)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();

        Group group = new Group();
        group.setGroupName(groupName);
        User user = em.find(User.class, ownerUid);
        group.setOwnerUid(user);

        em.persist(group);
        //em.getTransaction().commit();
    }

    public Group getGroupById(int groupId)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        System.out.println("in getGroupByID");
        try
        {
            Group group = em.find(Group.class, groupId);
            System.out.println("got ID");
            //em.getTransaction().commit();
            return group;
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }

        return new Group();
    }

    public void updateGroupName(int groupId, String groupName)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        Group group = getGroupById(groupId);
        group.setGroupName(groupName);
        em.persist(group);
        //em.getTransaction().commit();
    }

    public void updateGroupOwner(int groupId, int ownerUid)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        Group group = getGroupById(groupId);
        User user = em.find(User.class, ownerUid);
        group.setOwnerUid(user);
        em.persist(group);
        //em.getTransaction().commit();
    }

    public void deleteGroup(int groupId)
    {
        //EntityManager em = emf.createEntityManager();
        //em.getTransaction().begin();
        Group group = getGroupById(groupId);

        if(!em.contains(group))
            group = em.merge(group);

        em.remove(group);
        //em.getTransaction().commit();
    }
}

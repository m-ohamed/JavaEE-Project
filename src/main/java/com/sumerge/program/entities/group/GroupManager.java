package com.sumerge.program.entities.group;

import com.sumerge.program.entities.user.User;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
public class GroupManager
{
    //@PersistenceContext(unitName = "MyPU", type = PersistenceContextType.TRANSACTION)
    //EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(GroupManager.class.getName());

    @PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    public Group createGroup(int ownerUid, String groupName)
    {
        LOGGER.info("Entering create group method.");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Group group = new Group();
        group.setGroupName(groupName);
        User user = em.find(User.class, ownerUid);
        group.setOwnerUid(user);

        em.persist(group);
        em.getTransaction().commit();

        LOGGER.info("Leaving create group method.");
        return group;
    }

    public Group getGroupById(int groupId)
    {
        LOGGER.info("Entering get group by ID method.");

        EntityManager em = emf.createEntityManager();
        Group group = em.find(Group.class, groupId);

        LOGGER.info("Leaving get group by ID method.");

        return group;
    }

    public Group updateGroupName(int groupId, String groupName)
    {
        LOGGER.info("Entering update group name method.");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);
        group.setGroupName(groupName);
        em.merge(group);
        em.getTransaction().commit();

        LOGGER.info("Leaving update group name method.");

        return group;
    }

    public Group updateGroupOwner(int groupId, int ownerUid)
    {
        LOGGER.info("Entering update group owner method.");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);
        User user = em.find(User.class, ownerUid);
        group.setOwnerUid(user);
        em.merge(group);
        em.getTransaction().commit();

        LOGGER.info("Leaving update group owner method.");

        return group;
    }

    public void deleteGroup(int groupId)
    {
        LOGGER.info("Entering delete group method.");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);

        if(!em.contains(group))
            group = em.merge(group);

        em.remove(group);
        em.getTransaction().commit();

        LOGGER.info("Leaving delete group method.");
    }
}

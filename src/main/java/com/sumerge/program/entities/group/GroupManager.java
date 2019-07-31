package com.sumerge.program.entities.group;

import com.sumerge.program.entities.auditlog.AuditLogManager;
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
    private AuditLogManager auditLogManager;

    @PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    public Group createGroup(int ownerUid, String groupName, String actionAuthor)
    {
        auditLogManager = new AuditLogManager();

        //LOGGER.debug("Entering create group method.");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Group group = new Group();
        group.setGroupName(groupName);
        User user = em.find(User.class, ownerUid);
        group.setOwnerUid(user);

        auditLogManager.createLog("Create Group",actionAuthor,group,"SUCCESS");

        em.persist(group);
        em.getTransaction().commit();

        LOGGER.debug("Leaving create group method.");
        return group;
    }

    public Group getGroupById(int groupId)
    {
        //LOGGER.debug("Entering get group by ID method.");

        EntityManager em = emf.createEntityManager();
        Group group = em.find(Group.class, groupId);

        LOGGER.debug("Leaving get group by ID method.");

        return group;
    }

    public Group updateGroupName(int groupId, String groupName, String actionAuthor)
    {
        //LOGGER.debug("Entering update group name method.");
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);
        group.setGroupName(groupName);

        auditLogManager.createLog("Update Group",actionAuthor,group,"SUCCESS");

        em.merge(group);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update group name method.");

        return group;
    }

    public Group updateGroupOwner(int groupId, int ownerUid, String actionAuthor)
    {
        //LOGGER.debug("Entering update group owner method.");

        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);
        User user = em.find(User.class, ownerUid);
        group.setOwnerUid(user);

        auditLogManager.createLog("Update Group",actionAuthor,group,"SUCCESS");

        em.merge(group);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update group owner method.");

        return group;
    }

    public void deleteGroup(int groupId, String actionAuthor)
    {
        //LOGGER.debug("Entering delete group method.");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);

        if(!em.contains(group))
            group = em.merge(group);

        auditLogManager.createLog("Delete Group",actionAuthor,group,"SUCCESS");

        em.remove(group);
        em.getTransaction().commit();

        LOGGER.debug("Leaving delete group method.");
    }
}

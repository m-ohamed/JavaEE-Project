package com.sumerge.program.entities.group;

import com.sumerge.program.entities.auditlog.AuditLogManager;
import com.sumerge.program.entities.user.User;
import com.sumerge.program.entities.user.UserManager;
import com.sumerge.program.exceptions.MissingParameterException;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.SQLIntegrityConstraintViolationException;

@Stateless
public class GroupManager
{
    private static final Logger LOGGER = Logger.getLogger(GroupManager.class.getName());
    private AuditLogManager auditLogManager;

    @PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    public Group createGroup(int ownerUid, String groupName, String actionAuthor) throws MissingParameterException, SQLIntegrityConstraintViolationException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Group group = new Group();

        if(groupName == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Group name can not be null!");
        }
        else
            group.setGroupName(groupName);

        User user = em.find(User.class, ownerUid);

        if(user == null)
        {
            em.getTransaction().rollback();
            throw new SQLIntegrityConstraintViolationException("User not found!");
        }
        else
            group.setOwnerUid(user);

        auditLogManager.createLog("Create Group",actionAuthor,group,""+group.getGroupId());

        em.persist(group);
        em.getTransaction().commit();

        LOGGER.debug("Leaving create group method.");
        return group;
    }

    public Group getGroupById(int groupId) throws SQLIntegrityConstraintViolationException
    {
        EntityManager em = emf.createEntityManager();
        Group group = em.find(Group.class, groupId);

        if(group == null)
            throw new SQLIntegrityConstraintViolationException("Group not found!");

        LOGGER.debug("Leaving get group by ID method.");

        return group;
    }

    public Group updateGroupName(int groupId, String groupName, String actionAuthor) throws MissingParameterException, SQLIntegrityConstraintViolationException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);

        if(groupName == null)
        {
            em.getTransaction().rollback();
            throw new MissingParameterException("Group name can not be null!");
        }
        else
            group.setGroupName(groupName);

        auditLogManager.createLog("Update Group",actionAuthor,group,""+group.getGroupId());

        em.merge(group);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update group name method.");

        return group;
    }

    public Group updateGroupOwner(int groupId, int ownerUid, String actionAuthor) throws SQLIntegrityConstraintViolationException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Group group = getGroupById(groupId);

        UserManager userManager = new UserManager();
        User user = userManager.getUserById(ownerUid, true);

        group.setOwnerUid(user);

        auditLogManager.createLog("Update Group",actionAuthor,group,""+group.getGroupId());

        em.merge(group);
        em.getTransaction().commit();

        LOGGER.debug("Leaving update group owner method.");

        return group;
    }

    public void deleteGroup(int groupId, String actionAuthor) throws SQLIntegrityConstraintViolationException
    {
        auditLogManager = new AuditLogManager();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Group group = getGroupById(groupId);

        if(!em.contains(group))
            group = em.merge(group);

        auditLogManager.createLog("Delete Group",actionAuthor,group,""+group.getGroupId());

        em.remove(group);

        em.getTransaction().commit();

        LOGGER.debug("Leaving delete group method.");
    }
}

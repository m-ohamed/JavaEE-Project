package com.sumerge.program.entities.auditlog;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless
public class AuditLogManager
{
    @PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPU");

    public void createLog(String actionName, String actionAuthor, String entityDetails, String actionStatus)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        AuditLog auditLog = new AuditLog();
        auditLog.setActionName(actionName);
        auditLog.setActionTime(getDateTime());
        auditLog.setActionAuthor(actionAuthor);
        auditLog.setEntityDetails(entityDetails);
        auditLog.setActionStatus(actionStatus);

        em.persist(auditLog);
        em.getTransaction().commit();
    }



    public static String getDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        return dateFormat.format(date);
    }

}

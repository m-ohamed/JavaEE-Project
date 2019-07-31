package com.sumerge.program.entities.auditlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public void createLog(String actionName, String actionAuthor, Object entityDetails, String actionStatus)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        AuditLog auditLog = new AuditLog();
        auditLog.setActionName(actionName);
        auditLog.setActionTime(getDateTime());
        auditLog.setActionAuthor(actionAuthor);

        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            auditLog.setEntityDetails(objectMapper.writeValueAsString(entityDetails));
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

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

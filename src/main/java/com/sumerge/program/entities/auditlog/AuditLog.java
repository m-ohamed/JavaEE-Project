package com.sumerge.program.entities.auditlog;



import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "auditlog")
public class AuditLog
{
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ACTION_NAME", nullable = false)
    private String actionName;

    @Column(name = "ACTION_TIME", nullable = false)
    private String actionTime;

    @Column(name = "ACTION_AUTHOR", nullable = false)
    private String actionAuthor;

    @Column(name = "ENTITY_DETAILS", nullable = false)
    private String entityDetails;

    @Column(name = "ACTION_STATUS", nullable = false)
    private String actionStatus;

    public AuditLog(){}

    public AuditLog(String actionName, String actionTime, String actionAuthor, String entityDetails, String actionStatus) {
        this.actionName = actionName;
        this.actionTime = actionTime;
        this.actionAuthor = actionAuthor;
        this.entityDetails = entityDetails;
        this.actionStatus = actionStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionAuthor() {
        return actionAuthor;
    }

    public void setActionAuthor(String actionAuthor) {
        this.actionAuthor = actionAuthor;
    }

    public Object getEntityDetails() {
        return entityDetails;
    }

    public void setEntityDetails(String entityDetails) {
        this.entityDetails = entityDetails;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }
}

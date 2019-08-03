package com.sumerge.program.entities.auditlog;



import javax.persistence.*;

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

    @Column(name = "ACTION_IDENTIFIER", nullable = false)
    private String actionIdentifier;

    public AuditLog(){}

    public AuditLog(String actionName, String actionTime, String actionAuthor, String entityDetails, String actionIdentifier) {
        this.actionName = actionName;
        this.actionTime = actionTime;
        this.actionAuthor = actionAuthor;
        this.entityDetails = entityDetails;
        this.actionIdentifier = actionIdentifier;
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

    public String getActionIdentifier() {
        return actionIdentifier;
    }

    public void setActionIdentifier(String actionIdentifier) {
        this.actionIdentifier = actionIdentifier;
    }
}

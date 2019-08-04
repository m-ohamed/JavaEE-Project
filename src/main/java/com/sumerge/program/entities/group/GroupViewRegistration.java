package com.sumerge.program.entities.group;

public class GroupViewRegistration
{
    private String ownerUsername;
    private String groupName;

    public GroupViewRegistration(){}

    public GroupViewRegistration(String ownerUsername, String groupName) {
        this.ownerUsername = ownerUsername;
        this.groupName = groupName;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "GroupViewRegistration{" +
                "ownerUsername='" + ownerUsername + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}

package com.sumerge.program.entities.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sumerge.program.entities.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workgroups")
public class Group implements Serializable
{
    @Id
    @Column(name = "GID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;

    @ManyToOne
    @JoinColumn(name = "OWNER_UID", nullable = false)
    private User ownerUid;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @ManyToMany
    @JoinTable(
            name = "groupmember",
            joinColumns = @JoinColumn(name = "GID"),
            inverseJoinColumns = @JoinColumn(name = "UID"))
    private List<User> users = new ArrayList<>();

    public Group(){}

    public Group(int groupId, User ownerUid, String groupName, List<User> users) {
        this.groupId = groupId;
        this.ownerUid = ownerUid;
        this.groupName = groupName;
        this.users = users;
    }

    public User getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(User ownerUid) {
        this.ownerUid = ownerUid;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", ownerUid=" + ownerUid +
                ", groupName='" + groupName + '\'' +
                ", users=" + users +
                '}';
    }
}

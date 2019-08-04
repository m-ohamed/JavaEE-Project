package com.sumerge.program.entities.user;

import com.sumerge.program.entities.group.Group;

import java.util.ArrayList;
import java.util.List;

public class UserViewAdmin
{
    String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean isDeleted;
    private List<Group> groups = new ArrayList<>();

    public UserViewAdmin(){}

    public UserViewAdmin(String username, String firstName, String lastName, String email, String role, boolean isDeleted, List<Group> groups) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.isDeleted = isDeleted;
        this.groups = groups;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}

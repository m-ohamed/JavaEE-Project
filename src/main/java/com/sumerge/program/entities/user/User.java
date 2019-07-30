package com.sumerge.program.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sumerge.program.entities.group.Group;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT e.username, e.firstName, e.lastName, e.email FROM User e WHERE e.isDeleted = false"),
        @NamedQuery(name = "User.getAll", query = "SELECT e FROM User e"),
        @NamedQuery(name = "User.find", query = "SELECT e.username, e.firstName, e.lastName, e.email from User e WHERE e.userId = :userId"),
        @NamedQuery(name = "User.get", query = "SELECT e from User e WHERE e.userId = :userId"),
        @NamedQuery(name = "User.UsernameGet", query = "SELECT e from User e WHERE e.username = :username")
})
public class User implements Serializable
{
    @Id
    @Column(name = "UID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "USERNAME", unique = true)
    String username;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "IS_DELETED")
    private boolean isDeleted;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "groupmember",
            joinColumns = @JoinColumn(name = "UID"),
            inverseJoinColumns = @JoinColumn(name = "GID"))
    private List<Group> groups = new ArrayList<>();

    public User(){}

    public User(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String username, String firstName, String lastName, String email, String password, String role, boolean isDeleted, List<Group> groups) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", isDeleted=" + isDeleted +
                ", groups=" + groups +
                '}';
    }
}

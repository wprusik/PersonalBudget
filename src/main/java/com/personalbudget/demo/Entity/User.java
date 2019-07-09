package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="users")
@Table(name="users")
public class User {

    @Id
    @Column(name="username")
    private String username;
    
    @Column(name="password")
    private String password;
    
    @Column(name="enabled")
    private int enabled;
    
    @Transient
    private String repeatPassword;
    
    /*
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="authorities",joinColumns=@JoinColumn(name="username"), inverseJoinColumns=@JoinColumn(name="authority"))
    private List<Authority> authorities;
    */

    public User() {
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int isEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}

package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity(name="users")
@Table(name="users")
@Getter @Setter
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
    
    public User() {
        
    }    
}

package com.personalbudget.demo.user.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity(name="users_activation")
@Table(name="users_activation")
@Getter @Setter
public class UserActivation {

    @Id
    @Column(name="username")
    private String username;
    
    @Column(name="activation_code")
    private String activationCode;
    
    @Column(name="expiration")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime expiration;

    public UserActivation() {
    }    
    
    public UserActivation(String username, String activationCode, LocalDateTime expiration) {
        this.username = username;
        this.activationCode = activationCode;
        this.expiration = expiration;
    }
    
}

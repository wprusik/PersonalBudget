package com.personalbudget.demo.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity(name="authorities")
@Table(name="authorities")
@Getter @Setter
public class Authority {

    @Id    
    @Column(name="username")
    private String username;
    
    @Column(name="authority")
    private String authority;

    public Authority() {
    }
}

package com.personalbudget.demo.user.service;

import com.personalbudget.demo.user.Authority;
import com.personalbudget.demo.user.User;
import java.util.ArrayList;
import java.util.List;


public interface UserService {

    public List<User> getUsers();
    
    public void saveUser(User theUser);
    
    public void saveUser(User theUser, ArrayList<Authority> authorities);
    
    public User getUser(String username);
}

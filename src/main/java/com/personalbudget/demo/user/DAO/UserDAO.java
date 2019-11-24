package com.personalbudget.demo.user.dao;

import com.personalbudget.demo.user.entity.Authority;
import java.util.List;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.entity.UserActivation;
import java.util.ArrayList;

public interface UserDAO {

    public List<User> getUsers();
    
    public void saveUser(User theUser);
    
    public void saveUser(User theUser, ArrayList<Authority> authorities);
    
    public User getUser(String username);
    
    public void deleteUser(String username);
    
    public void updatePassword(String username, String password);
    
    public void updateEmail(String username, String email);
    
    public void enableUser(String username);
    
    public String getActivationCode(String username);
    
    public UserActivation getActivation(String username);
    
    public void updateActivation(UserActivation activation);
}

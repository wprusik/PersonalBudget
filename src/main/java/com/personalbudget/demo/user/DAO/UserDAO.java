package com.personalbudget.demo.user.dao;

import com.personalbudget.demo.user.Authority;
import java.util.List;
import com.personalbudget.demo.user.User;
import java.util.ArrayList;

public interface UserDAO {

    public List<User> getUsers();
    
    public void saveUser(User theUser);
    
    public void saveUser(User theUser, ArrayList<Authority> authorities);
    
    public User getUser(String username);
}

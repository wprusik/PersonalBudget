package com.personalbudget.demo.DAO;

import com.personalbudget.demo.Entity.Authority;
import java.util.List;
import com.personalbudget.demo.Entity.User;
import java.util.ArrayList;

public interface UserDAO {

    public List<User> getUsers();
    
    public void saveUser(User theUser);
    
    public void saveUser(User theUser, ArrayList<Authority> authorities);
    
    public User getUser(String username);
}

package com.personalbudget.demo.Service;

import com.personalbudget.demo.Entity.Authority;
import com.personalbudget.demo.Entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.personalbudget.demo.DAO.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;
    
    @Override
    @Transactional
    public List<User> getUsers() {
        return userDAO.getUsers();
    }

    @Override
    @Transactional
    public void saveUser(User theUser) {
        userDAO.saveUser(theUser);
    }

    @Override
    @Transactional
    public void saveUser(User theUser, ArrayList<Authority> authorities) {
        userDAO.saveUser(theUser, authorities);
    }

    @Override
    @Transactional
    public User getUser(String username) {
        return userDAO.getUser(username);
    }
    

    
}

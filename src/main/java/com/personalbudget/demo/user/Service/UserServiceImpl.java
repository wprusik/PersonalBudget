package com.personalbudget.demo.user.service;

import com.personalbudget.demo.user.entity.Authority;
import com.personalbudget.demo.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.personalbudget.demo.user.dao.UserDAO;
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

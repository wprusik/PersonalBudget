package com.personalbudget.demo.user.dao;

import com.personalbudget.demo.user.Authority;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.personalbudget.demo.user.User;
import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Repository
public class UserDAOImpl implements UserDAO {

    private EntityManager entityManager;
    
    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    

    @Override
    public List<User> getUsers() {
        Session currentSession = entityManager.unwrap(Session.class);
        
        Query<User> theQuery = currentSession.createQuery("from users", User.class);
        
        List<User> theUsers = (List<User>) theQuery.getResultList();
        
        return theUsers;        
    }

    @Override
    public void saveUser(User theUser) {
        Session currentSession = entityManager.unwrap(Session.class);        
        theUser.setPassword(new BCryptPasswordEncoder().encode(theUser.getPassword()));
        theUser.setEnabled(1);        
        currentSession.save(theUser);
    }

    @Override
    public User getUser(String username) {
        Session currentSession = entityManager.unwrap(Session.class);
                
        User theUser = currentSession.get(User.class, username);
        
        return theUser;
    }

    @Override
    public void saveUser(User theUser, ArrayList<Authority> authorities) {
        Session currentSession = entityManager.unwrap(Session.class);
        
        theUser.setPassword(new BCryptPasswordEncoder().encode(theUser.getPassword()));
        theUser.setEnabled(1);              
        
        System.out.println("PASSWORD: " + theUser.getPassword() + "\n\n\n");
        
        currentSession.save(theUser);
        
        for (Authority item : authorities)
            currentSession.save(item);        
    }
}

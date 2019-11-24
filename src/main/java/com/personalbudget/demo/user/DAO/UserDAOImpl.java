package com.personalbudget.demo.user.dao;

import com.personalbudget.demo.user.entity.Authority;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.entity.UserActivation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.commons.lang.RandomStringUtils;
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
    public void saveUser(User user) {
        Session currentSession = entityManager.unwrap(Session.class);        
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setEnabled(1);        
        currentSession.save(user);
    }

    @Override
    public User getUser(String username) {
        Session currentSession = entityManager.unwrap(Session.class);
        User user = currentSession.get(User.class, username);        
        return user;
    }

    @Override
    public void saveUser(User user, ArrayList<Authority> authorities) {
        Session currentSession = entityManager.unwrap(Session.class);        
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setEnabled(0);                              
        currentSession.save(user);        
        for (Authority item : authorities)
            currentSession.save(item);
        currentSession.save(new UserActivation(user.getUsername(), RandomStringUtils.random(50,true,true), LocalDateTime.now().plusHours(24)));
    }
    
    @Override
    public void deleteUser(String username) {
        Session currentSession = entityManager.unwrap(Session.class);
                
        List<String> queries = new LinkedList();
        queries.add("DELETE FROM expenditures WHERE exists (SELECT * FROM budgets WHERE username = '" + username + "' and expenditures.budget_id=budgets.budget_id)");
        String[] tables = {"transactions", "debts", "budgets", "expenditure_categories", "accounts", "authorities", "users"};         
        
        for (String table : tables) {
            queries.add("DELETE FROM " + table + " WHERE username='" + username + "'");
        }
        for (String item : queries) {
            currentSession.createNativeQuery(item).executeUpdate();
        }
    }

    @Override
    public void updatePassword(String username, String password) {
        Session currentSession = entityManager.unwrap(Session.class);
        User user = currentSession.get(User.class, username);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        currentSession.saveOrUpdate(user);
    }
    
    @Override
    public void updateEmail(String username, String email) {
        Session currentSession = entityManager.unwrap(Session.class);
        User user = currentSession.get(User.class, username);
        user.setEmail(email.trim());
        currentSession.saveOrUpdate(user);
    }

    @Override
    public void enableUser(String username) {
        Session currentSession = entityManager.unwrap(Session.class);
        User user = currentSession.get(User.class, username);        
        user.setEnabled(1);
        currentSession.saveOrUpdate(user);
        UserActivation userActivation = currentSession.get(UserActivation.class, username);
        currentSession.delete(userActivation);
    }    

    @Override
    public String getActivationCode(String username) {
        Session currentSession = entityManager.unwrap(Session.class);
        UserActivation userActivation = currentSession.get(UserActivation.class, username);
        return userActivation.getActivationCode();
    }
    
    @Override
    public UserActivation getActivation(String username) {
        Session currentSession = entityManager.unwrap(Session.class);
        UserActivation userActivation = currentSession.get(UserActivation.class, username);
        return userActivation;       
    }

    @Override
    public void updateActivation(UserActivation activation) {
        Session currentSession = entityManager.unwrap(Session.class);
        activation.setActivationCode(RandomStringUtils.random(50,true,true));
        activation.setExpiration(LocalDateTime.now().plusHours(24));                
        currentSession.saveOrUpdate(activation);
    }
}

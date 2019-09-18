package com.personalbudget.demo.user.dao;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.entity.Authority;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@Transactional
@SpringBootTest
public class UserDAOImplTest {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private EntityManager entityManager;

    
    @Test
    public void getUsersShouldReturnNonEmptyList() {
        assertThat(userDAO.getUsers(), not(empty()));
    }
    
    @Test
    public void saveUserTest() {
        // given
        Session session = entityManager.unwrap(Session.class);
        String username = "saveUserTest";
        Query<User> query = session.createQuery("from users where username='" + username + "'", User.class);
        User userCheck;
        User user = new User();        
        user.setUsername(username);
        user.setPassword("test password");
        // when
        userDAO.saveUser(user);
        // then
        try {
            userCheck = query.getSingleResult();
        }
        catch (NoResultException ex) {
            userCheck = null;
        }
        assertThat(userCheck, not(nullValue()));
    }
    
    @Test
    public void saveUserWithAuthoritiesTest() {
        // given        
        List<Authority> authorities = new ArrayList<Authority>();
        Authority authority = new Authority();        
        User userCheck;
        User user = new User();                
        String username = "saveUserTest";
        user.setUsername(username);
        user.setPassword("test password");
        authority.setUsername(username);
        authority.setAuthority("ROLE_USER");
        authorities.add(authority);
        
        Session session = entityManager.unwrap(Session.class);
        Query<User> queryUser = session.createQuery("from users where username='" + username + "'", User.class);
        Query<Authority> queryAuthority = session.createQuery("from authorities where username='" + username + "'", Authority.class);
        // when
        userDAO.saveUser(user, (ArrayList<Authority>) authorities);
        // then
        try {
            userCheck = queryUser.getSingleResult();
        }
        catch (NoResultException ex) {
            userCheck = null;
        }
        authorities = queryAuthority.getResultList();
        assertThat("User has not been saved", userCheck, not(nullValue()));
        assertThat("Authorities has not been saved", authorities, not(empty()));
    }
    
    @Test
    public void getUserShouldReturnNull() {
        assertNull(userDAO.getUser("non-existing user"));
    }
    
    @Test
    public void getUserShouldNotReturnNull() {
        assertNotNull(userDAO.getUser("test"));
    }
}
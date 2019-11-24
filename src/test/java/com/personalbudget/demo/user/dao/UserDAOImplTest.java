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
import com.personalbudget.demo.user.entity.UserActivation;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@Transactional
@SpringBootTest
public class UserDAOImplTest {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private EntityManager entityManager;

    private Session session;
    
    @BeforeEach
    public void prepareClass() {
        session = entityManager.unwrap(Session.class);
    }
    
    @Test
    public void getUsersShouldReturnNonEmptyList() {
        assertThat(userDAO.getUsers(), not(empty()));
    }
    
    @Test
    public void saveUserTest() {
        // given
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
    
    @Test
    public void getActivationShouldReturnNull() {
        assertNull(userDAO.getActivation("non-existing user"));
    }
    
    @Test
    public void getActivationShouldNotReturnNull() {
        assertNotNull(userDAO.getActivation("testNonActivated"));
    }
    
    @Test
    public void updatePasswordTest() {
        // given
        Query<String> query = session.createNativeQuery("select password from users where username='test'");
        String currentPassword = query.getSingleResult();        
        // when
        userDAO.updatePassword("test", "new password");
        // then
        assertThat(currentPassword.equals(query.getSingleResult()), is(false));
    }
    
    @Test
    public void updateEmailTest() {
        // given
        Query<String> query = session.createNativeQuery("select email from users where username='test'");
        String email = "new email";        
        // when
        userDAO.updateEmail("test", email);
        // then
        assertThat(email.equals(query.getSingleResult()), is(true));
    }
    
    @Test
    public void getActivationCodeShouldReturnNonEmptyString() {
        assertThat(userDAO.getActivationCode("testNonActivated"), not(isEmptyString()));
    }
    
    @Test
    public void updateActivationTest() {
        // given
        String username = "testNonActivated";
        Query<UserActivation> queryActivation = session.createQuery("from users_activation where username='" + username + "'", UserActivation.class);
        UserActivation activation = queryActivation.getSingleResult();
        String oldActivationCode = activation.getActivationCode();
        // when
        userDAO.updateActivation(activation);
        // then
        assertThat(queryActivation.getSingleResult().getActivationCode(), not(equalTo(oldActivationCode)));
    }
    
    @Test
    public void enableUserTest() {
        // before
        String username = "testNonActivated";
        Query<User> query = session.createQuery("from users where username='" + username + "'", User.class);
        Query<UserActivation> queryActivation = session.createQuery("from users_activation where username='" + username + "'", UserActivation.class);
        // when
        userDAO.enableUser(username);
        // then
        assertThat("'enabled' field has not been changed to 1", query.getSingleResult().getEnabled(), equalTo(1));
        assertThat("'users_activation' entry has not been removed", queryActivation.getResultList(), is(empty()));
    }
    
    @Test
    public void deleteUserTest() {
        // given
        String username = "test";
        Query<User> query = session.createQuery("from users where username='" + username + "'", User.class);
        // when
        userDAO.deleteUser(username);
        // then
        assertThat(query.getResultList(), is(empty()));
    }
}
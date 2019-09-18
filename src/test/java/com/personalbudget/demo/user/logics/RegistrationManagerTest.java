package com.personalbudget.demo.user.logics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.user.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class RegistrationManagerTest {

    @Autowired
    private RegistrationManager manager;
    
    @Autowired
    private EntityManager entityManager;
        
    @Test
    @Transactional
    public void registerUserTest() {
        // given
        String username = "test username";
        User userForm = new User();
        User userCheck;
        Model model = new ExtendedModelMap();
        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from users where username='" + username + "'", User.class);        
        userForm.setUsername(username);
        userForm.setPassword("password");
        userForm.setRepeatPassword("password");        
        // when
        manager.registerUser(userForm, model);
        // then
        try {
            userCheck = query.getSingleResult();
        }
        catch (NoResultException ex) {
            userCheck = null;
        }
        assertThat(userCheck, not(nullValue()));
    }
}
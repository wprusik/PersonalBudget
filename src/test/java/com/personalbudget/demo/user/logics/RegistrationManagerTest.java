package com.personalbudget.demo.user.logics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.user.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

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
        userForm.setEmail("address@domain.pl");
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
    
    @Test
    public void resendActivationMailShouldReturnError() {
        // given
        Model model = new ExtendedModelMap();
        String username = "wrong username";
        // when
        model = manager.resendActivationEmail(username, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(true));
    }
    
    @Test
    public void resetPasswordShouldReturnError() {
        // given
        Model model = new ExtendedModelMap();
        String username = "wrong username";
        // when
        model = manager.resetPassword(username, "email", model);
        // then
        assertThat(model.asMap().containsKey("error"), is(true));
    }
    
    @Test
    @WithMockUser("test")
    public void updateAccountShouldReturnError() {
        // given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        User user = new User();
        user.setPassword("wrong password");
        // when
        redirectAttributes = manager.updateAccount(user, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    @WithMockUser("test")
    @Transactional
    public void updateAccountShouldReturnMessage() {
        // given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from users where username='test'", User.class);
        User user = query.getSingleResult();
        User userForm = new User();
        userForm.setPassword("test123");
        userForm.setUsername(user.getUsername());
        userForm.setEmail("test@test.pl");
        // when
        redirectAttributes = manager.updateAccount(userForm, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("message"), is(true));
    }
    
    @Test
    @WithMockUser("test")
    public void removeAccountShouldReturnError() {
        // given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        User userForm = new User();
        userForm.setPassword("wrong password");
        // when
        redirectAttributes = manager.removeAccount(userForm, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    @WithMockUser("test")
    @Transactional
    public void removeAccountShouldReturnMessage() {
        // given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from users where username='test'", User.class);
        User user = query.getSingleResult();
        User userForm = new User();
        userForm.setPassword("test123");
        userForm.setUsername(user.getUsername());
        // when
        redirectAttributes = manager.removeAccount(userForm, redirectAttributes);
        
        System.out.println("\n\n");
        for (String item : redirectAttributes.getFlashAttributes().keySet()) {
            System.out.println(redirectAttributes.getFlashAttributes().get(item));
        }
        
        System.out.println("\n\n");
        
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("message"), is(true));
    }
}
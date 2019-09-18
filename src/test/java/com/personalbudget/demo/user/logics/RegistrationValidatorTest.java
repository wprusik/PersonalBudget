package com.personalbudget.demo.user.logics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.user.entity.User;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class RegistrationValidatorTest {

    @Autowired
    private RegistrationValidator validator;
    
    private User userForm;
    private Model model;
    
    @BeforeEach
    private void prepareMethod() {
        userForm = new User();
        model = new ExtendedModelMap();
    }
    
    @Test
    public void validateFieldsCompletionShouldReturnMessage() {
        // given
        userForm.setUsername("username");
        userForm.setPassword("");
        userForm.setRepeatPassword("");
        // when
        model = validator.validateFieldsCompletion(userForm, model);
        // then
        assertThat(model.asMap().containsKey("message"), is(true));
    }
    
    @Test
    public void validateFieldsCompletionShouldNotReturnMessage() {
        // given
        userForm.setUsername("username");
        userForm.setPassword("password");
        userForm.setRepeatPassword("password");
        // when
        model = validator.validateFieldsCompletion(userForm, model);
        // then
        assertThat(model.asMap().containsKey("message"), is(false));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateUsernameShouldReturnMessage() {
        // given
        userForm.setUsername("test");
        // when
        model = validator.validateUsername(userForm, model);
        // then
        assertThat(model.asMap().containsKey("message"), is(true));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateUsernameShouldNotReturnMessage() {
        // given
        userForm.setUsername("available username");
        // when
        model = validator.validateUsername(userForm, model);
        // then
        assertThat(model.asMap().containsKey("message"), is(true));
    }
    
    @Test
    public void validatePasswordShouldReturnMessage() {
        // given
        userForm.setPassword("password");
        userForm.setRepeatPassword("different password");
        // when
        model = validator.validatePassword(userForm, model);
        // then
        assertThat(model.asMap().containsKey("message"), is(true));
    }
    
    @Test
    public void validatePasswordShouldNotReturnMessage() {
        // given
        userForm.setPassword("password");
        userForm.setRepeatPassword("password");
        // when
        model = validator.validatePassword(userForm, model);
        // then
        assertThat(model.asMap().containsKey("message"), is(false));
    }

}
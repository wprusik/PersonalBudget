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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class RegistrationValidatorTest {

    @Autowired
    private RegistrationValidator validator;    
    
    private User userForm;
    private Model model;
    private RedirectAttributes redirectAttributes;
    
    @BeforeEach
    private void prepareMethod() {
        userForm = new User();
        model = new ExtendedModelMap();
    }
    
    @Test
    public void validateFieldsCompletionShouldReturnError() {
        // given
        userForm.setUsername("username");
        userForm.setPassword("");
        userForm.setRepeatPassword("");
        // when
        model = validator.validateFieldsCompletion(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(true));
    }
    
    @Test
    public void validateFieldsCompletionShouldNotReturnError() {
        // given
        userForm.setUsername("username");
        userForm.setPassword("password");
        userForm.setRepeatPassword("password");
        // when
        model = validator.validateFieldsCompletion(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(false));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateUsernameShouldReturnError() {
        // given
        userForm.setUsername("test");
        // when
        model = validator.validateUsername(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(true));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateUsernameShouldNotReturnError() {
        // given
        userForm.setUsername("available username");
        // when
        model = validator.validateUsername(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(true));
    }
    
    @Test
    public void validatePasswordShouldReturnError() {
        // given
        userForm.setPassword("password");
        userForm.setRepeatPassword("different password");
        // when
        model = validator.validatePassword(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(true));
    }
    
    @Test
    public void validatePasswordShouldNotReturnError() {
        // given
        userForm.setPassword("password");
        userForm.setRepeatPassword("password");
        // when
        model = validator.validatePassword(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(false));
    }
    
    @Test
    public void validateEmailShouldReturnError() {
        // given
        userForm.setEmail("wrong email@");
        // when
        model = validator.validateEmail(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(true));
    }
    
    @Test
    public void validateEmailShouldNotReturnError() {
        // given
        userForm.setEmail("correct@email.com");
        // when
        model = validator.validateEmail(userForm, model);
        // then
        assertThat(model.asMap().containsKey("error"), is(false));
    }
    
    @Test
    @WithMockUser("test")
    public void validateAccountUpdateShouldReturnError() {
        // given
        String email = "in";
        userForm.setEmail(email);
        User user = new User();
        user.setEmail("differentEmail@mail.com");
        redirectAttributes = new RedirectAttributesModelMap();
        // when
        redirectAttributes = validator.validateAccountUpdate(userForm, user, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    @WithMockUser("test")
    public void validateAccountUpdateShouldNotReturnError() {
        // given
        String email = "correct@email.com";
        userForm.setEmail(email);
        User user = new User();
        user.setEmail("differentEmail@mail.com");
        redirectAttributes = new RedirectAttributesModelMap();
        // when
        redirectAttributes = validator.validateAccountUpdate(userForm, user, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
}
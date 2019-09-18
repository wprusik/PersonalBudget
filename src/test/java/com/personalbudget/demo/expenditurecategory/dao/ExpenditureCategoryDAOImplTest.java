package com.personalbudget.demo.expenditurecategory.dao;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import javax.persistence.NoResultException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@WithMockUser(username="test")
@SpringBootTest
public class ExpenditureCategoryDAOImplTest {

    @Autowired
    private ExpenditureCategoryDAO expCatDAO;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    @Transactional
    @WithMockUser(username="a")
    public void getExpenditureCategoriesShouldReturnEmptyList() {
        assertThat(expCatDAO.getExpenditureCategories(), is(empty()));
    }
    
    @Test
    @Transactional
    public void getExpenditureCategoriesShouldReturnNonEmptyList() {
        assertThat(expCatDAO.getExpenditureCategories(), is(not(empty())));
    }
    
    @Test
    @Transactional
    public void saveExpenditureCategoryTest() {
        // given
        Session session = entityManager.unwrap(Session.class);
        String description = "saveExpenditureCategoryTest()";
        String type = "test exp category";
        Query<String> query = session.createQuery("select description from expenditure_categories where expenditureType='" + type + "'", String.class);
        ExpenditureCategory expenditureCategory = new ExpenditureCategory();        
        expenditureCategory.setDescription(description);
        expenditureCategory.setExpenditureType(type);
        expenditureCategory.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        String checkDescription;
        // when
        expCatDAO.saveExpenditureCategory(expenditureCategory);        
        // then
        try {
            checkDescription = query.getSingleResult();
        }
        catch (NoResultException ex) {
            checkDescription = "";
        }
        assertThat(checkDescription, equalTo(description));
    }
    
    @Test
    @Transactional
    public void deleteExpenditureCategoryTest() {
        // given
        Session session = entityManager.unwrap(Session.class);
        String type = "Testowanie";
        Query<String> query = session.createQuery("select description from expenditure_categories where expenditureType='" + type + "'", String.class);        
        String checkDescription;
        // when
        expCatDAO.deleteExpenditureCategory(type);
        // then
        try {
            checkDescription = query.getSingleResult();
        }
        catch (NoResultException ex) {
            checkDescription = "";
        }  
        assertThat(checkDescription, isEmptyString());
    }
    
    @Test
    @Transactional
    public void isUniqueShouldReturnTrue() {
        assertThat(expCatDAO.isUnique("Non-existing category"), is(true));
    }
    
    @Test
    @Transactional
    public void isUniqueShouldReturnFalse() {
        assertThat(expCatDAO.isUnique("Rozrywka"), is(false));
    }
}
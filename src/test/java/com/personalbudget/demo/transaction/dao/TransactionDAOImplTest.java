package com.personalbudget.demo.transaction.dao;

import javax.transaction.Transactional;
import com.personalbudget.demo.transaction.entity.Transaction;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import org.hibernate.Session;
import org.hibernate.query.Query;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;


@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@Transactional
@SpringBootTest
public class TransactionDAOImplTest {

   @Autowired
   private TransactionDAO transactionDAO;
   
   @Autowired
   private EntityManager entityManager;
   
   @Test
   @WithMockUser(username="a")
   public void getTransactionsShouldReturnEmptyList() {
       assertThat(transactionDAO.getTransactions(), is(empty()));
   }
   
   @Test
   @WithMockUser(username="test")
   public void getTransactionsShouldReturnNonEmptyList() {
       assertThat(transactionDAO.getTransactions(), not(empty()));
   }
   
   @Test
   @WithMockUser(username="a")
   public void getPlannedTransactionsShouldReturnEmptyList() {
       assertThat(transactionDAO.getPlannedTransactions(), is(empty()));
   }
   
   @Test
   @WithMockUser(username="test")
   public void getPlannedTransactionsShouldReturnNonEmptyList() {
       assertThat(transactionDAO.getPlannedTransactions(), not(empty()));
   }
   
   @Test
   @WithMockUser(username="a")
   public void getAllTransactionsShouldReturnEmptyList() {
       assertThat(transactionDAO.getAllTransactions(), is(empty()));
   }
   
   @Test
   @WithMockUser(username="test")
   public void getAllTransactionsShouldReturnNonEmptyList() {
       assertThat(transactionDAO.getAllTransactions(), not(empty()));
   }
   
   @Test
   @WithMockUser(username="test")
   public void saveTransactionTest() {
       // given
       Session session = entityManager.unwrap(Session.class);
       Query<String> query = session.createQuery("select description from transactions where id=(select max(id) from transactions)", String.class);
       String description = "saveTransactionTest";
       String descriptionCheck;
       Transaction transaction = new Transaction();
       transaction.setAccountNumberFrom("86578643065942356403625734");
       transaction.setAccountNumberTo("foo");
       transaction.setAmount(15);
       transaction.setCurrency("PLN");
       transaction.setDateTime(LocalDateTime.now());
       transaction.setDescription(description);
       transaction.setType("between");
       // when
       transactionDAO.saveTransaction(transaction);
       // then
       try {
           descriptionCheck = query.getSingleResult();
       }
       catch (NoResultException ex) {
           descriptionCheck = "";
       }
       assertThat(description, equalTo(descriptionCheck));
   }
   
   @Test
   @WithMockUser(username="test")
   public void deleteTransactionTest() {
       // given
       Session session = entityManager.unwrap(Session.class);
       int id = 5;
       Query<Transaction> query = session.createQuery("from transactions where id='" + id + "'", Transaction.class);
       Transaction transactionCheck;
       // when
       transactionDAO.deleteTransaction(id);
       // then
       try {
           transactionCheck = query.getSingleResult();
       }
       catch (NoResultException ex) {
           transactionCheck = null;
       }
       assertThat(transactionCheck, is(nullValue()));
   }
   
   @Test
   @WithMockUser(username="test")
   public void getTransactionByIdShouldReturnNull() {
       assertNull(transactionDAO.getTransactionById(999));
   }
   
   @Test
   @WithMockUser(username="test")
   public void getTransactionByIdShouldReturnTransaction() {
       assertThat(transactionDAO.getTransactionById(1), instanceOf(Transaction.class));
   }
   
   @Test
   @WithMockUser(username="test")
   public void updateTransactionTest() {
       // given
       int id = 3;
       Session session = entityManager.unwrap(Session.class);       
       Query<Transaction> query = session.createQuery("from transactions where id='" + id + "'", Transaction.class);
       Query<String> queryCheck = session.createQuery("select description from transactions where id='" + id + "'", String.class);
       String descriptionCheck;
       Transaction transaction = null;
       String description = "updateTransactionTest - test description";
       try {
           transaction = query.getSingleResult();
       }
       catch (NoResultException ex) {
           fail("Error retrieving transactions from database");
       }
       // when
       transaction.setDescription(description);
       transactionDAO.updateTransaction(transaction);
       // then
       try {
           descriptionCheck = queryCheck.getSingleResult();
       }
       catch (NoResultException ex) {
           descriptionCheck = "";
       }
       assertThat(descriptionCheck, equalTo(description));
   }
}
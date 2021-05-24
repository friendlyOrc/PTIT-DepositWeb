package bank.deposit.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import bank.deposit.data.AccountRepository;
import bank.deposit.model.Account;
import bank.deposit.model.Saving;
import bank.deposit.web.HomeController;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignUpControllerTest {
        private final HomeController home;
        private final AccountRepository accRepo;
        private MockMvc mockMvc;
        private Account acc;
        private int accNum;

        public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
                        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

        @Autowired
        public SignUpControllerTest(HomeController home, MockMvc mockMvc, AccountRepository accRepo) {
                this.home = home;
                this.mockMvc = mockMvc;
                this.accRepo = accRepo;

                accNum = ((ArrayList<Account>) accRepo.findAll()).size();
                Date date = Date.valueOf("1999-07-20");
                acc = new Account(1000, "testAcc", date, 1, "abc", "1000999999", "test@gmail.com", "", "",
                                new ArrayList<Saving>());
        }

        // Verify that controller is not null
        @Test
        void contextLoads() throws Exception {
                assertNotNull(home);
        }

        // Load sign up page with no account in session
        @Test
        void loadSignupPageWithNoAccount() throws Exception {
                MvcResult mvcResult = mockMvc.perform(get("/members")).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify login page loads", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view));

        }

        // Load signup page with account in session
        @Test
        void loadSignupPageWithAccount() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(get("/members").sessionAttrs(sessionattr)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify signup page loads", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view));

        }

        // Create a valid account
        @Test
        void signUpValidInput() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, acc)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Account> accList = (ArrayList<Account>) accRepo.findAll();
                Collections.sort(accList, new Comparator<Account>() {

                        public int compare(Account acc1, Account acc2) {
                                int id1 = acc1.getId();
                                int id2 = acc2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status), () -> assertEquals("user", view),
                                () -> assertEquals("success", msg),
                                () -> assertEquals(acc.getAddress(), accList.get(0).getAddress()),
                                () -> assertEquals(acc.getDob(), accList.get(0).getDob()),
                                () -> assertEquals(acc.getEmail(), accList.get(0).getEmail()),
                                () -> assertEquals(acc.getIdcard(), accList.get(0).getIdcard()),
                                () -> assertEquals(acc.getName(), accList.get(0).getName()),
                                () -> assertEquals(acc.getSex(), accList.get(0).getSex()));
        }

        // Fail create account - Session timeout
        @Test
        void signUpInvalidSession() throws Exception {
                String url = "/members";

                Account temp = acc;
                temp.setName("!@#@SAASA");

                MvcResult mvcResult = mockMvc.perform(
                                MockMvcRequestBuilderUtils.postForm(url, temp).contentType(APPLICATION_JSON_UTF8))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Special Session timeout", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view), () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Special key in name
        @Test
        void signUpInvalidNameSpe() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setName("!@#@SAASA");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Special key in name", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("name", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Number in name
        @Test
        void signUpInvalidNameNumber() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setName("123123SAASA");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Number in name", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("name", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Too long name
        @Test
        void signUpInvalidNameLong() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Too long name", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("longName", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Special key in address
        @Test
        void signUpInvalidAddressSpe() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setAddress("!@#aa");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Special key in address", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("addr", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Not valid Email
        @Test
        void signUpInvalidEmail() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setEmail("asd!@#@!aasd");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Not valid Email", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("email", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Special key in idcard
        @Test
        void signUpInvalidIdcardSpe() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setIdcard("@@@@@@@@@@");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Special key in idcard", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("cccd", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Invalid Idcard - Too short
        @Test
        void signUpInvalidIdcardShort() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setIdcard("11");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Invalid Idcard - Too short", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("cccdWord", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Invalid Idcard - Too long
        @Test
        void signUpInvalidIdcardLong() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setIdcard("111111111111111111");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Invalid Idcard - Too long", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("cccdWord", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Invalid Idcard - Contains text
        @Test
        void signUpInvalidIdcardText() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setIdcard("111111111z");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Invalid Idcard - Contains text", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("cccdWord", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Invalid DOB
        @Test
        void signUpInvalidDOB() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setDob(Date.valueOf("2010-01-01"));

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Invalid DOB", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("date", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

        // Fail create account - Duplicate ID card
        @Test
        void signUpInvalidIdcardDup() throws Exception {
                String url = "/members";
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                Account temp = acc;
                temp.setIdcard("0123456789");

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkAccNum = ((ArrayList<Account>) accRepo.findAll()).size();

                assertAll("Fail create account - Duplicate ID card", () -> assertEquals(200, status),
                                () -> assertEquals("add_user", view), () -> assertEquals("dup", msg),
                                () -> assertEquals(accNum, checkAccNum));
        }

}

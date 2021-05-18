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

import bank.deposit.Account;
import bank.deposit.Saving;
import bank.deposit.data.AccountRepository;
import bank.deposit.web.HomeController;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CreateControllerTest {
        private final HomeController home;
        private final AccountRepository accRepo;
        private MockMvc mockMvc;
        private Account acc;
        private Account accClient;
        private Saving sav;

        public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
                        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

        @Autowired
        public CreateControllerTest(HomeController home, AccountRepository accRepo, MockMvc mockMvc) {
                this.home = home;
                this.accRepo = accRepo;
                this.mockMvc = mockMvc;

                accClient = accRepo.findOneAccount(2);
                acc = accRepo.findOneAccount(1);
                sav = new Saving();
                sav.setBalance(10000000);
                sav.setInterest((float) 1.0);
                sav.setStatus(1);
                sav.setTime(4);
                sav.setType(1);
        }

        // Verify that controller is not null
        @Test
        void contextLoads() throws Exception {
                assertNotNull(home);
        }

        // Load create page with no account in session
        @Test
        void loadCreatePageWithNoAccount() throws Exception {
                MvcResult mvcResult = mockMvc.perform(get("/create")).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify login page loads", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view));

        }

        // Load create page with account in session
        @Test
        void loadCreatePageWithAccount() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(get("/create").sessionAttrs(sessionattr)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify search accout page loads", () -> assertEquals(200, status),
                                () -> assertEquals("search_account", view));

        }

        // Load create page with account in session and ID
        @Test
        void loadCreatePageWithAccountID() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(get("/create?accid=" + acc.getId()).sessionAttrs(sessionattr))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify create page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create", view));

        }

        // Create a valid saving
        @Test
        void createValidInput() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view));
        }

        // Fail create account - Session timeout
        @Test
        void createInvalidSession() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                MvcResult mvcResult = mockMvc.perform(
                                MockMvcRequestBuilderUtils.postForm(url, sav).contentType(APPLICATION_JSON_UTF8))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Fail create account - Session timeout", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view));
        }

        // Fail create saving - Special key in balance
        @Test
        void createInvalidInputSpe() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setBalance(-1000000000);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                assertAll("Fail create saving - Special key in balance", () -> assertEquals(200, status),
                                () -> assertEquals("create", view), () -> assertEquals("speBal", msg));
        }

        // Fail create saving - Under 1m balance
        @Test
        void createInvalidInputLow() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setBalance(1000);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                assertAll("Fail create saving - Under 1m balance", () -> assertEquals(200, status),
                                () -> assertEquals("create", view), () -> assertEquals("smInp", msg));
        }

}

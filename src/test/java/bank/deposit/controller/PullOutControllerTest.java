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
import bank.deposit.data.SavingRepository;
import bank.deposit.model.Account;
import bank.deposit.model.Saving;
import bank.deposit.web.HomeController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PullOutControllerTest {
        private final HomeController home;
        private final AccountRepository accRepo;
        private final SavingRepository savRepo;
        private MockMvc mockMvc;
        private Account acc;

        public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
                        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

        @Autowired
        public PullOutControllerTest(HomeController home, AccountRepository accRepo, MockMvc mockMvc,
                        SavingRepository savRepo) {
                this.home = home;
                this.accRepo = accRepo;
                this.mockMvc = mockMvc;
                this.savRepo = savRepo;

                acc = accRepo.findOneAccount(5);
        }

        // Verify that controller is not null
        @Test
        void contextLoads() throws Exception {
                assertNotNull(home);
        }

        // Load pullout page with no account in session
        @Test
        void loadPullOutPageWithNoAccount() throws Exception {
                MvcResult mvcResult = mockMvc.perform(get("/pullout")).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify login page loads", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view));

        }

        // Load pullout page with account in session
        @Test
        void loadPullOutPageWithAccount() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(get("/pullout").sessionAttrs(sessionattr)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify search accout page loads", () -> assertEquals(200, status),
                                () -> assertEquals("search_account_sav", view));

        }

        // Load pullout page with account in session and ID
        @Test
        void loadPullOutPageWithAccountID() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(get("/pullout?accid=" + acc.getId()).sessionAttrs(sessionattr))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify saving list page loads", () -> assertEquals(200, status),
                                () -> assertEquals("saving_list", view));

        }

        // Load pullout page with account in session and ID & error message
        @Test
        void loadPullOutPageWithError() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(
                                get("/pullout?accid=" + acc.getId()).param("msg", "fail").sessionAttrs(sessionattr))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify saving list page loads", () -> assertEquals(200, status),
                                () -> assertEquals("saving_list", view), () -> assertEquals("fail", msg));

        }

        // Delete a saving successfully
        @Test
        void deleteSavingSucc() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(
                                get("/pullout/delete?accid=" + acc.getId() + "&savid=1").sessionAttrs(sessionattr))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                Saving checkSav = savRepo.findSaving(1);

                assertAll("Verify saving list page loads", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/pullout_bill?accid=5&savid=1", view),
                                () -> assertEquals(0, checkSav.getStatus()));

        }

        // Delete a nonexist sav id
        @Test
        void deleteSavingNotExist() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(
                                get("/pullout/delete?accid=" + acc.getId() + "&savid=10").sessionAttrs(sessionattr))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify pull out page load with fail", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/pullout?accid=5&msg=fail", view));

        }

        // Delete a saving - Session Timeout
        @Test
        void deleteSavingTimout() throws Exception {

                MvcResult mvcResult = mockMvc.perform(get("/pullout/delete?accid=" + acc.getId() + "&savid=1"))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                Saving checkSav = savRepo.findSaving(1);

                assertAll("Verify saving list page loads", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view),
                                () -> assertEquals(1, checkSav.getStatus()));

        }

        // Load Pullout Bill
        @Test
        void deleteBill() throws Exception {
                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(
                                get("/pullout_bill?accid=" + acc.getId() + "&savid=3").sessionAttrs(sessionattr))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify saving list page loads", () -> assertEquals(200, status),
                                () -> assertEquals("pullout_bill", view));

        }

        // Load Pullout Bill - Session Timeout
        @Test
        void deleteBillTimeout() throws Exception {

                MvcResult mvcResult = mockMvc.perform(get("/pullout_bill?accid=" + acc.getId() + "&savid=3"))
                                .andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                assertAll("Verify saving list page loads", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view));

        }

}

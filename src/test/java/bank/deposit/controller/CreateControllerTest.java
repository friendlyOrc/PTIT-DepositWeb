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
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CreateControllerTest {
        private final HomeController home;
        private final SavingRepository savRepo;
        private MockMvc mockMvc;
        private Account acc;
        private Account accClient;
        private Saving sav;
        private int numSav;

        public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
                        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

        @Autowired
        public CreateControllerTest(HomeController home, AccountRepository accRepo, SavingRepository savRepo,
                        MockMvc mockMvc) {
                this.home = home;
                this.mockMvc = mockMvc;
                this.savRepo = savRepo;

                accClient = accRepo.findOneAccount(2);
                acc = accRepo.findOneAccount(1);
                sav = new Saving();
                sav.setBalance(10000000);
                sav.setInterest((float) 0.1);
                sav.setStatus(1);
                sav.setTime(0);
                sav.setType(1);

                numSav = savRepo.findAllSaving(accClient.getId()).size();
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

        // Create a valid saving - no term
        @Test
        void createValidInputNoTerm() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 1 month
        @Test
        void createValidInput1M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 3.1);
                sav.setTime(1);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 2 months
        @Test
        void createValidInput2M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 3.1);
                sav.setTime(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 3 months
        @Test
        void createValidInput3M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 3.4);
                sav.setTime(3);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 4 months
        @Test
        void createValidInput4M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 3.4);
                sav.setTime(4);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 5 months
        @Test
        void createValidInput5M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 3.4);
                sav.setTime(5);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 6 months
        @Test
        void createValidInput6M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 4);
                sav.setTime(6);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 7 months
        @Test
        void createValidInput7M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 4);
                sav.setTime(7);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 8 months
        @Test
        void createValidInput8M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 4);
                sav.setTime(8);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 9 months
        @Test
        void createValidInput9M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 4);
                sav.setTime(9);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 10 months
        @Test
        void createValidInput10M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 4);
                sav.setTime(10);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 11 months
        @Test
        void createValidInput11M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 4);
                sav.setTime(11);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 12 months
        @Test
        void createValidInput12M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 5.6);
                sav.setTime(12);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 13 months
        @Test
        void createValidInput13M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 5.6);
                sav.setTime(13);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 15 months
        @Test
        void createValidInput15M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 5.6);
                sav.setTime(15);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 18 months
        @Test
        void createValidInput18M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 5.6);
                sav.setTime(18);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 24 months
        @Test
        void createValidInput24M() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                sav.setInterest((float) 5.6);
                sav.setTime(24);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(sav.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(sav.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(sav.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(sav.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - no term type 2
        @Test
        void createValidInputNoTermType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 1 month type 2
        @Test
        void createValidInput1MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 3.1);
                temp.setTime(1);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 2 months type 2
        @Test
        void createValidInput2MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 3.1);
                temp.setTime(2);

                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 3 months type 2
        @Test
        void createValidInput3MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 3.4);
                temp.setTime(3);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 4 months type 2
        @Test
        void createValidInput4MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 3.4);
                temp.setTime(4);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 5 months type 2
        @Test
        void createValidInput5MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 3.4);
                temp.setTime(5);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 6 months type 2
        @Test
        void createValidInput6MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 4);
                temp.setTime(6);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 7 months type 2
        @Test
        void createValidInput7MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 4);
                temp.setTime(7);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 8 months type 2
        @Test
        void createValidInput8MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 4);
                temp.setTime(8);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 9 months type 2
        @Test
        void createValidInput9MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 4);
                temp.setTime(9);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 10 months type 2
        @Test
        void createValidInput10MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 4);
                temp.setTime(10);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 11 months type 2
        @Test
        void createValidInput11MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 4);
                temp.setTime(11);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 12 months type 2
        @Test
        void createValidInput12MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 5.6);
                temp.setTime(12);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 13 months type 2
        @Test
        void createValidInput13MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 5.6);
                temp.setTime(13);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 15 months type 2
        @Test
        void createValidInput15MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 5.6);
                temp.setTime(15);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 18 months type 2
        @Test
        void createValidInput18MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 5.6);
                temp.setTime(18);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
        }

        // Create a valid saving - 24 months type 2
        @Test
        void createValidInput24MType2() throws Exception {
                String url = "/create?accid=" + accClient.getId();

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                sessionattr.put("account", acc);
                Saving temp = sav;
                temp.setInterest((float) 5.6);
                temp.setTime(24);
                temp.setType(2);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, temp)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                int status = mvcResult.getResponse().getStatus();

                ArrayList<Saving> savList = savRepo.findAllSaving(accClient.getId());
                Collections.sort(savList, new Comparator<Saving>() {

                        public int compare(Saving s1, Saving s2) {
                                int id1 = s1.getId();
                                int id2 = s2.getId();

                                // descending order
                                return id2 - id1;
                        }
                });

                assertAll("Verify user page loads", () -> assertEquals(200, status),
                                () -> assertEquals("create_bill", view),
                                () -> assertEquals(temp.getBalance(), savList.get(0).getBalance()),
                                () -> assertEquals(temp.getInterest(), savList.get(0).getInterest()),
                                () -> assertEquals(temp.getTime(), savList.get(0).getTime()),
                                () -> assertEquals(temp.getType(), savList.get(0).getType()));
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

                int checkNumSav = savRepo.findAllSaving(accClient.getId()).size();

                assertAll("Fail create account - Session timeout", () -> assertEquals(302, status),
                                () -> assertEquals("redirect:/login", view), () -> assertEquals(numSav, checkNumSav));
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

                int checkNumSav = savRepo.findAllSaving(accClient.getId()).size();

                assertAll("Fail create saving - Special key in balance", () -> assertEquals(200, status),
                                () -> assertEquals("create", view), () -> assertEquals("speBal", msg),
                                () -> assertEquals(numSav, checkNumSav));
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

                int checkNumSav = savRepo.findAllSaving(accClient.getId()).size();

                assertAll("Fail create saving - Under 1m balance", () -> assertEquals(200, status),
                                () -> assertEquals("create", view), () -> assertEquals("smInp", msg),
                                () -> assertEquals(numSav, checkNumSav));
        }

        // Fail create a saving - Invalid acccount id leads to a query error
        @Test
        void createFailSql() throws Exception {
                String url = "/create?accid=1";

                HashMap<String, Object> sessionattr = new HashMap<String, Object>();
                acc.setId(7749);
                sessionattr.put("account", acc);

                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilderUtils.postForm(url, sav)
                                .sessionAttrs(sessionattr).contentType(APPLICATION_JSON_UTF8)).andReturn();
                String view = mvcResult.getModelAndView().getViewName();
                String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
                int status = mvcResult.getResponse().getStatus();

                int checkNumSav = savRepo.findAllSaving(accClient.getId()).size();

                assertAll("Verify fail msg", () -> assertEquals(200, status), () -> assertEquals("create", view),
                                () -> assertEquals("fail", msg), () -> assertEquals(numSav, checkNumSav));
        }

}

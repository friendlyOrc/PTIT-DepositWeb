package bank.deposit.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import bank.deposit.data.AccountRepository;
import bank.deposit.model.Account;
import bank.deposit.web.HomeController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {
    private final HomeController home;
    private final AccountRepository accRepo;
    private MockMvc mockMvc;
    private static Account acc;

    @Autowired
    public HomeControllerTest(HomeController home, AccountRepository accRepo, MockMvc mockMvc) {
        this.home = home;
        this.accRepo = accRepo;
        this.mockMvc = mockMvc;
        acc = accRepo.findOneAccount(1);
    }

    // Verify that controller is not null
    @Test
    void contextLoads() throws Exception {
        assertNotNull(home);
    }

    // Load main page with no account in session
    @Test
    void loadMainPageWithNoAccount() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();
        String location = mvcResult.getResponse().getHeader("Location");
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify login page loads", () -> assertEquals(302, status), () -> assertEquals("/login", location));

    }

    // Load main page with account in session
    @Test
    void loadMainPageWithAccount() throws Exception {
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("account", acc);

        MvcResult mvcResult = mockMvc.perform(get("/").sessionAttrs(sessionattr)).andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify main page loads", () -> assertEquals(200, status), () -> assertEquals("home", view));
    }

}

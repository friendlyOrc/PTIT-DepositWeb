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
import bank.deposit.web.HomeController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CalcControllerTest {
    private final HomeController home;
    private final AccountRepository accRepo;
    private MockMvc mockMvc;
    private Account acc;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    public CalcControllerTest(HomeController home, AccountRepository accRepo, MockMvc mockMvc) {
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

    // Load calc page with no account in session
    @Test
    void loadCalcPageWithNoAccount() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/calc")).andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify login page loads", () -> assertEquals(302, status),
                () -> assertEquals("redirect:/login", view));

    }

    // Load calc page with account in session
    @Test
    void loadCalcPageWithAccount() throws Exception {
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("account", acc);

        MvcResult mvcResult = mockMvc.perform(get("/calc").sessionAttrs(sessionattr)).andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify search accout page loads", () -> assertEquals(200, status),
                () -> assertEquals("calc", view));

    }

}

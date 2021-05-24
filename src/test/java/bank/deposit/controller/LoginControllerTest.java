package bank.deposit.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import bank.deposit.data.AccountRepository;
import bank.deposit.model.Account;
import bank.deposit.web.HomeController;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {
    private final HomeController home;
    private final AccountRepository accRepo;
    private MockMvc mockMvc;
    private Account acc;
    private Account accInvalidPW;
    private Account accInvalidUN;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    public LoginControllerTest(HomeController home, AccountRepository accRepo, MockMvc mockMvc) {
        this.home = home;
        this.accRepo = accRepo;
        this.mockMvc = mockMvc;
        acc = accRepo.findOneAccount(1);
        accInvalidPW = accRepo.findOneAccount(2);
        accInvalidUN = accRepo.findOneAccount(3);
        accInvalidPW.setPassword("wrong");
        accInvalidUN.setUsername("wrong");
    }

    // Verify that controller is not null
    @Test
    void contextLoads() throws Exception {
        assertNotNull(home);
    }

    // Load login page with no account in session
    @Test
    void loadLoginPageWithNoAccount() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login")).andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify login page loads", () -> assertEquals(200, status), () -> assertEquals("login", view));

    }

    // Load login page with account in session
    @Test
    void loadLoginPageWithAccount() throws Exception {
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("account", acc);

        MvcResult mvcResult = mockMvc.perform(get("/login").sessionAttrs(sessionattr)).andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify main page loads", () -> assertEquals(302, status), () -> assertEquals("redirect:/", view));

    }

    // Test post login valid
    @Test
    void loginValidInput() throws Exception {
        String url = "/login";

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilderUtils.postForm(url, acc).contentType(APPLICATION_JSON_UTF8)).andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify main page loads", () -> assertEquals(302, status), () -> assertEquals("redirect:/", view));
    }

    // Test post login with invalid username
    @Test
    void loginInvalidUsername() throws Exception {
        String url = "/login";

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilderUtils.postForm(url, accInvalidUN).contentType(APPLICATION_JSON_UTF8))
                .andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify login page loads", () -> assertEquals(200, status), () -> assertEquals("login", view),
                () -> assertEquals("username", msg));
    }

    // Test post login with invalid password
    @Test
    void loginInvalidPassword() throws Exception {
        String url = "/login";

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilderUtils.postForm(url, accInvalidPW).contentType(APPLICATION_JSON_UTF8))
                .andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify login page loads", () -> assertEquals(200, status), () -> assertEquals("login", view),
                () -> assertEquals("password", msg));
    }

    // Test post login with special character in username
    @Test
    void loginInvalidSpeUsername() throws Exception {
        String url = "/login";
        accInvalidUN.setUsername("kienpt = $%^");

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilderUtils.postForm(url, accInvalidUN).contentType(APPLICATION_JSON_UTF8))
                .andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        String msg = (String) mvcResult.getModelAndView().getModel().get("msg");
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify login page loads", () -> assertEquals(200, status), () -> assertEquals("login", view),
                () -> assertEquals("speUsername", msg));
    }

    // Log out lead user to the login page
    @Test
    void logout() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/logout")).andReturn();
        String view = mvcResult.getModelAndView().getViewName();
        int status = mvcResult.getResponse().getStatus();

        assertAll("Verify login page loads", () -> assertEquals(302, status),
                () -> assertEquals("redirect:/login", view));
    }

}

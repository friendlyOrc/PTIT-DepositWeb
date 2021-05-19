package bank.deposit.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import bank.deposit.Account;
import bank.deposit.data.AccountRepository;
import bank.deposit.web.HomeController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {
    private final HomeController home;
    private final AccountRepository accRepo;
    private MockMvc mockMvc;
    private static Account acc;
    private String url = "http://localhost:8082/";

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
    @Test
    void loginWithValidUser() {
    	HttpUriRequest request = new HttpPost(url + "login?username=lamnt&password=1234");

        // When
        HttpResponse httpResponse;
//		try {
			try {
				httpResponse = HttpClientBuilder.create().build().execute(request);
				assertEquals(302, httpResponse.getStatusLine().getStatusCode());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			HttpEntity entity = httpResponse.getEntity();
//	        String result = EntityUtils.toString(entity);
//	        JSONObject temp1 = new JSONObject(result);
//	        JSONArray arr = temp1.getJSONArray("result");
//	        System.out.print("XXXXXXXXXXXXXXXXXXXX"+arr);
	        // Then
	        
//		}
        
    }
    
    
    @Test
    /**
     * Input have some character
     */
    void isAllNumberInputWrong() {
    	String s = "123123aaa";
    	assertEquals(home.isAllNumber(s), false);
    }
    
    @Test
    /**
     * Input have some speacial character
     */
    void isAllNumberInputWrong2() {
    	String s = "123123$%#";
    	assertEquals(home.isAllNumber(s), false);
    }
    @Test
    /**
     * Input is null
     */
    void isAllNumberInputWrong3() {
    	String s = "";
    	assertEquals(home.isAllNumber(s), true);
    }
    @Test
    /**
     * Input have negative number
     */
    void isAllNumberInputWrong4() {
    	String s = "-1231231231";
    	assertEquals(home.isAllNumber(s), false);
    }
    @Test
    /**
     * Input is all digit
     */
    void isAllNumberInputRight() {
    	String s = "12312311";
    	assertEquals(home.isAllNumber(s), true);
    }
    
    @Test
    /**
     * Input with not all digit string
     */
    void verifyIdInputWrong() {
    	String s = "1232aaa";
    	assertEquals(home.verifyId(s), false);
    }
    
    @Test
    /**
     * Input with size is 9
     */
    void verifyIdInputSizeWrong() {
    	String s = "123123123";
    	assertEquals(home.verifyId(s), false);
    }
    
    @Test
    /**
     * Input is null
     */
    void verifyIdInputWrong2() {
    	String s = "";
    	assertEquals(home.verifyId(s), false);
    }
    
    @Test
    /**
     * Input is negative number
     */
    void verifyIdInputNegativeNumber() {
    	String s = "-123123123";
    	assertEquals(home.verifyId(s), false);
    }
    
    @Test
    /**
     * Input is all digit and size = 10
     */
    void verifyIdInputRight() {
    	String s = "1231231234";
    	assertEquals(home.verifyId(s), true);
    }
    @Test
    /**
     * Input is all digit and size > 10
     */
    void verifyIdInputWrongSize() {
    	String s = "123123123412312";
    	assertEquals(home.verifyId(s), false);
    }
    @Test
    /**
     * Input have some speacial character
     */
    void speacialKeyExistSpeacial() {
    	String s = "123123###";
    	assertEquals(home.specialKey(s), false);
    }
    
    @Test
    /**
     * Input don't have speacial character
     */
    void speacialKeyWithNoSpeacial() {
    	String s = "123123";
    	assertEquals(home.specialKey(s), true);
    }
    @Test
    /**
     * Input is null
     */
    void speacialKeyWithNullInput() {
    	String s = "";
    	assertEquals(home.specialKey(s), true);
    }
    
    @Test
    /**
     * Input year is 2010 which make different year lower than 18
     */
    void validDOBBelow() {
    	java.util.Date now = new java.util.Date();
    	now.setYear(2010);
    	Date date = new Date(now.getTime());
    	assertEquals(home.validDOB(date), false);
    }
    @Test
    /**
     * Input year is 2003 which make different year same as 18
     */
    void validDOBSame() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 2003);
    	Date date = new Date(cal.getTime().getTime());
    	assertEquals(home.validDOB(date), true);
    }
    
    @Test
    /**
     * Input year is 2023 which make different year is a negative number
     */
    void validDOBInvalid() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 2023);
    	Date date = new Date(cal.getTime().getTime());
    	assertEquals(home.validDOB(date), false);
    }
    
    @Test
    /**
     * Input year is 1999 which make different year higher than 18
     */
    void validDOBHigh() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 1999);
    	Date date = new Date(cal.getTime().getTime());
    	assertEquals(home.validDOB(date), true);
    }
    
}

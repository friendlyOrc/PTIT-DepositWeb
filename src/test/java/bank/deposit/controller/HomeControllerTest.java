package bank.deposit.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import javax.transaction.Transactional;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.restassured.RestAssured.get;

import bank.deposit.Account;
import bank.deposit.AjaxResponseBody;
import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;
import bank.deposit.web.HomeController;

@SpringBootTest
@Transactional
public class HomeControllerTest {
    private final HomeController home;
    private String url = "http://localhost:8082/";

    @Autowired
    public HomeControllerTest(HomeController home) {
        this.home = home;
    }

    @Test
    void loadMain() {
        get(url).then().assertThat().statusCode(200);
    }

    @Test
    void searchClient() {
        ResponseEntity<?> rs = home.getSearchResultViaAjax("Kiên");
        AjaxResponseBody res = (AjaxResponseBody) rs.getBody();

        assertEquals(2, res.getResult().size());
    }

    @Test
    void searchAPI() throws ClientProtocolException, IOException, JSONException {

        HttpUriRequest request = new HttpGet(url + "search?name=kien");

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject temp1 = new JSONObject(result);
        JSONArray arr = temp1.getJSONArray("result");
        System.out.print(arr);
        // Then
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
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
     * Input is all digit and size = 10
     */
    void verifyIdInputRight() {
    	String s = "1231231234";
    	assertEquals(home.verifyId(s), true);
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
     * Input year is 1999 which make different year higher than 18
     */
    void validDOBHigh() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 1999);
    	Date date = new Date(cal.getTime().getTime());
    	assertEquals(home.validDOB(date), true);
    }
    
}

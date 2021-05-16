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
}

package bank.deposit.controller;

import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

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

import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;
import bank.deposit.model.Saving;

@SpringBootTest
@Transactional
public class APITest {
    private final AccountRepository accRepo;
    private final SavingRepository savRepo;

    // Uncomment this line to test on staging
    private String url = "http://localhost:8082/";

    // Uncomment this line to test on build RC
    // private String url = "https://depositweb.herokuapp.com/";

    @Autowired
    APITest(AccountRepository accRepo, SavingRepository savRepo) {
        this.accRepo = accRepo;
        this.savRepo = savRepo;
    }

    // SEARCH API
    // =========================================================================

    // Call search API with results
    @Test
    void searchAPIValid() throws ClientProtocolException, IOException, JSONException {
        String name = "kien";

        HttpUriRequest request = new HttpGet(url + "search?name=" + name);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject temp1 = new JSONObject(result);
        JSONArray arr = temp1.getJSONArray("result");

        // System.out.print(arr);

        int accNum = accRepo.findByUserName("%" + name + "%").size();
        // Then
        assertAll("Search result verify!", () -> assertEquals(200, httpResponse.getStatusLine().getStatusCode()),
                () -> assertEquals(accNum, arr.length()));
    }

    // Call search API with no result
    @Test
    void searchAPINoResult() throws ClientProtocolException, IOException, JSONException {
        String name = "kienabcasd";

        HttpUriRequest request = new HttpGet(url + "search?name=" + name);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject temp1 = new JSONObject(result);
        JSONArray arr = temp1.getJSONArray("result");

        // System.out.print(arr);

        // Then
        assertAll("Search result verify!", () -> assertEquals(200, httpResponse.getStatusLine().getStatusCode()),
                () -> assertEquals(0, arr.length()));
    }

    // Call search API with empty param
    @Test
    void searchAPIAll() throws ClientProtocolException, IOException, JSONException {
        String name = "";

        HttpUriRequest request = new HttpGet(url + "search?name=" + name);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject temp1 = new JSONObject(result);
        JSONArray arr = temp1.getJSONArray("result");

        // System.out.print(arr);

        int accNum = accRepo.findByUserName("%" + name + "%").size();
        // Then
        assertAll("Search result verify!", () -> assertEquals(200, httpResponse.getStatusLine().getStatusCode()),
                () -> assertEquals(accNum, arr.length()));
    }

    // Call search API with more than 1 word name
    @Test
    void searchAPILongName() throws ClientProtocolException, IOException, JSONException {
        String name = "Trung Kiên";

        HttpUriRequest request = new HttpGet(url + "search?name=" + name.replace(" ", "%20"));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject temp1 = new JSONObject(result);
        JSONArray arr = temp1.getJSONArray("result");

        // System.out.print(arr + "========================");

        int accNum = accRepo.findByUserName("%" + name + "%").size();
        // Then
        assertAll("Search result verify!", () -> assertEquals(200, httpResponse.getStatusLine().getStatusCode()),
                () -> assertEquals(accNum, arr.length()));
    }

    // FIND SAVING API
    // =========================================================================

    // Find a exist saving
    @Test
    void findSaving() throws ClientProtocolException, IOException, JSONException {
        int id = 1;

        HttpUriRequest request = new HttpGet(url + "api/pullout?id=" + id);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject temp1 = new JSONObject(result);
        String msg = temp1.getString("msg");
        JSONObject savJSON = temp1.getJSONArray("resultSav").getJSONObject(0);

        System.out.println(temp1);

        Saving sav = savRepo.findSaving(id);
        // Then
        assertAll("Search result verify!", () -> assertEquals(200, httpResponse.getStatusLine().getStatusCode()),
                () -> assertEquals(sav.getId(), savJSON.getInt("id")), () -> assertEquals("success", msg));
    }

    // Find a non-exist saving
    @Test
    void findSavingEmpty() throws ClientProtocolException, IOException, JSONException {
        int id = 4444;

        HttpUriRequest request = new HttpGet(url + "api/pullout?id=" + id);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject temp1 = new JSONObject(result);
        String msg = temp1.getString("msg");

        System.out.println(temp1);

        // Then
        assertAll("Search result verify!", () -> assertEquals(200, httpResponse.getStatusLine().getStatusCode()),
                () -> assertEquals("no saving found!", msg));
    }

}

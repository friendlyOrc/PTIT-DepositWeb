package bank.deposit.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static io.restassured.RestAssured.get;

import bank.deposit.Account;
import bank.deposit.AjaxResponseBody;
import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;
import bank.deposit.web.HomeController;

public class HomeControllerTest {

    @Test
    void loadMain() {
        get("http://localhost:8082/").then().assertThat().statusCode(200);
    }

    @Test
    void searchClient() {
        ResponseEntity<?> rs = home.getSearchResultViaAjax("Kiên");
        AjaxResponseBody res = (AjaxResponseBody) rs.getBody();

        assertEquals(2, res.getResult().size());
    }
}

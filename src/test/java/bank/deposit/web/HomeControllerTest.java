package bank.deposit.web;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;

@SpringBootTest
@Transactional
class HomeControllerTest {
	private final String baseUrl = "https://depositweb.herokuapp.com";
	private final AccountRepository accRepo;
	private final SavingRepository savRepo;
	private final HomeController home;
	
	@Autowired
	HomeControllerTest(AccountRepository accRepo, SavingRepository savRepo, HomeController home) {
		this.accRepo = accRepo;
		this.savRepo = savRepo;
		this.home = home;
	}
	@Test
	void contextLoads() throws Exception{
		assertThat(home).isNotNull();
	}
	@Test 
	void loadMain() {
		get(baseUrl)
		.then()
		.assertThat()
		.statusCode(200);
	}
	@Test
	void loadLogin() {
		get(baseUrl+"/login")
		.then()
		.assertThat()
		.statusCode(200);
	}
	@Test
	void loadLogout() {
		get(baseUrl+"/logout")
		.then()
		.assertThat()
		.statusCode(200);
	}
	@Test
	void postLogin() {
		String loginUrl = baseUrl+"/login";
		
	}
}

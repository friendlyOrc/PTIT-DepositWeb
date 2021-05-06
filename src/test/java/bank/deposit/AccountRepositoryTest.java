package bank.deposit;

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

import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;
import bank.deposit.web.HomeController;

@SpringBootTest
@Transactional
class AccountRepositoryTest {
	private final AccountRepository accRepo;
	private final SavingRepository savRepo;
	private final HomeController home;

	@Autowired
	AccountRepositoryTest(AccountRepository accRepo, SavingRepository savRepo, HomeController home) {
		this.accRepo = accRepo;
		this.savRepo = savRepo;
		this.home = home;
	}

	@Test
	void loginValid() {
		String username = "kienpt";
		String password = "123";

		ArrayList<Account> accList = accRepo.findAccount(username, password);
		for (int i = 0; i < accList.size(); i++) {
			assertEquals(true, accList.get(i).getUsername().equalsIgnoreCase(username));
			assertEquals(true, accList.get(i).getPassword().equalsIgnoreCase(password));
		}
	}

	@Test
	void loginInvalidUN() {
		String username = "thisisawrongusername";
		String password = "123";

		ArrayList<Account> accList = accRepo.findAccount(username, password);
		assertEquals(0, accList.size());
	}

	@Test
	void loginInvalidPW() {
		String username = "kienpt";
		String password = "thisisawrongpassword";

		ArrayList<Account> accList = accRepo.findAccount(username, password);
		assertEquals(0, accList.size());
	}

	@Test
	void findAccountByIdcardValid() {
		String card = "0123456789";

		ArrayList<Account> accList = accRepo.findAccountByIdcard(card);
		for (int i = 0; i < accList.size(); i++) {
			assertEquals(true, accList.get(i).getIdcard().equalsIgnoreCase(card));
		}
	}

	@Test
	void findAccountByIdcardInvalid1() {
		String card = "0";

		ArrayList<Account> accList = accRepo.findAccountByIdcard(card);
		assertEquals(0, accList.size());
	}

	@Test
	void findAccountByIdcardInvalid2() {
		String card = "abc";

		ArrayList<Account> accList = accRepo.findAccountByIdcard(card);
		assertEquals(0, accList.size());
	}

	@Test 
	void findAccountByUsernameValid(){
		String username = "kienpt";

		ArrayList<Account> accList = accRepo.findAccountByUsername(username);
		for (int i = 0; i < accList.size(); i++) {
			assertEquals(true, accList.get(i).getUsername().equalsIgnoreCase(username));
		}
	}

	
	@Test 
	void findAccountByUsernameInvalid1(){
		String username = "kienptabc";

		ArrayList<Account> accList = accRepo.findAccountByUsername(username);
		assertEquals(0, accList.size());
	}

	
	@Test 
	void findAccountByUsernameInvalid2(){
		String username = "<>@#$@#$";

		ArrayList<Account> accList = accRepo.findAccountByUsername(username);
		assertEquals(0, accList.size());
	}

	@Test 
	void findOneAccountValid(){
		int id = 1;

		Account acc = accRepo.findOneAccount(id);
		assertEquals(id, acc.getId());

	}

	
	@Test 
	void findOneAccountInvalid1(){
		int id = 123123123;

		Account acc = accRepo.findOneAccount(id);
		assertEquals(null, acc);

	}

	
	@Test 
	void findOneAccountInvalid2(){
		int id = -1;

		Account acc = accRepo.findOneAccount(id);
		assertEquals(null, acc);

	}

	@Test
	void findByNameValid(){
		String name = "Kiên";

		ArrayList<Account> accList = accRepo.findByUserName(name);
		for (int i = 0; i < accList.size(); i++) {
			
			assertEquals(true, accList.get(i).getName().contains(name), accList.get(i).getName());
		}

	}

	@Test
	void findByNameInvalid1(){
		String name = "Tên không có";

		ArrayList<Account> accList = accRepo.findByUserName(name);
		assertEquals(0, accList.size());

	}
	
	@Test
	void findByNameInvalid2(){
		String name = "00000000000";

		ArrayList<Account> accList = accRepo.findByUserName(name);
		assertEquals(0, accList.size());
	}

	@Test
	void loadMain(){
		get("http://localhost:8082/")
				.then()
				.assertThat()
				.statusCode(200);
	}

	@Test
	void searchClient(){
		ResponseEntity<?> rs = home.getSearchResultViaAjax("Kiên");
		AjaxResponseBody res = (AjaxResponseBody) rs.getBody(); 

		assertEquals(2, res.getResult().size());
	}

	@Test
	void addClientValid(){
		Account acc = new Account();
		acc.setAddress("abc");
		acc.setDob(new java.sql.Date(0));
		acc.setEmail("Email@gmail.com");
		// acc.setId(10000);
		acc.setSex(1);
		acc.setIdcard("0123456789");
		acc.setName("Kien Milo");

		accRepo.save(acc);

		Account accRs = accRepo.findOneAccount(acc.getId());
		assertAll("Account",
					() -> assertNotEquals(null, accRs),
                    () -> assertTrue(accRs.getAddress().equalsIgnoreCase("abc")),
                    () -> assertEquals(accRs.getDob(), new java.sql.Date(0))
                );

	}

}

package bank.deposit.data;

import java.util.ArrayList;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bank.deposit.Account;

// Test cases for AccountDAO
@SpringBootTest
@Transactional
class AccountRepositoryTest {
	private final AccountRepository accRepo;

	@Autowired
	AccountRepositoryTest(AccountRepository accRepo) {
		this.accRepo = accRepo;
	}

	// Login valid
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

	// Login wrong username
	@Test
	void loginInvalidUN() {
		String username = "thisisawrongusername";
		String password = "123";

		ArrayList<Account> accList = accRepo.findAccount(username, password);
		assertEquals(0, accList.size());
	}

	// Login wrong password
	@Test
	void loginInvalidPW() {
		String username = "kienpt";
		String password = "thisisawrongpassword";

		ArrayList<Account> accList = accRepo.findAccount(username, password);
		assertEquals(0, accList.size());
	}

	// Find account by idcard - valid
	@Test
	void findAccountByIdcardValid() {
		String card = "0123456789";

		ArrayList<Account> accList = accRepo.findAccountByIdcard(card);
		for (int i = 0; i < accList.size(); i++) {
			assertEquals(true, accList.get(i).getIdcard().equalsIgnoreCase(card));
		}
	}

	// Find account by idcard - no result - numbertype
	@Test
	void findAccountByIdcardInvalid1() {
		String card = "0";

		ArrayList<Account> accList = accRepo.findAccountByIdcard(card);
		assertEquals(0, accList.size());
	}

	// Find account by idcard - no result - string
	@Test
	void findAccountByIdcardInvalid2() {
		String card = "abc";

		ArrayList<Account> accList = accRepo.findAccountByIdcard(card);
		assertEquals(0, accList.size());
	}

	// Find account by username - valid
	@Test
	void findAccountByUsernameValid() {
		String username = "kienpt";

		ArrayList<Account> accList = accRepo.findAccountByUsername(username);
		for (int i = 0; i < accList.size(); i++) {
			assertEquals(true, accList.get(i).getUsername().equalsIgnoreCase(username));
		}
	}

	// Find account by username - no result
	@Test
	void findAccountByUsernameInvalid1() {
		String username = "kienptabc";

		ArrayList<Account> accList = accRepo.findAccountByUsername(username);
		assertEquals(0, accList.size());
	}

	// Find account by username - no result - special character
	@Test
	void findAccountByUsernameInvalid2() {
		String username = "<>@#$@#$";

		ArrayList<Account> accList = accRepo.findAccountByUsername(username);
		assertEquals(0, accList.size());
	}

	// Find account by id - valid
	@Test
	void findOneAccountValid() {
		int id = 1;

		Account acc = accRepo.findOneAccount(id);
		assertEquals(id, acc.getId());

	}

	// Find account by id - no result
	@Test
	void findOneAccountInvalid1() {
		int id = 123123123;

		Account acc = accRepo.findOneAccount(id);
		assertEquals(null, acc);

	}

	// Find account by id - no result - negative number
	@Test
	void findOneAccountInvalid2() {
		int id = -1;

		Account acc = accRepo.findOneAccount(id);
		assertEquals(null, acc);

	}

	// Find account by name - valid
	@Test
	void findByNameValid() {
		String name = "Trung Kiên";
		ArrayList<Account> accList = accRepo.findByUserName("%" + name + "%");

		for (int i = 0; i < accList.size(); i++) {

			assertEquals(true, accList.get(i).getName().contains(name), accList.get(i).getName());
		}

	}

	// Find account by name - no result
	@Test
	void findByNameInvalid1() {
		String name = "Tên không có";

		ArrayList<Account> accList = accRepo.findByUserName("%" + name + "%");
		assertEquals(0, accList.size());

	}

	// Find account by name - no result - number
	@Test
	void findByNameInvalid2() {
		String name = "00000000000";

		ArrayList<Account> accList = accRepo.findByUserName("%" + name + "%");
		assertEquals(0, accList.size());
	}

	// Add a client with valid input
	@Test
	void addClientValid() {
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
		assertAll("Account", () -> assertNotEquals(null, accRs),
				() -> assertTrue(accRs.getAddress().equalsIgnoreCase("abc")),
				() -> assertEquals(new java.sql.Date(0), accRs.getDob()),
				() -> assertTrue(accRs.getEmail().equalsIgnoreCase("Email@gmail.com")),
				() -> assertEquals(1, accRs.getSex()), () -> assertEquals("0123456789", accRs.getIdcard()),
				() -> assertEquals("Kien Milo", accRs.getName()));
	}

}

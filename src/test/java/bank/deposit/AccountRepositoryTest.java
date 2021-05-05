package bank.deposit;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;

@SpringBootTest
class AccountRepositoryTest {
	private final AccountRepository accRepo;
	private final SavingRepository savRepo;

	@Autowired
	AccountRepositoryTest(AccountRepository accRepo, SavingRepository savRepo) {
		this.accRepo = accRepo;
		this.savRepo = savRepo;
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

}

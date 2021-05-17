package bank.deposit.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bank.deposit.Saving;
import bank.deposit.web.HomeController;

@SpringBootTest
@Transactional
class SavingRepositoryTest {
	private final HomeController home;
	private final AccountRepository accRepo;
	private final SavingRepository savRepo;
	@Autowired
	public SavingRepositoryTest(HomeController home, AccountRepository accRepo,SavingRepository saveRepo) {
		this.home = home;
		this.accRepo = accRepo;
		this.savRepo = saveRepo;
	}
	
	@Test
	//Test find saving with empty input account ID
	void findAllSavingEmptyAccountID() {
		String input = "";
		int accountId;
		try {
			accountId = Integer.parseInt(input);
		}catch (NumberFormatException e) {
			fail("Empty Input");
		}
	}
	
	@Test
	//Test find all saving with exist account Id
	void findAllSavingValidAccountID() {
		int accountId = 1;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(1, listSaving.size());
	}
	@Test
	//Test find all saving with exist account Id
	void findAllSavingValidAccountID2() {
		int accountId = 2;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(1, listSaving.size());
	}
	@Test
	//Test find all saving with non-exist account Id
	void findAllSavingInValidAccountID2() {
		int accountId = 3;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(0, listSaving.size());
	}
	@Test
	//Test find all saving with non-exist account Id
	void findAllSavingInValidAccountID() {
		int accountId = 100;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(0, listSaving.size());
	}
	@Test
	//Test find all saving with non-exist account Id
	void findAllSavingInValidAccountID1() {
		int accountId = 101;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(0, listSaving.size());
	}
	@Test
	//Test find saving with exist Id
	void findSavingWithValidID() {
		int savingID = 4;
		Saving saving = this.savRepo.findSaving(savingID);
		assertNotEquals(null,saving);
	}
	@Test
	//Test find saving with exist Id
	void findSavingWithValidID1() {
		int savingID = 14;
		Saving saving = this.savRepo.findSaving(savingID);
		assertNotEquals(null,saving);
	}
	@Test
	//Test find saving with non-exist Id
	void findSavingWithInValidID() {
		int savingID = 16;
		Saving saving = this.savRepo.findSaving(savingID);
		assertEquals(null,saving);
	}
	@Test
	//Test find saving with non-exist Id
	void findSavingWithInValidID1() {
		int savingID = 20;
		Saving saving = this.savRepo.findSaving(savingID);
		assertEquals(null,saving);
	}
	@Test
	//Test pull out with exist Id
	void pulloutWithValidID() {
		int id = 4;
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		if(saving == null) {
			fail("Can not find saving");
		}
		assertEquals(0, saving.getStatus());
	}
	@Test
	//Test pull out with exist Id
	void pulloutWithValidID1() {
		int id = 14;
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		if(saving == null) {
			fail("Can not find saving");
		}
		assertEquals(0, saving.getStatus());
	}
	@Test
	//Test pull out with non-exist Id
	void pulloutWithInValidID() {
		int id = 5;
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		if(saving == null) {
			fail("Can not find saving");
		}
		assertEquals(1, saving.getStatus());
	}
	@Test
	//Test pull out with non-exist Id
	void pulloutWithInValidID2() {
		int id = 15;
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		if(saving == null) {
			fail("Can not find saving");
		}
		assertEquals(1, saving.getStatus());
	}
}

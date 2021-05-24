package bank.deposit.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bank.deposit.model.Account;
import bank.deposit.model.Saving;
import bank.deposit.web.HomeController;

@SpringBootTest
@Transactional
class SavingRepositoryTest {
	private final HomeController home;
	private final AccountRepository accRepo;
	private final SavingRepository savRepo;
	private Account acc1;
	private Account acc2;
	private Saving sav1;
	private Saving sav2;

	@Autowired
	public SavingRepositoryTest(HomeController home, AccountRepository accRepo, SavingRepository saveRepo) {
		this.home = home;
		this.accRepo = accRepo;
		this.savRepo = saveRepo;
		acc1 = accRepo.findOneAccount(5);
		acc2 = accRepo.findOneAccount(2);
		sav1 = savRepo.findSaving(1);
		sav2 = savRepo.findSaving(2);
	}


	@Test
	// Test find all saving with exist account Id
	void findAllSavingValidAccountID() {
		int accountId = acc1.getId();
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(1, listSaving.size());
	}

	@Test
	// Test find all saving with exist account Id
	void findAllSavingValidAccountID2() {
		int accountId = acc2.getId();
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		for(int i = 0; i < listSaving.size(); i++) {
			assertEquals(accountId, listSaving.get(i).getAccount().getId());
		}
	}

	@Test
	// Test find all saving with non-exist account Id
	void findAllSavingInValidAccountID2() {
		int accountId = acc1.getId() + 100;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(0, listSaving.size());
	}

	@Test
	// Test find all saving with non-exist account Id
	void findAllSavingInValidAccountID() {
		int accountId = acc1.getId() + 1000;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(0, listSaving.size());
	}

	@Test
	// Test find all saving with non-exist account Id
	void findAllSavingInValidAccountID1() {
		int accountId = acc1.getId() + 101;
		ArrayList<Saving> listSaving = this.savRepo.findAllSaving(accountId);
		assertEquals(0, listSaving.size());
	}

	@Test
	// Test find saving with exist Id
	void findSavingWithValidID() {
		int savingID = sav1.getId();
		Saving saving = this.savRepo.findSaving(savingID);
		assertNotEquals(null, saving);
	}

	@Test
	// Test find saving with exist Id
	void findSavingWithValidID1() {
		int savingID = sav2.getId();
		Saving saving = this.savRepo.findSaving(savingID);
		assertNotEquals(null, saving);
	}

	@Test
	// Test find saving with non-exist Id
	void findSavingWithInValidID() {
		int savingID = sav1.getId() + 100;
		Saving saving = this.savRepo.findSaving(savingID);
		assertEquals(null, saving);
	}

	@Test
	// Test find saving with non-exist Id
	void findSavingWithInValidID1() {
		int savingID = sav1.getId() + 1000;
		Saving saving = this.savRepo.findSaving(savingID);
		assertEquals(null, saving);
	}

	@Test
	// Test pull out with exist Id
	void pulloutWithValidID() {
		int id = sav1.getId();
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		assertEquals(0, saving.getStatus());
	}

	@Test
	// Test pull out with exist Id
	void pulloutWithValidID1() {
		int id = sav2.getId();
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		assertEquals(0, saving.getStatus());
	}

	@Test
	// Test pull out with non-exist Id
	void pulloutWithInValidID() {
		int id = sav1.getId() + 100;
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		assertNull(saving);
	}

	@Test
	// Test pull out with non-exist Id
	void pulloutWithInValidID2() {
		int id = sav1.getId() + 1000;
		this.savRepo.pullout(id);
		Saving saving = this.savRepo.findSaving(id);
		assertNull(saving);
	}
}

package bank.deposit.data;

import bank.deposit.Account;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    @Query(value = "SELECT * FROM `account` WHERE username = ?1 and password = ?2", nativeQuery = true)
    ArrayList<Account> findAccount(String un, String pw);

    @Query(value = "SELECT * FROM `account` WHERE idcard = ?1", nativeQuery = true)
    ArrayList<Account> findAccountByIdcard(String card);

    @Query(value = "SELECT * FROM `account` WHERE username = ?1", nativeQuery = true)
    ArrayList<Account> findAccountByUsername(String un);

    @Query(value = "SELECT * FROM `account` WHERE id = ?1", nativeQuery = true)
    Account findOneAccount(int i);

}
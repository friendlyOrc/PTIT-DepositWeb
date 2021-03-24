package bank.deposit.data;

import bank.deposit.Saving;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SavingRepository extends CrudRepository<Saving, Long> {

}
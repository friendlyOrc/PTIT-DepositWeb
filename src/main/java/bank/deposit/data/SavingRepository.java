package bank.deposit.data;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import bank.deposit.model.Saving;

public interface SavingRepository extends CrudRepository<Saving, Long> {
    @Query(value = "SELECT * FROM `saving` WHERE accountid = ?1", nativeQuery = true)
    ArrayList<Saving> findAllSaving(int i);

    @Query(value = "SELECT * FROM `saving` WHERE id = ?1", nativeQuery = true)
    Saving findSaving(int i);
}
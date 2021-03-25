package bank.deposit.data;

import bank.deposit.Saving;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SavingRepository extends CrudRepository<Saving, Long> {
    @Query(value = "SELECT * FROM `saving` WHERE accountid = ?1", nativeQuery = true)
    ArrayList<Saving> findAllSaving(int i);

    
    @Query(value = "SELECT * FROM `saving` WHERE id = ?1", nativeQuery = true)
    Saving findSaving(int i);

    
    @Transactional
    @Modifying
    @Query(value = "UPDATE `saving` SET status = 0 WHERE id = ?1", nativeQuery = true)
    void pullout(int id);
}
package bank.deposit.web;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;
import bank.deposit.model.Account;
import bank.deposit.model.AjaxResponseBody;
import bank.deposit.model.Saving;

@Controller
public class APIController {

    private Environment env;
    private final AccountRepository accRepo;
    private final SavingRepository savRepo;

    @Autowired
    public APIController(Environment env, AccountRepository accRepo, SavingRepository savRepo) {
        this.env = env;
        this.accRepo = accRepo;
        this.savRepo = savRepo;
    }

    @GetMapping("/search")
    @PostMapping("/api/search")
    public ResponseEntity<?> getSearchResultViaAjax(@RequestParam(name = "name") String name) {

        AjaxResponseBody result = new AjaxResponseBody();

        ArrayList<Account> users = (ArrayList<Account>) accRepo.findByUserName("%" + name + "%");
        if (users.isEmpty()) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        result.setResult(users);

        return ResponseEntity.ok(result);

    }

    // Get a saving API
    @GetMapping("/api/pullout")
    public ResponseEntity<?> getSearchResultViaAjaxSaving(@RequestParam(name = "id") int id) {

        AjaxResponseBody result = new AjaxResponseBody();

        Saving sav = savRepo.findSaving(id);

        ArrayList<Saving> rs = new ArrayList<>();
        ArrayList<Account> rsAcc = new ArrayList<>();
        Account acc = new Account();

        if (sav == null) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
            acc = accRepo.findOneAccount(sav.getAccount().getId());
            sav.setAccount(acc);
            rs.add(sav);
        }
        rsAcc.add(acc);
        result.setResult(rsAcc);
        result.setResultSav(rs);

        return ResponseEntity.ok(result);

    }
}

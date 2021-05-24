package bank.deposit.web;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;
import bank.deposit.model.Account;
import bank.deposit.model.AjaxResponseBody;
import bank.deposit.model.Saving;

@Controller
public class PullOutController {

    private Environment env;
    private final AccountRepository accRepo;
    private final SavingRepository savRepo;

    @Autowired
    public PullOutController(Environment env, AccountRepository accRepo, SavingRepository savRepo) {
        this.env = env;
        this.accRepo = accRepo;
        this.savRepo = savRepo;
    }

    @GetMapping("/pullout/delete")
    public String pulloutSaving(@RequestParam(name = "savid") int id, @RequestParam(name = "accid") int accId,
            HttpSession session, Model model) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }

        savRepo.pullout(id);
        Saving sav = savRepo.findSaving(id);
        if (sav == null) {
            return "redirect:/pullout?accid=" + accId + "&msg=fail";
        }
        return "redirect:/pullout_bill?accid=" + accId + "&savid=" + id;

    }

    @GetMapping("/pullout_bill")
    public String pulloutBill(@RequestParam(name = "savid") int id, @RequestParam(name = "accid") int accId,
            HttpSession session, Model model) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }

        Saving saving = savRepo.findSaving(id);
        Account acc = accRepo.findOneAccount(accId);

        model.addAttribute("saving", saving);
        model.addAttribute("cusAcc", acc);
        model.addAttribute("title", "Rút sổ tiết kiệm");
        model.addAttribute("page", "Pullout");

        return "pullout_bill";

    }

    @GetMapping("/pullout")
    public String publlout(@RequestParam(required = false, name = "accid") String accId,
            @RequestParam(required = false, name = "msg") String msg, Model model, HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
        if (msg != null && msg.equalsIgnoreCase("fail"))
            model.addAttribute("msg", "fail");
        model.addAttribute("page", "Pullout");
        if (accId == null) {
            model.addAttribute("title", "Tìm tài khoản");
            return "search_account_sav";
        } else {
            model.addAttribute("title", "Danh sách sổ tiết kiệm");

            ArrayList<Saving> savings = savRepo.findAllSaving(Integer.parseInt(accId));
            Account acc = accRepo.findOneAccount(Integer.parseInt(accId));

            model.addAttribute("savings", savings);
            model.addAttribute("cusAcc", acc);

            return "saving_list";
        }
    }
}

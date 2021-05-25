package bank.deposit.web;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import bank.deposit.data.AccountRepository;
import bank.deposit.model.Account;

@Controller
public class SignUpController {

    private final AccountRepository accRepo;
    private ValidateFunction val;

    @Autowired
    public SignUpController(Environment env, AccountRepository accRepo, ValidateFunction val) {
        this.accRepo = accRepo;
        this.val = val;
    }

    @GetMapping("/members")
    public String members(Model model, HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
        model.addAttribute("page", "Member");
        model.addAttribute("title", "Đăng kí thành viên mới");
        model.addAttribute("repass", "");
        model.addAttribute("account", new Account());
        return "add_user";
    }

    @PostMapping("/members")
    public String postMember(Account account, Model model, HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
        boolean rs = false;
        if (!val.specialKey(account.getName()) || val.isContainsNumber(account.getName())) {
            model.addAttribute("msg", "name");
        } else if (account.getName().length() > 25) {
            model.addAttribute("msg", "longName");
        } else if (!val.specialKey(account.getAddress())) {
            model.addAttribute("msg", "addr");
        } else if (!val.specialKey(account.getIdcard())) {
            model.addAttribute("msg", "cccd");
        } else if (!val.verifyId(account.getIdcard())) {
            model.addAttribute("msg", "cccdWord");
        } else if (!val.validDOB(account.getDob())) {
            model.addAttribute("msg", "date");
        } else if (!val.validateEmail(account.getEmail())) {
            model.addAttribute("msg", "email");
        } else {
            ArrayList<Account> accList = accRepo.findAccountByIdcard(account.getIdcard());

            if (accList.size() != 0) {
                model.addAttribute("msg", "dup");
            } else {
                model.addAttribute("account", account);

                model.addAttribute("msg", "success");
                accRepo.save(account);
                rs = true;
            }
        }
        model.addAttribute("page", "Member");
        model.addAttribute("title", "Đăng kí thành viên mới");
        if (rs) {
            return "user";
        } else {
            return "add_user";
        }
    }
}

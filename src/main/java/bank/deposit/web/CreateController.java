package bank.deposit.web;

import java.sql.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;
import bank.deposit.model.Account;
import bank.deposit.model.Saving;
import bank.deposit.web.ValidateFunction;

@Controller
public class CreateController {

    private Environment env;
    private final AccountRepository accRepo;
    private final SavingRepository savRepo;
    private ValidateFunction val;

    @Autowired
    public CreateController(Environment env, AccountRepository accRepo, SavingRepository savRepo,
            ValidateFunction val) {
        this.env = env;
        this.accRepo = accRepo;
        this.savRepo = savRepo;
        this.val = val;
    }

    @GetMapping("/create")
    public String create(@RequestParam(required = false, name = "accid") String accId, Model model,
            HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
        model.addAttribute("page", "Create");
        if (accId == null) {
            model.addAttribute("title", "Tìm kiếm tài khoản");
            return "search_account";
        } else {

            model.addAttribute("title", "Mở sổ tiết kiệm");
            Saving saving = new Saving();
            Account acc = accRepo.findOneAccount(Integer.parseInt(accId));
            saving.setAccount(acc);
            model.addAttribute("saving", saving);
            model.addAttribute("cusAcc", acc);

            return "create";
        }
    }

    @PostMapping("/create")
    public String createSaving(@RequestParam(required = false, name = "accid") String accId, Saving saving, Model model,
            HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
        boolean succ = false;
        if (!val.specialKey(String.valueOf(saving.getBalance()))) {
            model.addAttribute("msg", "speBal");
        } else {

            if (saving.getBalance() <= 1000000) {
                model.addAttribute("msg", "smInp");
            } else {
                long millis = System.currentTimeMillis();
                Date date = new java.sql.Date(millis);
                try {
                    Account acc = accRepo.findOneAccount(Integer.parseInt(accId));
                    Account staff = (Account) session.getAttribute("account");
                    staff = accRepo.findOneAccount(staff.getId());
                    int size = savRepo.findAllSaving(acc.getId()).size();
                    switch (saving.getTime()) {
                        case 0:
                            saving.setInterest((float) 0.1);
                            break;
                        case 1:
                            saving.setInterest((float) 3.1);
                            break;
                        case 2:
                            saving.setInterest((float) 3.1);
                            break;
                        case 3:
                            saving.setInterest((float) 3.4);
                            break;
                        case 4:
                            saving.setInterest((float) 3.4);
                            break;
                        case 5:
                            saving.setInterest((float) 3.4);
                            break;
                        case 6:
                            saving.setInterest((float) 4.0);
                            break;
                        case 7:
                            saving.setInterest((float) 4.0);
                            break;
                        case 8:
                            saving.setInterest((float) 4.0);
                            break;
                        case 9:
                            saving.setInterest((float) 4.0);
                            break;
                        case 10:
                            saving.setInterest((float) 4.0);
                            break;
                        case 11:
                            saving.setInterest((float) 4.0);
                            break;
                        case 12:
                            saving.setInterest((float) 5.6);
                            break;
                        case 13:
                            saving.setInterest((float) 5.6);
                            break;
                        case 15:
                            saving.setInterest((float) 5.6);
                            break;
                        case 18:
                            saving.setInterest((float) 5.6);
                            break;
                        case 24:
                            saving.setInterest((float) 5.6);
                            break;
                        default:
                            saving.setInterest((float) 0.1);
                    }
                    saving.setStaff(staff);
                    saving.setAccount(acc);
                    saving.setCreateTime(date);
                    saving.setStatus(1);
                    saving.setId(staff.getId() + 1000);
                    savRepo.save(saving);
                    // saving = new Saving();
                    succ = true;
                } catch (Exception e) {
                    e.printStackTrace();

                    model.addAttribute("msg", "fail");

                }

            }
        }

        Account acc = accRepo.findOneAccount(Integer.parseInt(accId));
        model.addAttribute("saving", saving);
        model.addAttribute("cusAcc", acc);
        model.addAttribute("title", "Mở sổ tiết kiệm");
        model.addAttribute("page", "Create");

        if (succ) {
            return "create_bill";
        } else {
            return "create";
        }
    }

}

package bank.deposit.web;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import bank.deposit.Account;
import bank.deposit.AjaxResponseBody;
import bank.deposit.Saving;
import bank.deposit.data.AccountRepository;
import bank.deposit.data.SavingRepository;

@Controller
public class HomeController {

    private Environment env;
    private final AccountRepository accRepo;
    private final SavingRepository savRepo;

    @Autowired
    HomeController(Environment env, AccountRepository accRepo, SavingRepository savRepo) {
        this.env = env;
        this.accRepo = accRepo;
        this.savRepo = savRepo;
    }

    public boolean isAllNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            try {
                Double.parseDouble(String.valueOf(s.charAt(i)));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public boolean verifyId(String s) {
        if (isAllNumber(s) && s.length() == 10) {
            return true;
        }
        return false;
    }

    public boolean specialKey(String s) {
        if (s.indexOf("<") != -1)
            return false;
        if (s.indexOf(">") != -1)
            return false;
        if (s.indexOf("@") != -1)
            return false;
        if (s.indexOf("#") != -1)
            return false;
        if (s.indexOf("$") != -1)
            return false;
        if (s.indexOf("%") != -1)
            return false;
        if (s.indexOf("^") != -1)
            return false;
        if (s.indexOf("&") != -1)
            return false;
        if (s.indexOf("(") != -1)
            return false;
        if (s.indexOf(")") != -1)
            return false;
        if (s.indexOf("!") != -1)
            return false;
        if (s.indexOf("-") != -1)
            return false;

        return true;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }

        model.addAttribute("page", "Home");
        model.addAttribute("title", "Trang chủ");
        return "home";

    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        if (session.getAttribute("account") != null) {
            return "redirect:/";
        }

        model.addAttribute("account", new Account());
        model.addAttribute("page", "Login");
        model.addAttribute("title", "Đăng nhập");
        if (model.getAttribute("msg") == null)

            model.addAttribute("msg", "");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpSession session) {
        session.removeAttribute("account");
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String postLogin(Account account, Model model, HttpSession session) {
        try {
            ArrayList<Account> accList = accRepo.findAccount(account.getUsername(), account.getPassword());
            if (accList.size() == 1) {
                session.setAttribute("account", accList.get(0));
                return "redirect:/";
            } else {
                accList = accRepo.findAccountByUsername(account.getUsername());
                if (accList.size() == 0) {
                    model.addAttribute("msg", "username");
                } else {
                    model.addAttribute("msg", "password");
                }
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("msg", "error");
            return "login";
        }

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

    public String genPass() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        System.out.println(generatedString);
        return generatedString;
    }

    @PostMapping("/members")
    public String postMember(Account account, Model model, HttpSession session) {
        try {
            if (!specialKey(account.getName())) {
                model.addAttribute("msg", "name");
            } else if (!specialKey(account.getAddress())) {
                model.addAttribute("msg", "addr");
            } else if (!specialKey(account.getIdcard())) {
                model.addAttribute("msg", "cccd");
            } else if (!specialKey(account.getUsername())) {
                model.addAttribute("msg", "acc");
            } else if (!verifyId(account.getIdcard())) {
                model.addAttribute("msg", "cccdWord");
            } else {
                ArrayList<Account> accList = accRepo.findAccountByIdcard(account.getIdcard());
                ArrayList<Account> accList2 = accRepo.findAccountByUsername(account.getUsername());
                if (accList.size() != 0) {
                    model.addAttribute("msg", "dup");
                } else if (accList2.size() != 0) {
                    model.addAttribute("msg", "dupacc");
                } else {
                    String pass = genPass();
                    account.setPassword(pass);
                    model.addAttribute("username", account.getUsername());
                    model.addAttribute("pass", pass);
                    model.addAttribute("account", new Account());

                    model.addAttribute("msg", "success");
                    accRepo.save(account);
                }
            }
        } catch (Exception e) {
            model.addAttribute("msg", "fail");
        }

        model.addAttribute("page", "Member");
        model.addAttribute("title", "Đăng kí thành viên mới");
        return "add_user";
    }

    @GetMapping("/calc")
    public String calc(Model model, HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }

        model.addAttribute("page", "Calc");
        model.addAttribute("title", "Tính lãi");
        return "calc";
    }

    @GetMapping("/create")
    public String create(@RequestParam(required = false, name = "id") String accId, Model model, HttpSession session) {
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
    public String createSaving(@RequestParam(required = false, name = "id") String accId, Saving saving, Model model,
            HttpSession session) {
        if (!specialKey(String.valueOf(saving.getBalance()))) {
            model.addAttribute("msg", "speBal");
        } else if (!specialKey(String.valueOf(saving.getInterest()))) {
            model.addAttribute("msg", "speInt");
        } else if (!specialKey(String.valueOf(saving.getTime()))) {
            model.addAttribute("msg", "speTime");
        } else {

            if (saving.getBalance() <= 1000000) {
                model.addAttribute("msg", "smInp");
            } else if (saving.getInterest() <= 0) {
                model.addAttribute("msg", "smInt");
            } else if (saving.getTime() <= 0) {
                model.addAttribute("msg", "smTime");
            } else {
                long millis = System.currentTimeMillis();
                Date date = new java.sql.Date(millis);
                try {
                    Account acc = accRepo.findOneAccount(Integer.parseInt(accId));
                    saving.setAccount(acc);
                    saving.setCreateTime(date);
                    saving.setStatus(1);
                    savRepo.save(saving);
                    saving = new Saving();
                } catch (Exception e) {
                    model.addAttribute("msg", "fail");

                }

            }
        }

        Account acc = accRepo.findOneAccount(Integer.parseInt(accId));
        model.addAttribute("saving", saving);
        model.addAttribute("cusAcc", acc);
        model.addAttribute("title", "Mở sổ tiết kiệm");
        return "create";
    }

    @GetMapping("/search")
    @PostMapping("/api/search")
    public ResponseEntity<?> getSearchResultViaAjax(@RequestParam(name = "name") String name) {

        AjaxResponseBody result = new AjaxResponseBody();

        ArrayList<Account> users = (ArrayList<Account>) accRepo.findByUserName(name);
        if (users.isEmpty()) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        result.setResult(users);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/api/pullout")
    public ResponseEntity<?> getSearchResultViaAjaxSaving(@RequestParam(name = "id") int id) {

        AjaxResponseBody result = new AjaxResponseBody();

        Saving sav = savRepo.findSaving(id);
        if (sav == null) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        ArrayList<Saving> rs = new ArrayList<>();
        rs.add(sav);
        result.setResultSav(rs);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/api/pullout/delete")
    public ResponseEntity<?> pulloutSaving(@RequestParam(name = "id") int id) {

        AjaxResponseBody result = new AjaxResponseBody();

        savRepo.pullout(id);
        Saving sav = savRepo.findSaving(id);
        if (sav.getStatus() == 1) {
            result.setMsg("Error!");
        } else {
            result.setMsg("success");
        }
        ArrayList<Saving> rs = new ArrayList<>();
        rs.add(sav);
        result.setResultSav(rs);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/pullout")
    public String publlout(@RequestParam(required = false, name = "id") String accId, Model model,
            HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
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

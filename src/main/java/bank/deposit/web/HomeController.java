package bank.deposit.web;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
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

    public boolean containsNumber(String s) {
        for (int i = 0; i <= 9; i++) {
            if (s.indexOf(String.valueOf(i)) != -1)
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
        // session.removeAttribute("account");
        session.invalidate();
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
                model.addAttribute("title", "Đăng nhập");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("msg", "error");
            model.addAttribute("title", "Đăng nhập");
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

    public boolean validDOB(java.sql.Date date) {
        java.util.Date date2 = new java.util.Date(date.getTime());
        java.util.Date date1 = new java.util.Date();
        return getDiffYears(date2, date1) >= 18;
    }

    public static int getDiffYears(java.util.Date first, java.util.Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
                || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @PostMapping("/members")
    public String postMember(Account account, Model model, HttpSession session) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
        boolean rs = false;
        try {
            if (!specialKey(account.getName()) || containsNumber(account.getName())) {
                model.addAttribute("msg", "name");
            } else if (account.getName().length() > 25) {
                model.addAttribute("msg", "longName");
            } else if (!specialKey(account.getAddress())) {
                model.addAttribute("msg", "addr");
            } else if (!validateEmail(account.getEmail())) {
                model.addAttribute("msg", "email");
            } else if (!specialKey(account.getIdcard())) {
                model.addAttribute("msg", "cccd");
            } else if (!verifyId(account.getIdcard())) {
                model.addAttribute("msg", "cccdWord");
            } else if (!validDOB(account.getDob())) {
                model.addAttribute("msg", "date");
            } else {
                ArrayList<Account> accList = accRepo.findAccountByIdcard(account.getIdcard());
                ArrayList<Account> accList2 = accRepo.findAccountByUsername(account.getUsername());
                if (accList.size() != 0) {
                    model.addAttribute("msg", "dup");
                } else if (accList2.size() != 0) {
                    model.addAttribute("msg", "dupacc");
                } else {
                    // String pass = genPass();
                    // account.setPassword(pass);
                    // model.addAttribute("username", account.getUsername());
                    // model.addAttribute("pass", pass);
                    model.addAttribute("account", account);

                    model.addAttribute("msg", "success");
                    accRepo.save(account);
                    rs = true;
                }
            }
        } catch (Exception e) {
            model.addAttribute("msg", "fail");
        }
        model.addAttribute("page", "Member");
        model.addAttribute("title", "Đăng kí thành viên mới");
        if (rs) {
            return "user";
        } else {
            return "add_user";
        }
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
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }
        boolean succ = false;
        if (!specialKey(String.valueOf(saving.getBalance()))) {
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
                            saving.setInterest((float) 4.0);
                            break;
                        default:
                            saving.setInterest((float) 0.1);
                    }
                    saving.setStaff(staff);
                    saving.setAccount(acc);
                    saving.setCreateTime(date);
                    saving.setStatus(1);
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
        ArrayList<Account> rsAcc = new ArrayList<>();
        Account acc = accRepo.findOneAccount(sav.getAccount().getId());
        sav.setAccount(acc);
        rsAcc.add(acc);
        rs.add(sav);
        result.setResult(rsAcc);
        result.setResultSav(rs);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/pullout/delete")
    public String pulloutSaving(@RequestParam(name = "id") int id, @RequestParam(name = "acc") int accId,
            HttpSession session, Model model) {
        if (session.getAttribute("account") == null) {
            return "redirect:/login";
        }

        savRepo.pullout(id);
        Saving sav = savRepo.findSaving(id);
        if (sav.getStatus() == 1) {
            return "redirect:/pullout?id=" + accId + "&msg=fail";
        }
        return "redirect:/pullout_bill?acc=" + accId + "&id=" + id;

    }

    @GetMapping("/pullout_bill")
    public String pulloutBill(@RequestParam(name = "id") int id, @RequestParam(name = "acc") int accId,
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
    public String publlout(@RequestParam(required = false, name = "id") String accId,
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

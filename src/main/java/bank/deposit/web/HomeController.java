package bank.deposit.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    public HomeController(Environment env) {
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

}

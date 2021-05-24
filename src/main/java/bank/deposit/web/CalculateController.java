package bank.deposit.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalculateController {

    private Environment env;

    @Autowired
    public CalculateController(Environment env) {
        this.env = env;
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

}

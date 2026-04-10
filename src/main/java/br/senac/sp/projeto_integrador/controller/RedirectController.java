package br.senac.sp.projeto_integrador.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

}

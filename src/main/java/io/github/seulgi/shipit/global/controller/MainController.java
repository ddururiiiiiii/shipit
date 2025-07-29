package io.github.seulgi.shipit.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * 메인화면 (로그인 화면)
     * @return
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/auth/signin";
    }

}
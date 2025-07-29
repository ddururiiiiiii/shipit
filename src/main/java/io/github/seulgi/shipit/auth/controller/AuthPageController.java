package io.github.seulgi.shipit.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthPageController {

    /**
     * 로그인 화면
     * @return
     */
    @GetMapping("/signin")
    public String loginPage() {
        return "auth/signin";
    }

    /**
     * 회원가입 화면
     * @return
     */
    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }
}
package com.couponconcurrencylab.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 화면(Thymeleaf) 라우팅. 각 페이지는 REST API를 fetch 로 호출한다.
 */
@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/members")
    public String members() {
        return "members";
    }

    @GetMapping("/policies")
    public String policies() {
        return "policies";
    }

    @GetMapping("/issue")
    public String issue() {
        return "issue";
    }
}

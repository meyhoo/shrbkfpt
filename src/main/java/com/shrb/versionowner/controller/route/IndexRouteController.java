package com.shrb.versionowner.controller.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexRouteController {

    private final static String WELCOME_HTML = "/welcome";

    @RequestMapping("/")
    public String indexPage(){
        return "redirect:/user/loginPage.html";
    }

    @RequestMapping("/welcomePage.html")
    public String welcomePage() {
        return WELCOME_HTML;
    }
}

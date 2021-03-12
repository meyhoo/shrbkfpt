package com.shrb.versionowner.controller.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserRouteController {
    private final static String USER_LOGIN_HTML = "/user/loginPage";
    private final static String USER_MAIN_HTML = "/user/mainPage";

    @RequestMapping("/loginPage.html")
    public String loginPage(){
        return USER_LOGIN_HTML;
    }

    @RequestMapping("/mainPage.html")
    public String mainPage() {
        return USER_MAIN_HTML;
    }
}

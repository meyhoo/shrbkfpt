package com.shrb.versionowner.controller.route;

import com.shrb.versionowner.entity.business.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserRouteController {
    private final static String USER_LOGIN_HTML = "/user/loginPage";
    private final static String USER_MAIN_HTML = "/user/mainPage";

    @RequestMapping("/loginPage.html")
    public String loginPage(Model model){
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            model.addAttribute("userName", user.getUserName());
            model.addAttribute("role", user.getRole());
            return USER_MAIN_HTML;
        }
        return USER_LOGIN_HTML;
    }

    @RequestMapping("/mainPage.html")
    public String mainPage(Model model) {
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userName", user.getUserName());
        model.addAttribute("role", user.getRole());
        return USER_MAIN_HTML;
    }
}

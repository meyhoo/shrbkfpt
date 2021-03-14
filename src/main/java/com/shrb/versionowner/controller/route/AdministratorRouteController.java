package com.shrb.versionowner.controller.route;

import com.shrb.versionowner.entity.business.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator")
public class AdministratorRouteController {
    private final static String MANAGE_TEAM_HTML = "/administrator/manageTeamPage";
    private final static String MANAGE_VERSION_HTML = "/administrator/manageVersionPage";

    @RequestMapping("/manageTeamPage.html")
    public String mainPage(Model model) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userName", user.getUserName());
        model.addAttribute("role", user.getRole());
        return MANAGE_TEAM_HTML;
    }


}

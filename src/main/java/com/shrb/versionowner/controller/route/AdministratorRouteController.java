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
    private final static String ADD_USER_HTML = "/administrator/addUserPage";
    private final static String MANAGE_VERSION_HTML = "/administrator/manageVersionPage";
    private final static String ADD_VERSION_HTML = "/administrator/addVersionPage";
    private final static String VERSION_INFO_HTML = "/administrator/versionInfoPage";

    @RequestMapping("/manageTeamPage.html")
    public String manageTeamPage(Model model) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userName", user.getUserName());
        model.addAttribute("role", user.getRole());
        return MANAGE_TEAM_HTML;
    }

    @RequestMapping("/addUserPage.html")
    public String addUserPage() {
        return ADD_USER_HTML;
    }

    @RequestMapping("/manageVersionPage.html")
    public String manageVersionPage() {
        return MANAGE_VERSION_HTML;
    }

    @RequestMapping("/addVersionPage.html")
    public String addVersionPage() {
        return ADD_VERSION_HTML;
    }

    @RequestMapping("/versionInfoPage.html")
    public String versionInfoPage() {
        return VERSION_INFO_HTML;
    }
}

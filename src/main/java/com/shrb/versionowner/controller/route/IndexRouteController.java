package com.shrb.versionowner.controller.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexRouteController {
    @RequestMapping("/")
    public String indexPage(){
        return "redirect:/user/loginPage.html";
    }
}

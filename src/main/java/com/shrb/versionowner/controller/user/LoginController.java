package com.shrb.versionowner.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.User;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.service.RuntimeCacheService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private Configuration configuration;

    @ApiRsp
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse login(String account, String password) {
        ApiResponse apiResponse = new ApiResponse();
        JSONObject dataObject = new JSONObject();
        SecurityUtils.getSubject().getSession().setTimeout(configuration.getShiroSessionTimeout());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken uToken = new UsernamePasswordToken(account, password);
        try {
            subject.login(uToken);
            dataObject.put("resultCode", "00");
            dataObject.put("resultMsg", "登录成功");
            apiResponse.setData(dataObject);
        } catch (UnknownAccountException e) {
            dataObject.put("resultCode", "03");
            dataObject.put("resultMsg", "账号不存在");
            apiResponse.setData(dataObject);
        } catch (IncorrectCredentialsException e) {
            dataObject.put("resultCode", "01");
            dataObject.put("resultMsg", "账号与密码不匹配");
            apiResponse.setData(dataObject);
        }
        User user = runtimeCacheService.getUser(account);
        Session session = subject.getSession();
        session.setAttribute("user", user);
        return apiResponse;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return "redirect:/user/loginPage.html";
    }
}

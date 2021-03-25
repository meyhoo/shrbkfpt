package com.shrb.versionowner.controller.developer;

import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.User;
import com.shrb.versionowner.service.DeveloperVersionService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/developer")
public class DeveloperVersionController {

    @Autowired
    private DeveloperVersionService developerVersionService;

    @ApiRsp
    @RequestMapping(value = "/searchAdministratorVersionList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchAdministratorVersionList(HttpServletRequest request) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiExtendResponse apiResponse = developerVersionService.searchDeveloperVersionList(request, user.getUserName());
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/searchDeveloperVersionTaskList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchDeveloperVersionTaskList(HttpServletRequest request) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiExtendResponse apiResponse = developerVersionService.searchDeveloperVersionTaskList(request, user.getUserName());
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/addDeveloperVersionTask", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addDeveloperVersionTask(String versionId, String taskInfo) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.addDeveloperVersionTask(user.getUserName(), versionId, taskInfo);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/updateDeveloperVersionTaskState", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateDeveloperVersionTaskState(String versionId, String taskInfo, String state) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.updateDeveloperVersionTaskState(user.getUserName(), versionId, taskInfo, state);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/updateDeveloperVersionPriority", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateDeveloperVersionPriority(String versionId, String priority) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.updateDeveloperVersionPriority(user.getUserName(), versionId, priority);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/deleteDeveloperVersionTask", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteDeveloperVersionTask(String versionId, String taskInfo) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.deleteDeveloperVersionTask(user.getUserName(), versionId, taskInfo);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/becomeCommitter", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse becomeCommitter(String versionId) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.becomeCommitter(versionId, user.getUserName());
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @RequestMapping(value = "/uploadVersion", method = RequestMethod.POST)
    public String uploadVersion(String versionId, MultipartFile[] avatar) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            try {
                developerVersionService.uploadVersionFile(user.getUserName(), versionId, avatar);
                return "/developer/chooseVersionPage";
            } catch (Exception e) {
                return "/errorPage";
            }
        }
        return "/errorPage";
    }

    @RequestMapping(value = "/downloadVersionTemplate", method = RequestMethod.GET)
    public void downloadVersionTemplate(HttpServletResponse resp, String versionId) {
        File file = new File("D:/versionowner/data/developer/admin/version_20210325/taskInfo.dat");
        resp.setHeader("content-type", "application/octet-stream");
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment;filename=" + "taskInfo.dat");
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

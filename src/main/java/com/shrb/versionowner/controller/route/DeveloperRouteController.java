package com.shrb.versionowner.controller.route;

import com.shrb.versionowner.service.SqlAutoCreateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/developer")
public class DeveloperRouteController {
    private static final Logger log = LoggerFactory.getLogger(DeveloperRouteController.class);
    private final static String COMMIT_VERSION_HTML = "/developer/commitVersionPage";
    private final static String SELECT_TEMPLATE_GROUP_HTML = "/developer/selectTemplateGroupPage";
    private final static String SQL_AUTO_CREATE_HTML = "/developer/sqlAutoCreatePage";

    @Autowired
    private SqlAutoCreateService sqlAutoCreateService;

    @RequestMapping("/selectTemplateGroupPage.html")
    public String selectTemplateGroupPage() {
        return SELECT_TEMPLATE_GROUP_HTML;
    }

    @RequestMapping("/sqlAutoCreatePage.html")
    public String sqlAutoCreatePage(String templateGroupId, Model model) throws Exception {
        Map<String, Object> map = sqlAutoCreateService.analysisTemplateGroup(templateGroupId);
        model.addAllAttributes(map);
        return SQL_AUTO_CREATE_HTML;
    }
}

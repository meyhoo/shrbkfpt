package com.shrb.versionowner.controller.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class PublicRouteController {

    private final static String SQL_TEMPLATES_HTML = "/public/sqlTemplatesPage";
    private final static String ADD_SQL_TEMPLATE_HTML = "/public/addSqlTemplatePage";
    private final static String EDIT_SQL_TEMPLATE_HTML = "/public/editSqlTemplatePage";
    private final static String SQL_TEMPLATE_GROUP_HTML = "/public/sqlTemplateGroupPage";
    private final static String ADD_SQL_TEMPLATE_GROUP_HTML = "/public/addSqlTemplateGroupPage";
    private final static String EDIT_SQL_TEMPLATE_GROUP_HTML = "/public/editSqlTemplateGroupPage";
    private final static String SYSTEM_URLS_HTML = "/public/systemUrlsPage";
    private final static String ADD_SYSTEM_URL_HTML = "/public/addSystemUrlPage";

    @RequestMapping("/sqlTemplatesPage.html")
    public String sqlTemplatesPage() {
        return SQL_TEMPLATES_HTML;
    }

    @RequestMapping("/addSqlTemplatePage.html")
    public String addSqlTemplatePage() {
        return ADD_SQL_TEMPLATE_HTML;
    }

    @RequestMapping("/editSqlTemplatePage.html")
    public String editSqlTemplatePage() {
        return EDIT_SQL_TEMPLATE_HTML;
    }

    @RequestMapping("/sqlTemplateGroupPage.html")
    public String sqlTemplateGroupPage() {
        return SQL_TEMPLATE_GROUP_HTML;
    }

    @RequestMapping("/addSqlTemplateGroupPage.html")
    public String addSqlTemplateGroupPage() {
        return ADD_SQL_TEMPLATE_GROUP_HTML;
    }

    @RequestMapping("/editSqlTemplateGroupPage.html")
    public String editSqlTemplateGroupPage() {
        return EDIT_SQL_TEMPLATE_GROUP_HTML;
    }

    @RequestMapping("/systemUrlsPage.html")
    public String systemUrlsPage() {
        return SYSTEM_URLS_HTML;
    }

    @RequestMapping("/addSystemUrlPage.html")
    public String addSystemUrlPage() {
        return ADD_SYSTEM_URL_HTML;
    }
}

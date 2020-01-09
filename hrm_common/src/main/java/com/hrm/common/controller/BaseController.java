package com.hrm.common.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/7 17:01
 */
public class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected String companyId;
    protected String companyName;

    /**
     * 在进入controller之前执行的内容
     * @param request
     * @param response
     */
    @ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.companyId = "1";
        this.companyName = "传智播客";
    }
    //企业id，（暂时使用1,以后会动态获取）
    public String parseCompanyId() {
        return "1";
    }
    public String parseCompanyName() {
        return "江苏传智播客教育股份有限公司";
    }
}

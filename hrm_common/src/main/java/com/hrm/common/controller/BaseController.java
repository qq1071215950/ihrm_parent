package com.hrm.common.controller;

import com.hrm.domain.system.response.ProfileResult;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
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
    protected Claims claims;

    /**
     * 在进入controller之前执行的内容
     * @param request
     * @param response
     */
    // todo jwt的形式
   /* @ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        Object obj = request.getAttribute("user_claims");
        if (obj != null){
            this.claims = (Claims) obj;
            this.companyId = (String) claims.get("companyId");
            this.companyName = (String) claims.get("companyName");
        }
    }*/
   // todo shiro的形式
    @ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
       // 得到安全数据
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        if (principals != null && !principals.isEmpty()){
            ProfileResult result = (ProfileResult) principals.getPrimaryPrincipal();
            this.companyId = result.getCompanyId();
            this.companyName = result.getCompany();
        }
    }


}

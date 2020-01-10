package com.hrm.common.interceptor;

import com.hrm.common.entity.ResultCode;
import com.hrm.common.exception.CommonException;
import com.hrm.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/9 13:58
 */
//@Component
public class JwtInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 1.通过request获取请求token信息
        String authorization = request.getHeader("Authorization");
        //判断请求头信息是否为空，或者是否已Bearer开头
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer")) {
            //获取token数据
            String token = authorization.replace("Bearer ","");
            //解析token获取claims
            Claims claims = jwtUtil.parseJWT(token);
            if(claims != null) {
                // todo api权限访问控制
               /* //通过claims获取到当前用户的可访问API权限字符串
                String apis = (String) claims.get("apis");
                //api-user-delete,api-user�update 资源访问标识
                //通过handler
                HandlerMethod h = (HandlerMethod) handler;
                //获取接口上的reqeustmapping注解
                RequestMapping annotation = h.getMethodAnnotation(RequestMapping.class);
                //获取当前请求接口中的name属性
                String name = annotation.name();
                //判断当前用户是否具有响应的请求权限
                if(apis.contains(name)) {
                    request.setAttribute("user_claims",claims);
                    return true;
                }else {
                    throw new CommonException(ResultCode.UNAUTHORISE);
                }*/

                request.setAttribute("user_claims",claims);
                return true;
            }
         }
        throw new CommonException(ResultCode.UNAUTHENTICATED);
    }
}

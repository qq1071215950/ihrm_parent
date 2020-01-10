package com.hrm.common.handler;

import com.hrm.common.entity.Result;
import com.hrm.common.entity.ResultCode;
import com.hrm.common.exception.CommonException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/7 15:04
 */
@ControllerAdvice
public class BaseExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result error(HttpServletRequest request, HttpServletResponse response,
                        Exception e) throws IOException {
        e.printStackTrace();
        if (e.getClass() == CommonException.class) {
            CommonException ce = (CommonException) e;
            return new Result(ce.getCode());
        } else {
            return Result.ERROR();
        }
    }

    @ResponseBody
    @ExceptionHandler(value = AuthorizationException.class)
    public Result error(HttpServletRequest request, HttpServletResponse response,
                        AuthorizationException e) throws IOException {
        return new Result(ResultCode.QUAN_XIAN_BU_ZU);
    }
}

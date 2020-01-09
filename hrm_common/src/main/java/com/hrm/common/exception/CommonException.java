package com.hrm.common.exception;

import com.hrm.common.entity.ResultCode;
import lombok.Getter;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/7 15:01
 */
@Getter
public class CommonException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private ResultCode code = ResultCode.SERVER_ERROR;
    public CommonException(){}
    public CommonException(ResultCode resultCode) {
        super(resultCode.message());
        this.code = resultCode;
    }
}

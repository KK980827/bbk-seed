package com.bestbigkk.common.exception;

import com.bestbigkk.common.enums.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author: 开
* @date: 2020-03-24 09:26:55
* @describe: 常规业务异常，业务中可以直接抛出该异常进行快速失败。后续会有专门的拦截器进行处理进行包装。
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    private ResultCode resultCode;

    public BusinessException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode == null ? ResultCode.BAD_REQUEST : resultCode;
    }
}

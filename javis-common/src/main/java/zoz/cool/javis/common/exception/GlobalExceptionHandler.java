package zoz.cool.javis.common.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import zoz.cool.javis.common.api.CommonResult;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;


/**
 * 全局异常处理类 Created by javis on 2023/11/16.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    // 未登录异常
    @ResponseBody
    @ExceptionHandler(value = NotLoginException.class)
    public CommonResult<String> handlerNotLoginException(NotLoginException e) {
        return CommonResult.unauthorized(e.getMessage());
    }

    // 未授权异常(角色)
    @ResponseBody
    @ExceptionHandler(value = NotRoleException.class)
    public CommonResult<String> handlerNotRoleException(NotRoleException e) {
        return CommonResult.forbidden(e.getMessage());
    }

    // 未授权异常(权限)
    @ResponseBody
    @ExceptionHandler(value = NotPermissionException.class)
    public CommonResult<String> handlerNotPermissionException(NotPermissionException e) {
        return CommonResult.forbidden(e.getMessage());
    }

    // 捕捉其他所有异常
    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult<String> handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    // 请求参数校验异常
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<String> handleValidException(MethodArgumentNotValidException e) {
        return getBindResult(e);
    }

    // 参数绑定异常
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public CommonResult<String> handleValidException(BindException e) {
        return getBindResult(e);
    }

    @NotNull
    private CommonResult<String> getBindResult(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public CommonResult<String> handleMaxUploadSizeExceededException(BindException e) {
        return CommonResult.failed("文件大小超出限制");
    }
}

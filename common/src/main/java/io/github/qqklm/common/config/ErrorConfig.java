package io.github.qqklm.common.config;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import io.github.qqklm.common.BusinessException;
import io.github.qqklm.common.BusinessCode;
import io.github.qqklm.common.ReturnBean;
import io.github.qqklm.common.component.I18nComponent;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author wb
 * @date 2022/3/28 11:34
 */
@RestControllerAdvice
@ControllerAdvice
public class ErrorConfig {
    private final I18nComponent i18nComponent;

    public ErrorConfig(I18nComponent i18nComponent) {
        this.i18nComponent = i18nComponent;
    }

    /**
     * 请求类型不匹配异常处理
     *
     * @param e       参数验证异常
     * @param request 请求
     * @return 通用返回值
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ReturnBean<String> httpRequestMethodError(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        ArrayList<String> i18nParameters = ListUtil.toList(String.join("、", e.getSupportedMethods()));
        i18nParameters.add(0, e.getMethod());
        String message = i18nComponent.i18n(BusinessCode.HTTP_NOT_SUPPORT.getCode(), i18nParameters.toArray(), i18nComponent.getLocale(request));
        return new ReturnBean<>(BusinessCode.HTTP_NOT_SUPPORT.getCode(), message, message);
    }

    /**
     * 参数验证异常处理
     *
     * @param exception 参数验证异常
     * @return 通用返回值
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ReturnBean<String> methodValidationExceptionResponse(MethodArgumentNotValidException exception, HttpServletRequest request) {
        // 格式化错误信息
        String errorMsg = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.joining("、"));

        String message = i18nComponent.i18n(BusinessCode.PARAMETER_VALIDATION_FAILED.getCode(), new Object[]{errorMsg}, i18nComponent.getLocale(request));

        return new ReturnBean<>(BusinessCode.PARAMETER_VALIDATION_FAILED.getCode(), message, message);
    }

    /**
     * 单个参数验证异常，如NotBlank
     *
     * @param exception 异常信息
     * @return 通用返回
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ReturnBean<String> constraintValidationExceptionResponse(ConstraintViolationException exception, HttpServletRequest request) {
        final String errMsg = exception.getConstraintViolations()
                .stream()
                .map(each -> CharSequenceUtil.split(each.getPropertyPath().toString(), ".").get(1) + ":" + each.getMessage()).collect(Collectors.joining("、"));

        return new ReturnBean<>(BusinessCode.PARAMETER_VALIDATION_FAILED.getCode(), errMsg, errMsg);
    }

    /**
     * POST请求的请求体为空
     *
     * @param exception 异常信息
     * @return 通用返回
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ReturnBean<String> httpMessageNotReadableExceptionResponse(HttpMessageNotReadableException exception, HttpServletRequest request) {
        String errMsg = i18nComponent.i18n(BusinessCode.REQUEST_BODY_MISSING.getCode(), new Object[0], i18nComponent.getLocale(request));
        return new ReturnBean<>(BusinessCode.REQUEST_BODY_MISSING.getCode(), errMsg, errMsg);
    }

    /**
     * 参数验证注解错误
     *
     * @param exception 异常信息
     * @return 通用返回
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public ReturnBean<String> unexpectedTypeExceptionResponse(UnexpectedTypeException exception, HttpServletRequest request) {
        String errMsg = i18nComponent.i18n(BusinessCode.VALIDATION_ANNOTATIONS_INCORRECTLY.getCode(), new Object[0], i18nComponent.getLocale(request));
        return new ReturnBean<>(BusinessCode.VALIDATION_ANNOTATIONS_INCORRECTLY.getCode(), errMsg, errMsg);
    }

    /**
     * 业务异常
     *
     * @param e       异常信息
     * @param request 请求
     * @return 通用返回值
     */
    @ExceptionHandler(BusinessException.class)
    public ReturnBean<String> businessException(BusinessException e, HttpServletRequest request) {
        String message = i18nComponent.i18n(e.getStatus().getCode(), e.getMessageArgs(), i18nComponent.getLocale(request));

        return new ReturnBean<>(e.getStatus().getCode(), message, message);
    }

    /**
     * 未知异常
     *
     * @param e       异常信息
     * @param request 请求
     * @return 通用返回值
     */
    @ExceptionHandler(Exception.class)
    public ReturnBean<String> httpRequestMethodError(Exception e, HttpServletRequest request) {
        return new ReturnBean<>(BusinessCode.UNKNOWN_ERROR.getCode(), i18nComponent.i18n(BusinessCode.UNKNOWN_ERROR.getCode(), new Object[]{e.getMessage()}, i18nComponent.getLocale(request)), e.getMessage());
    }

}

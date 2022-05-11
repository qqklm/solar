package io.github.qqklm.common.config;

import io.github.qqklm.common.BusinessException;
import io.github.qqklm.common.BusinessStatus;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义错误控制器
 *
 * @author wb
 * @date 2022/3/28 11:32
 */
@RestController
public class ReDirectedErrorConfig implements ErrorController {

    /**
     * 将所有异常请求重定向到该控制器，再由其抛出，被该控制器增强器处理，可以处理filter， interceptor的异常
     *
     * @param request 请求
     */
    @RequestMapping("/error")
    public void handleError(HttpServletRequest request) {
        // NOT_FOUND异常
        if (Integer.valueOf(HttpStatus.NOT_FOUND.value()).equals(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))) {
            throw new BusinessException(BusinessStatus.HTTP_NOT_FOUND, new Object[]{request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)});
        }
    }
}
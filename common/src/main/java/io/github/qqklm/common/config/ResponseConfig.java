package io.github.qqklm.common.config;

import io.github.qqklm.common.BusinessException;
import io.github.qqklm.common.BusinessCode;
import io.github.qqklm.common.ReturnBean;
import io.github.qqklm.common.component.JacksonComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoint;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 统一响应
 *
 * @author wb
 * @date 2022/3/25 15:34
 */
@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class ResponseConfig implements ResponseBodyAdvice<Object> {
    private final JacksonComponent jacksonComponent;
    private final PathMappedEndpoints pathMappedEndpoints;

    public ResponseConfig(JacksonComponent jacksonComponent, PathMappedEndpoints pathMappedEndpoints) {
        this.jacksonComponent = jacksonComponent;
        this.pathMappedEndpoints = pathMappedEndpoints;
    }

    /**
     * 判断响应是否支持
     *
     * @param returnType    响应类型
     * @param converterType {@link org.springframework.http.converter.HttpMessageConverter}
     * @return true：支持，false：不支持
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Method method = returnType.getMethod();
        if (Objects.isNull(method)) {
            return false;
        }
        // 普通Controller并且没有使用ResponseBody注解，不需要序列化
        Controller controller = method.getDeclaringClass().getAnnotation(Controller.class);

        if (Objects.nonNull(controller) && Objects.isNull(method.getAnnotation(ResponseBody.class))) {
            return false;
        }

        // 处理ResponseBodyAdviceIgnore注解
        ResponseBodyAdviceIgnore clazzResponseBodyAdviceIgnore = method.getDeclaringClass().getAnnotation(ResponseBodyAdviceIgnore.class);

        if (Objects.nonNull(clazzResponseBodyAdviceIgnore) || Objects.nonNull(method.getAnnotation(ResponseBodyAdviceIgnore.class))) {
            return false;
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 剔除监控请求，不包装其返回结果
        String requestUri = request.getURI().getPath();
        String basePath = pathMappedEndpoints.getBasePath();
        for (PathMappedEndpoint pathMappedEndpoint : pathMappedEndpoints.stream().collect(Collectors.toList())) {
            String endpointPath = basePath + "/" + ((ExposableEndpoint<?>) pathMappedEndpoint).getEndpointId().toString();
            if (requestUri.equals(endpointPath)) {
                return body;
            }
        }
        try {
            if (Objects.isNull(body)) {
                log.debug("响应：{}", jacksonComponent.getObjectMapper().writeValueAsString(null));
                return jacksonComponent.getObjectMapper().writeValueAsString(new ReturnBean<Void>());
            }
            if (body instanceof ReturnBean) {
                log.debug("响应：{}", jacksonComponent.getObjectMapper().writeValueAsString(((ReturnBean<?>) body).getData()));
                return jacksonComponent.getObjectMapper().writeValueAsString(body);
            }
            log.debug("响应：{}", jacksonComponent.getObjectMapper().writeValueAsString(body));
            return jacksonComponent.getObjectMapper().writeValueAsString(new ReturnBean<>(body));
        } catch (Exception e) {
            throw new BusinessException(BusinessCode.JSON_SERIALIZATION_ERROR);
        }
    }

}

package io.github.qqklm.common.config;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author wb
 * @date 2022/3/29 9:59
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    /**
     * 跨域支持
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //添加映射路径
        registry.addMapping("/**")
                //是否发送Cookie
                .allowCredentials(true)
                //设置放行哪些原始域   SpringBoot2.4.4下低版本使用.allowedOrigins("*")
                .allowedOriginPatterns("*")
                //放行哪些请求方式
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name())
                //.allowedMethods("*") //或者放行全部
                //放行哪些原始请求头部信息
                .allowedHeaders("*")
                //暴露哪些原始请求头部信息
                .exposedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 如果拦截后发生异常，spring boot会去请求error，所以此处要剔除error拦截
        registry.addInterceptor(new LogInterceptor()).order(Ordered.HIGHEST_PRECEDENCE).addPathPatterns("/**").excludePathPatterns("/error");
    }

    @Slf4j
    private static class LogInterceptor implements HandlerInterceptor {
        // 链路追踪标识
        private static final String REQUEST_ID = "tranceId";
        // 接收请求的时间
        private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String tranceId = request.getHeader(REQUEST_ID);

            if (CharSequenceUtil.isBlank(tranceId)) {
                tranceId = IdUtil.getSnowflakeNextIdStr();
            }
            // 将请求标识放入日志上下文中
            MDC.put(REQUEST_ID, tranceId);
            START_TIME.set(System.currentTimeMillis());
            RequestWrapper requestWrapper = (RequestWrapper) request;
            Map<String, String[]> parameters = new HashMap<>(request.getParameterMap());
            log.debug(
                    "请求：{}，请求方式：{}，请求参数：{}，请求体：{}",
                    request.getRequestURI(),
                    request.getMethod(),
                    parameters.isEmpty() ? "无" : parameters,
                    ObjectUtil.defaultIfBlank(requestWrapper.getBody(), "无")
            );

            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            log.debug("响应耗时：{}ms", LocalDateTimeUtil.between(LocalDateTimeUtil.of(START_TIME.get()), LocalDateTimeUtil.now(), ChronoUnit.MILLIS));
            MDC.clear();
            START_TIME.remove();
        }
    }

    /**
     * 包装request
     *
     * @author wb
     * @date 2022/5/11 17:44
     */
    @Component
    public static class RequestFilter implements Filter {
        public RequestFilter() {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            ServletRequest requestWrapper = null;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new RequestWrapper((HttpServletRequest) request);
            }

            if (requestWrapper == null) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(requestWrapper, response);
            }

        }
    }

    /**
     * HttpServletRequest 的包装
     *
     * @author wb
     * @date 2022/5/11 17:30
     */
    @Getter
    private static class RequestWrapper extends HttpServletRequestWrapper {
        private final String body;

        public RequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.body = this.getBodyString(request);
        }

        public String getBody() {
            return this.body;
        }

        public String getBodyString(HttpServletRequest request) throws IOException {
            String contentType = request.getContentType();
            String bodyString = "";
            StringBuilder sb = new StringBuilder();
            if (CharSequenceUtil.isEmpty(contentType) || !contentType.contains("multipart/form-data") && !contentType.contains("x-www-form-urlencoded")) {
                return IoUtil.read(request.getInputStream(), StandardCharsets.UTF_8);
            } else {
                Map<String, String[]> parameterMap = request.getParameterMap();

                Map.Entry<String, String[]> next;
                String value;
                for (Iterator<Map.Entry<String, String[]>> var6 = parameterMap.entrySet().iterator(); var6.hasNext(); sb.append(next.getKey()).append("=").append(value).append("&")) {
                    next = var6.next();
                    String[] values = next.getValue();
                    if (values != null && values.length == 1) {
                        value = values[0];
                    } else {
                        value = Arrays.toString(values);
                    }
                }

                if (sb.length() > 0) {
                    bodyString = sb.substring(0, sb.toString().length() - 1);
                }

                return bodyString;
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream is = new ByteArrayInputStream(this.body.getBytes());
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }

                @Override
                public int read() {
                    return is.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }
    }
}

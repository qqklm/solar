package io.github.qqklm.common.config;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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
            String body = request.getReader().readLine();
            Map<String, String[]> parameters = new HashMap<>(request.getParameterMap());
            log.debug("请求：{}，请求方式：{}，请求参数：{}，请求体：{}", request.getRequestURI(), request.getMethod(), parameters.isEmpty() ? "无": parameters, ObjectUtil.defaultIfBlank(body, "无"));

            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            log.debug("响应耗时：{}s", LocalDateTimeUtil.between(LocalDateTimeUtil.now(), LocalDateTimeUtil.of(START_TIME.get()), ChronoUnit.SECONDS));
            MDC.clear();
            START_TIME.remove();
        }
    }
}

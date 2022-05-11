package io.github.qqklm.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 添加该注解的类不需要统一返回值、添加该注解的方法不需要统一返回值
 *
 * @author wb
 * @date 2022/04/1 09:49
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBodyAdviceIgnore {
}

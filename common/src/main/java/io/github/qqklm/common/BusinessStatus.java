package io.github.qqklm.common;

import lombok.Getter;

/**
 * 业务状态
 *
 * @author wb
 * @date 2022/3/25 16:39
 */
@Getter
public enum BusinessStatus {
    /**
     * 序列化失败
     */
    JSON_SERIALIZATION_ERROR("10001"),
    /**
     * 参数错误
     */
    ILLEGAL_ARGUMENT("10002"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR("10003"),
    /**
     * 参数验证失败
     */
    PARAMETER_VALIDATION_FAILED("10004"),
    /**
     * 国际化语种设置错误
     */
    I18N_LANGUAGE_FORMAT_ERROR("10005"),
    /**
     * 不支持的国际化
     */
    I18N_LANGUAGE_NOT_SUPPORT("10006"),
    /**
     * 不存在的国际化
     */
    I18N_NOT_FOUND("10007"),
    /**
     * 不支持的网络请求方式
     */
    HTTP_NOT_SUPPORT("10008"),
    /**
     * 请求路径不存在
     */
    HTTP_NOT_FOUND("10009"),
    /**
     * 请求路径不存在
     */
    REQUEST_BODY_MISSING("10010"),
    /**
     * 参数验证注解使用错误
     */
    VALIDATION_ANNOTATIONS_INCORRECTLY("10011");
    // ...
    /**
     * 异常代码
     */
    private final String code;

    BusinessStatus(String code) {
        this.code = code;
    }
}

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
    JSON_SERIALIZATION_ERROR(10001, "JSON序列化数据失败"),
    /**
     * 参数错误
     */
    ILLEGAL_ARGUMENT(10002, "参数({0})错误"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(10003, "未知错误({0})"),
    /**
     * 参数验证失败
     */
    PARAMETER_VALIDATION_FAILED(10004, "参数验证失败({0})"),
    /**
     * 国际化语种设置错误
     */
    I18N_LANGUAGE_FORMAT_ERROR(10005, "语种格式({0})设置错误.请参考https://www.rfc-editor.org/rfc/bcp/bcp47.txt"),
    /**
     * 不支持的国际化
     */
    I18N_LANGUAGE_NOT_SUPPORT(10006, "不支持的国际化语种({0})"),
    /**
     * 不存在的国际化
     */
    I18N_NOT_FOUND(10007, "不存在的国际化code({0})"),
    /**
     * 不支持的网络请求方式
     */
    HTTP_NOT_SUPPORT(10008, "不支持的网络请求方式:{0},只支持:{1}"),
    /**
     * 请求路径不存在
     */
    HTTP_NOT_FOUND(10009, "请求路径({0})不存在");
    // ...
    /**
     * 异常代码
     */
    private final int code;
    /**
     * 异常信息
     */
    private final String msg;

    BusinessStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

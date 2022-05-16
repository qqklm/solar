package io.github.qqklm.tools.common;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author wb
 * @date 2022/5/12 11:26
 */
@Getter
public enum BusinessCode {
    /**
     * 不支持的SQL操作
     */
    SQL_OPERATOR_NOT_SUPPORT("20001"),
    /**
     * SQL语法错误
     */
    SQL_SYNTAX_ERROR("20002"),
    /**
     * IP格式不正确
     */
    IP_PATTERN_ERROR("20003"),
    ;
    private final String code;

    BusinessCode(String code) {
        this.code = code;
    }
}

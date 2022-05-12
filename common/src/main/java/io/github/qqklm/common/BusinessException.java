package io.github.qqklm.common;

import lombok.Data;

/**
 * 业务异常
 *
 * @author wb
 * @date 2022/3/10 13:52
 */
@Data
public final class BusinessException extends RuntimeException {
    /**
     * 自定义业务异常代码
     */
    private String code;
    /**
     * 异常信息中的格式化参数
     */
    private Object[] messageArgs;

    /**
     * 构建业务异常
     *
     * @param code 业务异常
     */
    public BusinessException(String code) {
        this.code = code;
        this.messageArgs = new Object[]{};
    }

    /**
     * 构建业务异常
     *
     * @param code 业务异常
     */
    public BusinessException(String code, Object[] messageArgs) {
        this.code = code;
        this.messageArgs = messageArgs;
    }
//
//    /**
//     * 构建业务异常
//     *
//     * @param msg 业务异常信息
//     */
//    public BusinessException(String msg) {
//        this.msg = msg;
//        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
//    }
//
//    /**
//     * 构建业务异常
//     *
//     * @param msg  业务异常信息
//     * @param code 业务异常代码
//     */
//    public BusinessException(String msg, Integer code) {
//        this.msg = msg;
//        this.code = code;
//    }
//
//    /**
//     * 构建业务异常
//     *
//     * @param cause 异常栈
//     * @param msg  业务异常信息
//     * @param code 业务异常代码
//     */
//    public BusinessException(Throwable cause, String msg, Integer code) {
//        super(cause);
//        this.msg = msg;
//        this.code = code;
//    }
}

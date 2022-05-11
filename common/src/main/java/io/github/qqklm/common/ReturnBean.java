package io.github.qqklm.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 响应包装
 *
 * @author wb
 * @date 2022/3/25 17:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBean<T> {
    /**
     * 状态码
     * {@link org.springframework.http.HttpStatus}
     * {@link BusinessStatus}
     */
    private Integer code = HttpStatus.OK.value();
    /**
     * 额外信息，默认为空
     */
    private String msg = "";
    /**
     * 返回值
     */
    private T data;

    public ReturnBean(T data) {
        this.data = data;
    }
}

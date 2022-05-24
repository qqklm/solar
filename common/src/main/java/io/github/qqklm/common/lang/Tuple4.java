package io.github.qqklm.common.lang;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 四元元组
 *
 * @author wb
 * @date 2022/5/23 9:28
 */
@Data
@NoArgsConstructor
public class Tuple4<F, S, T, FF> extends Tuple3<F, S, T> {
    /**
     * 第四个元素
     */
    private FF ff;

    public Tuple4(F f, S s, T t, FF ff) {
        super(f, s, t);
        this.ff = ff;
    }
}

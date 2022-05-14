package io.github.qqklm.common.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三元元组
 *
 * @author wb
 * @date 2022/5/13 9:24
 */
@Data
@NoArgsConstructor
public class Tuple3<F, S, T> extends Tuple2<F, S> {
    /**
     * 第三个元素
     */
    private T t;

    public Tuple3(F f, S s, T t) {
        super(f, s);
        this.t = t;
    }
}

package io.github.qqklm.common.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二元元组
 *
 * @author wb
 * @date 2022/5/13 9:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tuple2<F, S> {
    /**
     * 第一个元素
     */
    private F f;
    /**
     * 第二个元素
     */
    private S s;
}

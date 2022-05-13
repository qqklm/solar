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
    private F f;
    private S s;
}

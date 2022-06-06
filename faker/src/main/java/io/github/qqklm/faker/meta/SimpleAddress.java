package io.github.qqklm.faker.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简单位置
 *
 * @author wb
 * @date 2022/5/23 10:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAddress {
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 县
     */
    private String country;
    /**
     * 镇
     */
    private String town;
}

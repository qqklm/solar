package io.github.qqklm.faker.meta;

import lombok.Data;

/**
 * @author wb
 * @date 2022/5/19 14:28
 */
@Data
public class ForeignKey {
    /**
     * 主表名
     */
    private String pkTableName;
    /**
     * 主表列
     */
    private String pkColumnName;
    /**
     * 从表名
     */
    private String fkTableName;
    /**
     * 从表列
     */
    private String fkColumnName;
}

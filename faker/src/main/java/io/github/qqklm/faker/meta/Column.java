package io.github.qqklm.faker.meta;

import lombok.Data;

import java.util.Set;

/**
 * @author wb
 * @date 2022/5/19 14:28
 */
@Data
public class Column extends cn.hutool.db.meta.Column {
    /**
     * 列的可选值
     */
    private Set<Object> optionalValues;
    /**
     * 生成规则
     */
    private String genRule;

    private Object value;
}

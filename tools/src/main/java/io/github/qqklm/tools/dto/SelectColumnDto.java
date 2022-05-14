package io.github.qqklm.tools.dto;

import io.github.qqklm.common.lang.Tuple2;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * select语句的列参数
 *
 * @author wb
 * @date 2022/5/12 9:25
 */
@Data
public class SelectColumnDto {
    /**
     * sql语句
     */
    @NotBlank
    private String sql;
    /**
     * 待添加列，key：字段名（可能包含表名或表别名），value：字段别名
     */
    @NotEmpty
    private List<Tuple2<String, String>> columnList;
}

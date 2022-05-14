package io.github.qqklm.tools.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * insert语句的列参数
 *
 * @author wb
 * @date 2022/5/12 10:09
 */
@Data
public class InsertColumnDto {
    /**
     * sql语句
     */
    @NotBlank
    private String sql;
    /**
     * 列字段
     */
    @NotEmpty
    private List<String> columnList;
}

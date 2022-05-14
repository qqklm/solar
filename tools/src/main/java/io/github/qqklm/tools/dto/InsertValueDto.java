package io.github.qqklm.tools.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * insert语句的值参数
 *
 * @author wb
 * @date 2022/5/12 11:33
 */
@Data
public class InsertValueDto {
    /**
     * sql语句
     */
    @NotBlank
    private String sql;
    /**
     * 待插入的值
     */
    @NotEmpty
    private List<List<Object>> values;
}

package io.github.qqklm.tools.dto;

import cn.hutool.core.collection.IterUtil;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * create语句的列参数
 *
 * @author wb
 * @date 2022/5/12 9:25
 */
@Data
public class CreateColumnDto {
    /**
     * sql语句
     */
    @NotBlank
    private String sql;

    /**
     *
     * example1:
     * <p/>
     * SQLColumnDefinition sqlColumnDefinition = new SQLColumnDefinition();<br/>
     * sqlColumnDefinition.setName("c1");<br/>
     * sqlColumnDefinition.setDataType(new SQLDataTypeImpl(SQLDataType.Constants.VARCHAR, 66));
     * <p/>
     * example2:
     * <p/>
     * SQLColumnDefinition sqlColumnDefinition = new SQLColumnDefinition();<br/>
     * sqlColumnDefinition.setName("c2");<br/>
     * sqlColumnDefinition.setDataType(new SQLDataTypeImpl(SQLDataType.Constants.DATE));
     * <p/>
     */
    @JsonIgnore
    private List<SQLColumnDefinition> columnList;

    /**
     * 列信息
     */
    @NotEmpty
    private List<ColumnDefinition> columnDefinitionList;


    public void setColumnList() {
        if (IterUtil.isEmpty(columnDefinitionList)) {
            return;
        }
        this.setColumnList(columnDefinitionList.stream().map(definition -> {
            SQLColumnDefinition sqlColumnDefinition = new SQLColumnDefinition();
            sqlColumnDefinition.setName(definition.getName());
            SQLDataTypeImpl sqlDataType = new SQLDataTypeImpl();
            sqlDataType.setName(definition.getDataType());
            sqlDataType.addArgument(new SQLIntegerExpr(definition.getLength()));
            sqlColumnDefinition.setDataType(sqlDataType);
            return sqlColumnDefinition;
        }).collect(Collectors.toList()));
    }

    @Data
    private static final class ColumnDefinition {
        /**
         * 列名
         */
        @NotBlank
        private String name;
        /**
         * 字段类型
         */
        @NotBlank
        private String dataType;
        /**
         * 字段长度，字符串类型的列需要
         */
        @Min(1)
        @Nullable
        private Integer length;
    }
}

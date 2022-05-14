package io.github.qqklm.tools.dto;

import cn.hutool.core.collection.IterUtil;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.qqklm.common.BusinessException;
import io.github.qqklm.common.lang.Tuple2;
import io.github.qqklm.common.lang.Tuple3;
import io.github.qqklm.tools.common.BusinessCode;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * sql语句的条件列参数
 *
 * @author wb
 * @date 2022/5/12 10:46
 */
@Data
public class SqlConditionDto {
    /**
     * sql语句
     */
    @NotBlank
    private String sql;

    /**
     * 条件参数
     */
    @NotEmpty
    private List<ConditionDefinition> conditionDefinitionList;

    @JsonIgnore
    private List<Tuple2<String, Tuple3<String, SQLBinaryOperator, Object>>> conditionList;

    public void setConditionList() {
        if (IterUtil.isEmpty(conditionDefinitionList)) {
            return;
        }
        conditionDefinitionList.stream().collect(Collectors.groupingBy(ConditionDefinition::getTableAlias)).forEach((tableAlias, conditionDefinitions) -> {
            conditionDefinitions.forEach(conditionDefinition -> {
                SQLBinaryOperator sqlBinaryOperator = getSqlBinaryOperatorByName(conditionDefinition.getSqlBinaryOperator());

                this.getConditionList().add(new Tuple2<>(tableAlias, new Tuple3<>(conditionDefinition.getColumnName(), sqlBinaryOperator, conditionDefinition.getValue())));
            });
        });
    }

    private SQLBinaryOperator getSqlBinaryOperatorByName(String operatorName) {
        switch (operatorName) {
            case "UNION":
                return SQLBinaryOperator.Union;
            case "COLLATE":
                return SQLBinaryOperator.COLLATE;
            case "^":
                return SQLBinaryOperator.BitwiseXor;
            case "^=":
                return SQLBinaryOperator.BitwiseXorEQ;
            case "*":
                return SQLBinaryOperator.Multiply;
            case "/":
                return SQLBinaryOperator.Divide;
            case "DIV":
                return SQLBinaryOperator.DIV;
            case "%":
                return SQLBinaryOperator.Modulus;
            case "MOD":
                return SQLBinaryOperator.Mod;
            case "+":
                return SQLBinaryOperator.Add;
            case "-":
                return SQLBinaryOperator.Subtract;
            case "->":
                return SQLBinaryOperator.SubGt;
            case "->>":
                return SQLBinaryOperator.SubGtGt;
            case "#>":
                return SQLBinaryOperator.PoundGt;
            case "#>>":
                return SQLBinaryOperator.PoundGtGt;
            case "??":
                return SQLBinaryOperator.QuesQues;
            case "?|":
                return SQLBinaryOperator.QuesBar;
            case "?&":
                return SQLBinaryOperator.QuesAmp;
            case "<<":
                return SQLBinaryOperator.LeftShift;
            case ">>":
                return SQLBinaryOperator.RightShift;
            case "&":
                return SQLBinaryOperator.BitwiseAnd;
            case "|":
                return SQLBinaryOperator.BitwiseOr;
            case ">":
                return SQLBinaryOperator.GreaterThan;
            case ">=":
                return SQLBinaryOperator.GreaterThanOrEqual;
            case "IS":
                return SQLBinaryOperator.Is;
            case "<":
                return SQLBinaryOperator.LessThan;
            case "<=":
                return SQLBinaryOperator.LessThanOrEqual;
            case "<=>":
                return SQLBinaryOperator.LessThanOrEqualOrGreaterThan;
            case "<>":
                return SQLBinaryOperator.LessThanOrGreater;
            case "IS DISTINCT FROM":
                return SQLBinaryOperator.IsDistinctFrom;
            case "IS NOT DISTINCT FROM":
                return SQLBinaryOperator.IsNotDistinctFrom;
            case "LIKE":
                return SQLBinaryOperator.Like;
            case "SOUNDS LIKE":
                return SQLBinaryOperator.SoudsLike;
            case "NOT LIKE":
                return SQLBinaryOperator.NotLike;
            case "ILIKE":
                return SQLBinaryOperator.ILike;
            case "NOT ILIKE":
                return SQLBinaryOperator.NotILike;
            case "@@":
                return SQLBinaryOperator.AT_AT;
            case "SIMILAR TO":
                return SQLBinaryOperator.SIMILAR_TO;
            case "~":
                return SQLBinaryOperator.POSIX_Regular_Match;
            case "~*":
                return SQLBinaryOperator.POSIX_Regular_Match_Insensitive;
            case "!~":
                return SQLBinaryOperator.POSIX_Regular_Not_Match;
            case "!~*":
                return SQLBinaryOperator.POSIX_Regular_Not_Match_POSIX_Regular_Match_Insensitive;
            case "@>":
                return SQLBinaryOperator.Array_Contains;
            case "<@":
                return SQLBinaryOperator.Array_ContainedBy;
            case "~=":
                return SQLBinaryOperator.SAME_AS;
            case "?":
                return SQLBinaryOperator.JSONContains;
            case "RLIKE":
                return SQLBinaryOperator.RLike;
            case "NOT RLIKE":
                return SQLBinaryOperator.NotRLike;
            case "!=":
                return SQLBinaryOperator.NotEqual;
            case "!<":
                return SQLBinaryOperator.NotLessThan;
            case "!>":
                return SQLBinaryOperator.NotGreaterThan;
            case "IS NOT":
                return SQLBinaryOperator.IsNot;
            case "ESCAPE":
                return SQLBinaryOperator.Escape;
            case "REGEXP":
                return SQLBinaryOperator.RegExp;
            case "NOT REGEXP":
                return SQLBinaryOperator.NotRegExp;
            case "=":
                return SQLBinaryOperator.Equality;
            case "!":
                return SQLBinaryOperator.BitwiseNot;
            case "||":
                return SQLBinaryOperator.Concat;
            case "AND":
                return SQLBinaryOperator.BooleanAnd;
            case "XOR":
                return SQLBinaryOperator.BooleanXor;
            case "OR":
                return SQLBinaryOperator.BooleanOr;
            case ":=":
                return SQLBinaryOperator.Assignment;
            case "&&":
                return SQLBinaryOperator.PG_And;
            case "<->":
                return SQLBinaryOperator.PG_ST_DISTANCE;
            default:
                throw new BusinessException(BusinessCode.SQL_OPERATOR_NOT_SUPPORT.getCode(), new String[]{operatorName});
        }
    }

    @Data
    private static final class ConditionDefinition {
        /**
         * 表别名
         */
        private String tableAlias;
        /**
         * 列明
         */
        private String columnName;
        /**
         * 操作符
         *
         * @see SQLBinaryOperator
         */
        private String sqlBinaryOperator;
        /**
         * 值
         */
        private Object value;
    }
}

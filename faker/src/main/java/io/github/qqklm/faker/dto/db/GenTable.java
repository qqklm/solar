package io.github.qqklm.faker.dto.db;

import io.github.qqklm.faker.meta.ForeignKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * 生成规则
 *
 * @author wb
 * @date 2022/5/23 10:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenTable {

    /**
     * 表的生成规则
     */
    private List<TableConfig> tableConfigList;

    /**
     * 数据库的连接信息
     */
    @NotNull
    private ConnectionMsg connectionMsg;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableConfig {
        /**
         * 表名
         */
        @NotBlank
        private String tableName;
        /**
         * 指定表间的关联关系
         */
        private List<ForeignKey> foreignKeyList;
        /**
         * 表中列信息
         */
        @NotBlank
        private List<ColumnConfig> columnConfigList;
        /**
         * fake的数据量
         */
        private Integer fakeSize = 100;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ColumnConfig {
            public static final String STR_RULE_ADDRESS = "STR_ADDRESS";
            public static final String STR_RULE_ADDRESS_PROVINCE = "STR_ADDRESS_PROVINCE";
            public static final String STR_RULE_ADDRESS_CITY = "STR_ADDRESS_CITY";
            public static final String STR_RULE_ADDRESS_COUNTRY = "STR_ADDRESS_COUNTRY";
            public static final String STR_RULE_ADDRESS_TOWN = "STR_ADDRESS_TOWN";
            public static final String STR_RULE_MOBILE = "STR_MOBILE";
            public static final String STR_RULE_EMAIL = "STR_EMAIL";
            public static final String STR_RULE_DEPT = "STR_DEPT";
            public static final String STR_RULE_USERNAME = "STR_USERNAME";
            /**
             * 字段名
             */
            @NotBlank
            private String field;
            /**
             * 字段生成规则
             */
            private String genRule;
            /**
             * 字段值的可选值
             */
            private Set<Object> optionalValues;
            /**
             * 字段指定值
             */
            private Object value;
        }
    }
}

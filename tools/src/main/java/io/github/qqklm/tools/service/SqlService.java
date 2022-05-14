package io.github.qqklm.tools.service;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.db.sql.SqlUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.builder.impl.SQLBuilderImpl;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import io.github.qqklm.common.BusinessException;
import io.github.qqklm.common.lang.Tuple2;
import io.github.qqklm.common.lang.Tuple3;
import io.github.qqklm.tools.common.BusinessCode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * sql相关
 *
 * @author wb
 * @date 2022/5/11 14:51
 */
@Service
public class SqlService {
    /**
     * 为查询语句添加查询列
     *
     * @param sql        sql语句
     * @param columnList 待添加列，f：字段名（可能包含表名或表别名），s：字段别名
     * @return 修改后sql
     */
    public String addSelectColumn(String sql, List<Tuple2<String, String>> columnList) {
        checkAndThrow(sql);
        if (IterUtil.isEmpty(columnList)) {
            return sql;
        }

        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseStatement();
        SQLSelect select = sqlStatement.getSelect();
        SQLSelectQueryBlock queryBlock = select.getQueryBlock();
        columnList.forEach(column -> {
            SQLSelectItem sqlSelectItem = new SQLSelectItem();
            sqlSelectItem.setExpr(new SQLIdentifierExpr(column.getF()));
            if (CharSequenceUtil.isNotBlank(column.getS())) {
                sqlSelectItem.setAlias(column.getS());
            }
            queryBlock.addSelectItem(sqlSelectItem);
        });

        return sqlStatement.toString();
    }

    /**
     * 构建表达式
     * example:
     * <p></p>
     * Pair.of("t1", Triple.of("cb", SQLBinaryOperator.Equality, 2))
     * <p></p>
     *
     * @param dbType    数据库类型
     * @param condition 构建条件
     * @return 表达式
     */
    public SQLBinaryOpExpr buildSqlBinaryOpExpr(DbType dbType, Tuple2<String, Tuple3<String, SQLBinaryOperator, Object>> condition) {
        SQLBinaryOpExpr expr = new SQLBinaryOpExpr(dbType);
        expr.setOperator(condition.getS().getS());
        expr.setLeft(new SQLPropertyExpr(condition.getF(), condition.getS().getF()));
        expr.setRight(SQLBuilderImpl.toSQLExpr(condition.getS().getT(), dbType));

        return expr;
    }

    /**
     * 为sql语句添加条件
     *
     * @param sql           sql语句
     * @param conditionList 条件列表
     * @return 修改后的sql
     */
    public String addCondition(String sql, List<Tuple2<String, Tuple3<String, SQLBinaryOperator, Object>>> conditionList) {
        checkAndThrow(sql);
        if (IterUtil.isEmpty(conditionList)) {
            return sql;
        }

        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
        SQLStatement sqlStatement = sqlStatementParser.parseStatement();
        if (sqlStatement instanceof SQLUpdateStatement) {
            SQLUpdateStatement sqlUpdateStatement = (SQLUpdateStatement) sqlStatement;
            conditionList.forEach(con -> sqlUpdateStatement.addCondition(buildSqlBinaryOpExpr(sqlStatement.getDbType(), con)));
            return sqlUpdateStatement.toString();
        } else if (sqlStatement instanceof SQLSelectStatement) {
            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
            SQLSelectQueryBlock queryBlock = sqlSelectStatement.getSelect().getQueryBlock();
            conditionList.forEach(condition -> queryBlock.addCondition(buildSqlBinaryOpExpr(sqlStatement.getDbType(), condition)));
            return sqlSelectStatement.toString();
        } else if (sqlStatement instanceof SQLDeleteStatement) {
            SQLDeleteStatement sqlSelectStatement = (SQLDeleteStatement) sqlStatement;
            conditionList.forEach(con -> sqlSelectStatement.addCondition(buildSqlBinaryOpExpr(sqlStatement.getDbType(), con)));
            return sqlSelectStatement.toString();
        } else {
            throw new RuntimeException("不支持的SQL语句");
        }
    }

    /**
     * 为插入语句添加值
     *
     * @param sql        sql语句
     * @param valuesList 待添加的值
     * @return 修改后的sql
     */
    public String addInsertValues(String sql, List<List<Object>> valuesList) {
        checkAndThrow(sql);
        if (IterUtil.isEmpty(valuesList)) {
            return sql;
        }

        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
        SQLInsertStatement sqlStatement = (SQLInsertStatement) sqlStatementParser.parseInsert();

        valuesList.forEach(values -> {
            sqlStatement.getValuesList().addAll(
                    values.stream().map(value -> {
                        final SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
                        valuesClause.addValue(SQLBuilderImpl.toSQLExpr(value, sqlStatement.getDbType()));
                        return valuesClause;
                    }).collect(Collectors.toList())
            );
        });

        return sqlStatement.toString();
    }

    /**
     * 为插入语句添加列，并添加占位符
     *
     * @param sql        sql语句
     * @param columnList 待添加的列
     * @return 修改后的sql
     */
    public String addInsertColumnWithPlaceholder(String sql, List<String> columnList) {
        checkAndThrow(sql);
        if (IterUtil.isEmpty(columnList)) {
            return sql;
        }

        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
        SQLInsertStatement sqlStatement = (SQLInsertStatement) sqlStatementParser.parseInsert();

        columnList.forEach(column -> {
            sqlStatement.addColumn(new SQLIdentifierExpr(column));
            sqlStatement.getValues().addValue(new SQLVariantRefExpr("?"));
        });

        return sqlStatement.toString();
    }

    /**
     * 为建表语句添加列
     * <p/>
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
     *
     * @param sql                     建表sql语句
     * @param sqlColumnDefinitionList 待添加的列
     * @return 修改后的sql
     */
    public String addCreateColumn(String sql, List<SQLColumnDefinition> sqlColumnDefinitionList) {
        checkAndThrow(sql);
        if (IterUtil.isEmpty(sqlColumnDefinitionList)) {
            return sql;
        }
        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
        SQLCreateTableStatement sqlStatement = (SQLCreateTableStatement) sqlStatementParser.parseCreate();

        List<SQLTableElement> tableElementList = sqlStatement.getTableElementList();
        tableElementList.addAll(sqlColumnDefinitionList);

        return sqlStatement.toString();
    }

    /**
     * 为sql添加limit条件
     *
     * @param sql      sql语句
     * @param offset   查询的偏移量
     * @param rowCount 查询的数据量
     * @return 修改后的sql语句
     */
    public String addLimit(String sql, Integer offset, Integer rowCount) {
        checkAndThrow(sql);
        if (Objects.isNull(offset) || NumberUtil.compare(offset, 0) < 0) {
            offset = 0;
        }
        if (Objects.isNull(rowCount) || NumberUtil.compare(rowCount, 0) <= 0) {
            rowCount = 10;
        }
        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseStatement();
        SQLSelectQueryBlock queryBlock = sqlStatement.getSelect().getQueryBlock();
        SQLLimit limit = queryBlock.getLimit();
        if (Objects.isNull(limit)) {
            queryBlock.setLimit(new SQLLimit(new SQLIntegerExpr(offset), new SQLIntegerExpr(rowCount)));
        } else {
            limit.setOffset(offset);
            limit.setRowCount(rowCount);
        }
        return sqlStatement.getSelect().toString();
    }

    /**
     * 表名和别名的映射
     *
     * @param from             关联关系
     * @param tableNameMapping 最终映射关系，key：别名，value：表名
     */
    private void getTableNameMapping(SQLTableSource from, Map<String, String> tableNameMapping) {
        if (from instanceof SQLJoinTableSource) {
            SQLTableSource left = ((SQLJoinTableSource) from).getLeft();
            SQLExprTableSource right = (SQLExprTableSource) ((SQLJoinTableSource) from).getRight();
            tableNameMapping.put(right.getAlias(), right.getExpr().toString());
            if (left instanceof SQLJoinTableSource) {
                getTableNameMapping(left, tableNameMapping);
            } else {
                tableNameMapping.put(left.getAlias(), ((SQLExprTableSource) left).getExpr().toString());
            }

        } else {
            tableNameMapping.put(from.getAlias(), ((SQLExprTableSource) from).getExpr().toString());
        }
    }

    /**
     * 解析sql，获取sql中表和对应字段
     *
     * @param sql sql语句
     * @return key：表名，value：字段名
     */
    public List<Pair<String, List<String>>> parseSql(String sql) {
        checkAndThrow(sql);
        SQLStatementParser sqlStatementParser = new MySqlStatementParser(sql);
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseSelect();
        SQLSelect select = sqlStatement.getSelect();
        Map<String, List<String>> tableFieldMapping = select.getQueryBlock().getSelectList()
                .stream()
                .collect(Collectors.toMap(
                        each -> ((SQLPropertyExpr) each.getExpr()).getOwnernName(),
                        each -> ListUtil.toList(((SQLPropertyExpr) each.getExpr()).getName()),
                        (o1, o2) -> {
                            o1.addAll(o2);
                            return o1;
                        }
                ));
        SQLTableSource from = select.getQueryBlock().getFrom();
        Map<String, String> tableNameMapping = new HashMap<>(6);
        getTableNameMapping(from, tableNameMapping);
        List<Pair<String, List<String>>> selectInfo = new ArrayList<>(tableFieldMapping.size());
        tableFieldMapping.forEach((tableName, fieldName) -> {
            selectInfo.add(Pair.of(CharSequenceUtil.blankToDefault(tableNameMapping.get(tableName), tableName), fieldName));
        });

        return selectInfo;
    }

    /**
     * 检测语法是否合法
     *
     * @param sql sql语句
     * @return true：合法，false：不合法
     */
    public boolean check(String sql) {
        try {
            SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
            sqlStatementParser.parseStatementList();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 格式化sql
     *
     * @param sql sql语句
     * @return 格式化后sql
     */
    public String format(String sql) {
        checkAndThrow(sql);
        return SqlUtil.formatSql(sql);
    }

    /**
     * 验证语法，出错时抛出异常
     * @param sql sql语句
     */
    private void checkAndThrow(String sql) {
        if (!check(sql)) {
            throw new BusinessException(BusinessCode.SQL_SYNTAX_ERROR.getCode());
        }
    }
}

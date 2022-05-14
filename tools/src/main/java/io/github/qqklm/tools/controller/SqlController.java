package io.github.qqklm.tools.controller;

import cn.hutool.core.lang.Pair;
import io.github.qqklm.tools.dto.*;
import io.github.qqklm.tools.service.SqlService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * sql相关控制器
 *
 * @author wb
 * @date 2022/5/11 15:00
 */
@Validated
@RestController
@RequestMapping("/sql")
public class SqlController {
    private final SqlService sqlService;

    public SqlController(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    /**
     * 增加select语句的列
     *
     * @param item 条件
     */
    @PostMapping("/column/select")
    public String addSelectColumn(@RequestBody @Validated SelectColumnDto item) {
        return this.sqlService.addSelectColumn(item.getSql(), item.getColumnList());
    }

    /**
     * 增加create语句的列
     *
     * @param item 条件
     */
    @PostMapping("/column/create")
    public String addCreateColumn(@RequestBody @Validated CreateColumnDto item) {
        item.setColumnList();
        return this.sqlService.addCreateColumn(item.getSql(), item.getColumnList());
    }

    /**
     * 增加insert语句的列
     *
     * @param item 条件
     */
    @PostMapping("/column/insert")
    public String addInsertColumnWithPlaceholder(@RequestBody @Validated InsertColumnDto item) {
        return this.sqlService.addInsertColumnWithPlaceholder(item.getSql(), item.getColumnList());
    }

    /**
     * 为sql添加limit条件
     *
     * @param sql      sql语句
     * @param offset   查询的偏移量
     * @param rowCount 查询的数据量
     * @return 修改后的sql语句
     */
    @PostMapping("/limit")
    public String addLimit(@NotBlank String sql, @RequestParam(defaultValue = "0") Integer offset, @Min(1) Integer rowCount) {
        return this.sqlService.addLimit(sql, offset, rowCount);
    }

    /**
     * 为sql添加条件
     *
     * @param item 条件
     * @return 修改后的sql语句
     */
    @PostMapping("/condition/select")
    public String addSelectCondition(@RequestBody @Validated SqlConditionDto item) {
        item.setConditionList();
        return this.sqlService.addCondition(item.getSql(), item.getConditionList());
    }

    /**
     * 为insert语句添加值
     *
     * @param item 参数
     * @return 修改后的sql语句
     */
    @PostMapping("/values")
    public String addInsertValues(@RequestBody @Validated InsertValueDto item) {
        return this.sqlService.addInsertValues(item.getSql(), item.getValues());
    }

    /**
     * sql格式化
     *
     * @param sql sql语句
     * @return 格式化后sql
     */
    @GetMapping("/format")
    public String format(@NotBlank String sql) {
        return this.sqlService.format(sql);
    }

    /**
     * 语法检测
     *
     * @param sql sql语句
     * @return true：合法，false：非法
     */
    @GetMapping("/check")
    public boolean check(@NotBlank String sql) {
        return this.sqlService.check(sql);
    }

    /**
     * sql解析
     *
     * @param sql sql语句
     * @return key：表名，value：字段名
     */
    @GetMapping("/parse")
    public List<Pair<String, List<String>>> parse(@NotBlank String sql) {
        return this.sqlService.parseSql(sql);
    }
}

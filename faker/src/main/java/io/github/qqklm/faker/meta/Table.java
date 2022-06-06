package io.github.qqklm.faker.meta;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wb
 * @date 2022/5/19 14:27
 */
@Setter
@Getter
public class Table extends cn.hutool.db.meta.Table {
    /**
     * 外键信息
     */
    private Set<ForeignKey> foreignKeys = new LinkedHashSet<>();

    private List<List<Column>> columnDataList = new ArrayList<>();

    private Integer fakeSize = 100;

    /**
     * 构造
     *
     * @param tableName 表名
     */
    public Table(String tableName) {
        super(tableName);
    }

    public Table addFk(ForeignKey fk) {
        this.foreignKeys.add(fk);
        return this;
    }

    public Table addColumn(List<Column> columnData) {
        this.columnDataList.add(columnData);
        return this;
    }
}

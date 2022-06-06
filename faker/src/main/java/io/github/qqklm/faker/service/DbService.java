package io.github.qqklm.faker.service;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Session;
import cn.hutool.db.meta.TableType;
import io.github.qqklm.faker.dto.db.ConnectionMsg;
import io.github.qqklm.faker.dto.db.DbTypeEnum;
import io.github.qqklm.faker.dto.db.GenTable;
import io.github.qqklm.faker.meta.Column;
import io.github.qqklm.faker.meta.ForeignKey;
import io.github.qqklm.faker.meta.Table;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wb
 * @date 2022/5/19 13:25
 */
public interface DbService {
    /**
     * 设置列值
     *
     * @param genTableData 数据完整的表
     * @param table        当前列所属表
     * @param column       当前列
     */
    static void fakeData(List<Table> genTableData, Table table, Column column) {

        // 如果当前列是外键，在数据完整表中查找对应值并填充
        table.getForeignKeys().stream()
                .filter(fk -> column.getName().equalsIgnoreCase(fk.getFkColumnName()))
                .findFirst()
                .flatMap(foreignKey -> genTableData.stream()
                        .filter(genTable -> genTable.getTableName().equalsIgnoreCase(foreignKey.getPkTableName()))
                        .findFirst()
                        .flatMap(genTable -> genTable.getColumnDataList().get(RandomUtil.randomInt(genTable.getColumnDataList().size())).stream()
                                .filter(genColumn -> genColumn.getName().equalsIgnoreCase(foreignKey.getPkColumnName()))
                                // 手动指定的外键可能不匹配，需要剔除
                                .filter(genColumn -> genColumn.getType() == column.getType() && genColumn.getSize() == column.getSize())
                                .findFirst()
                        )
                )
                .ifPresent(genColumn -> column.setValue(genColumn.getValue()));

        // 已被设置默认值
        if (Objects.nonNull(column.getValue())) {
            return;
        }

        // 有可选值，从可选值中选择任一一值
        if (IterUtil.isNotEmpty(column.getOptionalValues())) {
            column.setValue(ListUtil.toList(column.getOptionalValues()).get(FakeService.fakeUnsignedInt(column.getOptionalValues().size())));
            return;
        }

        // 非外键和自增主键则生成数据
        switch (JDBCType.valueOf(column.getType())) {
            // 按指定规则生成字符串
            case LONGVARCHAR:
            case VARCHAR:
                if (CharSequenceUtil.isBlank(column.getGenRule())) {
                    column.setValue(FakeService.fakeStr(RandomUtil.randomInt(Math.min(column.getSize(), 255))));
                    break;
                }
                switch (column.getGenRule()) {
                    case GenTable.TableConfig.ColumnConfig.STR_RULE_ADDRESS_PROVINCE:
                        column.setValue(FakeService.fakeProvince());
                        break;
                    case GenTable.TableConfig.ColumnConfig.STR_RULE_ADDRESS_CITY:
                        column.setValue(FakeService.fakeCity());
                        break;
                    case GenTable.TableConfig.ColumnConfig.STR_RULE_ADDRESS_COUNTRY:
                        column.setValue(FakeService.fakeCountry());
                        break;
                    case GenTable.TableConfig.ColumnConfig.STR_RULE_ADDRESS_TOWN:
                        column.setValue(FakeService.fakeTown());
                        break;
                    case GenTable.TableConfig.ColumnConfig.STR_RULE_ADDRESS:
                        column.setValue(FakeService.fakeSimpleAddress());
                        break;
                    case GenTable.TableConfig.ColumnConfig.STR_RULE_MOBILE:
                        column.setValue(FakeService.fakePhoneNumber());
                        break;
                    default:
                        break;

                }
                break;
            case DATE:
            case TIMESTAMP:
                column.setValue(FakeService.fakeDate());
                break;
            case BOOLEAN:
                column.setValue(FakeService.fakeBoolean());
                break;
            case INTEGER:
            case BIGINT:
                column.setValue(FakeService.fakeUnsignedIntLength(column.getSize()));
                break;
            case BIT:
                column.setValue(FakeService.fakeUnsignedInt(1));
                break;
            default:
        }
    }

    /**
     * 将生成的数据插入表中
     *
     * @param connection 连接
     * @param tableData  表信息
     * @param insertData 待插入的数据
     * @throws Exception 操作异常
     */
    static void insert(Connection connection, Table tableData, List<List<Column>> insertData) throws Exception {

        // 拼接除自增键外的insert语句
        String insertSqlPrefix = "INSERT INTO " + tableData.getTableName() + "( " + tableData.getColumnDataList().get(0).stream().filter(column -> !column.isAutoIncrement()).map(cn.hutool.db.meta.Column::getName).collect(Collectors.joining(",")) + ") VALUES ";

        // 处理占位符
        String insertSqlSuffix = insertData.stream()
                .map(columnDataList -> "(" + columnDataList.stream().map(columnData -> {
                    if (columnData.isAutoIncrement()) {
                        return "";
                    }
                    return "?";
                })
                        .filter(CharSequenceUtil::isNotBlank)
                        .collect(Collectors.joining(",")) + ")")
                .collect(Collectors.joining(","));

        String fullSql = insertSqlPrefix + insertSqlSuffix;

        PreparedStatement preparedStatement = connection.prepareStatement(fullSql, PreparedStatement.RETURN_GENERATED_KEYS);
        // 为占位符设置实际值
        int columnCount = (int) tableData.getColumnDataList().get(0).stream().filter(column -> !column.isAutoIncrement()).count();
        int sum = columnCount * insertData.size();
        for (int i = 1; i <= sum; i++) {
            int rowIndex = (i - 1) / columnCount;
            int columnIndex = (i - 1) % columnCount;
            try {
                Column columnData = insertData.get(rowIndex).stream()
                        .filter(column -> !column.isAutoIncrement())
                        .collect(Collectors.toList())
                        .get(columnIndex);
                preparedStatement.setObject(i, columnData.getValue(), columnData.getType());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        preparedStatement.execute();

        // 回填生成的自增键
        List<Object> generatedKeyList = new ArrayList<>();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        while (generatedKeys.next()) {
            generatedKeyList.add(generatedKeys.getObject(1));
        }

        List<Column> autoIncrementColumnList = insertData.stream()
                .flatMap(Collection::stream)
                .filter(cn.hutool.db.meta.Column::isAutoIncrement)
                .collect(Collectors.toList());
        if (IterUtil.isEmpty(autoIncrementColumnList)) {
            return;
        }
        for (int i = 0; i < generatedKeyList.size(); i++) {
            autoIncrementColumnList.get(i).setValue(generatedKeyList.get(i));
        }

    }

    /**
     * 插入数据
     *
     * @param connection 连接
     * @param tableData  待插入数据
     */
    static void insert(Connection connection, Table tableData) throws Exception {
        long l = System.currentTimeMillis();
        if (IterUtil.isEmpty(tableData.getColumnDataList())) {
            return;
        }

        // 外键值不为空
        // 自生外键
        if (tableData.getForeignKeys().stream().noneMatch(foreignKey -> foreignKey.getPkTableName().equalsIgnoreCase(foreignKey.getFkTableName()))) {
            insert(connection, tableData, tableData.getColumnDataList());
            System.out.println("---单次插入---" + (System.currentTimeMillis() - l) + "---单次插入---");
            return;
        }

        int partSize = tableData.getColumnDataList().size() / 2;

        List<List<Column>> firstPart = ListUtil.sub(tableData.getColumnDataList(), 0, partSize == 0 ? 1 : partSize);
        insert(connection, tableData, firstPart);

        List<List<Column>> secondPart = ListUtil.sub(tableData.getColumnDataList(), partSize == 0 ? 1 : partSize, tableData.getColumnDataList().size());

        if (IterUtil.isEmpty(secondPart)) {
            return;
        }


        Map<String, String> selfForeignMapping = tableData.getForeignKeys().stream()
                .filter(foreignKey -> foreignKey.getPkTableName().equalsIgnoreCase(foreignKey.getFkTableName()))
                .filter(foreignKey -> firstPart.get(0).stream()
                        .filter(column -> foreignKey.getPkColumnName().equalsIgnoreCase(column.getName()))
                        .anyMatch(cn.hutool.db.meta.Column::isPk)
                )
                .collect(Collectors.toMap(ForeignKey::getFkColumnName, ForeignKey::getPkColumnName, (o1, o2) -> o2));

        secondPart.stream()
                .flatMap(Collection::stream)
                .filter(column -> Objects.nonNull(selfForeignMapping.get(column.getName()))).collect(Collectors.toList())
                .forEach(column -> firstPart.get(RandomUtil.randomInt(firstPart.size())).stream()
                        .filter(c -> c.getName().equalsIgnoreCase(selfForeignMapping.get(column.getName())))
                        .findFirst()
                        .ifPresent(c -> column.setValue(c.getValue()))
                );

        insert(connection, tableData, secondPart);
        tableData.setColumnDataList(firstPart);
        tableData.getColumnDataList().addAll(secondPart);
        System.out.println("---单次插入---" + (System.currentTimeMillis() - l) + "---单次插入---");
    }

    /**
     * 给表属性赋予生成规则
     *
     * @param tableList       表
     * @param tableConfigList 生成规则
     * @return 表
     */
    static List<Table> setFetchTableConfig(List<Table> tableList, List<GenTable.TableConfig> tableConfigList) {
        if (IterUtil.isEmpty(tableConfigList)) {
            return tableList;
        }
        ArrayList<Table> result = new ArrayList<>();
        setFetchTableColumnConfig(result, tableList, tableConfigList);
        return result.stream().distinct().collect(Collectors.toList());
    }

    static void findPkTable(List<Table> result, List<Table> tableList, String tableName) {
        final Optional<Table> tableOptional = tableList.stream().filter(table -> table.getTableName().equalsIgnoreCase(tableName)).findFirst();
        if (tableOptional.isPresent()) {
            result.add(tableOptional.get());
            tableOptional.get().getForeignKeys().stream()
                    .filter(foreignKey -> !foreignKey.getPkTableName().equalsIgnoreCase(foreignKey.getFkTableName()))
                    .map(ForeignKey::getPkTableName)
                    .distinct()
                    .forEach(pkTableName -> findPkTable(result, tableList, pkTableName));
        }
    }

    /**
     * 查找指定表关联的表
     *
     * @param result               最终结果
     * @param tableList            原始数据
     * @param simpleFetchTableList 待处理的表
     */
    static void setFetchTableColumnConfig(List<Table> result, List<Table> tableList, List<GenTable.TableConfig> simpleFetchTableList) {
        for (GenTable.TableConfig simpleFetchTable : simpleFetchTableList) {
            final Optional<Table> tableOptional = tableList.stream().filter(t -> t.getTableName().equalsIgnoreCase(simpleFetchTable.getTableName())).findFirst();
            if (!tableOptional.isPresent()) {
                continue;
            }
            tableOptional.get().setFakeSize(simpleFetchTable.getFakeSize());
            setFetchColumnConfig(tableOptional.get(), simpleFetchTable);
            result.add(tableOptional.get());
            if (IterUtil.isNotEmpty(tableOptional.get().getForeignKeys())) {
                tableOptional.get().getForeignKeys().stream()
                        .filter(foreignKey -> !foreignKey.getPkTableName().equalsIgnoreCase(foreignKey.getFkTableName()))
                        .map(ForeignKey::getPkTableName)
                        .distinct()
                        .forEach(pkTable -> findPkTable(result, tableList, pkTable));
            }
            if (IterUtil.isNotEmpty(simpleFetchTable.getForeignKeyList())) {
                simpleFetchTable.getForeignKeyList().stream()
                        .filter(foreignKey -> !foreignKey.getPkTableName().equalsIgnoreCase(foreignKey.getFkTableName()))
                        .map(ForeignKey::getPkTableName)
                        .distinct()
                        .forEach(pkTable -> findPkTable(result, tableList, pkTable));
            }
        }
    }

    /**
     * 为列设置属性
     *
     * @param table      原始表
     * @param fetchTable 表配置信息
     */
    static void setFetchColumnConfig(Table table, GenTable.TableConfig fetchTable) {
        table.getColumns().forEach(column -> fetchTable.getColumnConfigList().stream()
                .filter(fetchColumn -> column.getName().equalsIgnoreCase(fetchColumn.getField()))
                .findFirst()
                .ifPresent(fetchColumn -> {
                    if (IterUtil.isNotEmpty(fetchColumn.getOptionalValues())) {
                        ((Column) column).setOptionalValues(fetchColumn.getOptionalValues());
                    }
                    if (CharSequenceUtil.isNotBlank(fetchColumn.getGenRule())) {
                        ((Column) column).setGenRule(fetchColumn.getGenRule());
                    }
                    if (Objects.nonNull(fetchColumn.getValue())) {
                        ((Column) column).setValue(fetchColumn.getValue());
                    }
                }));
    }

    /**
     * 构建数据
     *
     * @param connection   连接
     * @param tableList    待处理表
     * @param genTableList 已处理表
     */
    static void fake(Connection connection, List<Table> tableList, List<Table> genTableList) throws Exception {

        // 先处理没有依赖关系的表
        List<Table> normalTableList = tableList.stream()
                .filter(table -> IterUtil.isEmpty(table.getForeignKeys()))
                .collect(Collectors.toList());
        for (Table table : normalTableList) {
            fakeTableData(genTableList, table);
            insert(connection, table);
        }

        List<Table> foreignTableList = tableList.stream()
                .filter(table -> IterUtil.isNotEmpty(table.getForeignKeys()))
                .collect(Collectors.toList());
        for (Table table : foreignTableList) {
            fakeTableData(genTableList, table);
            List<String> selfForeignColumn = table.getForeignKeys().stream()
                    .filter(foreignKey -> foreignKey.getPkTableName().equalsIgnoreCase(foreignKey.getFkTableName()))
                    .map(ForeignKey::getFkColumnName)
                    .collect(Collectors.toList());
            // 有依赖关系的表，必须等依赖的表处理完，也即自身除了自增和自身依赖外的所有列都有值
            boolean b = table.getColumnDataList().get(0).stream()
                    .filter(column -> !column.isAutoIncrement() && !selfForeignColumn.contains(column.getName()))
                    .map(Column::getValue)
                    .noneMatch(Objects::isNull);
            if (b) {
                insert(connection, table);
            } else {
                fake(connection, Collections.singletonList(table), genTableList);
            }
        }

    }

    /**
     * 为表生成指定数量的数据
     *
     * @param genTableList 已生成完全的表
     * @param table        待生成数据的表
     */
    static void fakeTableData(List<Table> genTableList, Table table) {
        for (int i = 0; i < table.getFakeSize(); i++) {

            List<String> selfForeignColumn = table.getForeignKeys().stream()
                    .filter(foreignKey -> foreignKey.getPkTableName().equalsIgnoreCase(foreignKey.getFkTableName()))
                    .map(ForeignKey::getFkColumnName)
                    .collect(Collectors.toList());

            List<Column> tableData = table.getColumns().stream()
                    .map(column -> {
                        // 引用自身主键的列不需要mock
                        if (selfForeignColumn.contains(column.getName())) {
                            return ObjectUtil.clone((Column) column);
                        }
                        Column columnClone = ObjectUtil.clone((Column) column);
                        if (columnClone.isAutoIncrement()) {
                            return columnClone;
                        }
                        fakeData(genTableList, table, columnClone);
                        return columnClone;
                    })
                    .collect(Collectors.toList());
            table.addColumn(tableData);
        }
        Map<String, String> columnMapping = table.getForeignKeys().stream()
                .filter(foreignKey -> foreignKey.getFkTableName().equalsIgnoreCase(foreignKey.getPkTableName()))
                .collect(Collectors.toMap(ForeignKey::getFkColumnName, ForeignKey::getPkColumnName, (o1, o2) -> o2));

        if (table.getColumnDataList().get(0).stream()
                // 自增主键去除
                .filter(column -> !column.isAutoIncrement())
                // 引用自身主键的列去除
                .filter(column -> {
                    if (Objects.isNull(columnMapping.get(column.getName()))) {
                        return true;
                    }
                    Column foreignColumn = null;
                    Optional<Column> foreignColumnOptional = table.getColumnDataList().get(0).stream().filter(c -> c.getName().equalsIgnoreCase(column.getName())).findFirst();
                    if (foreignColumnOptional.isPresent()) {
                        foreignColumn = foreignColumnOptional.get();
                    }

                    return !Objects.nonNull(foreignColumn) || !foreignColumn.isPk();
                })
                .map(Column::getValue)
                .noneMatch(Objects::isNull)) {
            genTableList.add(table);
        }
    }

    DbTypeEnum dbType();

    /**
     * 生成数据
     *
     * @param genTable 信息
     */
    default void fake(GenTable genTable) {
        try (Session session = DbUtil.newSession(dataSource(genTable.getConnectionMsg())); Connection connection = session.getConnection()) {
            List<Table> tables = parseDb(connection);
            List<Table> confinedTables = setFetchTableConfig(tables, genTable.getTableConfigList());
            fake(connection, confinedTables, new ArrayList<>());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 构建DataSource
     *
     * @param connectionMsg 连接信息
     * @return DataSource
     */
    DataSource dataSource(ConnectionMsg connectionMsg) throws Exception;

    /**
     * 解析数据库
     *
     * @param connection 数据库连接
     * @return Table
     * @throws Exception 解析异常
     */

    default List<Table> parseDb(Connection connection) throws Exception {
        List<Table> tableList = new ArrayList<>();
        // 表
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tableSet = metaData.getTables(connection.getCatalog(), connection.getSchema(), null, new String[]{TableType.TABLE.value()});
        while (tableSet.next()) {
            Table table = new Table(tableSet.getString("TABLE_NAME"));
            tableList.add(table);
            table.setComment(tableSet.getString("REMARKS"));
            table.setCatalog(tableSet.getString("TABLE_CAT"));
            table.setSchema(tableSet.getString("TABLE_SCHEM"));

            // 主键
            ResultSet pkResultSet = metaData.getPrimaryKeys(connection.getCatalog(), connection.getSchema(), table.getTableName());
            while (pkResultSet.next()) {
                table.addPk(pkResultSet.getString("COLUMN_NAME"));
            }

            // 外键
            ResultSet fkResultSet = metaData.getImportedKeys(connection.getCatalog(), connection.getSchema(), table.getTableName());
            while (fkResultSet.next()) {
                ForeignKey fk = new ForeignKey();
                fk.setPkTableName(fkResultSet.getString("PKTABLE_NAME"));
                fk.setPkColumnName(fkResultSet.getString("PKCOLUMN_NAME"));
                fk.setFkTableName(fkResultSet.getString("FKTABLE_NAME"));
                fk.setFkColumnName(fkResultSet.getString("FKCOLUMN_NAME"));
                table.addFk(fk);
            }

            // 列
            ResultSet columnSet = metaData.getColumns(connection.getCatalog(), connection.getSchema(), tableSet.getString("TABLE_NAME"), null);
            while (columnSet.next()) {
                Column column = new Column();
                column.setNullable(columnSet.getBoolean("NULLABLE"));
                column.setSize(columnSet.getInt("COLUMN_SIZE"));
                column.setAutoIncrement(columnSet.getBoolean("IS_AUTOINCREMENT"));
                column.setType(columnSet.getInt("DATA_TYPE"));
                column.setName(columnSet.getString("COLUMN_NAME"));
                column.setColumnDef(columnSet.getString("COLUMN_DEF"));
                column.setComment(columnSet.getString("REMARKS"));
                column.setDigit(columnSet.getInt("DECIMAL_DIGITS"));
                column.setTypeName(columnSet.getString("TYPE_NAME"));
                column.setTableName(table.getTableName());
                column.setPk(table.isPk(column.getName()));
                table.setColumn(column);
            }
        }

        return tableList;
    }


    default List<Table> parseDb(ConnectionMsg connectionMsg) {
        try (Session session = DbUtil.newSession(dataSource(connectionMsg)); Connection connection = session.getConnection()) {
            return parseDb(connection);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }
}

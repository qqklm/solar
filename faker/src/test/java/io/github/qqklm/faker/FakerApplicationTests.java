package io.github.qqklm.faker;

import cn.hutool.json.JSONUtil;
import io.github.qqklm.faker.dto.db.ConnectionMsg;
import io.github.qqklm.faker.dto.db.GenTable;
import io.github.qqklm.faker.meta.ForeignKey;
import io.github.qqklm.faker.service.MySqlDbServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author wb
 * @date 2022/5/19 10:47
 */
@SpringBootTest
public class FakerApplicationTests {
    @Autowired
    private MySqlDbServiceImpl mySqlTableServiceImpl;
    @Test
    public void testMysqlFake() {
        ConnectionMsg connectionMsg = new ConnectionMsg();

        connectionMsg.setUserName("root");
        connectionMsg.setPassword("wb_239500");
        connectionMsg.setPort(3306);
        connectionMsg.setHost("localhost");
        connectionMsg.setDbName("hi");
        GenTable genTable = new GenTable();
        genTable.setConnectionMsg(connectionMsg);
        GenTable.TableConfig tableConfig = new GenTable.TableConfig();
        genTable.setTableConfigList(Collections.singletonList(tableConfig));
        tableConfig.setTableName("t3");
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setPkTableName("demo");
        foreignKey.setPkColumnName("CONTENT");
        foreignKey.setFkTableName("t3");
        foreignKey.setFkColumnName("name");
        tableConfig.setForeignKeyList(Collections.singletonList(foreignKey));
        GenTable.TableConfig.ColumnConfig column1 = new GenTable.TableConfig.ColumnConfig();
        column1.setField("ID");
        GenTable.TableConfig.ColumnConfig column2 = new GenTable.TableConfig.ColumnConfig();
        column2.setField("NAME");
        column2.setGenRule(GenTable.TableConfig.ColumnConfig.STR_RULE_ADDRESS_PROVINCE);
        tableConfig.setColumnConfigList(Arrays.asList(column1, column2));
        System.out.println(JSONUtil.toJsonStr(genTable));
        final long l = System.currentTimeMillis();
        mySqlTableServiceImpl.fake(genTable);
        System.out.println("--------" + (System.currentTimeMillis() - l) + "--------");
    }
}

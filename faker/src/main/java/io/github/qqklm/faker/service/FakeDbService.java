package io.github.qqklm.faker.service;

import io.github.qqklm.common.BusinessException;
import io.github.qqklm.faker.dto.db.ConnectionMsg;
import io.github.qqklm.faker.dto.db.GenTable;
import io.github.qqklm.faker.meta.Table;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wb
 * @date 2022/5/23 17:15
 */
@Service
public class FakeDbService {
    private final List<DbService> dbServiceList;

    public FakeDbService(List<DbService> dbServiceList) {
        this.dbServiceList = dbServiceList;
    }

    public List<Table> schema(ConnectionMsg connectionMsg) {
        for (DbService dbService : dbServiceList) {
            if (dbService.dbType().name().equalsIgnoreCase(connectionMsg.getDbType())) {
                return dbService.parseDb(connectionMsg);
            }
        }
        throw new BusinessException("不支持的数据库");
    }

    public void fake(GenTable genTable) {
        for (DbService dbService : dbServiceList) {
            if (dbService.dbType().name().equalsIgnoreCase(genTable.getConnectionMsg().getDbType())) {
                dbService.fake(genTable);
                return;
            }
        }
        throw new BusinessException("不支持的数据库");
    }
}

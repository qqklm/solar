package io.github.qqklm.faker.service;

import io.github.qqklm.faker.dto.db.ConnectionMsg;
import io.github.qqklm.faker.dto.db.DbTypeEnum;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author wb
 * @date 2022/5/23 16:32
 */
@Service
public class PostGreSqlDbServiceImpl implements DbService {
    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.POSTGRE_SQL;
    }

    @Override
    public DataSource dataSource(ConnectionMsg connectionMsg) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://" + connectionMsg.getHost()+":"+connectionMsg.getPort() + "/" + connectionMsg.getDbName() + "?currentSchema=" + connectionMsg.getSchemaName());
        dataSource.setUser(connectionMsg.getUserName());
        dataSource.setPassword(connectionMsg.getPassword());
        dataSource.setDatabaseName(connectionMsg.getDbName());
        dataSource.setCurrentSchema(connectionMsg.getSchemaName());
        return dataSource;
    }

}

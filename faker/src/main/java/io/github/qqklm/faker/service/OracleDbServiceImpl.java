package io.github.qqklm.faker.service;

import cn.hutool.db.meta.TableType;
import io.github.qqklm.common.lang.Tuple2;
import io.github.qqklm.faker.dto.db.ConnectionMsg;
import io.github.qqklm.faker.dto.db.DbTypeEnum;
import io.github.qqklm.faker.meta.Column;
import io.github.qqklm.faker.meta.ForeignKey;
import io.github.qqklm.faker.meta.Table;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wb
 * @date 2022/5/23 17:30
 */
@Service
public class OracleDbServiceImpl implements DbService {
    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.ORACLE;
    }

    @Override
    public DataSource dataSource(ConnectionMsg connectionMsg) throws Exception {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(connectionMsg.getUserName());
        dataSource.setPassword(connectionMsg.getPassword());
        dataSource.setDatabaseName(connectionMsg.getSchemaName());
        dataSource.setURL("jdbc:oracle:thin:@" + connectionMsg.getHost() + ":" + connectionMsg.getPort() + ":" + "orcl");
        return dataSource;
    }

}

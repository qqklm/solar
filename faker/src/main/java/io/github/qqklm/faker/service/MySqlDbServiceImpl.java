package io.github.qqklm.faker.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.qqklm.faker.dto.db.ConnectionMsg;
import io.github.qqklm.faker.dto.db.DbTypeEnum;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author wb
 * @date 2022/5/19 13:25
 */
@Service
public class MySqlDbServiceImpl implements DbService {

    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.MYSQL;
    }

    /**
     * 构建数据源
     *
     * @param conMsg 连接信息
     * @return 数据源
     */
    @Override
    public DataSource dataSource(ConnectionMsg conMsg) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(conMsg.getUserName());
        dataSource.setPassword(conMsg.getPassword());
        dataSource.setDatabaseName(conMsg.getSchemaName());
        dataSource.setUrl("jdbc:mysql://" + conMsg.getHost() + ":" + conMsg.getPort() + "/" + conMsg.getSchemaName());
        return dataSource;
    }

}
